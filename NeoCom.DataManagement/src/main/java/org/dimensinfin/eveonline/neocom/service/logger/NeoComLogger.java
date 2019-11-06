package org.dimensinfin.eveonline.neocom.service.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeoComLogger {
	private static final Logger logger = LoggerFactory.getLogger( NeoComLogger.class );

	private NeoComLogger() {}

	public static void info( final String message ) {
		logger.info( wrapper( generateCaller() ) + "> " + message );
	}

	public static void info( final String message, String... arguments ) {
		logger.info( "-- " + header() + message, arguments );
	}

	public static void enter( final String message, String... arguments ) {
		logger.info( ">> " + header() + message, arguments );
	}

	public static void exit( final String message, String... arguments ) {
		logger.info( "<< " + header() + message, arguments );
	}

	private static String header() {
		return wrapper( generateCaller() ) + "> ";
	}

	private static String wrapper( final String data ) {
		return "[" + data + "]";
	}

	private static String generateCaller() {
		StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
		final StackTraceElement element = traceElements[3];
		return element.getClassName().substring( element.getClassName().lastIndexOf( '.' ) ) + "." +
				element.getMethodName();
	}
}