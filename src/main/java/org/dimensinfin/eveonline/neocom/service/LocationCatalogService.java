package org.dimensinfin.eveonline.neocom.service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.dimensinfin.eveonline.neocom.core.AccessStatistics;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocationImplementation;
import org.dimensinfin.eveonline.neocom.domain.space.Structure;
import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStructuresStructureIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;
import org.dimensinfin.eveonline.neocom.service.scheduler.JobScheduler;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.Job;
import org.dimensinfin.logging.LogWrapper;

import retrofit2.Response;

/**
 * The location catalog service will be used to define Eve Online space locations. It is able to understand their different contents depending on the
 * type of location.
 * Locations should be cached and have a simple Object dump process registered on the scheduler that saves the current location list every minute.
 * Locations already defined are read back from the storage at creation time.
 * Heroku implementations will not have available storage space so the serialization and recovery should be an injectable dependency module that
 * can change the storage implementation depending on the target environment. By default then the location list is not persisted.
 *
 * Now Locations are completely normalized and just depend on two class implementations. Storage can then be done into a database instance.
 *
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.19.0
 */
public class LocationCatalogService extends Job {
	public enum LocationCacheAccessType {
		NOT_FOUND, GENERATED, DATABASE_ACCESS, MEMORY_ACCESS
	}

	private static final AccessStatistics locationsCacheStatistics = new AccessStatistics();
	private static Map<Long, SpaceLocation> locationCache = new HashMap<>();
	// - C O M P O N E N T S
	private final IConfigurationService configurationProvider;
	private final IFileSystem fileSystemAdapter;
	private final ESIUniverseDataProvider esiUniverseDataProvider;
	private final RetrofitFactory retrofitFactory;

	private Map<String, Integer> locationTypeCounters = new HashMap<>();
	private boolean dirtyCache = false; // Flag used to detect if the cache should be persisted because it has changed.
	@Deprecated
	private LocationCacheAccessType lastLocationAccess = LocationCacheAccessType.NOT_FOUND;

	// - C O N S T R U C T O R S
	public LocationCatalogService( final IConfigurationService configurationProvider, final IFileSystem fileSystemAdapter, final RetrofitFactory retrofitFactory ) {
		this.configurationProvider = configurationProvider;
		this.fileSystemAdapter = fileSystemAdapter;
		this.retrofitFactory = retrofitFactory;
	}

	// - G E T T E R S   &   S E T T E R S
	public Map<String, Integer> getLocationTypeCounters() {
		return this.locationTypeCounters;
	}

	// - J O B
	@Override
	public int getUniqueIdentifier() {
		return new HashCodeBuilder( 97, 137 )
				.append( this.getSchedule() )
				.append( this.getClass().getSimpleName() )
				.toHashCode();
	}

	@Override
	public Boolean call() {
		LogWrapper.enter();
		try {
			return this.writeLocationsDataCache();
		} finally {
			LogWrapper.exit();
		}
	}

	// - S T O R A G E
	public void cleanLocationsCache() {
		locationCache.clear();
		this.dirtyCache = false;
	}

	/**
	 * Use a single place where to search for locations if we know a full location identifier. It will detect if the location is a space or
	 * structure location and search for the right record.
	 *
	 * @param locationId full location identifier obtained from any asset with the full location identifier.
	 * @return a SpaceLocation record with the complete location data identifiers and descriptions.
	 */
	public SpaceLocation searchLocation4Id( final LocationIdentifier locationId, final Credential credential ) {
		if (locationId.getSpaceIdentifier() > 64e6)
			return this.searchStructure4Id( locationId.getSpaceIdentifier(), credential );
		else return this.searchLocation4Id( locationId.getSpaceIdentifier() );
	}

	public SpaceLocation searchLocation4Id( final Long locationId ) {
		this.lastLocationAccess = LocationCacheAccessType.NOT_FOUND;
		if (locationCache.containsKey( locationId ))
			return this.searchOnMemoryCache( locationId );
		final int access = locationsCacheStatistics.accountAccess( false );
		final int hits = locationsCacheStatistics.getHits();
		SpaceLocation hit = this.buildUpLocation( locationId );
		if (null != hit) {
			this.lastLocationAccess = LocationCacheAccessType.GENERATED;
			this.storeOnCacheLocation( hit );
			LogWrapper.info( MessageFormat.format( "[HIT-{0}/{1} ] Location {2} generated from ESI data.",
					hits, access, locationId ) );
			return hit;
		} else return null;
	}

	// - S E A R C H   L O C A T I O N   A P I

	public SpaceLocation searchStructure4Id( final Long locationId, final Credential credential ) {
		this.lastLocationAccess = LocationCacheAccessType.NOT_FOUND;
		if (locationCache.containsKey( locationId ))
			return this.searchOnMemoryCache( locationId );
		final int access = locationsCacheStatistics.accountAccess( false );
		final int hits = locationsCacheStatistics.getHits();
		SpaceLocation hit = this.buildUpStructure( locationId, credential );
		if (null != hit) {
			this.lastLocationAccess = LocationCacheAccessType.GENERATED;
			this.storeOnCacheLocation( hit );
			logger.info( ">< [LocationCatalogService.searchStructure4Id]> [HIT-{}/{} ] Location {} generated from Public " +
							"Structure Data.",
					hits, access, locationId );
			return hit;
		} else return null;
	}

	// - C A C H E   M A N A G E M E N T
	public void stopService() {
		this.writeLocationsDataCache();
		this.cleanLocationsCache();
	}

	private SpaceLocation buildUpLocation( final Long locationId ) {
		if (locationId < 20000000) { // Can be a Region
			return this.storeOnCacheLocation(
					new SpaceLocationImplementation.Builder()
							.withRegion( Objects.requireNonNull( this.esiUniverseDataProvider.getUniverseRegionById( locationId.intValue() ) ) )
							.build() );
		}
		if (locationId < 30000000) { // Can be a Constellation
			final GetUniverseConstellationsConstellationIdOk constellation = Objects.requireNonNull( this.esiUniverseDataProvider
					.getUniverseConstellationById( locationId.intValue() ) );
			final GetUniverseRegionsRegionIdOk region = Objects.requireNonNull( this.esiUniverseDataProvider
					.getUniverseRegionById( constellation.getRegionId() ) );
			return this.storeOnCacheLocation(
					new SpaceLocationImplementation.Builder()
							.withRegion( region )
							.withConstellation( constellation )
							.build() );
		}
		if (locationId < 40000000) { // Can be a system
			final GetUniverseSystemsSystemIdOk solarSystem = Objects.requireNonNull( this.esiUniverseDataProvider
					.getUniverseSystemById( locationId.intValue() ) );
			final GetUniverseConstellationsConstellationIdOk constellation = Objects.requireNonNull( this.esiUniverseDataProvider
					.getUniverseConstellationById( solarSystem.getConstellationId() ) );
			final GetUniverseRegionsRegionIdOk region = Objects.requireNonNull( this.esiUniverseDataProvider
					.getUniverseRegionById( constellation.getRegionId() ) );
			return this.storeOnCacheLocation(
					new SpaceLocationImplementation.Builder()
							.withRegion( region )
							.withConstellation( constellation )
							.withSolarSystem( solarSystem )
							.build() );
		}
		if (locationId < 61000000) { // Can be a game station
			final GetUniverseStationsStationIdOk station = Objects.requireNonNull( this.esiUniverseDataProvider
					.getUniverseStationById( locationId.intValue() ) );
			final GetUniverseSystemsSystemIdOk solarSystem = Objects.requireNonNull( this.esiUniverseDataProvider
					.getUniverseSystemById( station.getSystemId()) );
			final GetUniverseConstellationsConstellationIdOk constellation = Objects.requireNonNull( this.esiUniverseDataProvider
					.getUniverseConstellationById( solarSystem.getConstellationId() ) );
			final GetUniverseRegionsRegionIdOk region = Objects.requireNonNull( this.esiUniverseDataProvider
					.getUniverseRegionById( constellation.getRegionId() ) );
			return this.storeOnCacheLocation(
					new SpaceLocationImplementation.Builder()
							.withRegion( region )
							.withConstellation( constellation )
							.withSolarSystem( solarSystem )
							.withStation( station )
							.build() );
		}
		return null;
	}

	private SpaceLocation buildUpStructure( final Long locationId, final Credential credential ) {
		final GetUniverseStructuresStructureIdOk structure = Objects.requireNonNull( this.search200OkStructureById( locationId, credential ) );
		final GetUniverseSystemsSystemIdOk solarSystem = Objects.requireNonNull( this.esiUniverseDataProvider
				.getUniverseSystemById( locationId.intValue() ) );
		final GetUniverseConstellationsConstellationIdOk constellation = Objects.requireNonNull( this.esiUniverseDataProvider
				.getUniverseConstellationById( solarSystem.getConstellationId() ) );
		final GetUniverseRegionsRegionIdOk region = Objects.requireNonNull( this.esiUniverseDataProvider
				.getUniverseRegionById( constellation.getRegionId() ) );
		return this.storeOnCacheLocation(
				new Structure.Builder()
						.withRegion( region )
						.withConstellation( constellation )
						.withSolarSystem( solarSystem )
						.withStructure( locationId, structure )
						.build() );
	}

	private void registerOnScheduler() {
		JobScheduler.getJobScheduler().registerJob( this );
	}

	private GetUniverseStructuresStructureIdOk search200OkStructureById( final Long structureId, final Credential credential ) {
		try {
			final Response<GetUniverseStructuresStructureIdOk> universeResponse = this.retrofitFactory
					.accessAuthenticatedConnector( credential )
					.create( UniverseApi.class )
					.getUniverseStructuresStructureId( structureId,
							credential.getDataSource().toLowerCase(), null, null )
					.execute();
			if (universeResponse.isSuccessful()) {
				return universeResponse.body();
			}
		} catch (final IOException ioe) {
			LogWrapper.error( "[IOException]> locating public structure: ", ioe );
		} catch (final RuntimeException rte) {
			LogWrapper.error( "[RuntimeException]> locating public structure: ", rte );
		}
		return null;
	}

	private SpaceLocation searchOnMemoryCache( final Long locationId ) {
		int access = locationsCacheStatistics.accountAccess( true );
		this.lastLocationAccess = LocationCacheAccessType.MEMORY_ACCESS;
		int hits = locationsCacheStatistics.getHits();
		LogWrapper.info( MessageFormat.format( "[HIT-{0}/{1} ] Location {2} found at cache.",
				hits, access, locationId ) );
		return locationCache.get( locationId );
	}

	private void startService() {
		// TODO - This is not required until the citadel structures get stored on the SDE database.
		//		this.verifySDERepository(); // Check that the LocationsCache table exists and verify the contents
		this.readLocationsDataCache(); // Load the cache from the storage.
		this.registerOnScheduler(); // Register on scheduler to update storage every some minutes
	}

	private SpaceLocation storeOnCacheLocation( final SpaceLocation entry ) {
		if (null != entry) {
			locationCache.put( entry.getLocationId(), entry );
			this.dirtyCache = true;
		}
		return entry;
	}

	// - B U I L D E R
	public static class Builder {
		private LocationCatalogService onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new LocationCatalogService( configurationProvider, fileSystemAdapter, retrofitFactory );
		}

		public LocationCatalogService build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			Objects.requireNonNull( this.onConstruction.fileSystemAdapter );
			Objects.requireNonNull( this.onConstruction.esiUniverseDataProvider );
			this.onConstruction.startService();
			return this.onConstruction;
		}

		public Builder withConfigurationProvider( final IConfigurationService configurationProvider ) {
			Objects.requireNonNull( configurationProvider );
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public Builder withESIUniverseDataProvider( final ESIUniverseDataProvider esiUniverseDataProvider ) {
			Objects.requireNonNull( esiUniverseDataProvider );
			this.onConstruction.esiUniverseDataProvider = esiUniverseDataProvider;
			return this;
		}

		public Builder withFileSystemAdapter( final IFileSystem fileSystemAdapter ) {
			Objects.requireNonNull( fileSystemAdapter );
			this.onConstruction.fileSystemAdapter = fileSystemAdapter;
			return this;
		}

		public Builder withRetrofitFactory( final RetrofitFactory retrofitFactory ) {
			Objects.requireNonNull( retrofitFactory );
			this.onConstruction.retrofitFactory = retrofitFactory;
			return this;
		}
	}

	synchronized void readLocationsDataCache() {
	}

	synchronized boolean writeLocationsDataCache() {
		return false;
	}
}
