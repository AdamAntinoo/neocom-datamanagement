package org.dimensinfin.eveonline.neocom.industry.converter;

import java.io.IOException;

import org.dimensinfin.eveonline.neocom.industry.domain.IndustryJob;
import org.dimensinfin.eveonline.neocom.industry.persistence.JobEntity;

import retrofit2.Converter;

public class JobEntityToIndustryJobConverter implements Converter<JobEntity, IndustryJob> {
	@Override
	public IndustryJob convert( final JobEntity value ) throws IOException {
		return new IndustryJob.Builder()
				.build();
	}
}