package org.dimensinfin.eveonline.neocom.integration.support;

import java.io.IOException;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;

class GetCharactersCharacterIdAssets200OkDeserializerTest {
	@Test
	void deserialize() throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		final SimpleModule testModule = new SimpleModule( "NoeComIntegrationModule",
				Version.unknownVersion() );
		testModule.addDeserializer( GetCharactersCharacterIdAssets200Ok.class,
				new GetCharactersCharacterIdAssets200OkDeserializer( GetCharactersCharacterIdAssets200Ok.class ) );
		mapper.registerModule( testModule );
		final String serialized = "{\n" +
				"    \"is_singleton\": false,\n" +
				"    \"item_id\": 1025702882268,\n" +
				"    \"location_flag\": \"Hangar\",\n" +
				"    \"location_id\": 60015001,\n" +
				"    \"location_type\": \"station\",\n" +
				"    \"quantity\": 23804,\n" +
				"    \"type_id\": 17471\n" +
				"  }";
		final GetCharactersCharacterIdAssets200Ok deserialized = mapper.readValue(
				serialized, GetCharactersCharacterIdAssets200Ok.class );
		Assertions.assertNotNull( deserialized );
		Assertions.assertEquals( false, deserialized.getIsSingleton() );
		Assertions.assertEquals( 1025702882268L, deserialized.getItemId() );
		Assertions.assertEquals( GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HANGAR, deserialized.getLocationFlag() );
		Assertions.assertEquals( GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.STATION, deserialized.getLocationType() );
		Assertions.assertEquals( 60015001, deserialized.getLocationId() );
		Assertions.assertEquals( 23804, deserialized.getQuantity() );
		Assertions.assertEquals( 17471, deserialized.getTypeId() );
	}
}