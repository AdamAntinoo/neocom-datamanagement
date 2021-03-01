package org.dimensinfin.eveonline.neocom.utility;

import java.text.MessageFormat;

import org.dimensinfin.logging.LogWrapper;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public final class NeoObjects {
	public static <T> T requireNonNull( final T target ) {
		return requireNonNull( target, "Null Pointer validation detected unexpected null value." );
	}

	public static <T> T requireNonNull( final T var0, final String message ) {
		if (var0 == null) {
			final NullPointerException npe = new NullPointerException( MessageFormat.format( "[{0}]>" + message, generateCaller() ) );
			LogWrapper.error( npe );
			throw npe;
		} else {
			return var0;
		}
	}

	private static String generateCaller() {
		final StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
		int androidDisplacement = 0;
		if (traceElements[0].getMethodName().equalsIgnoreCase( "getThreadStackTrace" ))
			androidDisplacement = 1;
		final StackTraceElement element = traceElements[4 + androidDisplacement];
		return element.getClassName().substring( element.getClassName().lastIndexOf( '.' ) + 1 ) + "." +
				element.getMethodName();
	}
}