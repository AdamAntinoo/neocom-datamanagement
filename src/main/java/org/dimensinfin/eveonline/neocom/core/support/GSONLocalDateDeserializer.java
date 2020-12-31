package org.dimensinfin.eveonline.neocom.core.support;

import java.lang.reflect.Type;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;

public class GSONLocalDateDeserializer implements JsonDeserializer<LocalDate> {
	private static final DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");

	@Override
	public LocalDate deserialize(
			com.google.gson.JsonElement element,
			Type arg1,
			com.google.gson.JsonDeserializationContext arg2 ) throws JsonParseException {
		String date = element.getAsString();
		return LocalDate.parse(date, format);
	}
}
