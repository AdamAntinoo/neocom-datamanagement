package org.dimensinfin.eveonline.neocom.character.converter;

import javax.validation.constraints.NotNull;

import org.dimensinfin.core.interfaces.Converter;
import org.dimensinfin.eveonline.neocom.character.domain.PublicCorporationV1;
import org.dimensinfin.eveonline.neocom.character.domain.PublicPilotV1;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class GetCharactersCharacterIdToPublicPilotV1Converter implements Converter<GetCharactersCharacterIdOk, PublicPilotV1> {
	private final Integer pilotId;
	private final ESIDataService esiDataService;
	private final PublicCorporationV1 corporation;

	// - C O N S T R U C T O R S
	public GetCharactersCharacterIdToPublicPilotV1Converter( @NotNull final Integer pilotId,
	                                                         @NotNull final ESIDataService esiDataService,
	                                                         @NotNull final PublicCorporationV1 corporation ) {
		this.pilotId = pilotId;
		this.esiDataService = esiDataService;
		this.corporation = corporation;
	}

	@Override
	public PublicPilotV1 convert( final GetCharactersCharacterIdOk pilotData ) {
		return new PublicPilotV1.Builder()
				.withPilotId( this.pilotId )
				.withCorporation( this.corporation )
				.withPilotPublicData( pilotData )
				.withRaceData( this.esiDataService.searchSDERace( pilotData.getRaceId() ) )
				.withAncestryData( this.esiDataService.searchSDEAncestry( pilotData.getAncestryId() ) )
				.withBloodlineData( this.esiDataService.searchSDEBloodline( pilotData.getBloodlineId() ) )
				.build();
	}
}