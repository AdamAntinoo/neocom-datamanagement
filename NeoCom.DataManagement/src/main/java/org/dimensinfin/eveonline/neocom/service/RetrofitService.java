package org.dimensinfin.eveonline.neocom.service;

import javax.validation.constraints.NotNull;

import com.google.inject.name.Named;

import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;

public class RetrofitService extends RetrofitFactory {
//	private final  IConfigurationService configurationProvider;
//	private final IFileSystem fileSystemAdapter;

	public RetrofitService( final @NotNull @Named("IConfigurationService") IConfigurationService configurationProvider,
	                        final @NotNull @Named("IFileSystem") IFileSystem fileSystemAdapter ) {
		this.configurationProvider = configurationProvider;
		this.fileSystemAdapter = fileSystemAdapter;
	}
}