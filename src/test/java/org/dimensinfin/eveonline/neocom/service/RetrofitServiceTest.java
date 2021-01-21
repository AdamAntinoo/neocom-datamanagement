package org.dimensinfin.eveonline.neocom.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;

import retrofit2.Retrofit;

public class RetrofitServiceTest {

	private IConfigurationService configurationService;
	private IFileSystem fileSystem;

	@Test
	public void accessAuthenticatedConnector() {
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		final String scopes ="-SCOPES-";
		// When
		Mockito.when( credential.getScope() ).thenReturn( scopes );
		Mockito.when( this.configurationService.getResourceString( Mockito.anyString() ) ).thenReturn( "-CONFIGURATION-VALUE-" );
		Mockito.when( this.configurationService.getResourceString( Mockito.anyString(), Mockito.anyString() ) ).thenReturn( "http://localhost/" );
		// Test
		final RetrofitService retrofitService = new RetrofitService( this.configurationService, this.fileSystem );
		final Retrofit obtained = retrofitService.accessAuthenticatedConnector( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertTrue( obtained instanceof Retrofit );
	}

	@Test
	public void accessUniverseConnector() {
		// When
		Mockito.when( this.configurationService.getResourceString( Mockito.anyString() ) ).thenReturn( "-CONFIGURATION-VALUE-" );
		Mockito.when( this.configurationService.getResourceString( Mockito.anyString(), Mockito.anyString() ) ).thenReturn( "http://localhost/" );
		// Test
		final RetrofitService retrofitService = new RetrofitService( this.configurationService, this.fileSystem );
		final Retrofit obtained = retrofitService.accessUniverseConnector();
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertTrue( obtained instanceof Retrofit );
	}

	@BeforeEach
	public void beforeEach() {
		this.configurationService = Mockito.mock( IConfigurationService.class );
		this.fileSystem = Mockito.mock( IFileSystem.class );
	}

	@Test
	public void constructorContract() {
		final RetrofitService retrofitService = new RetrofitService( this.configurationService, this.fileSystem );
		Assertions.assertNotNull( retrofitService );
	}
}