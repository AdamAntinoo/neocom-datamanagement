package org.dimensinfin.eveonline.neocom.asset.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdAssets200Ok;

public class EsiAssets200OkTest {
	@Test
	public void buildCompleteCharacter() {
		final EsiAssets200Ok esiAssets200Ok = new EsiAssets200Ok.Builder()
				.withItemId( 12345L )
				.withTypeId( 34 )
				.withLocationFlag( GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.ASSETSAFETY )
				.withLocationType( GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.SOLAR_SYSTEM )
				.withIsBlueprintCopy( true )
				.withIsSingleton( false )
				.withLocationId( 7654321L )
				.withQuantity( 100 )
				.build();
		Assertions.assertNotNull( esiAssets200Ok );
	}

	@Test
	public void buildCompleteCorporation() {
		final EsiAssets200Ok esiAssets200Ok = new EsiAssets200Ok.Builder()
				.withItemId( 12345L )
				.withTypeId( 34 )
				.withLocationFlag( GetCorporationsCorporationIdAssets200Ok.LocationFlagEnum.ASSETSAFETY )
				.withLocationType( GetCorporationsCorporationIdAssets200Ok.LocationTypeEnum.SOLAR_SYSTEM )
				.withIsBlueprintCopy( true )
				.withIsSingleton( false )
				.withLocationId( 7654321L )
				.withQuantity( 100 )
				.build();
		Assertions.assertNotNull( esiAssets200Ok );
	}

	@Test
	public void buildFailure() {
		Assertions.assertThrows( NullPointerException.class, () -> {
					final EsiAssets200Ok esiAssets200Ok = new EsiAssets200Ok.Builder()
							.withItemId( null )
							.withTypeId( 34 )
							.withLocationFlag( GetCorporationsCorporationIdAssets200Ok.LocationFlagEnum.ASSETSAFETY )
							.withLocationType( GetCorporationsCorporationIdAssets200Ok.LocationTypeEnum.SOLAR_SYSTEM )
							.withIsBlueprintCopy( true )
							.withIsSingleton( false )
							.withLocationId( 7654321L )
							.withQuantity( 100 )
							.build();
				},
				"Expected EsiAssets200Ok.Builder() to throw null verification, but it didn't." );
		Assertions.assertThrows( NullPointerException.class, () -> {
					final EsiAssets200Ok esiAssets200Ok = new EsiAssets200Ok.Builder()
							.withItemId( 12345L )
							.withTypeId( null )
							.withLocationFlag( GetCorporationsCorporationIdAssets200Ok.LocationFlagEnum.ASSETSAFETY )
							.withLocationType( GetCorporationsCorporationIdAssets200Ok.LocationTypeEnum.SOLAR_SYSTEM )
							.withIsBlueprintCopy( true )
							.withIsSingleton( false )
							.withLocationId( 7654321L )
							.withQuantity( 100 )
							.build();
				},
				"Expected EsiAssets200Ok.Builder() to throw null verification, but it didn't." );
		Assertions.assertThrows( NullPointerException.class, () -> {
					final EsiAssets200Ok esiAssets200Ok = new EsiAssets200Ok.Builder()
							.withItemId( 12345L )
							.withTypeId( 34 )
							.withLocationFlag( GetCorporationsCorporationIdAssets200Ok.LocationFlagEnum.ASSETSAFETY )
							.withLocationType( GetCorporationsCorporationIdAssets200Ok.LocationTypeEnum.SOLAR_SYSTEM )
							.withIsBlueprintCopy( true )
							.withIsSingleton( false )
							.withLocationId( null )
							.withQuantity( 100 )
							.build();
				},
				"Expected EsiAssets200Ok.Builder() to throw null verification, but it didn't." );
		Assertions.assertThrows( NullPointerException.class, () -> {
					final EsiAssets200Ok esiAssets200Ok = new EsiAssets200Ok.Builder()
							.withItemId( 12345L )
							.withTypeId( 34 )
							.withLocationFlag( GetCorporationsCorporationIdAssets200Ok.LocationFlagEnum.ASSETSAFETY )
							.withLocationType( GetCorporationsCorporationIdAssets200Ok.LocationTypeEnum.SOLAR_SYSTEM )
							.withIsBlueprintCopy( true )
							.withIsSingleton( false )
							.withLocationId( 7654321L )
							.withQuantity( null )
							.build();
				},
				"Expected EsiAssets200Ok.Builder() to throw null verification, but it didn't." );
	}

	@Test
	public void gettersContract() {
		final EsiAssets200Ok esiAssets200Ok = new EsiAssets200Ok.Builder()
				.withItemId( 12345L )
				.withTypeId( 34 )
				.withLocationFlag( GetCorporationsCorporationIdAssets200Ok.LocationFlagEnum.ASSETSAFETY )
				.withLocationType( GetCorporationsCorporationIdAssets200Ok.LocationTypeEnum.SOLAR_SYSTEM )
				.withIsBlueprintCopy( true )
				.withIsSingleton( false )
				.withLocationId( 7654321L )
				.withQuantity( 100 )
				.build();
		Assertions.assertNotNull( esiAssets200Ok );

		Assertions.assertEquals( 12345L, esiAssets200Ok.getItemId() );
		Assertions.assertEquals( 100, esiAssets200Ok.getQuantity() );
		Assertions.assertEquals( true, esiAssets200Ok.getIsBlueprintCopy() );
		Assertions.assertEquals( false, esiAssets200Ok.getIsSingleton() );
		Assertions.assertEquals( 7654321L, esiAssets200Ok.getLocationId() );
		Assertions.assertEquals( EsiAssets200Ok.LocationFlagEnum.ASSETSAFETY, esiAssets200Ok.getLocationFlag() );
		Assertions.assertEquals( EsiAssets200Ok.LocationTypeEnum.SOLAR_SYSTEM, esiAssets200Ok.getLocationType() );
	}
}
