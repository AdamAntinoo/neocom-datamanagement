package org.dimensinfin.eveonline.neocom.provider;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.dimensinfin.annotation.LogEnterExit;
import org.dimensinfin.annotation.TimeElapsed;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.api.AssetsApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.CorporationApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.FittingsApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.IndustryApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.PlanetaryInteractionApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.StatusApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdFittings200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdMining200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdDivisionsOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetStatusOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniversePlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSchematicsSchematicIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.PostCharactersCharacterIdAssetsNames200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.PostCorporationsCorporationIdAssetsNames200Ok;
import org.dimensinfin.eveonline.neocom.service.IStoreCache;
import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.service.RetrofitService;
import org.dimensinfin.logging.LogWrapper;

import retrofit2.Response;

/**
 * This class will be the base to access most of the non authenticated SDE data available though the ESI data service.
 *
 * The new data service allows to access many endpoints with data that do not require pilot authentication. With this endpoints I
 * will try to remove
 * the required SDE database and remove the need to add that heavy resource to the application download when implemented in
 * Android.
 *
 * This class will also use other components to be able to store downloaded SDE data into caches, be them temporal in memory or
 * persisted on disk.
 */
public class ESIDataProvider extends ESIUniverseDataProvider {
	public static final String DEFAULT_ESI_SERVER = "Tranquility".toLowerCase();
	public static final String DEFAULT_ACCEPT_LANGUAGE = "en-us";
	protected static final String CREDENTIAL_LOG_LITERAL = "Credential: {0}";
	// - C O M P O N E N T S
	protected LocationCatalogService locationCatalogService;

	// - C O N S T R U C T O R S
	protected ESIDataProvider() {}


	@TimeElapsed
	public List<GetCharactersCharacterIdFittings200Ok> getCharactersCharacterIdFittings( final Credential credential ) {
		LogWrapper.enter( CREDENTIAL_LOG_LITERAL + credential.toString() );
		try {
			final Response<List<GetCharactersCharacterIdFittings200Ok>> fittingsResponse = this.retrofitService
					.accessAuthenticatedConnector( credential )
					.create( FittingsApi.class )
					.getCharactersCharacterIdFittings( credential.getAccountId(),
							credential.getDataSource(), null, null )
					.execute();
			if (fittingsResponse.isSuccessful()) return fittingsResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return new ArrayList<>();
	}

	/**
	 * This method encapsulates the call to the esi server to retrieve the current list of mining operations. This listing will
	 * contain the operations
	 * for the last 30 days. It will be internally cached during 1800 seconds so we have to check the hour change less
	 * frequently.
	 *
	 * @param credential the credential to be used when composing the ESI call.
	 * @return the list of mining actions performed during the last 30 days.
	 */
	@TimeElapsed
	public List<GetCharactersCharacterIdMining200Ok> getCharactersCharacterIdMining( final Credential credential ) {
		LogWrapper.enter( CREDENTIAL_LOG_LITERAL + credential.toString() );
		final List<GetCharactersCharacterIdMining200Ok> returnMiningList = new ArrayList<>( 1000 );
		try {
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetCharactersCharacterIdMining200Ok>> industryApiResponse = this.retrofitService
						.accessAuthenticatedConnector( credential )
						.create( IndustryApi.class )
						.getCharactersCharacterIdMining(
								credential.getAccountId(),
								credential.getDataSource().toLowerCase(), null,
								pageCounter, null )
						.execute();
				if (industryApiResponse.isSuccessful()) {
					// Copy the assets to the result list.
					returnMiningList.addAll( Objects.requireNonNull( industryApiResponse.body() ) );
					pageCounter++;
					// Check for out of page running.
					if (Objects.requireNonNull( industryApiResponse.body() ).isEmpty()) morePages = false;
				}
			}
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return returnMiningList;
	}

	@TimeElapsed
	public List<GetCharactersCharacterIdPlanets200Ok> getCharactersCharacterIdPlanets( final Credential credential ) {
		LogWrapper.enter( CREDENTIAL_LOG_LITERAL + credential.toString() );
		try {
			// Create the request to be returned so it can be called.
			final Response<List<GetCharactersCharacterIdPlanets200Ok>> planetaryApiResponse = this.retrofitService
					.accessAuthenticatedConnector( credential )
					.create( PlanetaryInteractionApi.class )
					.getCharactersCharacterIdPlanets(
							credential.getAccountId(),
							credential.getDataSource().toLowerCase(), null, null )
					.execute();
			if (planetaryApiResponse.isSuccessful()) return planetaryApiResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return new ArrayList<>();
	}

	@TimeElapsed
	public GetCharactersCharacterIdPlanetsPlanetIdOk getCharactersCharacterIdPlanetsPlanetId( final Integer planetId,
	                                                                                          final Credential credential ) {
		LogWrapper.enter( CREDENTIAL_LOG_LITERAL + credential.toString() );
		try {
			final Response<GetCharactersCharacterIdPlanetsPlanetIdOk> planetaryApiResponse = this.retrofitService
					.accessAuthenticatedConnector( credential )
					.create( PlanetaryInteractionApi.class )
					.getCharactersCharacterIdPlanetsPlanetId(
							credential.getAccountId(),
							planetId,
							credential.getDataSource().toLowerCase(), null )
					.execute();
			if (planetaryApiResponse.isSuccessful()) return planetaryApiResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return null;
	}


	@TimeElapsed
	public List<GetCorporationsCorporationIdAssets200Ok> getCorporationsCorporationIdAssets( final Credential credential,
	                                                                                         final Integer corporationId ) {
		LogWrapper.enter( CREDENTIAL_LOG_LITERAL + credential.toString() );
		final List<GetCorporationsCorporationIdAssets200Ok> returnAssetList = new ArrayList<>( 1000 );
		try {
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetCorporationsCorporationIdAssets200Ok>> assetsApiResponse = this.retrofitService
						.accessAuthenticatedConnector( credential )
						.create( AssetsApi.class )
						.getCorporationsCorporationIdAssets( corporationId,
								credential.getDataSource().toLowerCase(),
								null, pageCounter, null )
						.execute();
				if (assetsApiResponse.isSuccessful()) {
					// Copy the assets to the result list.
					returnAssetList.addAll( Objects.requireNonNull( assetsApiResponse.body() ) );
					pageCounter++;
					// Check for out of page running.
					if (Objects.requireNonNull( assetsApiResponse.body() ).isEmpty()) morePages = false;
				}
			}
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return returnAssetList;
	}

	@TimeElapsed
	@LogEnterExit
	public GetCorporationsCorporationIdDivisionsOk getCorporationsCorporationIdDivisions( final Integer corporationId, final Credential credential ) {
		LogWrapper.enter();
		try {
			final Response<GetCorporationsCorporationIdDivisionsOk> divisionsResponse = this.retrofitService
					.accessAuthenticatedConnector( credential )
					.create( CorporationApi.class )
					.getCorporationsCorporationIdDivisions(
							corporationId,
							credential.getDataSource().toLowerCase(),
							null, null )
					.execute();
			if (divisionsResponse.isSuccessful()) return divisionsResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return null;
	}

	@TimeElapsed
	public GetUniverseSchematicsSchematicIdOk getUniversePlanetarySchematicsById( final int schematicId ) {
		LogWrapper.enter( MessageFormat.format( "Schematic id: {0}", Integer.toString( schematicId ) ) );
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseSchematicsSchematicIdOk> schematicistResponse = this.retrofitService
					.accessUniverseConnector()
					.create( PlanetaryInteractionApi.class )
					.getUniverseSchematicsSchematicId(
							schematicId, DEFAULT_ESI_SERVER, null )
					.execute();
			if (schematicistResponse.isSuccessful()) return schematicistResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return null;
	}

	@TimeElapsed
	public GetUniversePlanetsPlanetIdOk getUniversePlanetsPlanetId( final int identifier ) {
		LogWrapper.enter( MessageFormat.format( "Planet identifier: {0}", Integer.toString( identifier ) ) );
		try {
			final Response<GetUniversePlanetsPlanetIdOk> universeApiResponse = this.retrofitService
					.accessUniverseConnector()
					.create( UniverseApi.class )
					.getUniversePlanetsPlanetId(
							identifier,
							DEFAULT_ESI_SERVER
									.toLowerCase(),
							null ).execute();
			if (universeApiResponse.isSuccessful()) return universeApiResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return null;
	}

	@TimeElapsed
	public GetStatusOk getUniverseStatus( final String server ) {
		LogWrapper.enter( MessageFormat.format( "Server: {0}", server ) );
		try {
			String datasource = DEFAULT_ESI_SERVER; // Set the server to the default or to the selected server.
			if (null != server) datasource = server;
			final Response<GetStatusOk> statusApiResponse = this.retrofitService
					.accessUniverseConnector()
					.create( StatusApi.class )
					.getStatus( datasource.toLowerCase(), null ).execute();
			if (statusApiResponse.isSuccessful()) return statusApiResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return null;
	}

	public List<PostCharactersCharacterIdAssetsNames200Ok> postCharactersCharacterIdAssetsNames( final List<Long> listItemIds,
	                                                                                             final Credential credential ) {
		LogWrapper.enter();
		try {
			final Response<List<PostCharactersCharacterIdAssetsNames200Ok>> assetsApiResponse = this.retrofitService
					.accessAuthenticatedConnector( credential )
					.create( AssetsApi.class )
					.postCharactersCharacterIdAssetsNames(
							credential.getAccountId(),
							listItemIds,
							credential.getDataSource().toLowerCase(),
							null )
					.execute();
			if (assetsApiResponse.isSuccessful()) return assetsApiResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return new ArrayList<>();
	}

	public List<PostCorporationsCorporationIdAssetsNames200Ok> postCorporationsCorporationIdAssetsNames( final List<Long> listItemIds,
	                                                                                                     final Credential credential ) {
		LogWrapper.enter();
		try {
			final Response<List<PostCorporationsCorporationIdAssetsNames200Ok>> assetsApiResponse = this.retrofitService
					.accessAuthenticatedConnector( credential )
					.create( AssetsApi.class )
					.postCorporationsCorporationIdAssetsNames(
							credential.getAccountId(),
							listItemIds,
							credential.getDataSource().toLowerCase(),
							null )
					.execute();
			if (assetsApiResponse.isSuccessful()) return assetsApiResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return new ArrayList<>();
	}

	// - B U I L D E R
	public static class Builder {
		private final ESIDataProvider onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new ESIDataProvider();
		}

		public ESIDataProvider build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			Objects.requireNonNull( this.onConstruction.fileSystemAdapter );
			Objects.requireNonNull( this.onConstruction.locationCatalogService );
			Objects.requireNonNull( this.onConstruction.storeCacheManager );
			Objects.requireNonNull( this.onConstruction.retrofitService );
			return this.onConstruction;
		}

		public ESIDataProvider.Builder withConfigurationProvider( final IConfigurationService configurationProvider ) {
			Objects.requireNonNull( configurationProvider );
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public ESIDataProvider.Builder withFileSystemAdapter( final IFileSystem fileSystemAdapter ) {
			Objects.requireNonNull( fileSystemAdapter );
			this.onConstruction.fileSystemAdapter = fileSystemAdapter;
			return this;
		}

		public ESIDataProvider.Builder withLocationCatalogService( final LocationCatalogService locationCatalogService ) {
			Objects.requireNonNull( locationCatalogService );
			this.onConstruction.locationCatalogService = locationCatalogService;
			return this;
		}

		public ESIDataProvider.Builder withRetrofitFactory( final RetrofitService retrofitService ) {
			Objects.requireNonNull( retrofitService );
			this.onConstruction.retrofitService = retrofitService;
			return this;
		}

		public ESIDataProvider.Builder withStoreCacheManager( final IStoreCache storeCacheManager ) {
			Objects.requireNonNull( storeCacheManager );
			this.onConstruction.storeCacheManager = storeCacheManager;
			return this;
		}
	}
}
