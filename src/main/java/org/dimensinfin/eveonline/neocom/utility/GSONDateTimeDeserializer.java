package org.dimensinfin.eveonline.neocom.utility;

import java.lang.reflect.Type;

import org.joda.time.DateTime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class GSONDateTimeDeserializer implements JsonDeserializer<DateTime> {
	@Override
	public DateTime deserialize(
			JsonElement element,
			Type arg1,
			JsonDeserializationContext arg2 ) throws JsonParseException {
		String date = element.getAsString();
		return DateTime.parse(date);
	}
}
