//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.pilot.SkillQueueItem;
import com.beimin.eveapi.model.shared.Asset;
import com.beimin.eveapi.model.shared.Blueprint;
import com.beimin.eveapi.model.shared.EveAccountBalance;
import com.beimin.eveapi.model.shared.IndustryJob;
import com.beimin.eveapi.model.shared.MarketOrder;
import com.beimin.eveapi.parser.corporation.AccountBalanceParser;
import com.beimin.eveapi.parser.pilot.BlueprintsParser;
import com.beimin.eveapi.parser.pilot.CharacterSheetParser;
import com.beimin.eveapi.parser.pilot.IndustryJobsParser;
import com.beimin.eveapi.parser.pilot.MarketOrdersParser;
import com.beimin.eveapi.parser.pilot.PilotAssetListParser;
import com.beimin.eveapi.parser.pilot.SkillInTrainingParser;
import com.beimin.eveapi.parser.pilot.SkillQueueParser;
import com.beimin.eveapi.response.pilot.CharacterSheetResponse;
import com.beimin.eveapi.response.pilot.SkillInTrainingResponse;
import com.beimin.eveapi.response.pilot.SkillQueueResponse;
import com.beimin.eveapi.response.shared.AccountBalanceResponse;
import com.beimin.eveapi.response.shared.AssetListResponse;
import com.beimin.eveapi.response.shared.BlueprintsResponse;
import com.beimin.eveapi.response.shared.IndustryJobsResponse;
import com.beimin.eveapi.response.shared.MarketOrdersResponse;
import com.j256.ormlite.dao.Dao;

// - CLASS IMPLEMENTATION ...................................................................................
public class Pilot extends NeoComCharacter {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger												logger					= Logger.getLogger("NeoComPilot");

	// - F I E L D - S E C T I O N ............................................................................

	// - T R A N S I E N T   D A T A
	protected transient CharacterSheetResponse	characterSheet	= null;
	private transient Set<SkillQueueItem>				skills					= null;
	private transient SkillInTrainingResponse		skillInTraining	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Pilot() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Returns the number of invention jobs that can be launched simultaneously. This will depend on the skills
	 * <code>Laboratory Operation</code> and <code>Advanced Laboratory Operation</code>.
	 * 
	 * @return
	 */
	public int calculateInventionQueues() {
		int queues = 1;
		final Set<com.beimin.eveapi.model.pilot.Skill> currentskills = characterSheet.getSkills();
		for (final com.beimin.eveapi.model.pilot.Skill apiSkill : currentskills) {
			if (apiSkill.getTypeID() == ModelWideConstants.eveglobal.skillcodes.LaboratoryOperation)
				queues += apiSkill.getLevel();
			if (apiSkill.getTypeID() == ModelWideConstants.eveglobal.skillcodes.AdvancedLaboratoryOperation)
				queues += apiSkill.getLevel();
		}
		return queues;
	}

	/**
	 * Returns the number of manufacture jobs that can be launched simultaneously. This will depend on the
	 * skills <code>Mass Production</code> and <code>Advanced Mass Production</code>.
	 * 
	 * @return
	 */
	public int calculateManufactureQueues() {
		int queues = 1;
		final Set<com.beimin.eveapi.model.pilot.Skill> currentskills = characterSheet.getSkills();
		for (final com.beimin.eveapi.model.pilot.Skill apiSkill : currentskills) {
			if (apiSkill.getTypeID() == ModelWideConstants.eveglobal.skillcodes.MassProduction) queues += apiSkill.getLevel();
			if (apiSkill.getTypeID() == ModelWideConstants.eveglobal.skillcodes.AdvancedMassProduction)
				queues += apiSkill.getLevel();
		}
		return queues;
	}

	/**
	 * Return the elements collaborated by this object. For a Character it depends on the implementation being a
	 * Pilot or a Corporation. For a Pilot the result depends on the variant received as the parameter
	 */
	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		final ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		return results;
	}

	/**
	 * The processing of the assets will be performed with a SAX parser instead of the general use of a DOM
	 * parser. This requires then that the cache verification and other cache tasks be performed locally to
	 * avoid downloading the same information multiple times.<br>
	 * Cache expiration is of 6 hours but we will set it up to 3.<br>
	 * After verification we have to update the list, we then fire the events to signal asset list modification
	 * to any dependent data structures or UI objects that may be showing this information.<br>
	 * This update mechanism may require reading the last known state of the assets list from the sdcard file
	 * storage. This information is not stored automatically with the character information to speed up the
	 * initialization process and is loading only when needed and this data should be accessed. This is an
	 * special case because the assets downloaded are being written to a special set of records in the User
	 * database. Then, after the download terminates the database is updated to move those assets to the right
	 * character. It is supposed that this is performed in the background and that while we are doing this the
	 * uses has access to an older set of assets. New implementation. With the use of the eveapi library there
	 * is no need to use the URL to locate and download the assets. We use the eveapi locator and parser to get
	 * the data structures used to generate and store the assets into the local database. We first clear any
	 * database records not associated to any owner, the add records for a generic owner and finally change the
	 * owner to this character.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public synchronized void downloadAssets() {
		Pilot.logger.info(">> EveChar.updateAssets");
		try {
			// Clear any previous record with owner -1 from database.
			AppConnector.getDBConnector().clearInvalidRecords();
			PilotAssetListParser parser = new PilotAssetListParser();
			AssetListResponse response = parser.getResponse(this.getAuthorization());
			if (null != response) {
				List<Asset> assets = response.getAll();
				assetsCacheTime = new Instant(response.getCachedUntil());
				// Assets may be parent of other assets so process them recursively.
				for (final Asset eveAsset : assets)
					this.processAsset(eveAsset, null);
			}
			//			}
			AppConnector.getDBConnector().replaceAssets(this.getCharacterID());

			// Update the caching time to the time set by the eveapi.
			assetsCacheTime = new Instant(response.getCachedUntil());
		} catch (final ApiException apie) {
			apie.printStackTrace();
		}
		// Clean all user structures invalid after the reload of the assets.
		assetsManager = null;
		//		totalAssets = -1;
		//		clearTimers();
		//		JobManager.clearCache();

		this.setDirty(true);
		this.fireStructureChange("EVENTSTRUCTURE_EVECHARACTER_ASSETS", null, null);
		Pilot.logger.info("<< EveChar.updateAssets");
	}

	/**
	 * Download the blueprint list for this character from CCP using the new API over the eveapi library and
	 * then processes the response. It creates our model Blueprints that before being stored at the database are
	 * grouped into stacks to reduce the number of registers to manage on other Industry operations.<br>
	 * Current grouping is by IF-LOCATION-CONTAINER.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public synchronized void downloadBlueprints() {
		try {
			AppConnector.startChrono();
			// Clear any previous records with owner -1 from database.
			AppConnector.getDBConnector().clearInvalidRecords();
			// Download and parse the blueprints using the eveapi.
			// Set the default connector for blueprints to a cache connector.
			//			if (null == apiCacheConnector) {
			//				apiCacheConnector = new CachingConnector();
			//			}
			//			EveApi.setConnector(apiCacheConnector);
			//			BlueprintListResponse response = null;
			ArrayList<NeoComBlueprint> bplist = new ArrayList<NeoComBlueprint>();
			BlueprintsParser parser = new BlueprintsParser();
			BlueprintsResponse response = parser.getResponse(this.getAuthorization());
			if (null != response) {
				//					final ArrayList<Blueprint> bplist = new ArrayList<Blueprint>();
				Set<Blueprint> blueprints = response.getAll();
				for (Blueprint bp : blueprints)
					try {
						bplist.add(this.convert2Blueprint(bp));
					} catch (final RuntimeException rtex) {
						// Intercept any exception for blueprints that do not match the asset. Remove them from the listing
						Pilot.logger.info("W> The Blueprint " + bp.getItemID() + " has no matching asset.");
						Pilot.logger.info("W> " + bp.toString());
					}
			}
			//			}
			// Pack the blueprints and store them on the database.
			this.getAssetsManager().storeBlueprints(bplist);
			AppConnector.getDBConnector().replaceBlueprints(this.getCharacterID());
			// Update the caching time to the time set by the eveapi.
			blueprintsCacheTime = new Instant(response.getCachedUntil());
			// Update the dirty state to signal modification of store structures.
			this.setDirty(true);
		} catch (final ApiException apie) {
			apie.printStackTrace();
		}
		final Duration lapse = AppConnector.timeLapse();
		Pilot.logger.info("~~ Time lapse for [UPDATEBLUEPRINTS] - " + lapse);
	}

	/**
	 * The industry jobs are obsolete so an update was triggered. Go to the CCP servers and get a fresh set of
	 * Industry Jobs data. After the processing write the records down to the database and then remove the CCP
	 * records.<br>
	 * After the processing check for User Jobs converted to running jobs and remove them from the app list
	 * because the user has already launched them on the real EVE client.
	 */
	@Override
	public void downloadIndustryJobs() {
		Pilot.logger.info(">> EveChar.updateIndustryJobs");
		try {
			// Clear any previous record with owner -1 from database.
			//		AppConnector.getDBConnector().clearInvalidRecords();
			// Download and parse the industry jobs history.
			IndustryJobsParser parserhist = new IndustryJobsParser();
			IndustryJobsResponse responsehist = parserhist.getResponse(this.getAuthorization());
			if (null != responsehist) {
				Set<IndustryJob> jobs = responsehist.getAll();
				jobsCacheTime = new Instant(responsehist.getCachedUntil());
				for (final IndustryJob evejob : jobs) {
					final Job myjob = this.convert2Job(evejob);
					// Set the owner my there is not job cleanup.
					//					myjob.setOwnerID(getCharacterID());
					try {
						final Dao<Job, String> jobDao = AppConnector.getDBConnector().getJobDAO();
						jobDao.createOrUpdate(myjob);
						Pilot.logger.finest("-- Wrote job to database id [" + myjob.getJobID() + "]");
					} catch (final SQLException sqle) {
						Pilot.logger.severe("E> Unable to create the new Job [" + myjob.getJobID() + "]. " + sqle.getMessage());
						sqle.printStackTrace();
					}
				}
			}

			//			// Download and parse the industry jobs.
			//			final NewIndustryJobsParser parser = NewIndustryJobsParser.getInstance();
			//			final NewIndustryJobsResponse response = parser.getResponse(getAuthorization());
			//			if (null != response) {
			//				final HashSet<ApiNewIndustryJob> jobs = new HashSet<ApiNewIndustryJob>(response.getAll());
			//				jobsCacheTime = new Instant(response.getCachedUntil());
			//				for (final ApiNewIndustryJob evejob : jobs) {
			//					final Job myjob = convert2Job(evejob);
			//					// Set the owner my there is not job cleanup.
			//					//					myjob.setOwnerID(getCharacterID());
			//					try {
			//						final Dao<Job, String> jobDao = AppConnector.getDBConnector().getJobDAO();
			//						jobDao.createOrUpdate(myjob);
			//						logger.finest("-- Wrote job to database id [" + myjob.getJobID() + "]");
			//					} catch (final SQLException sqle) {
			//						logger.severe("E> Unable to create the new Job [" + myjob.getJobID() + "]. " + sqle.getMessage());
			//						sqle.printStackTrace();
			//					}
			//				}
			//			}
			//		AppConnector.getDBConnector().replaceJobs(getCharacterID());
		} catch (final ApiException apie) {
			apie.printStackTrace();
		}
		this.setDirty(true);
		Pilot.logger.info("<< EveChar.updateIndustryJobs");
	}

	@Override
	public void downloadMarketOrders() {
		Pilot.logger.info(">> EveChar.updateMarketOrders");
		try {
			// Download and parse the market orders.
			MarketOrdersParser parser = new MarketOrdersParser();
			final MarketOrdersResponse response = parser.getResponse(this.getAuthorization());
			if (null != response) {
				Set<MarketOrder> orders = response.getAll();
				for (final MarketOrder eveorder : orders) {
					final NeoComMarketOrder myorder = this.convert2Order(eveorder);
					try {
						final Dao<NeoComMarketOrder, String> marketOrderDao = AppConnector.getDBConnector().getMarketOrderDAO();
						marketOrderDao.createOrUpdate(myorder);
						Pilot.logger.finest(
								"-- EveChar.updateMarketOrders.Wrote MarketOrder to database id [" + myorder.getOrderID() + "]");
					} catch (final SQLException sqle) {
						Pilot.logger.severe("E> Unable to create the new Job [" + myorder.getOrderID() + "]. " + sqle.getMessage());
						sqle.printStackTrace();
					}
				}
				marketCacheTime = new Instant(response.getCachedUntil());
			}
		} catch (final ApiException apie) {
			apie.printStackTrace();
		}
		this.setDirty(true);
		Pilot.logger.info("<< EveChar.updateMarketOrders");
	}

	public String getLastKnownLocation() {
		return characterInfo.getLastKnownLocation();
	}

	public int getSkillLevel(final int skillID) {
		// Corporation api will have all skills maxed.
		//		if (isCorporation()) return 5;
		final Set<com.beimin.eveapi.model.pilot.Skill> currentskills = characterSheet.getSkills();
		for (final com.beimin.eveapi.model.pilot.Skill apiSkill : currentskills)
			if (apiSkill.getTypeID() == skillID) return apiSkill.getLevel();
		return 0;
	}

	public String getURLForAvatar() {
		return "http://image.eveonline.com/character/" + this.getCharacterID() + "_256.jpg";
	}

	public void setCharacterSheet(final CharacterSheetResponse sheet) {
		characterSheet = sheet;
	}

	public void setSkillInTraining(final SkillInTrainingResponse training) {
		skillInTraining = training;
	}

	public void setSkillQueue(final Set<SkillQueueItem> skilllist) {
		skills = skilllist;
	}

	/**
	 * At the Character creation we only have the key values to locate it into the CCP databases. During this
	 * execution we have to download many different info from many CCP API calls so it will take some time.<br>
	 * After this update we will have access to all the direct properties of a character. Other multiple value
	 * properties like assets or derived lists will be updated when needed by using other update calls.
	 */
	@Override
	public synchronized void updateCharacterInfo() {
		try {
			// Go to the API and get more information for this character.
			// Balance information
			AccountBalanceParser balanceparser = new AccountBalanceParser();
			AccountBalanceResponse balanceresponse = balanceparser.getResponse(this.getAuthorization());
			if (null != balanceresponse) {
				Set<EveAccountBalance> balance = balanceresponse.getAll();
				if (balance.size() > 0) this.setAccountBalance(balance.iterator().next().getBalance());
			}
			// Character sheet information
			CharacterSheetParser sheetparser = new CharacterSheetParser();
			CharacterSheetResponse sheetresponse = sheetparser.getResponse(this.getAuthorization());
			if (null != sheetresponse) this.setCharacterSheet(sheetresponse);
			// Skill list
			SkillQueueParser skillparser = new SkillQueueParser();
			SkillQueueResponse skillresponse = skillparser.getResponse(this.getAuthorization());
			if (null != skillresponse) this.setSkillQueue(skillresponse.getAll());
			// Skill in training
			SkillInTrainingParser trainingparser = new SkillInTrainingParser();
			SkillInTrainingResponse trainingresponse = trainingparser.getResponse(this.getAuthorization());
			if (null != skillresponse) this.setSkillInTraining(trainingresponse);
			// Full list of assets from database.
			this.accessAllAssets();
		} catch (ApiException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
}

// - UNUSED CODE ............................................................................................
