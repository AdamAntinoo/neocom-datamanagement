package org.dimensinfin.eveonline.neocom.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.utility.GlobalWideConstants;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_CATEGORY_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_GROUP_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_HULLGROUP_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_INDUSTRYGROUP_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_TECH;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_TYPEICON_URL;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_VOLUME;

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
	public void equalsContract() {
		EqualsVerifier.forClass( EsiType.class )
				.suppress( Warning.NONFINAL_FIELDS )
				.withIgnoredFields( "type", "group", "category" )
				.usingGetClass().verify();
	}

	@Test
	public void getterContract() {
		// When
		Mockito.when( this.type.getName() ).thenReturn( TEST_ESITYPE_NAME );
		Mockito.when( this.group.getName() ).thenReturn( TEST_ESITYPE_GROUP_NAME );
		Mockito.when( this.category.getName() ).thenReturn( TEST_ESITYPE_CATEGORY_NAME );
		Mockito.when( this.type.getVolume() ).thenReturn( TEST_ESITYPE_VOLUME );
		// Test
		EsiType esiType = new EsiType.Builder()
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
		Assertions.assertEquals( (double) TEST_ESITYPE_VOLUME, esiType.getVolume(), 0.01 );
		Assertions.assertNotNull( esiType.getType() );
		Assertions.assertNotNull( esiType.getGroup() );
		Assertions.assertNotNull( esiType.getCategory() );
		// Industry group
		Mockito.when( this.category.getName() ).thenReturn( "Ship" );
		esiType = new EsiType.Builder()
				.withTypeId( TEST_ESITYPE_ID )
				.withItemType( this.type )
				.withGroup( this.group )
				.withCategory( this.category )
				.build();
		Assertions.assertEquals( IndustryGroup.HULL, esiType.getIndustryGroup() );
	}

	@Test
	public void isBlueprintFalse() {
		// When
		Mockito.when( this.type.getName() ).thenReturn( TEST_ESITYPE_NAME );
		Mockito.when( this.group.getName() ).thenReturn( TEST_ESITYPE_GROUP_NAME );
		Mockito.when( this.category.getName() ).thenReturn( TEST_ESITYPE_CATEGORY_NAME );
		Mockito.when( this.type.getVolume() ).thenReturn( TEST_ESITYPE_VOLUME );
		// Test
		final EsiType esiType = new EsiType.Builder()
				.withTypeId( TEST_ESITYPE_ID )
				.withItemType( this.type )
				.withGroup( this.group )
				.withCategory( this.category )
				.build();
		Assertions.assertNotNull( esiType );
		Assertions.assertFalse( esiType.isBlueprint() );
	}

	@Test
	public void isBlueprintTrue() {
		// When
		Mockito.when( this.type.getName() ).thenReturn( TEST_ESITYPE_NAME );
		Mockito.when( this.group.getName() ).thenReturn( TEST_ESITYPE_GROUP_NAME );
		Mockito.when( this.category.getName() ).thenReturn( GlobalWideConstants.EveGlobal.BLUEPRINT );
		Mockito.when( this.type.getVolume() ).thenReturn( TEST_ESITYPE_VOLUME );
		// Test
		final EsiType esiType = new EsiType.Builder()
				.withTypeId( TEST_ESITYPE_ID )
				.withItemType( this.type )
				.withGroup( this.group )
				.withCategory( this.category )
				.build();
		Assertions.assertNotNull( esiType );
		Assertions.assertTrue( esiType.isBlueprint() );
	}

	//	@Test
	public void toStringContract() {
		// Test
		final EsiType esiType = new EsiType.Builder()
				.withTypeId( TEST_ESITYPE_ID )
				.withItemType( this.type )
				.withGroup( this.group )
				.withCategory( this.category )
				.build();
		final String expected = "{\"typeId\":11535,\"industryGroup\":\"UNDEFINED\",\"type\":\"Mock for GetUniverseTypesTypeIdOk, hashCode: 1330500972\",\"group\":\"Mock for GetUniverseGroupsGroupIdOk, hashCode: 964740518\",\"category\":\"Mock for GetUniverseCategoriesCategoryIdOk, hashCode: 267794346\"}";
		final String obtained = esiType.toString();
		// Assertions
		Assertions.assertEquals( expected, obtained );
	}
}