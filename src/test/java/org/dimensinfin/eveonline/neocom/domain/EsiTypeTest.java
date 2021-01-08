package org.dimensinfin.eveonline.neocom.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_CATEGORY_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_GROUP_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_HULLGROUP_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_INDUSTRYGROUP_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_TECH;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_TYPEICON_URL;

public class EsiTypeTest {

	private GetUniverseTypesTypeIdOk type;
	private GetUniverseGroupsGroupIdOk group;
	private GetUniverseCategoriesCategoryIdOk category;

	@BeforeEach
	public void beforeEach() {
		this.type = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		this.group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		this.category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
	}

	@Test
	public void buildContract() {
		// Test
		final EsiType esiType = new EsiType.Builder()
				.withTypeId( TEST_ESITYPE_ID )
				.withItemType( this.type )
				.withGroup( this.group )
				.withCategory( this.category )
				.build();
		// Assertions
		Assertions.assertNotNull( esiType );
	}

	@Test
	public void buildContractFailureMissing() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			new EsiType.Builder()
					.withItemType( this.type )
					.withGroup( this.group )
					.withCategory( this.category )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new EsiType.Builder()
					.withTypeId( TEST_ESITYPE_ID )
					.withGroup( this.group )
					.withCategory( this.category )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new EsiType.Builder()
					.withTypeId( TEST_ESITYPE_ID )
					.withItemType( this.type )
					.withCategory( this.category )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new EsiType.Builder()
					.withTypeId( TEST_ESITYPE_ID )
					.withItemType( this.type )
					.withGroup( this.group )
					.build();
		} );
	}

	@Test
	public void buildContractFailureNull() {
		// Given
		final GetUniverseTypesTypeIdOk type = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new EsiType.Builder()
					.withTypeId( null )
					.withItemType( this.type )
					.withGroup( this.group )
					.withCategory( this.category )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new EsiType.Builder()
					.withTypeId( TEST_ESITYPE_ID )
					.withItemType( null )
					.withGroup( this.group )
					.withCategory( this.category )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new EsiType.Builder()
					.withTypeId( TEST_ESITYPE_ID )
					.withItemType( this.type )
					.withGroup( null )
					.withCategory( this.category )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new EsiType.Builder()
					.withTypeId( TEST_ESITYPE_ID )
					.withItemType( this.type )
					.withGroup( this.group )
					.withCategory( null )
					.build();
		} );
	}

	@Test
	public void getterContract() {
		// When
		Mockito.when( this.type.getName() ).thenReturn( TEST_ESITYPE_NAME );
		Mockito.when( this.group.getName() ).thenReturn( TEST_ESITYPE_GROUP_NAME );
		Mockito.when( this.category.getName() ).thenReturn( TEST_ESITYPE_CATEGORY_NAME );
		// Test
		final EsiType esiType = new EsiType.Builder()
				.withTypeId( TEST_ESITYPE_ID )
				.withItemType( this.type )
				.withGroup( this.group )
				.withCategory( this.category )
				.build();
		// Assertions
		Assertions.assertEquals( TEST_ESITYPE_ID, esiType.getTypeId() );
		Assertions.assertEquals( TEST_ESITYPE_NAME, esiType.getName() );
		Assertions.assertEquals( TEST_ESITYPE_GROUP_NAME, esiType.getGroupName() );
		Assertions.assertEquals( TEST_ESITYPE_CATEGORY_NAME, esiType.getCategoryName() );
		Assertions.assertEquals( TEST_ESITYPE_TYPEICON_URL, esiType.getTypeIconURL() );
		Assertions.assertEquals( TEST_ESITYPE_INDUSTRYGROUP_NAME, esiType.getIndustryGroup() );
		Assertions.assertEquals( TEST_ESITYPE_HULLGROUP_NAME, esiType.getHullGroup() );
		Assertions.assertEquals( TEST_ESITYPE_TECH, esiType.getTech() );
		Assertions.assertNotNull( esiType.getType() );
		Assertions.assertNotNull( esiType.getGroup() );
		Assertions.assertNotNull( esiType.getCategory() );
	}
}