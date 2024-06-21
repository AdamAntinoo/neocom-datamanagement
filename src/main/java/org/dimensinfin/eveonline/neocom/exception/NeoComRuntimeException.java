package org.dimensinfin.eveonline.neocom.exception;

public class NeoComRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 8864888568628860054L;
	private String sourceClass;
	private String sourceMethod;
	private String errorCode = ErrorCodesData.NOT_INTERCEPTED_EXCEPTION;

	// - C O N S T R U C T O R S
	private NeoComRuntimeException() {
		super();
		final StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		final StackTraceElement stackElement = stacktrace[3]; // This is to check if we are using Dalvik
		this.sourceMethod = stackElement.getMethodName();
		this.sourceClass = stackElement.getClassName();
	}

	public NeoComRuntimeException( final ErrorInfoCatalog error, final Object... arguments ) {
		this( error.getErrorMessage( arguments ) );
		this.errorCode = error.getErrorCode();
	}

	public NeoComRuntimeException( final String message ) {
		super( message );
	}

	public NeoComRuntimeException( final Exception rootException ) {
		this( rootException.getMessage() );
	}

	// - G E T T E R S   &   S E T T E R S
	public String getErrorCode() {
		return this.errorCode;
	}

	public String getSourceClass() {
		return this.sourceClass;
	}

	public String getSourceMethod() {
		return this.sourceMethod;
	}
}
