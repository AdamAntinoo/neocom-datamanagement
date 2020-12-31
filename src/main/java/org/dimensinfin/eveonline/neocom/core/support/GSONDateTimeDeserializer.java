package org.dimensinfin.eveonline.neocom.core.support;

import java.lang.reflect.Type;

import org.joda.time.DateTime;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;

public class GSONDateTimeDeserializer implements JsonDeserializer<DateTime> {
	@Override
	public DateTime deserialize(
			com.google.gson.JsonElement element,
			Type arg1,
			com.google.gson.JsonDeserializationContext arg2 ) throws JsonParseException {
		String date = element.getAsString();
		return DateTime.parse(date);
	}
}
