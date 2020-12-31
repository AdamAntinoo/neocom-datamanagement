package org.dimensinfin.eveonline.neocom.database.persister;

import java.io.IOException;
import java.sql.SQLException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.LongStringType;
import com.j256.ormlite.support.DatabaseResults;

import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

public class EsiAssets200OkPersister extends LongStringType {
	private static final EsiAssets200OkPersister singleTon = new EsiAssets200OkPersister();
	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.registerModule( new JodaModule() );
	}

	public static EsiAssets200OkPersister getSingleton() {
		return singleTon;
	}

	private EsiAssets200OkPersister() {
		super( SqlType.LONG_STRING, new Class<?>[0] );
	}

	/**
	 * @param sqlType Type of the class as it is persisted in the databases.
	 * @param classes Associated classes for this type. These should be specified if you want this type to be always used
	 */
	private EsiAssets200OkPersister( final SqlType sqlType, final Class<?>[] classes ) {
		super( sqlType, classes );
	}

	@Override
	public Object javaToSqlArg( FieldType fieldType, Object obj ) throws SQLException {
		try {
			return mapper.writeValueAsString( obj );
		} catch (final JsonProcessingException jpe) {
			throw new SQLException( "Exception parsing back EsiAssets200OkPersister field: " +
					jpe.getMessage() );
		}
	}

	@Override
	public Object sqlArgToJava( FieldType fieldType, Object sqlArg, int columnPos ) throws SQLException {
		try {
			return (EsiAssets200OkPersister) mapper.readValue( (String) sqlArg,
					EsiAssets200OkPersister.class );
		} catch (final IOException sqle) {
			throw new SQLException( "Exception converting back EsiAssets200OkPersister field: " +
					sqle.getMessage() );
		}
	}

	@Override
	public Object parseDefaultString( final FieldType fieldType, final String defaultStr ) {
		try {
			return (EsiAssets200OkPersister) mapper.readValue( defaultStr,
					EsiAssets200OkPersister.class );
		} catch (final IOException sqle) {
			NeoComLogger.error( sqle );
		}
		return null;
	}

	@Override
	public Object resultToSqlArg( final FieldType fieldType, final DatabaseResults results, final int columnPos ) throws SQLException {
		return results.getString( columnPos );
	}
}
