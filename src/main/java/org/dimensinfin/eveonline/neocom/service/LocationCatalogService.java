package org.dimensinfin.eveonline.neocom.service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.Job;
import org.dimensinfin.eveonline.neocom.utility.AccessStatistics;
import org.dimensinfin.eveonline.neocom.utility.NeoObjects;
import org.dimensinfin.logging.LogWrapper;

import retrofit2.Response;
import static org.dimensinfin.eveonline.neocom.provider.ESIDataProvider.DEFAULT_ACCEPT_LANGUAGE;
import static org.dimensinfin.eveonline.neocom.provider.ESIDataProvider.DEFAULT_ESI_SERVER;
import static org.dimensinfin.eveonline.neocom.utility.GlobalWideConstants.REDIS_SEPARATOR;

/**
 * The location catalog service will be used to define Eve Online space locations. It is able to understand their different contents depending on the
 * type of location. Locations should be cached and have a simple Object dump process registered on the scheduler that saves the current location list
 * every minute. Locations already defined are read back from the storage at creation time. Heroku implementations will not have available storage
 * space so the serialization and recovery should be an injectable dependency module that can change the storage implementation depending on the
 * target environment. By default then the location list is not persisted.
 *
 * Now Locations are completely normalized and just depend on two class implementations. Storage can then be done into a database instance.
 *
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.19.0
 */
public class LocationCatalogService extends Job {
	@Deprecated
	public enum LocationCacheAccessType {
		NOT_FOUND, GENERATED, DATABASE_ACCESS, MEMORY_ACCESS
	}

	private static final AccessStatistics locationsCacheStatistics = new AccessStatistics();
	@Deprecated
	private static final Map<Long, SpaceLocation> locationCache = new HashMap<>();
	// - C O M P O N E N T S
	private final RetrofitService retrofitService;
	private final IDataStore dataStore;

	private final Map<String, Integer> locationTypeCounters = new HashMap<>();

	// - C O N S T R U C T O R S
	@Inject
	public LocationCatalogService( final @NotNull @Named(DMServicesDependenciesModule.RETROFIT_SERVICE) RetrofitService retrofitService,
	                               final @NotNull @Named(DMServicesDependenciesModule.IDATA_STORE) IDataStore dataStore ) {
		this.retrofitService = retrofitService;
		this.dataStore = dataStore;
	}

	// - G E T T E R S   &   S E T T E R S
	@Deprecated
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

	// - C O R E
	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 ).appendSuper( super.hashCode() ).append( this.locationTypeCounters ).toHashCode();
	}

	@Override
	public boolean equals( final Object o ) {
		if ( this == o ) return true;
		if ( !(o instanceof LocationCatalogService) ) return false;
		final LocationCatalogService that = (LocationCatalogService) o;
		return new EqualsBuilder().appendSuper( super.equals( o ) )
				.append( this.locationTypeCounters, that.locationTypeCounters ).isEquals();
	}

	@Override
	public Boolean call() {
		LogWrapper.enter();
		try {
			return this.persistLocationsDataCache();
		} finally {
			LogWrapper.exit();
		}
	}

	// - S T O R A G E
	public int cleanLocationsCache() {
		final int contentCount = locationCache.size();
		locationCache.clear();
		return contentCount;
	}

	public GetUniverseConstellationsConstellationIdOk getUniverseConstellationById( final Integer constellationId ) {
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseConstellationsConstellationIdOk> systemResponse = this.retrofitService
					.accessUniverseConnector()
					.create( UniverseApi.class )
					.getUniverseConstellationsConstellationId( constellationId,
							DEFAULT_ACCEPT_LANGUAGE,
							DEFAULT_ESI_SERVER, null, null )
					.execute();
			if ( systemResponse.isSuccessful() )
				return systemResponse.body();
		} catch (final IOException ioe) {
			LogWrapper.error( ioe );
		}
		return null;
	}

	public GetUniverseRegionsRegionIdOk getUniverseRegionById( final Integer regionId ) {
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseRegionsRegionIdOk> systemResponse = this.retrofitService
					.accessUniverseConnector()
					.create( UniverseApi.class )
					.getUniverseRegionsRegionId( regionId,
							DEFAULT_ACCEPT_LANGUAGE,
							DEFAULT_ESI_SERVER.toLowerCase(), null, null )
					.execute();
			if ( systemResponse.isSuccessful() ) return systemResponse.body();
		} catch (final IOException ioe) {
			LogWrapper.error( ioe );
		}
		return null;
	}

	public GetUniverseStationsStationIdOk getUniverseStationById( final Integer stationId ) {
		LogWrapper.enter( MessageFormat.format( "stationId: {0, number, integer}", stationId ) );
		try {
			final Response<GetUniverseStationsStationIdOk> stationResponse = this.retrofitService
					.accessUniverseConnector()
					.create( UniverseApi.class )
					.getUniverseStationsStationId( stationId, DEFAULT_ESI_SERVER, null )
					.execute();
			if ( stationResponse.isSuccessful() )
				return stationResponse.body();
		} catch (final IOException ioe) {
			LogWrapper.error( ioe );
		}
		return null;
	}

	public GetUniverseSystemsSystemIdOk getUniverseSystemById( final Integer systemId ) {
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseSystemsSystemIdOk> systemResponse = this.retrofitService
					.accessUniverseConnector()
					.create( UniverseApi.class )
					.getUniverseSystemsSystemId( systemId
							, DEFAULT_ACCEPT_LANGUAGE
							, DEFAULT_ESI_SERVER, null, null )
					.execute();
			if ( systemResponse.isSuccessful() )
				return systemResponse.body();
		} catch (final IOException ioe) {
			LogWrapper.error( ioe );
		}
		return null;
	}

	// - S E A R C H   L O C A T I O N   A P I

	/**
	 * Use a single place where to search for locations if we know a full location identifier. It will detect if the location is a space or structure
	 * location and search for the right record. This method is used for Pilot and structure locations.
	 *
	 * @param locationId full location identifier obtained from any asset with the full location identifier.
	 * @param credential the pilot Credential to be used to access the list of structures. Not all structures are available and it is a secured
	 *                   endpoint.
	 * @return a SpaceLocation record with the complete location data identifiers and descriptions.
	 */
	public SpaceLocation searchLocation4Id( final LocationIdentifier locationId, final Credential credential ) {
		if ( locationId.getSpaceIdentifier() > 64e6 )
			return this.searchStructure4Id( locationId.getSpaceIdentifier(), credential );
		else return this.searchLocation4Id( locationId.getSpaceIdentifier() );
	}

	/**
	 * New search methosd the uses a Redis cache and that returns empty Optional in case the location cannot be constructed. Nulls should be avoided.
	 *
	 * @param locationId full location identifier obtained from any asset with the full location identifier.
	 * @param credential the pilot Credential to be used to access the list of structures. Not all structures are available and it is a secured
	 *                   endpoint.
	 * @return a SpaceLocation record with the complete location data identifiers and descriptions. Or Optional.empty() if the location cannot be
	 * 		constructed.
	 */
	public Optional<SpaceLocation> lookupLocation4Id( final Long locationId, final Credential credential ) {
		if ( locationId > 64e6 ) {
			// - This is a corporation structure so needs additional scope privileges to be accessible.
			final String locationCacheId = credential.getAccountId() + REDIS_SEPARATOR + locationId;
			final Optional<SpaceLocation> target = this.dataStore.accessLocation( locationCacheId );
			if ( target.isPresent() ) return target;
			else {
				final SpaceLocation location = this.buildUpStructure( locationId, credential );
				if ( Objects.isNull( location ) ) return Optional.empty();
				else {
					this.dataStore.updateLocation( locationCacheId, location );
					return Optional.of( location );
				}
			}
		} else {
			final String locationCacheId = locationId.toString();
			final Optional<SpaceLocation> target = this.dataStore.accessLocation( locationCacheId );
			if ( target.isPresent() ) return target;
			else {
				final SpaceLocation location = this.buildUpLocation( locationId );
				if ( Objects.isNull( location ) ) return Optional.empty();
				else {
					this.dataStore.updateLocation( locationCacheId, location );
					return Optional.of( location );
				}
			}
		}
	}

	/**
	 * Method to search for public locations and space stations that do not belong to players.
	 *
	 * @param locationId the unique identifier for the location
	 * @return a SpaceLocation with the correct fields filled. Can be Region or Constellation or Space or Station.
	 */
	public SpaceLocation searchLocation4Id( final Long locationId ) {
		if ( locationCache.containsKey( locationId ) )
			return this.searchOnMemoryCache( locationId );
		final int access = locationsCacheStatistics.accountAccess( false );
		final int hits = locationsCacheStatistics.getHits();
		final SpaceLocation hit = this.buildUpLocation( locationId );
		if ( null != hit ) {
			this.storeOnCacheLocation( hit );
			LogWrapper.info( MessageFormat.format( "[HIT-{0}/{1} ] Location {2, number, integer} generated from ESI data.",
					hits, access, locationId ) );
			return hit;
		} else return null;
	}

	public SpaceLocation searchStructure4Id( final Long locationId, final Credential credential ) {
		if ( locationCache.containsKey( locationId ) )
			return this.searchOnMemoryCache( locationId );
		final int access = locationsCacheStatistics.accountAccess( false );
		final int hits = locationsCacheStatistics.getHits();
		final SpaceLocation hit = this.buildUpStructure( locationId, credential );
		if ( null != hit ) {
			this.storeOnCacheLocation( hit );
			LogWrapper.info( MessageFormat.format(
					"[HIT-{0,number,integer}/{1,number,integer} ] Location {2,number,integer} generated from Public Structure Data.",
					hits, access, locationId ) );
			return hit;
		} else return null;
	}

	// - C A C H E   M A N A G E M E N T
	private SpaceLocation buildUpLocation( final Long locationId ) {
		if ( locationId < 20000000 ) { // Can be a Region
			return this.storeOnCacheLocation(
					new SpaceLocationImplementation.Builder()
							.withRegion( Objects.requireNonNull( this.getUniverseRegionById( locationId.intValue() ) ) )
							.build() );
		}
		if ( locationId < 30000000 ) { // Can be a Constellation
			final GetUniverseConstellationsConstellationIdOk constellation = Objects.requireNonNull( this
					.getUniverseConstellationById( locationId.intValue() ) );
			final GetUniverseRegionsRegionIdOk region = Objects.requireNonNull( this
					.getUniverseRegionById( constellation.getRegionId() ) );
			return this.storeOnCacheLocation(
					new SpaceLocationImplementation.Builder()
							.withRegion( region )
							.withConstellation( constellation )
							.build() );
		}
		if ( locationId < 40000000 ) { // Can be a system
			final GetUniverseSystemsSystemIdOk solarSystem = Objects.requireNonNull( this.getUniverseSystemById( locationId.intValue() ) );
			final GetUniverseConstellationsConstellationIdOk constellation = Objects.requireNonNull( this
					.getUniverseConstellationById( solarSystem.getConstellationId() ) );
			final GetUniverseRegionsRegionIdOk region = Objects.requireNonNull( this
					.getUniverseRegionById( constellation.getRegionId() ) );
			return this.storeOnCacheLocation(
					new SpaceLocationImplementation.Builder()
							.withRegion( region )
							.withConstellation( constellation )
							.withSolarSystem( solarSystem )
							.build() );
		}
		if ( locationId < 61000000 ) { // Can be a game station
			final GetUniverseStationsStationIdOk station = NeoObjects.requireNonNull( this
							.getUniverseStationById( locationId.intValue() ),
					"ESI Station should not be null while creating Location." );
			final GetUniverseSystemsSystemIdOk solarSystem = NeoObjects.requireNonNull( this
							.getUniverseSystemById( station.getSystemId() ),
					"ESI Solar System should not be null while creating Location." );
			final GetUniverseConstellationsConstellationIdOk constellation = NeoObjects.requireNonNull( this
							.getUniverseConstellationById( solarSystem.getConstellationId() ),
					"ESI Constellation should not be null while creating Location." );
			final GetUniverseRegionsRegionIdOk region = NeoObjects.requireNonNull( this
							.getUniverseRegionById( constellation.getRegionId() ),
					"ESI Region should not be null while creating Location." );
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
		final GetUniverseStructuresStructureIdOk structure = Objects.requireNonNull(
				this.search200OkStructureById( locationId, credential )
		);
		final GetUniverseSystemsSystemIdOk solarSystem = Objects.requireNonNull(
				this.getUniverseSystemById( locationId.intValue() )
		);
		final GetUniverseConstellationsConstellationIdOk constellation = Objects.requireNonNull(
				this.getUniverseConstellationById( solarSystem.getConstellationId() )
		);
		final GetUniverseRegionsRegionIdOk region = Objects.requireNonNull(
				this.getUniverseRegionById( constellation.getRegionId() )
		);
		return this.storeOnCacheLocation(
				new Structure.Builder()
						.withRegion( region )
						.withConstellation( constellation )
						.withSolarSystem( solarSystem )
						.withStructure( locationId, structure )
						.withCorporation( 1,"-TEST-CORPORATION-" )
						.build() );
	}

	private GetUniverseStructuresStructureIdOk search200OkStructureById( final Long structureId, final Credential credential ) {
		try {
			final Response<GetUniverseStructuresStructureIdOk> universeResponse = this.retrofitService
					.accessAuthenticatedConnector( credential )
					.create( UniverseApi.class )
					.getUniverseStructuresStructureId( structureId,
							credential.getDataSource().toLowerCase(), null, null )
					.execute();
			if ( universeResponse.isSuccessful() ) {
				return universeResponse.body();
			}
		} catch (final IOException ioe) {
			LogWrapper.error( ioe );
		} catch (final RuntimeException rte) {
			LogWrapper.error( rte );
		}
		return null;
	}

	private SpaceLocation searchOnMemoryCache( final Long locationId ) {
		final int access = locationsCacheStatistics.accountAccess( true );
		//		this.lastLocationAccess = LocationCacheAccessType.MEMORY_ACCESS;
		final int hits = locationsCacheStatistics.getHits();
		LogWrapper.info( MessageFormat.format( "[HIT-{0}/{1} ] Location {2} found at cache.",
				hits, access, locationId ) );
		return locationCache.get( locationId );
	}

	private SpaceLocation storeOnCacheLocation( final SpaceLocation entry ) {
		if ( null != entry ) {
			locationCache.put( entry.getLocationId(), entry );
			//			this.dirtyCache = true;
		}
		return entry;
	}

	synchronized boolean persistLocationsDataCache() {
		return false;
	}
}
