package org.dimensinfin.eveonline.neocom.adapter;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.service.RetrofitService;

import io.reactivex.Single;

public class StoreCacheManagerTest {
	private IConfigurationService configurationProvider;
	private IFileSystem fileSystemAdapter;
	private RetrofitService retrofitService;
	private StoreCacheManager storeCacheManager4test;

	//	@Test
	public void accessCategory() throws InterruptedException {
		final Single<GetUniverseCategoriesCategoryIdOk> categorySingle = this.storeCacheManager4test
				.accessCategory( 4 );

		Assertions.assertNotNull( categorySingle );
		Assertions.assertNotNull( categorySingle.blockingGet() );

		final GetUniverseCategoriesCategoryIdOk category = categorySingle.blockingGet();
		Assertions.assertNotNull( category );
		Assertions.assertEquals( 4, category.getCategoryId().intValue() );
		Assertions.assertEquals( "Material", category.getName() );
	}

	//	@Test
	public void accessGroup() throws InterruptedException {
		final Single<GetUniverseGroupsGroupIdOk> groupSingle = this.storeCacheManager4test.accessGroup( 18 );

		Assertions.assertNotNull( groupSingle );
		Assertions.assertNotNull( groupSingle.blockingGet() );

		final GetUniverseGroupsGroupIdOk group = groupSingle.blockingGet();
		Assertions.assertNotNull( group );
		Assertions.assertEquals( 18, group.getGroupId().intValue() );
		Assertions.assertEquals( "Mineral", group.getName() );
	}

	//	@Test
	public void accessItem() {
		// Test
		final StoreCacheManager storeCacheManager = new StoreCacheManager.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withRetrofitFactory( retrofitService )
				.build();
		final Single<GetUniverseTypesTypeIdOk> itemSingle = storeCacheManager.accessItem( 34 );

		Assertions.assertNotNull( itemSingle );
		Assertions.assertNotNull( itemSingle.blockingGet() );

		final GetUniverseTypesTypeIdOk item = itemSingle.blockingGet();
		Assertions.assertNotNull( item );
		Assertions.assertEquals( 34, item.getTypeId().intValue() );
		Assertions.assertEquals( "Tritanium", item.getName() );
	}

	@BeforeEach
	public void beforeEach() {
		this.configurationProvider = Mockito.mock( IConfigurationService.class );
		this.fileSystemAdapter = Mockito.mock( IFileSystem.class );
		this.retrofitService = Mockito.mock( RetrofitService.class );
		// Test
		this.storeCacheManager4test = new StoreCacheManager.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withRetrofitFactory( this.retrofitService )
				.build();
	}

//	@Test
	public void buildContract() throws IOException {
		// Given
		final String resourcePath = "./mock-resource.txt";
		// When
		Mockito.when( this.configurationProvider.getResourceString( Mockito.anyString() ) ).thenReturn( "-CONFIGURATIONS-TEST-" );
		Mockito.when( this.fileSystemAdapter.accessResource4Path( Mockito.anyString() ) ).thenReturn( resourcePath );
		// Test
		final StoreCacheManager storeCacheManager = new StoreCacheManager.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withRetrofitFactory( this.retrofitService )
				.build();
		// Assertions
		Assertions.assertNotNull( storeCacheManager );
	}
}
