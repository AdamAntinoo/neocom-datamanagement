package org.dimensinfin.eveonline.neocom.provider;

public class PropertiesDefinitionsConstants {
	// - E S I   C O N S TA N T S
	public static final String ESI_LOGIN_HOST = "login.eveonline.com";
	public static final String ESI_OAUTH_AUTHENTICATION_TYPE = "Bearer ";

	// - P R O P E R T Y   N A M E S
	// - C A C H E
	public static final String CACHE_DIRECTORY_PATH = "P.cache.directory.path";
	public static final String CACHE_STORE_ESI_ITEM_DATA = "P.cache.directory.store.esiitem";
	public static final String CACHE_STORE_ESI_UNIVERSE_DATA = "P.cache.directory.store.universe";
	public static final String CACHE_STORE_ESI_AUTHENTICATED_DATA = "P.cache.directory.store.authenticated";
	public static final String CACHE_STORE_BACKEND_DATA = "P.cache.directory.store.backend";
	// - L O C A T I O N S
	public static final String LOCATIONS_CACHE_LOCATION = "P.cache.locationscache.filename";
	public static final String LOCATIONS_CACHE_STATE = "P.cache.locationscache.activestate";
	// - E S I   U N I V E R S E   A P I
	public static final String UNIVERSE_RETROFIT_SERVER_LOCATION = "P.universe.retrofit.server.location";
	public static final String UNIVERSE_RETROFIT_SERVER_AGENT = "P.universe.retrofit.server.agent";
	public static final String UNIVERSE_RETROFIT_SERVER_TIMEOUT = "P.universe.retrofit.server.timeout";
	public static final String UNIVERSE_RETROFIT_CACHE_STATE = "P.universe.retrofit.cache.state";
	public static final String UNIVERSE_RETROFIT_CACHE_NAME = "P.universe.retrofit.cache.directory.name";
	public static final String UNIVERSE_RETROFIT_CACHE_SIZE = "P.universe.retrofit.cache.size.gb";
	// - E S I   A U T H E N T I C A T E D   A P I
	public static final String AUTHENTICATED_RETROFIT_SERVER_LOCATION = "P.authenticated.retrofit.server.location";
	public static final String AUTHENTICATED_RETROFIT_SERVER_AGENT = "P.authenticated.retrofit.server.agent";
	public static final String AUTHENTICATED_RETROFIT_SERVER_TIMEOUT = "P.authenticated.retrofit.server.timeout";
	public static final String AUTHENTICATED_RETROFIT_CACHE_STATE = "P.authenticated.retrofit.cache.state";
	public static final String AUTHENTICATED_RETROFIT_CACHE_NAME = "P.authenticated.retrofit.cache.directory.name";
	public static final String AUTHENTICATED_RETROFIT_CACHE_SIZE = "P.authenticated.retrofit.cache.size.gb";
	// - B A C K E N D   A P I
	public static final String BACKEND_RETROFIT_SERVER_LOCATION = "P.backend.retrofit.server.location";
	public static final String BACKEND_RETROFIT_CACHE_FILE_NAME = "P.backend.retrofit.cache.directory.name";
	// - E S I   O A U T H   A U T H O R I Z A T I O N
	public static final String ESI_OAUTH_AUTHORIZATION_SERVER_NAME = "P.esi.authorization.server";
	public static final String ESI_OAUTH_AUTHORIZATION_CONTENT_TYPE = "P.esi.authorization.content.type";
	public static final String ESI_OAUTH_AUTHORIZATION_AGENT = "P.esi.authorization.agent";
	public static final String ESI_OAUTH_AUTHORIZATION_STATE = "P.esi.authorization.state";
	public static final String ESI_OAUTH_AUTHORIZATION_ACCESS_TOKEN = "P.esi.authorization.accesstoken.url";
	public static final String ESI_OAUTH_AUTHORIZATION_AUTHORIZE = "P.esi.authorization.authorize.url";
	// - E S I A U T H O R I Z A T I O N - T R A N Q U I L I T Y
	public static final String ESI_TRANQUILITY_AUTHORIZATION_SERVER_URL = "P.esi.tranquility.authorization.server.url";
	public static final String ESI_TRANQUILITY_AUTHORIZATION_CLIENTID = "P.esi.tranquility.authorization.clientid";
	public static final String ESI_TRANQUILITY_AUTHORIZATION_SECRETKEY = "P.esi.tranquility.authorization.secretkey";
	public static final String ESI_TRANQUILITY_AUTHORIZATION_CALLBACK = "P.esi.tranquility.authorization.callback";
	public static final String ESI_TRANQUILITY_AUTHORIZATION_AGENT = "P.esi.tranquility.authorization.agent";
	public static final String ESI_TRANQUILITY_AUTHORIZATION_CONTENT_TYPE = "P.esi.tranquility.authorization.content.type";
	public static final String ESI_TRANQUILITY_AUTHORIZATION_STATE = "P.esi.tranquility.authorization.state";
}
