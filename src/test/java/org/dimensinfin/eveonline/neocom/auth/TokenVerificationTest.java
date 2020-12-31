package org.dimensinfin.eveonline.neocom.auth;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;

public class TokenVerificationTest {
	private TokenTranslationResponse tokenTranslationResponse;
	private VerifyCharacterResponse verifyCharacterResponse;
	private TokenVerification tokenVerification;

	@BeforeEach
	public void beforeEach() {
		this.tokenTranslationResponse = Mockito.mock( TokenTranslationResponse.class );
		this.verifyCharacterResponse = Mockito.mock( VerifyCharacterResponse.class );
		Mockito.when( this.verifyCharacterResponse.getCharacterID() )
				.thenReturn( 123456L );
		this.tokenVerification = new TokenVerification()
				.setAuthCode( "-TEST-AUTH-CODE-" )
				.setDataSource( "Tranquility" )
				.setTokenTranslationResponse( this.tokenTranslationResponse )
				.setVerifyCharacterResponse( this.verifyCharacterResponse )
				.setPeck( "-PECK-" );
	}

	@Test
	public void getAccountIdentifierFailure() {
		Assertions.assertThrows( NeoComRuntimeException.class, () -> {
					this.tokenVerification.setVerifyCharacterResponse( null );
					this.tokenVerification.getAccountIdentifier();
				},
				"Expected JobScheduler.Builder() to throw null verification, but it didn't." );
	}

	@Test
	public void getAccountIdentifierValid() {
		final Long identifier = 123456L;
		Mockito.when( this.verifyCharacterResponse.getCharacterID() )
				.thenReturn( identifier );
		Assertions.assertEquals( identifier.intValue(), this.tokenVerification.getAccountIdentifier() );
	}

	@Test
	public void getterContract() {
		Assertions.assertEquals( "-TEST-AUTH-CODE-", this.tokenVerification.getAuthCode() );
		Assertions.assertEquals( "Tranquility", this.tokenVerification.getDataSource() );
		Assertions.assertEquals( this.tokenTranslationResponse, this.tokenVerification.getTokenTranslationResponse() );
		Assertions.assertEquals( "-PECK-", this.tokenVerification.getPeck() );
		Assertions.assertEquals( 123456, this.tokenVerification.getAccountIdentifier() );
		Assertions.assertEquals( this.verifyCharacterResponse, this.tokenVerification.getVerifyCharacterResponse() );
	}
}
