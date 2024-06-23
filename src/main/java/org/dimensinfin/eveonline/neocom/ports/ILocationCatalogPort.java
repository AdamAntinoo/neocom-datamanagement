package org.dimensinfin.eveonline.neocom.ports;

import java.util.Optional;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;

public interface ILocationCatalogPort {
	Optional<SpaceLocation> lookupLocation4Id( final Long locationId, final Credential credential );
}
