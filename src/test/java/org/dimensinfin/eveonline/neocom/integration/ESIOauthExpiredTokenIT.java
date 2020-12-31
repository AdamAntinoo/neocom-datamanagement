package org.dimensinfin.eveonline.neocom.integration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Assertions;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.support.IntegrationEnvironmentDefinition;

public class ESIOauthExpiredTokenIT extends IntegrationEnvironmentDefinition {
//	private ESIDataProvider esiDataProvider;

//	@Test
	void esiExpirationTokenTest() throws IOException, SQLException {
		final Credential credential = new Credential.Builder( 92223647 )
				.withAccountName( "Beth Ripley" )
				.withAccessToken(
						"1|CfDJ8O+5Z0aH+aBNj61BXVSPWfgEBs0UE9nnwXM7TMStXMsNmI9M/GfET+KNnfaIo3OHorsCSm0IAep+gylHx6lolkuq1+BUPYYPTWpYwGAsPrMVMywe6N2YRXL42stHjv5710EGznyP1cojdVmnotwKhVFMdF5HxFwuozRhgWjGPiXh" )
				.withRefreshToken(
						"plJhR5xjdSCmdB__NH_Cwva2eylakEHmnElSuJX7-m-muxTBVs49cwGyrkTNXEci6UCbZ8WDTx4Yz63egsCR0EWcahV0qo5IsslLUHTstKF_JeJllIm90h_o2lVr3lBjkVclWMPWyDPVQ8CNKO_sSEN7gZqWS-DWOYcyToZca6-XcjnxgEjoidyyt9ujDfAV" )
				.withDataSource( "tranquility" )
				.withScope(
						"publicData esi-location.read_location.v1 esi-location.read_ship_type.v1 esi-mail.read_mail.v1 esi-skills.read_skills.v1 esi-skills.read_skillqueue.v1 esi-wallet.read_character_wallet.v1 esi-wallet.read_corporation_wallet.v1 esi-search.search_structures.v1 esi-clones.read_clones.v1 esi-universe.read_structures.v1 esi-assets.read_assets.v1 esi-planets.manage_planets.v1 esi-fittings.read_fittings.v1 esi-industry.read_character_jobs.v1 esi-markets.read_character_orders.v1 esi-characters.read_blueprints.v1 esi-contracts.read_character_contracts.v1 esi-clones.read_implants.v1 esi-wallet.read_corporation_wallets.v1 esi-characters.read_notifications.v1 esi-corporations.read_divisions.v1 esi-assets.read_corporation_assets.v1 esi-corporations.read_blueprints.v1 esi-contracts.read_corporation_contracts.v1 esi-industry.read_corporation_jobs.v1 esi-markets.read_corporation_orders.v1 esi-industry.read_character_mining.v1 esi-industry.read_corporation_mining.v1" )
				.build()
				.setAssetsCount( 1289 )
				.setWalletBalance( 2.76586637596E9 )
				.setRaceName( "Minmatar" );
		this.setupEnvironment();
		final List<GetCharactersCharacterIdAssets200Ok> assets = this.esiDataProvider
				.getCharactersCharacterIdAssets( credential );
		Assertions.assertEquals( 1289, assets.size() );
	}
}
