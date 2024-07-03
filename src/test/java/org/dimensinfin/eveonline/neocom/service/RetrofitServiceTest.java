package org.dimensinfin.eveonline.neocom.service;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.repositories.CredentialRepository;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;

import retrofit2.Retrofit;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.AUTHENTICATED_RETROFIT_CACHE_SIZE;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.AUTHENTICATED_RETROFIT_CACHE_STATE;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.AUTHENTICATED_RETROFIT_SERVER_AGENT;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.AUTHENTICATED_RETROFIT_SERVER_LOCATION;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.AUTHENTICATED_RETROFIT_SERVER_TIMEOUT;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.CACHE_DIRECTORY_PATH;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_AGENT;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_CALLBACK;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_CLIENTID;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_SECRETKEY;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_SERVER_URL;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_STATE;

public class RetrofitServiceTest {

	private IConfigurationService configurationService;
	private IFileSystem fileSystem;
	private CredentialRepository credentialRepository;

	@Test
	public void accessAuthenticatedConnector() {
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		final String scopes = "-SCOPES-";
		// When
		Mockito.when( credential.getScope() ).thenReturn( scopes );
		Mockito.when( this.configurationService.getResourceString( Mockito.anyString() ) ).thenReturn( "-CONFIGURATION-VALUE-" );
		Mockito.when( this.configurationService.getResourceString( Mockito.anyString(), Mockito.anyString() ) ).thenReturn( "http://localhost/" );
		// Test
		final RetrofitService retrofitService = this.getRetrofitService();
		final Retrofit obtained = retrofitService.accessAuthenticatedConnector( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertTrue( obtained instanceof Retrofit );
	}

	@Test
	public void accessAuthenticatedConnectorWithCache() throws IOException {
		// Given
		final String DEFAULT_ESI_DATA_SERVER = "https://esi.evetech.net/latest/";
		final String DEFAULT_RETROFIT_AGENT = "Default agent";
		final Integer DEFAULT_CACHE_SIZE_1GB = 1;
		final String DEFAULT_ESI_OAUTH_LOGIN_SERVER = "https://login.eveonline.com/";
		final Credential credential = Mockito.mock( Credential.class );
		final String scopes = "-SCOPES-";
		// When
		Mockito.when( credential.getScope() ).thenReturn( scopes );
		Mockito.when( this.configurationService.getResourceString( AUTHENTICATED_RETROFIT_SERVER_LOCATION, DEFAULT_ESI_DATA_SERVER ) )
				.thenReturn( DEFAULT_ESI_DATA_SERVER );
		Mockito.when( this.configurationService.getResourceString( AUTHENTICATED_RETROFIT_SERVER_AGENT, DEFAULT_RETROFIT_AGENT ) )
				.thenReturn( DEFAULT_RETROFIT_AGENT );
		Mockito.when( this.configurationService.getResourceInteger( AUTHENTICATED_RETROFIT_SERVER_TIMEOUT ) )
				.thenReturn( 3600 );
		Mockito.when( this.configurationService.getResourceInteger( AUTHENTICATED_RETROFIT_CACHE_SIZE, DEFAULT_CACHE_SIZE_1GB ) )
				.thenReturn( DEFAULT_CACHE_SIZE_1GB );
		Mockito.when( this.fileSystem.accessResource4Path( Mockito.anyString() ) ).thenReturn( "testfile" );
		Mockito.when( this.configurationService.getResourceBoolean( AUTHENTICATED_RETROFIT_CACHE_STATE ) )
				.thenReturn( true );
		Mockito.when( this.configurationService.getResourceString( CACHE_DIRECTORY_PATH ) )
				.thenReturn( "./" );
		Mockito.when( this.fileSystem.checkWritable( Mockito.anyString() ) ).thenReturn( true );

		Mockito.when( this.configurationService.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_SERVER_URL, DEFAULT_ESI_OAUTH_LOGIN_SERVER ) )
				.thenReturn( DEFAULT_ESI_OAUTH_LOGIN_SERVER );
		Mockito.when( this.configurationService.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_CLIENTID ) )
				.thenReturn( "-CLIENT-ID-" );
		Mockito.when( this.configurationService.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_SECRETKEY ) )
				.thenReturn( "-SECRET-KEY-" );
		Mockito.when( this.configurationService.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_CALLBACK ) )
				.thenReturn( "-CALLBACK-" );
		Mockito.when( this.configurationService.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_AGENT, DEFAULT_RETROFIT_AGENT ) )
				.thenReturn( DEFAULT_RETROFIT_AGENT );
		Mockito.when( this.configurationService.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_STATE ) )
				.thenReturn( "-STATE-" );

		// Test
		final RetrofitService retrofitService = this.getRetrofitService();
		final Retrofit obtained = retrofitService.accessAuthenticatedConnector( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertTrue( obtained instanceof Retrofit );
	}

	@AfterEach
	public void tearDown() {
		new File( "testfile" ).delete();
	}

	@Test
	public void accessUniverseConnector() {
		// When
		Mockito.when( this.configurationService.getResourceString( Mockito.anyString() ) ).thenReturn( "-CONFIGURATION-VALUE-" );
		Mockito.when( this.configurationService.getResourceString( Mockito.anyString(), Mockito.anyString() ) ).thenReturn( "http://localhost/" );
		// Test
		final RetrofitService retrofitService = this.getRetrofitService();
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
		final RetrofitService retrofitService = this.getRetrofitService();
		Assertions.assertNotNull( retrofitService );
	}

	private RetrofitService getRetrofitService() {
		return new RetrofitService( this.configurationService, this.fileSystem, this.credentialRepository );
	}
}