package org.dimensinfin.eveonline.neocom.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.utility.Base64;

public class NeoComOAuth2FlowTest {

	private IConfigurationService configurationService;

	@BeforeEach
	public void beforeEach() {
		this.configurationService = Mockito.mock( IConfigurationService.class );
	}

	@Test
	public void buildComplete() {
		final NeoComOAuth2Flow neoComOAuth2Flow = new NeoComOAuth2Flow.Builder()
				.withConfigurationService( this.configurationService )
				.build();
		Assertions.assertNotNull( neoComOAuth2Flow );
	}

	@Test
	public void buildFailure() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			final NeoComOAuth2Flow neoComOAuth2Flow = new NeoComOAuth2Flow.Builder()
					.withConfigurationService( null )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final NeoComOAuth2Flow neoComOAuth2Flow = new NeoComOAuth2Flow.Builder()
					.build();
		} );
	}

	@Test
	public void generateLoginUrl() {
		// Given
		final NeoComOAuth2Flow neoComOAuth2Flow = new NeoComOAuth2Flow.Builder()
				.withConfigurationService( this.configurationService )
				.build();
		final String expected = "https://-TEST-STRING-/-TEST-STRING-?response_type=code&redirect_uri=eveauth-neocom%3A%2F%2Fesiauthentication&scope" +
				"=publicData esi-location.read_location.v1 esi-location.read_ship_type.v1 esi-mail.read_mail.v1 esi-skills.read_skills.v1 esi-skills.read_skillqueue.v1 esi-wallet.read_character_wallet.v1 esi-wallet.read_corporation_wallet.v1 esi-search.search_structures.v1 esi-clones.read_clones.v1 esi-universe.read_structures.v1 esi-assets.read_assets.v1 esi-planets.manage_planets.v1 esi-fittings.read_fittings.v1 esi-industry.read_character_jobs.v1 esi-markets.read_character_orders.v1 esi-characters.read_blueprints.v1 esi-contracts.read_character_contracts.v1 esi-clones.read_implants.v1 esi-wallet.read_corporation_wallets.v1 esi-characters.read_notifications.v1 esi-corporations.read_divisions.v1 esi-assets.read_corporation_assets.v1 esi-corporations.read_blueprints.v1 esi-contracts.read_corporation_contracts.v1 esi-industry.read_corporation_jobs.v1 esi-markets.read_corporation_orders.v1 esi-industry.read_character_mining.v1 esi-industry.read_corporation_mining.v1&client_id=-TEST-STRING-&state=LVRFU1QtU1RSSU5HLQ==";
		// When
		Mockito.when( this.configurationService.getResourceString( Mockito.anyString() ) ).thenReturn( "-TEST-STRING-" );
		// Assertions
		Assertions.assertEquals( expected, neoComOAuth2Flow.generateLoginUrl() );
	}

	@Test
	public void onStartFlow() {
		// Given
		final NeoComOAuth2Flow neoComOAuth2Flow = new NeoComOAuth2Flow.Builder()
				.withConfigurationService( this.configurationService )
				.build();
		final String code = "-CODE-";
		final String state = "-STATE-";
		final String datasource = "-DATASOURCE-";
		// Test
		neoComOAuth2Flow.onStartFlow( code, state, datasource );
		// Assertions
		Assertions.assertEquals( code, neoComOAuth2Flow.tokenVerificationStore.getAuthCode() );
		Assertions.assertEquals( state, neoComOAuth2Flow.tokenVerificationStore.getState() );
		Assertions.assertEquals( datasource.toLowerCase(), neoComOAuth2Flow.tokenVerificationStore.getDataSource() );
	}

	@Test
	public void verifyState() {
		// Given
		final NeoComOAuth2Flow neoComOAuth2Flow = new NeoComOAuth2Flow.Builder()
				.withConfigurationService( this.configurationService )
				.build();
		final String state = Base64.encodeBytes( "-STATE-".getBytes() ).replaceAll( "\n", "" );
		// When
		Mockito.when( this.configurationService.getResourceString( Mockito.anyString() ) ).thenReturn( "-STATE-" );
		// Assertions
		Assertions.assertTrue( neoComOAuth2Flow.verifyState( state ) );
		Assertions.assertFalse( neoComOAuth2Flow.verifyState( "-INVALID-STATE-" ) );
	}
}
