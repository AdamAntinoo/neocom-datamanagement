package org.dimensinfin.eveonline.neocom.database.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class CredentialTest {
	private Credential credential4Test;

	@BeforeEach
	public void beforeEach() {
		this.credential4Test = new Credential.Builder( 123456 )
				.withAccountName( "TEST CREDENTIAL" )
				.withCorporationId( 4321987 )
				.withAccessToken( "-TEST INVALID ACCESS TOKEN-" )
				.withRefreshToken( "-TEST INVALID ACCESS TOKEN-" )
				.withDataSource( "Tranquility" )
				.withScope( "SCOPE" )
				.build()
				.setAssetsCount( 89 )
				.setWalletBalance( 876567.54 )
				.setMiningResourcesEstimatedValue( 123456789.98 )
				.setRaceName( "TEST RACE" );
	}

	@Test
	public void buildCheckclass() {
		final Credential credential = new Credential.Builder( 123456 )
				.withAccountName( "TEST CREDENTIAL" )
				.build();
		Assertions.assertNotNull( credential );
		Assertions.assertEquals( "Credential", credential.getJsonClass() );
	}

	@Test
	public void buildComplete() {
		final Credential credential = new Credential.Builder( 123456 )
				.withAccountName( "TEST CREDENTIAL" )
				.withAccessToken( "-TEST INVALID ACCESS TOKEN-" )
				.withTokenType( "Bearer" )
				.withRefreshToken( "-TEST INVALID ACCESS TOKEN-" )
				.withDataSource( "Tranquility" )
				.withScope( "SCOPE" )
				.build()
				.setAssetsCount( 98 )
				.setWalletBalance( 876567.54 )
				.setMiningResourcesEstimatedValue( 123456789.98 )
				.setRaceName( "TEST RACE" );
		Assertions.assertNotNull( credential );
		Assertions.assertEquals( "tranquility.123456", credential.getUniqueCredential() );
		Assertions.assertEquals( 98, credential.getAssetsCount() );
		Assertions.assertEquals( 876567.54, credential.getWalletBalance(), 0.01 );
		Assertions.assertEquals( 123456789.98, credential.getMiningResourcesEstimatedValue(), 0.01 );
	}

	@Test
	public void buildFailure() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			final Credential credential = new Credential.Builder( 4321 )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final Credential credential = new Credential.Builder( 4321 )
					.withAccountName( null )
					.build();
		} );
	}

	@Test
	public void constructorContract() {
		final Credential credential = new Credential();
		Assertions.assertNotNull( credential );
	}

//	@Test
	public void equalsContract() {
		EqualsVerifier.forClass( Credential.class )
				.withIgnoredFields( "creationTime", "lastUpdateTime" )
				.usingGetClass().verify();
	}

	@Test
	public void gettersContract() {
		Assertions.assertNotNull( credential4Test );
		Assertions.assertEquals( 123456, credential4Test.getAccountId().intValue() );
		Assertions.assertEquals( "TEST CREDENTIAL", credential4Test.getAccountName() );
		Assertions.assertEquals( 4321987, credential4Test.getCorporationId() );
		Assertions.assertEquals( "-TEST INVALID ACCESS TOKEN-", credential4Test.getAccessToken() );
		Assertions.assertEquals( "-TEST INVALID ACCESS TOKEN-", credential4Test.getRefreshToken() );
		Assertions.assertEquals( "Tranquility".toLowerCase(), credential4Test.getDataSource() );
		Assertions.assertEquals( "SCOPE", credential4Test.getScope() );
		Assertions.assertEquals( 89, credential4Test.getAssetsCount() );
		Assertions.assertEquals( 876567.54, credential4Test.getWalletBalance(), 0.1 );
		Assertions.assertEquals( 123456789.98, credential4Test.getMiningResourcesEstimatedValue(), 0.1 );
		Assertions.assertEquals( "TEST RACE", credential4Test.getRaceName() );

		Assertions.assertEquals( "tranquility.123456", credential4Test.getUniqueCredential() );
		Assertions.assertEquals( "TEST CREDENTIAL", credential4Test.getName() );
	}

	@Test
	public void settersContract() {
		final Credential credential = new Credential.Builder( 123456 )
				.withAccountName( "TEST CREDENTIAL" )
				.withAccessToken( "-TEST INVALID ACCESS TOKEN-" )
				.withRefreshToken( "-TEST INVALID ACCESS TOKEN-" )
				.withDataSource( "Tranquility" )
				.build();
		credential.setAccessToken("-ACCESS-TOKEN-");
		Assertions.assertEquals( "-ACCESS-TOKEN-" ,credential.getAccessToken());
		credential.setJwtToken("-JWT-TOKEN-");
		Assertions.assertEquals( "-JWT-TOKEN-" ,credential.getJwtToken());
		credential.setUniqueCredential("-UNIQUE-CREDENTIAL-");
		Assertions.assertEquals( "-UNIQUE-CREDENTIAL-" ,credential.getUniqueCredential());
	}

	@Test
	public void isValid_failure() {
		final Credential credential = new Credential.Builder( 123456 )
				.withAccountName( "TEST CREDENTIAL" )
				.build();
		Assertions.assertNotNull( credential );
		Assertions.assertFalse( credential.isValid() );
	}

	@Test
	public void isValid_ok() {
		final Credential credential = new Credential.Builder( 123456 )
				.withAccountName( "TEST CREDENTIAL" )
				.withAccessToken( "-TEST INVALID ACCESS TOKEN-" )
				.withRefreshToken( "-TEST INVALID ACCESS TOKEN-" )
				.withDataSource( "Tranquility" )
				.build();
		Assertions.assertNotNull( credential );
		Assertions.assertTrue( credential.isValid() );
	}

	@Test
	public void setterContract() {
		credential4Test.setWalletBalance( 123456789.98 );
		Assertions.assertEquals( 123456789.98, credential4Test.getWalletBalance(), 0.1 );
		credential4Test.setMiningResourcesEstimatedValue( 123456789.98 );
		Assertions.assertEquals( 123456789.98, credential4Test.getMiningResourcesEstimatedValue(), 0.1 );
		credential4Test.setAssetsCount( 12 );
		Assertions.assertEquals( 12, credential4Test.getAssetsCount(), 0.1 );
		credential4Test.setRaceName( "Amarr" );
		Assertions.assertEquals( "Amarr", credential4Test.getRaceName() );
	}

	@Test
	public void toStringContract() {
		final Credential credential = new Credential.Builder( 123456 )
				.withAccountName( "TEST CREDENTIAL" )
				.withDataSource( "TESTING" )
				.build()
				.setAssetsCount( 98 )
				.setWalletBalance( 876567.54 )
				.setRaceName( "TEST RACE" );
		Assertions.assertNotNull( credential );
		final String expected = "{\"jsonClass\":\"Credential\",\"uniqueCredential\":\"tranquility.123456\",\"walletBalance\":876567.54,\"assetsCount\":98,\"miningResourcesEstimatedValue\":0.0,\"accountId\":123456,\"accountName\":\"TEST CREDENTIAL\",\"raceName\":\"TEST RACE\"}";
		final String obtained = credential.toString();
		Assertions.assertEquals( expected, obtained );
	}
}

