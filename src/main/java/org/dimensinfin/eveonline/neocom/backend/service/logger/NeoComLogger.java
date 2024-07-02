package org.dimensinfin.eveonline.neocom.backend.service.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeoComLogger {
	protected static final Logger logger = LoggerFactory.getLogger( NeoComLogger.class );

	public static void info( String message ) {
		if ( null != message ) {
			String header = header();
			logger.info( "-- {}> {}", header, message );
		}
	}

	public static void info( String message, Exception exception ) {
		if ( null != message ) {
			String header = header();
			logger.info( "-- {}> {}{}", new Object[]{ header, message, null != exception ? "-" + exception.getMessage() : "" } );
		}
	}

	public static void debug( String message ) {
		if ( null != message ) {
			String header = header();
			logger.debug( "== {}> {}", header, message );
		}
	}

	public static void enter() {
		String header = header();
		logger.info( ">> {}", header );
	}

	public static void enter( String message ) {
		if ( null != message ) {
			String header = header();
			logger.info( ">> {}> {}", header, message );
		}

	}

	@Deprecated
	public static void error( final String message, final Exception exception ) {
		final String headerMessage = wrapper( generateCaller4Exception() );
		logger.error( ">E {}x {}-{}", headerMessage, message, exception.getMessage() );
		final String trace = defaultExceptionLogAction( exception );
		logger.debug( trace );
	}

	public static void exit() {
		String header = header();
		logger.info( "<< {}", header );
	}

	public static void exit( String message ) {
		if ( null != message ) {
			String header = header();
			logger.info( "<<  {}> {}", header, message );
		}

	}

	public static void error( Exception exception ) {
		if ( null != exception ) {
			String headerMessage = header();
			logger.error( ">E {}x {}", headerMessage, null != exception.getMessage() ? exception.getMessage() : exception.toString() );

			try {
				String trace = defaultExceptionLogAction( exception );
				logger.debug( trace );
			} catch (NullPointerException var3) {
				logger.error( ">E {}> Null Pointer exception.", headerMessage );
			}
		}

	}

	public static String defaultExceptionLogAction( Exception exception ) {
		StackTraceElement[] elements = exception.getStackTrace();
		String stack = "";

		for (int i = 0; i < elements.length; ++i) {
			stack = stack.concat( "className: " ).concat( elements[i].getClassName() );
			stack = stack.concat( "methodName: " ).concat( elements[i].getMethodName() );
			stack = stack.concat( "fileName: " ).concat( elements[i].getFileName() );
			stack = stack.concat( "lineNumber: " ).concat( Integer.toString( elements[i].getLineNumber() ) );
			stack = stack.concat( "\n" );
		}

		return stack;
	}

	private static String header() {
		return wrapper( generateCaller() );
	}

	private static String wrapper( String data ) {
		return "[" + data + "]";
	}

	private static String generateCaller() {
		StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
		int androidDisplacement = 0;
		if ( traceElements[0].getMethodName().equalsIgnoreCase( "getThreadStackTrace" ) ) {
			androidDisplacement = 1;
		}

		StackTraceElement element = traceElements[4 + androidDisplacement];
		return element.getClassName()
				.substring( element.getClassName().lastIndexOf( 46 ) + 1 ) + "." + element.getMethodName();
	}

	private static String generateCaller4Exception() {
		StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
		int androidDisplacement = 0;
		if ( traceElements[0].getMethodName().equalsIgnoreCase( "getThreadStackTrace" ) ) {
			androidDisplacement = 1;
		}

		StackTraceElement element = traceElements[4 + androidDisplacement];
		return element.getClassName()
				.substring( element.getClassName().lastIndexOf( 46 ) + 1 ) + "." + element.getMethodName() + ":" + element.getLineNumber();
	}

	protected NeoComLogger() {
	}
}
