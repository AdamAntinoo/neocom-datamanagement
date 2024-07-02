package org.dimensinfin.eveonline.neocom.adapter.httpclient;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.repositories.CredentialRepository;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.service.RetrofitService;

import retrofit2.Retrofit;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.AUTHENTICATED_RETROFIT_CACHE_STATE;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.UNIVERSE_RETROFIT_CACHE_STATE;

public class RetrofitFactory extends RetrofitService {
	private static final String UNIVERSE_CONNECTOR_IDENTIER = "UNIVERSE";
	private final IConfigurationService configurationProvider;
	private final IFileSystem fileSystemAdapter;
	private final CredentialRepository credentialRepository;

	protected final Map<String, Retrofit> connectors = new HashMap<>();

	@Inject
	public RetrofitFactory( final IConfigurationService configurationProvider,
	                        final IFileSystem fileSystemAdapter,
	                        final CredentialRepository credentialRepository ) {
		super ( configurationProvider, fileSystemAdapter);
		this.configurationProvider = configurationProvider;
		this.fileSystemAdapter = fileSystemAdapter;
		this.credentialRepository = credentialRepository;
	}

	public Retrofit accessUniverseConnector() {
		return this.connectors.getOrDefault( UNIVERSE_CONNECTOR_IDENTIER, this.createUniverseConnector() );
	}

	public Retrofit accessAuthenticatedConnector( final Credential credential ) {
		return this.connectors.getOrDefault( credential.getUniqueCredential(), this.createSecuredConnector( credential ) );
	}

	private Retrofit createUniverseConnector() {
		final Retrofit connector = UpgradedRetrofitService.builder()
				.withConfiguration( new RetrofitConfiguration( this.configurationProvider, this.fileSystemAdapter, UNIVERSE_RETROFIT_CACHE_STATE ) )
				.build()
				.accessUniverseConnector();
		this.connectors.put( UNIVERSE_CONNECTOR_IDENTIER, connector );
		return connector;
	}

	private Retrofit createSecuredConnector( final Credential credential ) {
		final Retrofit connector = UpgradedRetrofitService.builder()
				.withConfiguration(
						new RetrofitConfiguration( this.configurationProvider, this.fileSystemAdapter, AUTHENTICATED_RETROFIT_CACHE_STATE )
				)
				.withCredentialPersistenceFunction( ( Credential cred ) -> {
					try {
						return this.credentialRepository.persist( cred );
					} catch (SQLException e) {
						throw new RuntimeException( e );
					}
				} )
				.build()
				.accessAuthenticatedConnector( credential );
		this.connectors.put( credential.getUniqueCredential(), connector );
		return connector;
	}
}
