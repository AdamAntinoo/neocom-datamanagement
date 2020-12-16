package org.dimensinfin.eveonline.neocom.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotNull;

import com.google.inject.name.Named;
import com.jcabi.aspects.Cacheable;
import com.jcabi.aspects.LogExceptions;
import com.jcabi.aspects.Loggable;

import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.annotation.TimeElapsed;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
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
	// - C O N S T R U C T O R S
	public ESIDataService( final @NotNull @Named("IConfigurationService") IConfigurationService configurationService,
	                       final @NotNull @Named("IFileSystem") IFileSystem fileSystem,
	                       final @NotNull @Named("StoreCacheManager") StoreCacheManager storeCacheManager,
	                       final @NotNull @Named("RetrofitFactory") RetrofitFactory retrofitFactory ) {
		this.configurationProvider = configurationService;
		this.fileSystemAdapter = fileSystem;
		this.storeCacheManager = storeCacheManager;
		this.retrofitFactory = retrofitFactory;
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
}