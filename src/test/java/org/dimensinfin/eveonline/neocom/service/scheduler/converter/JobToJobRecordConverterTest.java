package org.dimensinfin.eveonline.neocom.service.scheduler.converter;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.Job;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.JobRecord;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.JobStatus;

public class JobToJobRecordConverterTest {
	@Test
	public void convert() {
		final IConfigurationService configurationProvider = Mockito.mock( IConfigurationService.class );
		final IFileSystem fileSystem = Mockito.mock( IFileSystem.class );
		final RetrofitFactory retrofitFactory = Mockito.mock( RetrofitFactory.class );
		final ESIUniverseDataProvider esiUniverseDataProvider = Mockito.mock( ESIUniverseDataProvider.class );
		final LocationCatalogService job = new LocationCatalogService.Builder()
				.withConfigurationProvider( configurationProvider )
				.withFileSystemAdapter( fileSystem )
				.withRetrofitFactory( retrofitFactory )
				.withESIUniverseDataProvider( esiUniverseDataProvider )
				.build();
		final JobRecord obtained = new JobToJobRecordConverter().convert( job );
		// Assertions
		Assertions.assertEquals( "LocationCatalogService", obtained.getJobName() );
		Assertions.assertEquals( "* - *", obtained.getSchedule() );
		Assertions.assertEquals( JobStatus.READY, obtained.getStatus() );
	}

	public static class Job4Test extends Job {
		private String registration;

		@Override
		public int getUniqueIdentifier() {
			return new HashCodeBuilder( 17, 37 )
					.appendSuper( super.hashCode() )
					.append( this.registration )
					.toHashCode();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder( 17, 37 )
					.appendSuper( super.hashCode() )
					.append( this.registration )
					.toHashCode();
		}

		@Override
		public Boolean call() throws Exception {
			return true;
		}

		// - B U I L D E R
		public static class Builder extends Job.Builder<Job4Test, Job4Test.Builder> {
			private Job4Test onConstruction;

			@Override
			protected Job4Test getActual() {
				if (null == this.onConstruction) this.onConstruction = new Job4Test();
				return this.onConstruction;
			}

			@Override
			protected Job4Test.Builder getActualBuilder() {
				return this;
			}

			public Job4Test.Builder withRegistrationTest( final String registration ) {
				this.onConstruction.registration = registration;
				return this;
			}
		}
	}
}
