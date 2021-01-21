package org.dimensinfin.eveonline.neocom.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.domain.EsiType;
import org.dimensinfin.eveonline.neocom.domain.Resource;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.ResourceConstants.TEST_RESOURCE_CATEGORY;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.ResourceConstants.TEST_RESOURCE_GROUP;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.ResourceConstants.TEST_RESOURCE_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.ResourceConstants.TEST_RESOURCE_QUANTITY;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.ResourceConstants.TEST_RESOURCE_TYPE_ID;

public class ResourceFactoryTest {
	private ESIDataService esiDataService;

	@BeforeEach
	public void beforeEach() {
		this.esiDataService = Mockito.mock(ESIDataService.class);
	}

	@Test
	public void constructorContract() {
		final ResourceFactory resourceFactory=new ResourceFactory(this.esiDataService);
		Assertions.assertNotNull(resourceFactory);
	}

	@Test
	public void generateResource4Id() {
		// Given
		final GetUniverseTypesTypeIdOk esiItem =Mockito.mock(GetUniverseTypesTypeIdOk.class);
		final GetUniverseGroupsGroupIdOk esiGroup=Mockito.mock(GetUniverseGroupsGroupIdOk.class);
		final GetUniverseCategoriesCategoryIdOk esiCategory=Mockito.mock(GetUniverseCategoriesCategoryIdOk.class);
		// When
		Mockito.when( this.esiDataService.searchEsiItem4Id(Mockito.anyInt()) ).thenReturn( esiItem );
		Mockito.when( this.esiDataService.searchItemGroup4Id(Mockito.anyInt()) ).thenReturn(esiGroup);
		Mockito.when( this.esiDataService.searchItemCategory4Id(Mockito.anyInt()) ).thenReturn( esiCategory );
		Mockito.when( esiItem.getTypeId() ).thenReturn(TEST_RESOURCE_TYPE_ID  );
		Mockito.when( esiItem.getName() ).thenReturn(TEST_RESOURCE_NAME  );
		Mockito.when( esiGroup.getName() ).thenReturn(TEST_RESOURCE_GROUP  );
		Mockito.when( esiCategory.getName() ).thenReturn(TEST_RESOURCE_CATEGORY  );
		// Test
		final ResourceFactory resourceFactory=new ResourceFactory(this.esiDataService);
		final Resource obtained = resourceFactory.generateResource4Id( TEST_RESOURCE_TYPE_ID , TEST_RESOURCE_QUANTITY);
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( TEST_RESOURCE_TYPE_ID, obtained.getTypeId());
		Assertions.assertEquals( TEST_RESOURCE_NAME, obtained.getName());
		Assertions.assertEquals( TEST_RESOURCE_GROUP, obtained.getGroupName());
		Assertions.assertEquals( TEST_RESOURCE_CATEGORY, obtained.getCategoryName());
		Assertions.assertEquals( TEST_RESOURCE_QUANTITY, obtained.getQuantity());
	}

	@Test
	public void generateType4Id() {
		// Given
		final GetUniverseTypesTypeIdOk esiItem =Mockito.mock(GetUniverseTypesTypeIdOk.class);
		final GetUniverseGroupsGroupIdOk esiGroup=Mockito.mock(GetUniverseGroupsGroupIdOk.class);
		final GetUniverseCategoriesCategoryIdOk esiCategory=Mockito.mock(GetUniverseCategoriesCategoryIdOk.class);
		// When
		Mockito.when( this.esiDataService.searchEsiItem4Id(Mockito.anyInt()) ).thenReturn( esiItem );
		Mockito.when( this.esiDataService.searchItemGroup4Id(Mockito.anyInt()) ).thenReturn(esiGroup);
		Mockito.when( this.esiDataService.searchItemCategory4Id(Mockito.anyInt()) ).thenReturn( esiCategory );
		Mockito.when( esiItem.getTypeId() ).thenReturn(TEST_RESOURCE_TYPE_ID  );
		Mockito.when( esiItem.getName() ).thenReturn(TEST_RESOURCE_NAME  );
		Mockito.when( esiGroup.getName() ).thenReturn(TEST_RESOURCE_GROUP  );
		Mockito.when( esiCategory.getName() ).thenReturn(TEST_RESOURCE_CATEGORY  );
		// Test
		final ResourceFactory resourceFactory=new ResourceFactory(this.esiDataService);
		final EsiType obtained = resourceFactory.generateType4Id( TEST_RESOURCE_TYPE_ID );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( TEST_RESOURCE_TYPE_ID, obtained.getTypeId());
		Assertions.assertEquals( TEST_RESOURCE_NAME, obtained.getName());
		Assertions.assertEquals( TEST_RESOURCE_GROUP, obtained.getGroupName());
		Assertions.assertEquals( TEST_RESOURCE_CATEGORY, obtained.getCategoryName());
	}
}