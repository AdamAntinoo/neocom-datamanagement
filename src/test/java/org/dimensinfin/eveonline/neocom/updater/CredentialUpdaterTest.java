package org.dimensinfin.eveonline.neocom.updater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;

public class CredentialUpdaterTest {
	private Credential model4Test;
	private CredentialUpdater updater4Test;

	@Before
	public void setUp() throws IOException {
//		super.setUp();
		this.model4Test = new Credential.Builder( 123456 ).withAccountName( "TEST CREDENTIAL" ).build();
		this.updater4Test = new CredentialUpdater( this.model4Test );
	}

	@Test
	public void CredentialUpdaterConstructor() {
		final Credential credential = Mockito.mock( Credential.class );
		final CredentialUpdater updater = new CredentialUpdater( credential );

		Assert.assertNotNull( updater );
	}

	@Test
	public void getIdentifier() {
		final Credential model = new Credential.Builder( 123456 ).withAccountName( "TEST CREDENTIAL" ).build();
		final CredentialUpdater updater = new CredentialUpdater( model );
		final String obtained = updater.getIdentifier();

		Assert.assertNotNull( updater );
		Assert.assertEquals( "Check the updater generated identifier.", "CREDENTIAL:123456", obtained );
	}

	@Test
	public void needsRefreshNeeds() {
		final boolean obtained = this.updater4Test.needsRefresh();
		Assert.assertTrue( "This model2Test needs refresh", obtained );
	}

	@Test
	public void needsRefreshDoesNotNeed() {
		this.updater4Test.getModel().timeStamp();
		final boolean obtained = this.updater4Test.needsRefresh();
		Assert.assertFalse( "This model2Test does not need refresh", obtained );
	}

	@Test
	public void onRun() {
//		Mockito.when( credential.isValid() ).thenReturn( true );
		final Credential credential = new Credential.Builder( 93813310 )
				.withAccountName( "Perico Tuerto" )
				.withAccessToken( "-TEST-ACCESS-TOKEN-" )
				.withRefreshToken( "-TEST-REFRESH-TOKEN-" )
				.withDataSource( "tranquility" )
				.build();
//				.withScope(
//						"publicData esi-location.read_location.v1 esi-location.read_ship_type.v1 esi-mail.read_mail.v1 esi-skills.read_skills.v1 esi-skills.read_skillqueue.v1 esi-wallet.read_character_wallet.v1 esi-wallet.read_corporation_wallet.v1 esi-search.search_structures.v1 esi-clones.read_clones.v1 esi-universe.read_structures.v1 esi-assets.read_assets.v1 esi-planets.manage_planets.v1 esi-fittings.read_fittings.v1 esi-industry.read_character_jobs.v1 esi-markets.read_character_orders.v1 esi-characters.read_blueprints.v1 esi-contracts.read_character_contracts.v1 esi-clones.read_implants.v1 esi-wallet.read_corporation_wallets.v1 esi-characters.read_notifications.v1 esi-corporations.read_divisions.v1 esi-assets.read_corporation_assets.v1 esi-corporations.read_blueprints.v1 esi-contracts.read_corporation_contracts.v1 esi-industry.read_corporation_jobs.v1 esi-markets.read_corporation_orders.v1 esi-industry.read_character_mining.v1 esi-industry.read_corporation_mining.v1" )
//				.withAssetsCount( 1476 )
//				.withWalletBalance( 6.309543632E8 )
//				.withRaceName( "Amarr" )
//				.build();
		final GetCharactersCharacterIdAssets200Ok esiAsset = Mockito.mock( GetCharactersCharacterIdAssets200Ok.class );
		final List<GetCharactersCharacterIdAssets200Ok> assetList = new ArrayList<>();
		assetList.add( esiAsset );
		assetList.add( esiAsset );
		final GetCharactersCharacterIdOk pilotPublicData = Mockito.mock( GetCharactersCharacterIdOk.class );
		final ESIDataService esiDataProvider = Mockito.mock( ESIDataService.class );
		final GetUniverseRaces200Ok raceData = Mockito.mock( GetUniverseRaces200Ok.class );
		Mockito.when( raceData.getName() ).thenReturn( "Amarr" );
		Mockito.when( esiDataProvider.getCharactersCharacterIdAssets( Mockito.any( Credential.class ) ) )
				.thenReturn( assetList );
		Mockito.when( esiDataProvider.getCharactersCharacterIdWallet( Mockito.any( Credential.class ) ) )
				.thenReturn( 6.309543632E8 );
		Mockito.when( esiDataProvider.getCharactersCharacterId( Mockito.anyInt() ) )
				.thenReturn( pilotPublicData );
		Mockito.when( esiDataProvider.searchSDERace( Mockito.anyInt() ) )
				.thenReturn( raceData );
		NeoComUpdater.injectsEsiDataAdapter( esiDataProvider );
		final CredentialUpdater updater = new CredentialUpdater( credential );
		updater.onRun();
		Assert.assertEquals( "Check the number of assets.", 2, updater.getModel().getAssetsCount() );
		Assert.assertEquals( "Check the wallet amount.",
				6.309543632E8, updater.getModel().getWalletBalance(), 0.01 );
//		Assert.assertEquals( "Check the value of mineral resources",
//				4.5, updater.getModel().getMiningResourcesEstimatedValue(), 0.6 );
		Assert.assertEquals( "Check the value of the race",
				"Amarr", updater.getModel().getRaceName() );
	}
}
