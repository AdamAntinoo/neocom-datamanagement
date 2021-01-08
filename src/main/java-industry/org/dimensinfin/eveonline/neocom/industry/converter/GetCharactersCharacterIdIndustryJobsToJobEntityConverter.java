package org.dimensinfin.eveonline.neocom.industry.converter;

import javax.print.attribute.standard.Destination;
import javax.validation.constraints.NotNull;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdIndustryJobs200Ok;
import org.dimensinfin.eveonline.neocom.industry.persistence.JobEntity;

import retrofit2.Converter;

public class GetCharactersCharacterIdIndustryJobsToJobEntityConverter implements Converter<GetCharactersCharacterIdIndustryJobs200Ok, JobEntity> {
	private static final ModelMapper modelMapper = new ModelMapper();

	static {
		modelMapper.getConfiguration()
				.setFieldMatchingEnabled( true )
				.setMethodAccessLevel( Configuration.AccessLevel.PRIVATE );
	}

	private String installerName;

	// - C O N S T R U C T O R S
	public GetCharactersCharacterIdIndustryJobsToJobEntityConverter( final @NotNull String installerName ) {
		this.installerName = installerName;
	}

	@Override
	public JobEntity convert( final GetCharactersCharacterIdIndustryJobs200Ok value ) {
		final JobEntity job = modelMapper.map( value, JobEntity.class );
		return job.setInstallerName( this.installerName );
	}
}