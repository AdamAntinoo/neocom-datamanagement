package org.dimensinfin.eveonline.neocom.ports;

import java.util.Optional;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;

public interface ILocationFactoryPort {
	Optional<SpaceLocation> buildUpLocation4LocationId( final Long locationId );

	Optional<SpaceLocation> buildUpStructure4Pilot( Long locationId, Credential credential );
}
