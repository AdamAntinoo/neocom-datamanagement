package org.dimensinfin.eveonline.neocom.auth;

import org.junit.Assert;
import org.junit.Test;

public class TokenRequestBodyTest {
	@Test
	public void gettersContract() {
		final TokenRequestBody tokenRequestBody = new TokenRequestBody();
		Assert.assertNotNull(tokenRequestBody);
		tokenRequestBody.setCode( "-TEST-CODE-" );
		Assert.assertEquals("-TEST-CODE-", tokenRequestBody.getCode());
		Assert.assertEquals("authorization_code", tokenRequestBody.getGrant_type());
	}
}