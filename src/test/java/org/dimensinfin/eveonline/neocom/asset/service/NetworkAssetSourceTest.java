package org.dimensinfin.eveonline.neocom.asset.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.service.ESIDataService;
import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.database.repositories.CredentialRepository;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;

public class NetworkAssetSourceTest {
	private static final Integer TEST_CORPORATION_ID = 98384726;

	@Test
	public void buildComplete() {
		final Credential credential = Mockito.mock( Credential.class );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		final CredentialRepository credentialRepository = Mockito.mock( CredentialRepository.class );
		final ESIDataService esiDataProvider = Mockito.mock( ESIDataService.class );
		final LocationCatalogService locationCatalogService = Mockito.mock( LocationCatalogService.class );
		final NetworkAssetSource networkAssetSource = new NetworkAssetSource.Builder()
				.withCredential( credential )
				.withAssetRepository( assetRepository )
				.withCredentialRepository( credentialRepository )
				.withEsiDataProvider( esiDataProvider )
				.withLocationCatalogService( locationCatalogService )
				.build();
		Assertions.assertNotNull( networkAssetSource );
	}

	@Test
	public void buildFailure() {
		final Credential credential = Mockito.mock( Credential.class );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		final CredentialRepository credentialRepository = Mockito.mock( CredentialRepository.class );
		final ESIDataService esiDataProvider = Mockito.mock( ESIDataService.class );
		final LocationCatalogService locationCatalogService = Mockito.mock( LocationCatalogService.class );
		Assertions.assertThrows( NullPointerException.class, () -> {
					final NetworkAssetSource networkAssetSource = new NetworkAssetSource.Builder()
							.withCredential( null )
							.withAssetRepository( assetRepository )
							.withCredentialRepository( credentialRepository )
							.withEsiDataProvider( esiDataProvider )
							.withLocationCatalogService( locationCatalogService )
							.build();
				},
				"Expected NetworkAssetSource.Builder() to throw null verification, but it didn't." );
		Assertions.assertThrows( NullPointerException.class, () -> {
					final NetworkAssetSource networkAssetSource = new NetworkAssetSource.Builder()
							.withCredential( credential )
							.withAssetRepository( null )
							.withCredentialRepository( credentialRepository )
							.withEsiDataProvider( esiDataProvider )
							.withLocationCatalogService( locationCatalogService )
							.build();
				},
				"Expected NetworkAssetSource.Builder() to throw null verification, but it didn't." );
		Assertions.assertThrows( NullPointerException.class, () -> {
					final NetworkAssetSource networkAssetSource = new NetworkAssetSource.Builder()
							.withCredential( credential )
							.withAssetRepository( assetRepository )
							.withCredentialRepository( null )
							.withEsiDataProvider( esiDataProvider )
							.withLocationCatalogService( locationCatalogService )
							.build();
				},
				"Expected NetworkAssetSource.Builder() to throw null verification, but it didn't." );
		Assertions.assertThrows( NullPointerException.class, () -> {
					final NetworkAssetSource networkAssetSource = new NetworkAssetSource.Builder()
							.withCredential( credential )
							.withAssetRepository( assetRepository )
							.withCredentialRepository( credentialRepository )
							.withEsiDataProvider( null )
							.withLocationCatalogService( locationCatalogService )
							.build();
				},
				"Expected NetworkAssetSource.Builder() to throw null verification, but it didn't." );
		Assertions.assertThrows( NullPointerException.class, () -> {
					final NetworkAssetSource networkAssetSource = new NetworkAssetSource.Builder()
							.withCredential( credential )
							.withAssetRepository( assetRepository )
							.withCredentialRepository( credentialRepository )
							.withEsiDataProvider( esiDataProvider )
							.withLocationCatalogService( null )
							.build();
				},
				"Expected NetworkAssetSource.Builder() to throw null verification, but it didn't." );
		Assertions.assertThrows( NullPointerException.class, () -> {
					final NetworkAssetSource networkAssetSource = new NetworkAssetSource.Builder()
							.withAssetRepository( assetRepository )
							.withCredentialRepository( credentialRepository )
							.withEsiDataProvider( esiDataProvider )
							.withLocationCatalogService( locationCatalogService )
							.build();
				},
				"Expected NetworkAssetSource.Builder() to throw null verification, but it didn't." );
		Assertions.assertThrows( NullPointerException.class, () -> {
					final NetworkAssetSource networkAssetSource = new NetworkAssetSource.Builder()
							.withCredential( credential )
							.withAssetRepository( assetRepository )
							.withEsiDataProvider( esiDataProvider )
							.withLocationCatalogService( locationCatalogService )
							.build();
				},
				"Expected NetworkAssetSource.Builder() to throw null verification, but it didn't." );
	}

	@Test
	public void findAllByCorporationId() {
		final Integer corporationId = TEST_CORPORATION_ID;
		final Credential credential = Mockito.mock( Credential.class );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		final CredentialRepository credentialRepository = Mockito.mock( CredentialRepository.class );
		final ESIDataService esiDataProvider = Mockito.mock( ESIDataService.class );
		final LocationCatalogService locationCatalogService = Mockito.mock( LocationCatalogService.class );
		final AssetDownloadProcessorJob assetDownloadProcessorJob = Mockito.mock( AssetDownloadProcessorJob.class );
		Mockito.when( assetDownloadProcessorJob.downloadCorporationAssets( Mockito.anyInt() ) ).thenReturn( new ArrayList<>() );
//		final AssetDownloadProcessorJob assetDownloadProcessorJobSpy = Mockito.spy( assetDownloadProcessorJob );
		final NetworkAssetSource networkAssetSource = new NetworkAssetSource.Builder()
				.withCredential( credential )
				.withAssetRepository( assetRepository )
				.withCredentialRepository( credentialRepository )
				.withEsiDataProvider( esiDataProvider )
				.withLocationCatalogService( locationCatalogService )
				.build();
		Assertions.assertNotNull( networkAssetSource );

		final List<NeoAsset> assetList = networkAssetSource
				.findAllByCorporationId( corporationId );
//		Mockito.verify( assetDownloadProcessorJobSpy, Mockito.times( 1 ) ).downloadCorporationAssets( Mockito.anyInt() );
		Assertions.assertNotNull( assetList );
		Assertions.assertEquals( 0, assetList.size() );
	}
}
