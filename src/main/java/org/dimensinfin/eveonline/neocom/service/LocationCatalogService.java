package org.dimensinfin.eveonline.neocom.service;

import java.util.Optional;
import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.ports.ILocationFactoryPort;

/**
 * The location catalog service will be used to define Eve Online space locations. It is able to understand their different contents depending on the
 * type of location. Locations should be cached and have a simple Object dump process registered on the scheduler that saves the current location list
 * every minute. Locations already defined are read back from the storage at creation time. Heroku implementations will not have available storage
 * space so the serialization and recovery should be an injectable dependency module that can change the storage implementation depending on the
 * target environment. By default then the location list is not persisted.
 *
 * Now Locations are completely normalized and just depend on two class implementations. Storage can then be done into a database instance.
 *
 * @since 0.19.0
 */
public class LocationCatalogService {
	// - C O M P O N E N T S
	private final ILocationFactoryPort locationFactory;

	// - C O N S T R U C T O R S
	@Inject
	public LocationCatalogService( final @NotNull @Named(DMServicesDependenciesModule.LOCATION_FACTORY) ILocationFactoryPort locationFactory
	) {
		this.locationFactory = locationFactory;
	}

	// - S E A R C H   L O C A T I O N   A P I

	/**
	 * Use a single place where to search for locations if we know a full location identifier. It will detect if the location is a space or structure
	 * location and search for the right record. This method is used for Pilot and structure locations.
	 *
	 * This method is deprecated in favor to use <code>access</code> methods that will return <code>Optional</code> and will not deal with null
	 * values.
	 *
	 * @param locationId full location identifier obtained from any asset with the full location identifier.
	 * @param credential the pilot Credential to be used to access the list of structures. Not all structures are available and it is a secured
	 *                   endpoint.
	 * @return a SpaceLocation record with the complete location data identifiers and descriptions.
	 * @deprecated
	 */
	@Deprecated
	public SpaceLocation searchLocation4Id( final LocationIdentifier locationId, final Credential credential ) {
		if ( locationId.getSpaceIdentifier() > 64e6 )
			return this.searchStructure4Id( locationId.getSpaceIdentifier(), credential );
		else return this.searchLocation4Id( locationId.getSpaceIdentifier() );
	}

	/**
	 * New search method the uses the Data Store cache and that returns empty Optional in case the location cannot be constructed. Nulls should be
	 * avoided.
	 *
	 * @param locationId full location identifier obtained from any asset with the full location identifier.
	 * @param credential the pilot Credential to be used to access the list of structures. Not all structures are available and it is a secured
	 *                   endpoint.
	 * @return a SpaceLocation record with the complete location data identifiers and descriptions. Or Optional.empty() if the location cannot be
	 * 		constructed.
	 */
	public Optional<SpaceLocation> lookupLocation4Id( final Long locationId, final Credential credential ) {
		if ( locationId > 64e6 ) // - This is a corporation structure so needs additional scope privileges to be accessible.
			return this.locationFactory.buildUpStructure4Pilot( locationId, credential );
		else
			return this.locationFactory.buildUpLocation4LocationId( locationId );
	}

	/**
	 * Method to search for public locations and space stations that do not belong to players.
	 *
	 * @param locationId the unique identifier for the location
	 * @return a SpaceLocation with the correct fields filled. Can be Region or Constellation or Space or Station.
	 */
	@Deprecated
	public SpaceLocation searchLocation4Id( final Long locationId ) {
		final Optional<SpaceLocation> location = this.locationFactory.buildUpLocation4LocationId( locationId );
		return location.orElse( null );
	}

	@Deprecated
	public SpaceLocation searchStructure4Id( final Long locationId, final Credential credential ) {
		final Optional<SpaceLocation> location = this.lookupLocation4Id( locationId, credential );
		return location.orElse( null );
	}
}
