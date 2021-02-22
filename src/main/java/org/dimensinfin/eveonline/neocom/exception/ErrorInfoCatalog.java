package org.dimensinfin.eveonline.neocom.exception;

import java.text.MessageFormat;

import static org.dimensinfin.eveonline.neocom.exception.ErrorCodesData.ASSETS_DOWNLOAD_PROCESS_ERRORCODE;
import static org.dimensinfin.eveonline.neocom.exception.ErrorCodesData.AUTHENTICATION_VERIFICATION_ERRORCODE;
import static org.dimensinfin.eveonline.neocom.exception.ErrorCodesData.CHARACTER_SERVICE_ERRORCODE;
import static org.dimensinfin.eveonline.neocom.exception.ErrorCodesData.INVALID_LOCATION_TYPE_ERRORCODE;
import static org.dimensinfin.eveonline.neocom.exception.ErrorCodesData.MINING_REPOSITORY_EXTRACTION_SQL_ERRORCODE;
import static org.dimensinfin.eveonline.neocom.exception.ErrorCodesData.RETROFIT_CACHE_FILE_SYSTEM_ERRORCODE;

public enum ErrorInfoCatalog {
	AUTHENTICATION_FAILURE_ESI_SSO(
			AUTHENTICATION_VERIFICATION_ERRORCODE,
			"The access token conversion failed at ESI SSO with message - {0}"
	),
	PILOT_NOT_FOUND(
			CHARACTER_SERVICE_ERRORCODE,
			"The pilot with identifier {0} is not found on the ESI repository."
	),
	FILESYSTEM_FAILURE_RETROFIT_CACHE_RELATED(
			RETROFIT_CACHE_FILE_SYSTEM_ERRORCODE,
			"File System exception error during retrofit cache configuration." ),
	RUNTIME_PROCESSING_ASSET(
			ASSETS_DOWNLOAD_PROCESS_ERRORCODE,
			"Runtime while processing asset; {0}" ),
	MANDATORY_CONFIGURATION_PROPERTY_EMPTY(
			"data.management.retrofit.configuration.error",
			"ESI configuration property is empty while configuring the OAuth parameters." ),
	LOCATION_NOT_THE_CORRECT_TYPE(
			INVALID_LOCATION_TYPE_ERRORCODE,
			"The expected location obtained is not of the type expected." ),
	MINING_EXTRACTION_BYID_SEARCH_FAILED(
			MINING_REPOSITORY_EXTRACTION_SQL_ERRORCODE,
			"SQL exception while searching for extraction with id {0}." ),
	MINING_EXTRACTION_PERSISTENCE_FAILED(
			MINING_REPOSITORY_EXTRACTION_SQL_ERRORCODE,
			"SQL exception while persisting mining extraction {0} - {1}" ),
	INVALID_CREDENTIAL_IDENTIFIER(
			AUTHENTICATION_VERIFICATION_ERRORCODE,
			"The validation character response is not valid and then the unique character identifier is not found." );

	public final String errorCode;
	public final String errorMessage;

	// - C O N S T R U C T O R S
	ErrorInfoCatalog( final String errorCode, final String errorMessage ) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	// - G E T T E R S   &   S E T T E R S
	public String getErrorCode() {
		return this.errorCode;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public String getErrorMessage( final String... arguments ) {
		return MessageFormat.format( this.errorMessage, (Object[]) arguments );
	}
}
