package org.dimensinfin.eveonline.neocom.service.scheduler.converter;

import org.dimensinfin.eveonline.neocom.service.scheduler.domain.Job;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.JobRecord;

import retrofit2.Converter;

public class JobToJobRecordConverter implements Converter<Job, JobRecord> {
	public JobToJobRecordConverter() {}

	@Override
	public JobRecord convert( final Job value ) {
		return new JobRecord.Builder()
				.withJobName( value.getClass().getSimpleName() )
				.withStatus( value.getStatus() )
				.withSchedule( value.getSchedule() )
				.build();
	}
}