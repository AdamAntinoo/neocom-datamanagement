package org.dimensinfin.eveonline.neocom.support;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.NoFieldShadowingRule;
import com.openpojo.validation.rule.impl.SerializableMustHaveSerialVersionUIDRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

public class PojoTestUtils {

	private static final Validator ACCESSOR_VALIDATOR = ValidatorBuilder.create()
			.with( new GetterTester() )
			.with( new SetterTester() )
			.with( new SerializableMustHaveSerialVersionUIDRule() )
			.with( new NoFieldShadowingRule() )
			.build();

	public static void validateAccessors( final Class<?> clazz ) {
		validate( clazz, ACCESSOR_VALIDATOR );
	}

	private static void validate( final Class<?> clazz, Validator validator ) {
		validator.validate( PojoClassFactory.getPojoClass( clazz ) );
	}

	public static String jsonField( final String fieldName, final String fieldValue ) {
		return quote( fieldName ) + ": " + quote( fieldValue );
	}
	public static String jsonField( final String fieldName, final Integer fieldValue ) {
		return quote( fieldName ) + ": " + quote( fieldValue.toString() );
	}
	public static String jsonField( final String fieldName, final Long fieldValue ) {
		return quote( fieldName ) + ": " + quote( fieldValue.toString() );
	}

	private static String quote( final String data ) {
		return '"' + data + '"';
	}
}
