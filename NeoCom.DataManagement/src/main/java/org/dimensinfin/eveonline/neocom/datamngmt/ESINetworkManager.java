package org.dimensinfin.eveonline.neocom.datamngmt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dimensinfin.core.util.Chrono;
import org.dimensinfin.core.util.Chrono.ChronoOptions;
import org.dimensinfin.eveonline.neocom.auth.NeoComRetrofitHTTP;
import org.dimensinfin.eveonline.neocom.esiswagger.api.AllianceApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.CorporationApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.RoutesApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.StatusApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.*;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import retrofit2.Response;

/**
 * This class downloads the OK data classes from the ESI api using the ESI authorization. It will then simply return the
 * results to the caller to be converted or to be used. There should be no exceptio interception so the caller can result any
 * unexpected situation.
 * The results should be stored on last access so if the network is down we can return last accessed data and not result into an
 * exception.
 *
 * @author Adam Antinoo
 */
public class ESINetworkManager extends ESINetworkManagerCharacter {
	// - S T A T I C - S E C T I O N

	// - S T A T I C   S W A G G E R   I N T E R F A C E - P U B L I C   A P I
	// - S E R V E R
	public GetStatusOk getStatus( final String server ) {
		logger.info(">> [ESINetworkManager.getStatus]");
		// Store the response at the cache or if there is a network failure return the last access if available
		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.SERVERSTATUS, 0);
		final Chrono accessFullTime = new Chrono();
		try {
			String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<GetStatusOk> statusApiResponse = neocomRetrofit
					                                                .create(StatusApi.class)
					                                                .getStatus(datasource, null).execute();
			if (statusApiResponse.isSuccessful()) {
				// Store results on the cache.
				okResponseCache.put(reference, statusApiResponse);
				return statusApiResponse.body();
			} else {
				// Use the cached data is available.
				return (GetStatusOk) okResponseCache.get(reference).body();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getStatus]> [TIMING] Full elapsed: {}"
					, accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}

	// - S T A T I C   S W A G G E R   I N T E R F A C E - U N I V E R S E   A P I
	// - U N I V E R S E
	//	public static GetUniverseTypesTypeIdOk getUniverseTypeById( final int typeId, final String server ) {
	//		return getUniverseTypeById(server, typeId);
	//	}

	public GetUniverseTypesTypeIdOk getUniverseTypeById( final String server, final int typeId ) {
		logger.info(">> [ESINetworkManagerMock.getUniverseTypeById]");
		final DateTime startTimePoint = DateTime.now();
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseTypesTypeIdOk> itemListResponse = neocomRetrofitNoAuth
					                                                            .create(UniverseApi.class)
					                                                            .getUniverseTypesTypeId(typeId
							                                                            , "en-us"
							                                                            , server
							                                                            , null
							                                                            , null)
					                                                            .execute();
			if (!itemListResponse.isSuccessful()) {
				return null;
			} else return itemListResponse.body();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException runtime) {
			runtime.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getMarketsPrices]> [TIMING] Full elapsed: {}"
					, new Duration(startTimePoint, DateTime.now()).getMillis() + "ms");
		}
		return null;
	}

	public List<GetUniverseAncestries200Ok> getUniverseAncestries( final String server ) {
		logger.info(">> [ESINetworkManagerMock.getUniverseAncestries]");
		final DateTime startTimePoint = DateTime.now();
		try {
			// Create the request to be returned so it can be called.
			final Response<List<GetUniverseAncestries200Ok>> ancestriesResponse = neocomRetrofitNoAuth
					                                                                      .create(UniverseApi.class)
					                                                                      .getUniverseAncestries("en-us"
							                                                                      , server
							                                                                      , null
							                                                                      , null)
					                                                                      .execute();
			if (!ancestriesResponse.isSuccessful()) {
				return null;
			} else return ancestriesResponse.body();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException runtime) {
			runtime.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getUniverseAncestries]> [TIMING] Full elapsed: {}"
					, new Duration(startTimePoint, DateTime.now()).getMillis() + "ms");
		}
		return null;
	}

	public List<GetUniverseBloodlines200Ok> getUniverseBloodlines( final String server ) {
		logger.info(">> [ESINetworkManagerMock.getUniverseBloodlines]");
		final DateTime startTimePoint = DateTime.now();
		try {
			// Create the request to be returned so it can be called.
			final Response<List<GetUniverseBloodlines200Ok>> bloodlinesResponse = neocomRetrofitNoAuth
					                                                                      .create(UniverseApi.class)
					                                                                      .getUniverseBloodlines("en-us"
							                                                                      , server
							                                                                      , null
							                                                                      , null)
					                                                                      .execute();
			if (!bloodlinesResponse.isSuccessful()) {
				return null;
			} else return bloodlinesResponse.body();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException runtime) {
			runtime.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getUniverseBloodlines]> [TIMING] Full elapsed: {}"
					, new Duration(startTimePoint, DateTime.now()).getMillis() + "ms");
		}
		return null;
	}

	public List<GetUniverseRaces200Ok> getUniverseRaces( final String server ) {
		logger.info(">> [ESINetworkManagerMock.getUniverseRaces]");
		final DateTime startTimePoint = DateTime.now();
		try {
			// Create the request to be returned so it can be called.
			final Response<List<GetUniverseRaces200Ok>> racesResponse = neocomRetrofitNoAuth
					                                                            .create(UniverseApi.class)
					                                                            .getUniverseRaces("en-us"
							                                                            , server
							                                                            , null
							                                                            , null)
					                                                            .execute();
			if (!racesResponse.isSuccessful()) {
				return null;
			} else return racesResponse.body();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException runtime) {
			runtime.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getUniverseRaces]> [TIMING] Full elapsed: {}"
					, new Duration(startTimePoint, DateTime.now()).getMillis() + "ms");
		}
		return null;
	}

	public static GetUniversePlanetsPlanetIdOk getUniversePlanetsPlanetId( final int identifier, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getUniversePlanetsPlanetId]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			//			final UniverseApi universeApiRetrofit = neocomRetrofit.create(UniverseApi.class);
			final Response<GetUniversePlanetsPlanetIdOk> universeApiResponse = neocomRetrofit
					                                                                   .create(UniverseApi.class)
					                                                                   .getUniversePlanetsPlanetId(identifier, datasource, null).execute();
			if (!universeApiResponse.isSuccessful()) {
				return null;
			} else return universeApiResponse.body();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			logger.info("<< [ESINetworkManager.getUniversePlanetsPlanetId]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
	}

	public static List<PostUniverseNames200Ok> postUserLabelNameDownload( final List<Integer> sourceidList, final String server ) {
		logger.info(">> [ESINetworkManager.postUserLabelNameDownload]");
		// Store the response at the cache or if there is a network failure return the last access if available
		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.SERVERSTATUS, 0);
		final Chrono accessFullTime = new Chrono();
		final List<Integer> idList = new ArrayList<>();
		try {
			String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			Response<List<PostUniverseNames200Ok>> universeApiResponse = neocomRetrofit.create(UniverseApi.class)
					                                                             .postUniverseNames(idList, datasource).execute();
			if (universeApiResponse.isSuccessful()) {
				// Store results on the cache.
				okResponseCache.put(reference, universeApiResponse);
				return universeApiResponse.body();
			} else {
				// Use the cached data is available.
				return (List<PostUniverseNames200Ok>) okResponseCache.get(reference).body();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		} finally {
			logger.info("<< [ESINetworkManager.postUserLabelNameDownload]> [TIMING] Full elapsed: {}"
					, accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
	}

	public static List<Integer> calculateRouteJumps( final int origin, final int destination, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.calculateRouteJumps]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<List<Integer>> routeApiResponse = neocomRetrofit
					                                                 .create(RoutesApi.class)
					                                                 .getRouteOriginDestination(destination, origin, null, null
							                                                 , datasource, "shortest", null).execute();
			if (!routeApiResponse.isSuccessful()) {
				return new ArrayList<>();
			} else return routeApiResponse.body();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		} finally {
			logger.info("<< [ESINetworkManager.calculateRouteJumps]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
	}

	// - S T A T I C   S W A G G E R   I N T E R F A C E - C O R P O R A T I O N   A P I
	// --- C O R P O R A T I O N   P U B L I C   I N F O R M A T I O N
	public GetCorporationsCorporationIdOk getCorporationsCorporationId( final int identifier, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCorporationsCorporationId]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
			// Use server parameter to override configuration server to use.
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<GetCorporationsCorporationIdOk> corporationResponse = neocomRetrofit
					                                                                     .create(CorporationApi.class)
					                                                                     .getCorporationsCorporationId(identifier, datasource, null).execute();
			if (corporationResponse.isSuccessful())
				return corporationResponse.body();
		} catch (IOException ioe) {
			logger.error("EX [ESINetworkManager.getCorporationsCorporationId]> [EXCEPTION]: {}", ioe.getMessage());
			ioe.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getCorporationsCorporationId]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}

	// --- A L L I A N C E   P U B L I C   I N F O R M A T I O N
	public GetAlliancesAllianceIdOk getAlliancesAllianceId( final int identifier, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCorporationsCorporationId]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
			// Use server parameter to override configuration server to use.
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<GetAlliancesAllianceIdOk> allianceResponse = neocomRetrofit
					                                                            .create(AllianceApi.class)
					                                                            .getAlliancesAllianceId(identifier, datasource, null).execute();
			if (allianceResponse.isSuccessful())
				return allianceResponse.body();
		} catch (IOException ioe) {
			logger.error("EX [ESINetworkManager.getCorporationsCorporationId]> [EXCEPTION]: {}", ioe.getMessage());
			ioe.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getCorporationsCorporationId]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}
}
