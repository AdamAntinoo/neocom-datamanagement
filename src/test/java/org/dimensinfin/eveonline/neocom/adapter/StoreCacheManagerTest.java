package org.dimensinfin.eveonline.neocom.adapter;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;
import org.dimensinfin.eveonline.neocom.support.TestConfigurationService;
import org.dimensinfin.eveonline.neocom.support.SupportFileSystem;

import io.reactivex.Single;

public class StoreCacheManagerTest {
	private StoreCacheManager storeCacheManager4test;
	private IConfigurationService configurationProvider;
	private IFileSystem fileSystemAdapter;

	@Before
	public void setUp() throws Exception {
		this.configurationProvider = new TestConfigurationService.Builder()
				.optionalPropertiesDirectory( "/src/test/resources/properties.unittest" )
				.build();
		this.fileSystemAdapter = new SupportFileSystem.Builder()
				.optionalApplicationDirectory( "./src/test/NeoCom.UnitTest" )
				.build();
		final RetrofitFactory retrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( configurationProvider )
				.withFileSystemAdapter( fileSystemAdapter )
				.build();
		this.storeCacheManager4test = new StoreCacheManager.Builder()
				.withConfigurationProvider( configurationProvider )
				.withFileSystemAdapter( fileSystemAdapter )
				.withRetrofitFactory( retrofitFactory )
				.build();
	}

	@Test
	public void buildComplete() throws IOException {
//		final IConfigurationService configurationProvider = new SupportConfigurationProvider.Builder().build();
//		final IFileSystem fileSystemAdapter = new SupportFileSystem.Builder()
//				.optionalApplicationDirectory( "./src/test/NeoCom.UnitTest" )
//				.build();
		final RetrofitFactory retrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.build();
		final StoreCacheManager storeCacheManager = new StoreCacheManager.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withRetrofitFactory( retrofitFactory )
				.build();
		Assert.assertNotNull( storeCacheManager );
	}

	@Test
	public void accessItem() {
		final Single<GetUniverseTypesTypeIdOk> itemSingle = this.storeCacheManager4test.accessItem( 34 );

		Assert.assertNotNull( itemSingle );
		Assert.assertNotNull( itemSingle.blockingGet() );

		final GetUniverseTypesTypeIdOk item = itemSingle.blockingGet();
		Assert.assertNotNull( item );
		Assert.assertEquals( 34, item.getTypeId().intValue() );
		Assert.assertEquals( "Tritanium", item.getName() );
	}

	@Test
	public void accessGroup() throws InterruptedException {
		final Single<GetUniverseGroupsGroupIdOk> groupSingle = this.storeCacheManager4test.accessGroup( 18 );

		Assert.assertNotNull( groupSingle );
		Assert.assertNotNull( groupSingle.blockingGet() );

		final GetUniverseGroupsGroupIdOk group = groupSingle.blockingGet();
		Assert.assertNotNull( group );
		Assert.assertEquals( 18, group.getGroupId().intValue() );
		Assert.assertEquals( "Mineral", group.getName() );
	}

	@Test
	public void accessCategory() throws InterruptedException {
		final Single<GetUniverseCategoriesCategoryIdOk> categorySingle = this.storeCacheManager4test
				.accessCategory( 4 );

		Assert.assertNotNull( categorySingle );
		Assert.assertNotNull( categorySingle.blockingGet() );

		final GetUniverseCategoriesCategoryIdOk category = categorySingle.blockingGet();
		Assert.assertNotNull( category );
		Assert.assertEquals( 4, category.getCategoryId().intValue() );
		Assert.assertEquals( "Material", category.getName() );
	}
}
