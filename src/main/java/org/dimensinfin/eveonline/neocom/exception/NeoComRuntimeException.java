package org.dimensinfin.eveonline.neocom.exception;

public class NeoComRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 8864888568628860054L;
	private String jsonClass = "NeoComRuntimeException";
	private String sourceClass;
	private String sourceMethod;
	private Exception rootException;
	private ErrorInfoCatalog error;

	// - C O N S T R U C T O R S
	public NeoComRuntimeException() {
		super();
		final StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		final StackTraceElement stackElement = stacktrace[3]; // This is to check if we are using Dalvik
		this.sourceMethod = stackElement.getMethodName();
		this.sourceClass = stackElement.getClassName();
	}

	public NeoComRuntimeException( final ErrorInfoCatalog error ) {
		this.error = error;
	}

	public NeoComRuntimeException( final String message ) {
		super( message );
	}

	public NeoComRuntimeException( final Exception rootException ) {
		this();
//		this.errorInfo = ErrorInfo.NOT_INTERCEPTED_EXCEPTION;
		this.rootException = rootException;
	}

	public String getJsonClass() {
		return jsonClass;
	}

	public String getMessage() {
		String message = "";
		if (null != super.getMessage()) message = super.getMessage();
		if (null != this.rootException) message = message.concat( ":" ).concat( this.rootException.getMessage() );
		return message;
	}

	public String getSourceClass() {
		return this.sourceClass;
	}

	public String getSourceMethod() {
		return this.sourceMethod;
	}

	public Exception getRootException() {
		return this.rootException;
	}
}
