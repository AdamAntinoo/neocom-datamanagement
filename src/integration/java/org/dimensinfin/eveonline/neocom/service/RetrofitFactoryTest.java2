package org.dimensinfin.eveonline.neocom.provider;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.support.TestConfigurationService;
import org.dimensinfin.eveonline.neocom.support.SBFileSystemAdapter;

import retrofit2.Retrofit;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.BACKEND_RETROFIT_CACHE_FILE_NAME;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.CACHE_DIRECTORY_PATH;

public class RetrofitFactoryTest {
	private TestConfigurationService configurationProvider;
	private IFileSystem fileSystemAdapter;

	@Test
	public void accessAuthenticatedConnectorFailure() {
		this.configurationProvider.setProperty( CACHE_DIRECTORY_PATH, "-INVALID-PATH-/.." );
		final Credential credential = Mockito.mock( Credential.class );
		final RetrofitFactory retrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.build();
		Assertions.assertThrows( NeoComRuntimeException.class, () -> {
			final Retrofit connector = retrofitFactory.accessAuthenticatedConnector( credential );
		} );
	}

	@Test
	public void accessAuthenticatedConnectorSucess() throws IOException {
		this.configurationProvider.setProperty( BACKEND_RETROFIT_CACHE_FILE_NAME, "TestFile.delete" );
		final Credential credential = Mockito.mock( Credential.class );
		final RetrofitFactory retrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.build();
		Mockito.when( credential.getUniqueCredential() ).thenReturn( "tranquility/98876543" );
		Mockito.when( credential.getDataSource() ).thenReturn( "tranquility" );

		final Retrofit connector = retrofitFactory.accessAuthenticatedConnector( credential );
		Assertions.assertNotNull( connector );
	}

	@Test
	public void accessBackendConnectorFailure() {
		this.configurationProvider.setProperty( CACHE_DIRECTORY_PATH, "-INVALID-PATH-/.." );
		final RetrofitFactory retrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.build();
		Assertions.assertThrows( NeoComRuntimeException.class, () -> {
			final Retrofit connector = retrofitFactory.accessBackendConnector();
		} );
	}

	@Test
	public void accessBackendConnectorSuccess() {
		this.configurationProvider.setProperty( BACKEND_RETROFIT_CACHE_FILE_NAME, "TestFile.delete" );
		final RetrofitFactory retrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.build();

		final Retrofit connector = retrofitFactory.accessBackendConnector();
		Assertions.assertNotNull( connector );
	}

	@Test
	public void accessUniverseConnectorFailure() {
		this.configurationProvider.setProperty( CACHE_DIRECTORY_PATH, "-INVALID-PATH-/.." );
		final RetrofitFactory retrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.build();
		Assertions.assertThrows( NeoComRuntimeException.class, () -> {
			final Retrofit connector = retrofitFactory.accessUniverseConnector();
		} );
	}

	@Test
	public void accessUniverseConnectorSuccess() {
		this.configurationProvider.setProperty( BACKEND_RETROFIT_CACHE_FILE_NAME, "TestFile.delete" );
		final RetrofitFactory retrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.build();

		final Retrofit connector = retrofitFactory.accessUniverseConnector();
		Assertions.assertNotNull( connector );
	}

	@BeforeEach
	public void beforeEach() {
		this.configurationProvider = new TestConfigurationService.Builder()
				.optionalPropertiesDirectory( "/src/test/resources/properties.unittest" )
				.build();
		this.fileSystemAdapter = new SBFileSystemAdapter.Builder()
				.optionalApplicationDirectory( "./out/test/NeoCom.UnitTest/" )
				.build();
	}
}
