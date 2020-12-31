package org.dimensinfin.eveonline.neocom.exception;

public class NeoComError {
	public enum ErroInfo {
		INITIALIZATION_RUNTIME_ERROR(EErrorType.RUNTIME, "neocom.error.initialisation.runtime.exception.runtime.error"),
		INITIALIZATION_EXCEPTION_ERROR(EErrorType.EXCEPTION, "neocom.error.initialisation.exception.runtime.error");

		private EErrorType type;
		private String errorCode;

		ErroInfo( final EErrorType type, final String errorCode ) {
			this.type = type;
			this.errorCode = errorCode;

		}
	}

	private Exception ex;
	private EErrorType type = EErrorType.EXCEPTION;
	private String code;
	private String origin;

	public NeoComError( final Exception ex ) {
		this.ex = ex;
	}

	// -  B U I L D E R
	public static class Builder {
		private NeoComError onConstruction;

		public Builder( final Exception rtex ) {
			this.onConstruction = new NeoComError(rtex);
		}

		public Builder withErrorInfo( final ErroInfo errorInfo ) {
			this.onConstruction.type = errorInfo.type;
			this.onConstruction.code = errorInfo.errorCode;
			return this;
		}

		public Builder withType( final EErrorType type ) {
			this.onConstruction.type = type;
			return this;
		}

		public Builder withCode( final String code ) {
			this.onConstruction.code = code;
			return this;
		}

		//		public Builder withOrigin( final String origin ) {
		//			this.onConstruction.origin = origin;
		//			return this;
		//		}

		public NeoComError build() {
			this.onConstruction.origin = this.stackCallerName();
			return this.onConstruction;
		}

		private String stackCallerName() {
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
			StackTraceElement e = stacktrace[2];//maybe this number needs to be corrected
			final String methodName = e.getMethodName();
			final String className = e.getClassName();
			return className + "." + methodName;
		}
	}
}
