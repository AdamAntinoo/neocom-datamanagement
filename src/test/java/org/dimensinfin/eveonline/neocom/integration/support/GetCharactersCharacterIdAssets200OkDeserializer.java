package org.dimensinfin.eveonline.neocom.integration.support;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;

public class GetCharactersCharacterIdAssets200OkDeserializer extends StdDeserializer<GetCharactersCharacterIdAssets200Ok> {
	private static final Map<String, GetCharactersCharacterIdAssets200Ok.LocationFlagEnum> locationFlagMap =
			new HashMap<>();
	private static final Map<String, GetCharactersCharacterIdAssets200Ok.LocationTypeEnum> locationTypeMap =
			new HashMap<>();

	static {
		final GetCharactersCharacterIdAssets200Ok.LocationFlagEnum[] flagValues =
				GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.values();
		for (int i = 0; i < flagValues.length; i++)
			locationFlagMap.put( flagValues[i].toString(), flagValues[i] );
		final GetCharactersCharacterIdAssets200Ok.LocationTypeEnum[] typeValues =
				GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.values();
		for (int i = 0; i < typeValues.length; i++)
			locationTypeMap.put( typeValues[i].toString(), typeValues[i] );
	}

	public GetCharactersCharacterIdAssets200OkDeserializer( final Class<?> vc ) {
		super( vc );
	}

	@Override
	public GetCharactersCharacterIdAssets200Ok deserialize( final JsonParser p, final DeserializationContext ctxt ) throws IOException, JsonProcessingException {
		TreeNode tn = p.readValueAsTree();
		final GetCharactersCharacterIdAssets200Ok instance = new GetCharactersCharacterIdAssets200Ok();
		if (tn.get( "is_blueprint_copy" ) != null)
			instance.setIsBlueprintCopy( Boolean.parseBoolean( tn.get( "is_blueprint_copy" ).toString() ) );
		if (tn.get( "is_singleton" ) != null)
			instance.setIsSingleton( Boolean.parseBoolean( tn.get( "is_singleton" ).toString() ) );
		if (tn.get( "item_id" ) != null)
			instance.setItemId( Long.parseLong( tn.get( "item_id" ).toString() ) );
		if (tn.get( "location_flag" ) != null)
			instance.setLocationFlag( this.decodeLocationFlag( ((TextNode) tn.get( "location_flag" )).textValue() ) );
		if (tn.get( "location_id" ) != null)
			instance.setLocationId( Long.parseLong( tn.get( "location_id" ).toString() ) );
		if (tn.get( "location_type" ) != null)
			instance.setLocationType( this.decodeLocationType( ((TextNode) tn.get( "location_type" )).textValue() ) );
		if (tn.get( "quantity" ) != null)
			instance.setQuantity( Integer.parseInt( tn.get( "quantity" ).toString() ) );
		if (tn.get( "type_id" ) != null)
			instance.setTypeId( Integer.parseInt( tn.get( "type_id" ).toString() ) );
		return instance;
	}

	private GetCharactersCharacterIdAssets200Ok.LocationFlagEnum decodeLocationFlag( final String flagValue ) {
		return locationFlagMap.get( flagValue );
	}

	private GetCharactersCharacterIdAssets200Ok.LocationTypeEnum decodeLocationType( final String flagValue ) {
		return locationTypeMap.get( flagValue );
	}
}