package org.dimensinfin.eveonline.neocom.service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;
import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocationImplementation;
import org.dimensinfin.eveonline.neocom.domain.space.Structure;
import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStructuresStructureIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.ports.ILocationFactoryPort;
import org.dimensinfin.eveonline.neocom.utility.NeoObjects;
import org.dimensinfin.logging.LogWrapper;

import retrofit2.Response;
import static org.dimensinfin.eveonline.neocom.provider.ESIDataProvider.DEFAULT_ACCEPT_LANGUAGE;
import static org.dimensinfin.eveonline.neocom.provider.ESIDataProvider.DEFAULT_ESI_SERVER;

/**
 * The responsibility for this class is to generate <code>SpaceLocation</code>s from the ESI different sources like Region, Constellation, System and
 * Structures. There is no other dependency than with the Retrofit service to access publis non secured ESI data.
 */
public class LocationFactory implements ILocationFactoryPort {
	private final RetrofitService retrofitService;

	@Inject
	public LocationFactory( final @NotNull @Named(DMServicesDependenciesModule.RETROFIT_SERVICE) RetrofitService retrofitService ) {this.retrofitService = retrofitService;}

	@Override
	public Optional<SpaceLocation> buildUpLocation4LocationId( final Long locationId ) {
		try {
			if ( locationId < 20000000 ) { // Can be a Region
				return Optional.of( new SpaceLocationImplementation.Builder()
						.withRegion( Objects.requireNonNull( this.getUniverseRegionById( locationId.intValue() ) ) )
						.build()
				);
			}
			if ( locationId < 30000000 ) { // Can be a Constellation
				final GetUniverseConstellationsConstellationIdOk constellation = Objects.requireNonNull( this
						.getUniverseConstellationById( locationId.intValue() ) );
				final GetUniverseRegionsRegionIdOk region = Objects.requireNonNull( this
						.getUniverseRegionById( constellation.getRegionId() ) );
				return Optional.of( new SpaceLocationImplementation.Builder()
						.withRegion( region )
						.withConstellation( constellation )
						.build()
				);
			}
			if ( locationId < 40000000 ) { // Can be a system
				final GetUniverseSystemsSystemIdOk solarSystem = Objects.requireNonNull( this.getUniverseSystemById( locationId.intValue() ) );
				final GetUniverseConstellationsConstellationIdOk constellation = Objects.requireNonNull( this
						.getUniverseConstellationById( solarSystem.getConstellationId() ) );
				final GetUniverseRegionsRegionIdOk region = Objects.requireNonNull( this
						.getUniverseRegionById( constellation.getRegionId() ) );
				return Optional.of( new SpaceLocationImplementation.Builder()
						.withRegion( region )
						.withConstellation( constellation )
						.withSolarSystem( solarSystem )
						.build()
				);
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
				return Optional.of( new SpaceLocationImplementation.Builder()
						.withRegion( region )
						.withConstellation( constellation )
						.withSolarSystem( solarSystem )
						.withStation( station )
						.build()
				);
			}
		} catch (final RuntimeException runtime) {
			LogWrapper.error( runtime );
			return Optional.empty();
		}
		return Optional.empty();
	}

	@Override
	public Optional<SpaceLocation> buildUpStructure4Pilot( final Long locationId, final Credential credential ) {
		LogWrapper.enter("locationId->"+locationId);
		try {
			final GetUniverseStructuresStructureIdOk structure = Objects.requireNonNull(
					this.search200OkStructureById( locationId, credential ),
					"ESI Structure should not be null while creating Location."
			);
			final GetUniverseSystemsSystemIdOk solarSystem = Objects.requireNonNull(
					this.getUniverseSystemById( structure.getSolarSystemId() ),
					"ESI Structure should not be null while creating Location."
			);
			final GetUniverseConstellationsConstellationIdOk constellation = Objects.requireNonNull(
					this.getUniverseConstellationById( solarSystem.getConstellationId() ),
					"ESI Structure should not be null while creating Location."
			);
			final GetUniverseRegionsRegionIdOk region = Objects.requireNonNull(
					this.getUniverseRegionById( constellation.getRegionId() ),
					"ESI Structure should not be null while creating Location."
			);
			return Optional.of( new Structure.Builder()
					.withRegion( region )
					.withConstellation( constellation )
					.withSolarSystem( solarSystem )
					.withStructure( locationId, structure )
					.withCorporation( structure.getOwnerId(), "-TO-DE-DEFINED-" )
					.build()
			);
		} catch (final RuntimeException runtime) {
			LogWrapper.error( runtime );
			return Optional.empty();
		}finally {
			LogWrapper.exit();
		}
	}

	protected GetUniverseStructuresStructureIdOk search200OkStructureById( final Long structureId, final Credential credential ) {
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

	protected GetUniverseRegionsRegionIdOk getUniverseRegionById( final Integer regionId ) {
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

	protected GetUniverseConstellationsConstellationIdOk getUniverseConstellationById( final Integer constellationId ) {
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

	protected GetUniverseSystemsSystemIdOk getUniverseSystemById( final Integer systemId ) {
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

	protected GetUniverseStationsStationIdOk getUniverseStationById( final Integer stationId ) {
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
}
