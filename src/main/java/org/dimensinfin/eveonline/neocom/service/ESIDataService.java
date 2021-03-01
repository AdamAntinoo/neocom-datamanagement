package org.dimensinfin.eveonline.neocom.service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jcabi.aspects.Cacheable;
import com.jcabi.aspects.LogExceptions;
import com.jcabi.aspects.Loggable;
import org.jetbrains.annotations.NonNls;

import org.dimensinfin.annotation.TimeElapsed;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.domain.space.Station;
import org.dimensinfin.eveonline.neocom.esiswagger.api.AssetsApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.CharacterApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.IndustryApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.LocationApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.LoyaltyApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.MarketApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.MarketApiV2;
import org.dimensinfin.eveonline.neocom.esiswagger.api.SkillsApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.WalletApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdBlueprints200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdIndustryJobs200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdLocationOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOrders200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdShipOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdSkillsOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetLoyaltyStoresCorporationIdOffers200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdHistory200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdOrders200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.logging.LogWrapper;

import retrofit2.Response;

public class ESIDataService extends ESIDataProvider {
	@NonNls
	private static final ResourceBundle i18Bundle = ResourceBundle.getBundle( "i18Properties" );
	private static final String PILOT_IDENTIFIER_LOG_LITERAL = i18Bundle.getString( "pilot.identifier.literal" );
	// - C O N S T R U C T O R S
	@Inject
	public ESIDataService( @NotNull @Named(DMServicesDependenciesModule.ICONFIGURATION_SERVICE) final IConfigurationService configurationService,
	                       @NotNull @Named(DMServicesDependenciesModule.IFILE_SYSTEM) final IFileSystem fileSystem,
	                       @NotNull @Named(DMServicesDependenciesModule.ISTORE_CACHE) final IStoreCache storeCacheManager,
	                       @NotNull @Named(DMServicesDependenciesModule.RETROFIT_SERVICE) final RetrofitService retrofitService,
	                       @NotNull @Named(DMServicesDependenciesModule.LOCATION_CATALOG_SERVICE) final LocationCatalogService locationCatalogService ) {
		this.configurationProvider = configurationService;
		this.fileSystemAdapter = fileSystem;
		this.storeCacheManager = storeCacheManager;
		this.retrofitService = retrofitService;
		this.locationCatalogService = locationCatalogService;
	}

	// - C H A R A C T E R   P U B L I C   I N F O R M A T I O N
	@TimeElapsed
	public GetCharactersCharacterIdOk getCharactersCharacterId( final int identifier ) {
		LogWrapper.enter( MessageFormat.format( PILOT_IDENTIFIER_LOG_LITERAL, identifier ) );
		try {
			final Response<GetCharactersCharacterIdOk> characterResponse = this.retrofitService
					.accessUniverseConnector()
					.create( CharacterApi.class )
					.getCharactersCharacterId( identifier, DEFAULT_ESI_SERVER, null )
					.execute();
			if (characterResponse.isSuccessful()) return characterResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return null;
	}

	@TimeElapsed
	public List<GetCharactersCharacterIdAssets200Ok> getCharactersCharacterIdAssets( final Credential credential ) {
		LogWrapper.enter( MessageFormat.format( CREDENTIAL_LOG_LITERAL, credential.toString() ) );
		List<GetCharactersCharacterIdAssets200Ok> returnAssetList = new ArrayList<>( 1000 );
		try {
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetCharactersCharacterIdAssets200Ok>> assetsApiResponse = this.retrofitService
						.accessAuthenticatedConnector( credential )
						.create( AssetsApi.class )
						.getCharactersCharacterIdAssets(
								credential.getAccountId(),
								credential.getDataSource().toLowerCase(),
								null, pageCounter, null )
						.execute();
				if (assetsApiResponse.isSuccessful()) {
					// Check for out of page running.
					if (Objects.requireNonNull( assetsApiResponse.body() ).isEmpty()) morePages = false;
					else {
						// Copy the assets to the result list.
						returnAssetList.addAll( Objects.requireNonNull( assetsApiResponse.body() ) );
						pageCounter++;
					}
				}
			}
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return returnAssetList;
	}

	@TimeElapsed
	@Loggable(Loggable.DEBUG)
	@LogExceptions
	@Cacheable(lifetime = 3600, unit = TimeUnit.SECONDS)
	public List<GetCharactersCharacterIdBlueprints200Ok> getCharactersCharacterIdBlueprints( final Credential credential ) {
		LogWrapper.enter( MessageFormat.format( CREDENTIAL_LOG_LITERAL, credential.toString() ) );
		List<GetCharactersCharacterIdBlueprints200Ok> returnBlueprintList = new ArrayList<>( 1000 );
		try {
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetCharactersCharacterIdBlueprints200Ok>> blueprintResponse = this.retrofitService
						.accessAuthenticatedConnector( credential )
						.create( CharacterApi.class )
						.getCharactersCharacterIdBlueprints(
								credential.getAccountId(),
								credential.getDataSource().toLowerCase(),
								null,
								pageCounter,
								null )
						.execute();
				if (blueprintResponse.isSuccessful()) {
					// Check for out of page running.
					if (Objects.requireNonNull( blueprintResponse.body() ).isEmpty()) morePages = false;
					else {
						// Copy the assets to the result list.
						returnBlueprintList.addAll( Objects.requireNonNull( blueprintResponse.body() ) );
						pageCounter++;
					}
				}
			}
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return returnBlueprintList;
	}

	@TimeElapsed
	@Loggable(Loggable.DEBUG)
	@LogExceptions
	@Cacheable(lifetime = 300, unit = TimeUnit.SECONDS)
	public List<GetCharactersCharacterIdIndustryJobs200Ok> getCharactersCharacterIdIndustryJobs( final Credential credential ) {
		LogWrapper.enter();
		try {
			final Response<List<GetCharactersCharacterIdIndustryJobs200Ok>> industryJobsResponse = this.retrofitService
					.accessAuthenticatedConnector( credential )
					.create( IndustryApi.class )
					.getCharactersCharacterIdIndustryJobs(
							credential.getAccountId(),
							credential.getDataSource().toLowerCase(),
							null, false, null
					)
					.execute();
			if (industryJobsResponse.isSuccessful()) return industryJobsResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return new ArrayList<>();
	}

	@TimeElapsed
	public GetCharactersCharacterIdLocationOk getCharactersCharacterIdLocation( final Credential credential ) {
		LogWrapper.enter( MessageFormat.format( CREDENTIAL_LOG_LITERAL, credential.toString() ) );
		try {
			final Response<GetCharactersCharacterIdLocationOk> locationResponse = this.retrofitService
					.accessAuthenticatedConnector( credential )
					.create( LocationApi.class )
					.getCharactersCharacterIdLocation( credential.getAccountId()
							, credential.getDataSource(), null, null )
					.execute();
			if (locationResponse.isSuccessful()) return locationResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return null;
	}

	@TimeElapsed
	@Loggable(Loggable.DEBUG)
	@LogExceptions
	@Cacheable(lifetime = 1200, unit = TimeUnit.SECONDS)
	public List<GetCharactersCharacterIdOrders200Ok> getCharactersCharacterIdOrders( final Credential credential ) {
		LogWrapper.enter();
		try {
			final Response<List<GetCharactersCharacterIdOrders200Ok>> industryJobsResponse = this.retrofitService
					.accessUniverseConnector()
					.create( MarketApi.class )
					.getCharactersCharacterIdOrders(
							credential.getAccountId(),
							credential.getDataSource().toLowerCase(),
							null, null
					)
					.execute();
			if (industryJobsResponse.isSuccessful()) return industryJobsResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return new ArrayList<>();
	}

	@TimeElapsed
	public GetCharactersCharacterIdShipOk getCharactersCharacterIdShip( final Credential credential ) {
		LogWrapper.enter( MessageFormat.format( CREDENTIAL_LOG_LITERAL, credential.toString() ) );
		try {
			final Response<GetCharactersCharacterIdShipOk> shipResponse = this.retrofitService
					.accessAuthenticatedConnector( credential )
					.create( LocationApi.class )
					.getCharactersCharacterIdShip( credential.getAccountId()
							, credential.getDataSource(), null, null )
					.execute();
			if (shipResponse.isSuccessful()) return shipResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return null;
	}

	@TimeElapsed
	public GetCharactersCharacterIdSkillsOk getCharactersCharacterIdSkills( final Credential credential ) {
		LogWrapper.enter( MessageFormat.format( CREDENTIAL_LOG_LITERAL, credential.toString() ) );
		try {
			final Response<GetCharactersCharacterIdSkillsOk> skillsResponse = this.retrofitService
					.accessAuthenticatedConnector( credential )
					.create( SkillsApi.class )
					.getCharactersCharacterIdSkills( credential.getAccountId()
							, credential.getDataSource(), null, null )
					.execute();
			if (skillsResponse.isSuccessful()) return skillsResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return null;
	}

	@TimeElapsed
	public Double getCharactersCharacterIdWallet( final Credential credential ) {
		LogWrapper.enter( MessageFormat.format( CREDENTIAL_LOG_LITERAL, credential.toString() ) );
		try {
			final Response<Double> walletApiResponse = this.retrofitService
					.accessAuthenticatedConnector( credential )
					.create( WalletApi.class )
					.getCharactersCharacterIdWallet( credential.getAccountId()
							, credential.getDataSource(), null, null )
					.execute();
			if (walletApiResponse.isSuccessful()) return walletApiResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return -1.0;
	}

	@TimeElapsed
	public List<GetLoyaltyStoresCorporationIdOffers200Ok> getLoyaltyStoresOffers( final Integer corporationId ) {
		LogWrapper.enter( MessageFormat.format( "corporationId: {0,number,integer}", corporationId ) );
		try {
			final Response<List<GetLoyaltyStoresCorporationIdOffers200Ok>> loyaltyOffersResponse = this.retrofitService
					.accessUniverseConnector()
					.create( LoyaltyApi.class )
					.getLoyaltyStoresCorporationIdOffers(
							corporationId, DEFAULT_ESI_SERVER, null
					)
					.execute();
			if (loyaltyOffersResponse.isSuccessful()) return loyaltyOffersResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return new ArrayList<>();
	}

	@TimeElapsed
	public List<GetMarketsRegionIdHistory200Ok> getMarketsHistoryForRegion( final Integer regionId, final Integer typeId ) {
		LogWrapper.enter( MessageFormat.format( "RegionId: {0,number,integer} - Type: {1, number, integer}", regionId , typeId) );
		try {
			final Response<List<GetMarketsRegionIdHistory200Ok>> marketHistoryResponse = this.retrofitService
					.accessUniverseConnector()
					.create( MarketApi.class )
					.getMarketsRegionIdHistory(
							regionId, typeId, DEFAULT_ESI_SERVER, null
					)
					.execute();
			if (marketHistoryResponse.isSuccessful()) return marketHistoryResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return new ArrayList<>();
	}

//	/**
//	 * Searches on a predefined table for the match on the Region identifier. This reference table will store the preferred Market Hub for the
//	 * selected region. If the region value of not found on the reference table then the spacial Jita market is selected as the region hub.
//	 *
//	 * There is no check on the type of location that should be reported. If the data table is wrong then there should be a runtime exception.
//	 *
//	 * @param regionId the target region to search for the market hub.
//	 * @return the region's selected market hub predefined on the application. The returned value is a complete <code>Station</code> location record.
//	 */
//	public Station getRegionMarketHub( final int regionId ) {
//		Long hit = regionMarketHubReferenceTable.get( regionId );
//		if (null == hit) hit = PREDEFINED_MARKET_HUB_STATION_ID;
//		final SpaceLocation location = this.locationCatalogService.searchLocation4Id( hit );
//		return (Station) location;
//	}

	// - L O C A T I O N   D E L E G A T I O N
	public GetUniverseConstellationsConstellationIdOk getUniverseConstellationById( final Integer constellationId ) {
		return this.locationCatalogService.getUniverseConstellationById( constellationId );
	}

	/**
	 * Returns the list of markets orders for an item on an specified region.
	 *
	 * On new implementation if there are no more pages of data the response is a 404 instead of an empty list. Detect also this case.
	 */
	@TimeElapsed
	public List<GetMarketsRegionIdOrders200Ok> getUniverseMarketOrdersForId( final Integer regionId, final Integer typeId ) {
		LogWrapper.enter( MessageFormat.format( "regionId: {0, number , integer} - typeId: {1, number , integer}", regionId, typeId ) );
		final List<GetMarketsRegionIdOrders200Ok> returnMarketOrderList = new ArrayList<>( 1000 );
		try {
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetMarketsRegionIdOrders200Ok>> marketOrdersResponse = this.retrofitService
						.accessUniverseConnector()
						.create( MarketApiV2.class )
						.getMarketsRegionIdOrders( regionId, "all", DEFAULT_ESI_SERVER, pageCounter, typeId, null )
						.execute();
				if (marketOrdersResponse.isSuccessful()) {
					// Check for out of page running.
					if (Objects.requireNonNull( marketOrdersResponse.body() ).isEmpty()) morePages = false;
					else {
						// Copy the assets to the result list.
						returnMarketOrderList.addAll( Objects.requireNonNull( marketOrdersResponse.body() ) );
						pageCounter++;
					}
				} else morePages = false;
			}
		} catch (final IOException | RuntimeException rtex) {
			LogWrapper.error( rtex );
		} finally {
			LogWrapper.exit();
		}
		return returnMarketOrderList;
	}

	public GetUniverseRegionsRegionIdOk getUniverseRegionById( final Integer regionId ) {
		return this.locationCatalogService.getUniverseRegionById( regionId );
	}

	public GetUniverseStationsStationIdOk getUniverseStationById( final Integer stationId ) {
		return this.locationCatalogService.getUniverseStationById( stationId );
	}

	public GetUniverseSystemsSystemIdOk getUniverseSystemById( final Integer systemId ) {
		return this.locationCatalogService.getUniverseSystemById( systemId );
	}
}