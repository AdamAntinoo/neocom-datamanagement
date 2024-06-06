package org.dimensinfin.eveonline.neocom.support;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;

public class PojoTest {
	public static void validateGetters( final Class target ) {
		final PojoClass targetPojo = PojoClassFactory.getPojoClass( target );
		final Validator validator = ValidatorBuilder.create()
				.with( new GetterMustExistRule() )
				.with( new GetterTester() )
				.build();
		validator.validate( targetPojo );
	}
}
