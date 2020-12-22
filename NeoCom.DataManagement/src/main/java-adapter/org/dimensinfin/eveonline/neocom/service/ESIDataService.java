package org.dimensinfin.eveonline.neocom.service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jcabi.aspects.Cacheable;
import com.jcabi.aspects.LogExceptions;
import com.jcabi.aspects.Loggable;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.annotation.TimeElapsed;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.domain.space.Station;
import org.dimensinfin.eveonline.neocom.esiswagger.api.IndustryApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.MarketApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdIndustryJobs200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOrders200Ok;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;
import org.dimensinfin.logging.LogWrapper;

import retrofit2.Response;

public class ESIDataService extends ESIDataProvider {
	public static final Long PREDEFINED_MARKET_HUB_STATION_ID = 60003760L;
	private static final Map<Integer, Long> regionMarketHubReferenceTable = new HashMap<>();

	static {
		regionMarketHubReferenceTable.put( 10000043, 60008494L ); // Make the Amarr VIII (Oris) the hub for Domain
	}

	// - C O N S T R U C T O R S
	@Inject
	public ESIDataService( final @NotNull @Named("IConfigurationService") IConfigurationService configurationService,
	                       final @NotNull @Named("IFileSystem") IFileSystem fileSystem,
	                       final @NotNull @Named("StoreCacheManager") StoreCacheManager storeCacheManager,
	                       final @NotNull @Named("RetrofitFactory") RetrofitFactory retrofitFactory ,
	                       final @NotNull @Named("LocationCatalogService") LocationCatalogService locationCatalogService) {
		this.configurationProvider = configurationService;
		this.fileSystemAdapter = fileSystem;
		this.storeCacheManager = storeCacheManager;
		this.retrofitFactory = retrofitFactory;
		this.locationCatalogService=locationCatalogService;
	}

	// - I N D U S T R Y
	@TimeElapsed
	@Loggable(Loggable.DEBUG)
	@LogExceptions
	@Cacheable(lifetime = 300, unit = TimeUnit.SECONDS)
	public List<GetCharactersCharacterIdIndustryJobs200Ok> getCharactersCharacterIdIndustryJobs( final Credential credential ) {
		LogWrapper.enter();
		try {
			final Response<List<GetCharactersCharacterIdIndustryJobs200Ok>> industryJobsResponse = this.retrofitFactory
					.accessUniverseConnector()
					.create( IndustryApi.class )
					.getCharactersCharacterIdIndustryJobs(
							credential.getAccountId(),
							credential.getDataSource().toLowerCase(),
							null, false, null
					)
					.execute();
			if (industryJobsResponse.isSuccessful()) return industryJobsResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return new ArrayList<>();
	}

	@TimeElapsed
	@Loggable(Loggable.DEBUG)
	@LogExceptions
	@Cacheable(lifetime = 1200, unit = TimeUnit.SECONDS)
	public List<GetCharactersCharacterIdOrders200Ok> getCharactersCharacterIdOrders( final Credential credential ) {
		LogWrapper.enter();
		try {
			final Response<List<GetCharactersCharacterIdOrders200Ok>> industryJobsResponse = this.retrofitFactory
					.accessUniverseConnector()
					.create( MarketApi.class )
					.getCharactersCharacterIdOrders(
							credential.getAccountId(),
							credential.getDataSource().toLowerCase(),
							null, null
					)
					.execute();
			if (industryJobsResponse.isSuccessful()) return industryJobsResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return new ArrayList<>();
	}

	// - M A R K E T

	/**
	 * Searches on a predefined table for the match on the Region identifier. This reference table will store the preferred Market Hub for the
	 * selected region. If the region value of not found on the reference table then the spacial Jita market is selected as the region hub.
	 *
	 * @param regionId the target region to search for the market hub.
	 * @return the region's selected market hub predefined on the application. The returned value is a complete <code>Station</code> location record.
	 */
	public Station getRegionMarketHub( final int regionId ) {
		Long hit = regionMarketHubReferenceTable.get( regionId );
		if (null == hit) hit = PREDEFINED_MARKET_HUB_STATION_ID;
		final SpaceLocation location = this.locationCatalogService.searchLocation4Id( hit );
		if (location instanceof Station) return (Station) location;
		else {
			LogWrapper.info( MessageFormat.format(
					"Configured region [{0}] market hub identifier does not point to an Station", regionId )
			);
			return (Station) this.locationCatalogService.searchLocation4Id( PREDEFINED_MARKET_HUB_STATION_ID );
		}
	}
}