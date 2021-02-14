package org.dimensinfin.eveonline.neocom.character.converter;

import javax.validation.constraints.NotNull;

import org.dimensinfin.core.interfaces.Converter;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdLocationOk;
import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class GetCharactersCharacterIdLocationToSpaceLocationConverter implements Converter<GetCharactersCharacterIdLocationOk, SpaceLocation> {
	private final LocationCatalogService locationCatalogService;
	private final Credential credential;

	// - C O N S T R U C T O R S
	public GetCharactersCharacterIdLocationToSpaceLocationConverter( @NotNull final LocationCatalogService locationCatalogService,
	                                                                 @NotNull final Credential credential ) {
		this.locationCatalogService = locationCatalogService;
		this.credential = credential;
	}

	/**
	 * Detects the type of current location (Space, Station or Structure) and then converts the code to a SpaceLocation instance depending on that
	 * location.
	 *
	 * @param input the current pilot space location identifiers.
	 * @return a <code>SpaceLocation</code> instance of the correct type.
	 */
	@Override
	public SpaceLocation convert( final GetCharactersCharacterIdLocationOk input ) {
		if (null != input.getStructureId())
			return this.locationCatalogService.searchStructure4Id( input.getStructureId(), this.credential );
		if (null != input.getStationId())
			return this.locationCatalogService.searchLocation4Id( (long) input.getStationId() );
		return this.locationCatalogService.searchLocation4Id( (long) input.getSolarSystemId() );
	}
}