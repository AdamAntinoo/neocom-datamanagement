package org.dimensinfin.eveonline.neocom.utility;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.joda.time.LocalDate;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class GSONLocalDateSerializer implements JsonSerializer<LocalDate> {

	@Override
	public JsonElement serialize( final LocalDate src, final Type typeOfSrc, final JsonSerializationContext context ) {
		return new JsonPrimitive( src.toString( "yyyy-MM-dd" ) );
	}
}