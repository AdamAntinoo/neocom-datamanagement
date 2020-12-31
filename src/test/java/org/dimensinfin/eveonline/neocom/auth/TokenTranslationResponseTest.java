package org.dimensinfin.eveonline.neocom.auth;

import org.junit.Assert;
import org.junit.Test;

import org.dimensinfin.eveonline.neocom.support.PojoTestUtils;

public class TokenTranslationResponseTest {
	@Test
	public void accessorContract() {
		PojoTestUtils.validateAccessors( TokenTranslationResponse.class );
	}

	@Test
	public void gettersContract() {
		final TokenTranslationResponse tokenTranslationResponse = new TokenTranslationResponse();
		Assert.assertNotNull( tokenTranslationResponse );
		tokenTranslationResponse.setExpires( 1000 );
		final long expected = (System.currentTimeMillis() + tokenTranslationResponse.getExpires() * 1000);
		final long obtained = tokenTranslationResponse.getExpiresOn();
		Assert.assertTrue(expected>=obtained);
		Assert.assertEquals( 1000, tokenTranslationResponse.getExpires() );
	}
}