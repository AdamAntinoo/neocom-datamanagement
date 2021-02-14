package org.dimensinfin.eveonline.neocom.character.service;

import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.dimensinfin.eveonline.neocom.character.converter.GetCharactersCharacterIdLocationToSpaceLocationConverter;
import org.dimensinfin.eveonline.neocom.character.domain.PilotV1;
import org.dimensinfin.eveonline.neocom.character.domain.PublicCorporationV1;
import org.dimensinfin.eveonline.neocom.character.domain.PublicPilotV1;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdLocationOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdShipOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdSkillsOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.service.DMServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;
import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.service.ResourceFactory;

import static org.dimensinfin.eveonline.neocom.exception.ErrorInfoCatalog.PILOT_NOT_FOUND;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class CharacterService {
	private final ESIDataService esiDataService;
	private final LocationCatalogService locationCatalogService;
	private final ResourceFactory resourceFactory;

	// - C O N S T R U C T O R S
	@Inject
	public CharacterService( @NotNull @Named(DMServicesDependenciesModule.ESIDATA_SERVICE) final ESIDataService esiDataService,
	                          @NotNull @Named(DMServicesDependenciesModule.LOCATION_CATALOG_SERVICE) final LocationCatalogService locationCatalogService,
	                          @NotNull @Named(DMServicesDependenciesModule.RESOURCE_FACTORY) final ResourceFactory resourceFactory ) {
		this.esiDataService = esiDataService;
		this.locationCatalogService = locationCatalogService;
		this.resourceFactory = resourceFactory;
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

	public PilotV1 getPilotV1( final Credential credential ) {
		final GetCharactersCharacterIdOk pilotData = this.esiDataService.getCharactersCharacterId( credential.getAccountId() );
		if (null == pilotData)
			throw new NeoComRuntimeException( PILOT_NOT_FOUND, credential.getAccountId().toString() );
		final GetCharactersCharacterIdSkillsOk skillPoints = this.esiDataService.getCharactersCharacterIdSkills( credential );
		final Double walletBalance = this.esiDataService.getCharactersCharacterIdWallet( credential );
		final GetCharactersCharacterIdLocationOk lastKnownLocation = this.esiDataService.getCharactersCharacterIdLocation( credential );
		final GetCharactersCharacterIdShipOk currentShip = this.esiDataService.getCharactersCharacterIdShip( credential );
		return new PilotV1.Builder()
				.withPilotId( credential.getAccountId() )
				.withCorporation( this.getCorporationPublicData( pilotData.getCorporationId() ) )
				.withPilotPublicData( pilotData )
				.withRaceData( this.esiDataService.searchSDERace( pilotData.getRaceId() ) )
				.withAncestryData( this.esiDataService.searchSDEAncestry( pilotData.getAncestryId() ) )
				.withBloodlineData( this.esiDataService.searchSDEBloodline( pilotData.getBloodlineId() ) )
				.withTotalSkillPoints( skillPoints.getTotalSp() )
				.withWalletBalance( walletBalance )
				.withLastKnownLocation( new GetCharactersCharacterIdLocationToSpaceLocationConverter(
						this.locationCatalogService, credential ).convert( lastKnownLocation ) )
				.withCurrentShip( currentShip )
				.withCurrentShipType( this.resourceFactory.generateType4Id( currentShip.getShipTypeId() ) )
				.build();
	}
}