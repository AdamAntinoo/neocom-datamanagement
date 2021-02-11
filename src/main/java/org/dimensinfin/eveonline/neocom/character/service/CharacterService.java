package org.dimensinfin.eveonline.neocom.character.service;

import javax.validation.constraints.NotNull;

import com.google.inject.name.Named;

import org.dimensinfin.eveonline.neocom.character.domain.PublicCorporationV1;
import org.dimensinfin.eveonline.neocom.character.domain.PublicPilotV1;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.dimensinfin.eveonline.neocom.service.DMServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class CharacterService {
	private final ESIDataService esiDataService;

	// - C O N S T R U C T O R S
	private CharacterService( @NotNull @Named(DMServicesDependenciesModule.ESIDATA_SERVICE) final ESIDataService esiDataService ) {
		this.esiDataService = esiDataService;
	}

	public PublicCorporationV1 getCorporationPublicData( final int corporationId ) {
		final GetCorporationsCorporationIdOk publicData = this.esiDataService.getCorporationsCorporationId( corporationId );
		return new PublicCorporationV1.Builder()
				.withCorporationId( corporationId )
				.withCorporationPublicData( publicData )
				.withCeoPilotData( this.getPilotPublicData( publicData.getCeoId() ) )
				.build();
	}

	public PublicPilotV1 getPilotPublicData( final int pilotId ) {
		final GetCharactersCharacterIdOk publicData = this.esiDataService.getCharactersCharacterId( pilotId );
		return new PublicPilotV1.Builder<PublicPilotV1, PublicPilotV1.Builder>()
				.withPilotId( pilotId )
				.withPilotPublicData( publicData )
				.withRaceData( this.esiDataService.searchSDERace( publicData.getRaceId() ) )
				.withAncestryData( this.esiDataService.searchSDEAncestry( publicData.getAncestryId() ) )
				.withBloodlineData( this.esiDataService.searchSDEBloodline( publicData.getBloodlineId() ) )
				.withCorporation( this.getCorporationPublicData( publicData.getCorporationId() ) )
				.build();
	}
}