package org.dimensinfin.eveonline.neocom.database.repositories;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.domain.NeoItem;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MiningRepository {
	protected static Logger logger = LoggerFactory.getLogger(MiningRepository.class);
	// - C O M P O N E N T S
	protected ESIDataAdapter esiDataAdapter;
	protected Dao<MiningExtraction, String> miningExtractionDao;

	/**
	 * This other method does the same Mining Extractions processing but only for the records for the current date. The difference is
	 * that today records are aggregated by hour instead of by day. So we will have a record for one ore/system since the hour we did the
	 * extractions until the 23 hours. The first extraction will add to the hour until the next hour starts. Then the accounting for this
	 * new hour will show the new ore totals and so on hour after hour.
	 */
	protected MiningRepository() { }

	public List<MiningExtraction> accessTodayMiningExtractions4Pilot( final Credential credential ) {
		return this.accessDatedMiningExtractions4Pilot(credential, LocalDate.now());
	}

	public List<MiningExtraction> accessDatedMiningExtractions4Pilot( final Credential credential, final LocalDate filterDate ) {
		try {
			final QueryBuilder<MiningExtraction, String> builder = this.miningExtractionDao.queryBuilder();
			final Where<MiningExtraction, String> where = builder.where();
			where.eq("ownerId", credential.getAccountId());
			builder.orderBy("extractionDateName", true);
			builder.orderBy("extractionHour", true);
			builder.orderBy("solarSystemId", true);
			builder.orderBy("typeId", true);
			final PreparedQuery<MiningExtraction> preparedQuery = builder.prepare();
			logger.info("-- [MiningRepository.accessTodayMiningExtractions4Pilot]> SELECT: {}", preparedQuery.getStatement());
			final List<MiningExtraction> dataList = this.miningExtractionDao.query(preparedQuery);
			logger.info("-- [MiningRepository.accessTodayMiningExtractions4Pilot]> Records read: {}",
			            dataList.size());
			return this.filterOutNotTodayRecords(dataList, filterDate);
		} catch (SQLException sqle) {
			logger.error("SQL [MiningRepository.accessDatedMiningExtractions4Pilot]> SQL Exception: {}", sqle.getMessage());
			return new ArrayList<>();
		}
	}

	/**
	 * Filter out all records not belonging to today.
	 *
	 * @param dataList records read from the database.
	 * @return completed list of extractions for the date of today and with all the delegates reloaded
	 */
	private List<MiningExtraction> filterOutNotTodayRecords( final List<MiningExtraction> dataList,
	                                                         final LocalDate filterDate ) {
		List<MiningExtraction> results = new ArrayList<>();
		// Filter out all records not belonging to today.
		for (MiningExtraction extraction : dataList)
			if (extraction.getExtractionDateName().equalsIgnoreCase(
					filterDate.toString(MiningExtraction.EXTRACTION_DATE_FORMAT)))
				results.add(this.postProcessExtraction(extraction));
		return results;
	}

	/**
	 * Because transient fields lost their contents when the record is stored on the database, when retrieving the record the
	 * date should be set again by searching for the esi resources again.
	 *
	 * @param extraction the mining extraction to update.
	 */
	private MiningExtraction postProcessExtraction( final MiningExtraction extraction ) {
		extraction.setResourceItem(new NeoItem(extraction.getTypeId()));
		extraction.setSolarSystemLocation(this.esiDataAdapter.searchLocation4Id(extraction.getSolarSystemId()));
		return extraction;
	}

	public List<MiningExtraction> accessResources4Date( final Credential credential, final LocalDate filterDate ) {
		try {
			final QueryBuilder<MiningExtraction, String> builder = this.miningExtractionDao.queryBuilder();
			final Where<MiningExtraction, String> where = builder.where();
			where.eq("ownerId", credential.getAccountId())
					.and()
					.eq("extractionDateName", filterDate.toString("YYYY-MM-dd"));
			builder.selectRaw("id", "typeId", "MAX(quantity)");
			builder.groupBy("typeId");
			logger.info("-- [MiningRepository.accessTodayMiningExtractions4Pilot]> SELECT: {}", builder.prepareStatementString());
			final GenericRawResults<String[]> dataList = this.miningExtractionDao.queryRaw(
					builder.prepareStatementString());
			List<MiningExtraction> results = new ArrayList<>();
			for (String[] record : dataList.getResults()) {
				results.add(this.miningExtractionDao.queryForId(record[0]));
			}
			logger.info("-- [MiningRepository.accessTodayMiningExtractions4Pilot]> Records read: {}",
			            results.size());
			return results;
		} catch (SQLException sqle) {
			logger.error("SQL [MiningRepository.accessResources4Date]> SQL Exception: {}", sqle.getMessage());
			return new ArrayList<>();
		}
	}

	public MiningExtraction accessMiningExtractionFindById( final String recordIdentifier ) throws SQLException {
		return this.miningExtractionDao.queryForId(recordIdentifier);
	}

	public void persist( final MiningExtraction record ) throws SQLException {
		if (null != record) {
			record.timeStamp();
			this.miningExtractionDao.createOrUpdate(record);
		}
	}

	/**
	 * Get the list of Mining Extractions that are registered on the database. This can be a lot of records that need sorting and also
	 * grouping previously to rendering. This method can do the sorting but the grouping is not one of its features.
	 * The mining operations for a single day aggregate all the ore for a single type, but have different records for different systems
	 * and for different ores. So for a single day we can have around 6-8 records. The mining ledger information at the neocom database has
	 * to expiration time so the number of days is still not predetermined.
	 */
	public List<MiningExtraction> accessMiningExtractions4Pilot( final Credential credential ) {
		try {
			final QueryBuilder<MiningExtraction, String> builder = this.miningExtractionDao.queryBuilder();
			final Where<MiningExtraction, String> where = builder.where();
			where.eq("ownerId", credential.getAccountId());
			builder.orderBy("id", false);
			final PreparedQuery<MiningExtraction> preparedQuery = builder.prepare();
			logger.info("-- [MiningRepository.accessMiningExtractions4Pilot]> SELECT: {}", preparedQuery.getStatement());
			return this.miningExtractionDao.query(preparedQuery);
		} catch (SQLException sqle) {
			logger.error("");
			return new ArrayList<>();
		}
	}

	// - B U I L D E R
	public static class Builder {
		private MiningRepository onConstruction;

		public Builder() {
			this.onConstruction = new MiningRepository();
		}

		public MiningRepository.Builder withEsiDataAdapter( final ESIDataAdapter esiDataAdapter ) {
			this.onConstruction.esiDataAdapter = esiDataAdapter;
			return this;
		}

		public MiningRepository.Builder withMiningExtractionDao( final Dao<MiningExtraction, String> miningExtractionDao ) {
			this.onConstruction.miningExtractionDao = miningExtractionDao;
			return this;
		}

		public MiningRepository build() {
			Objects.requireNonNull(this.onConstruction.esiDataAdapter);
			Objects.requireNonNull(this.onConstruction.miningExtractionDao);
			return this.onConstruction;
		}
	}
}
