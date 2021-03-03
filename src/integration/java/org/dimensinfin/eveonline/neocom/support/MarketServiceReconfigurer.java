package org.dimensinfin.eveonline.neocom.support;

import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.service.DMServicesDependenciesModule;
import org.dimensinfin.logging.LogWrapper;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class MarketServiceReconfigurer {
	private final IConfigurationService configurationService;

	// - C O N S T R U C T O R S
	@Inject
	public MarketServiceReconfigurer( @NotNull @Named(DMServicesDependenciesModule.ICONFIGURATION_SERVICE) final IConfigurationService configurationService ) {
		LogWrapper.enter();
		this.configurationService = configurationService;
		// Change configuration to use mock data.
		if (this.configurationService instanceof SBConfigurationService) {
			LogWrapper.info( "Updated the 'P.universe.retrofit.server.location' property." );
			((SBConfigurationService) this.configurationService).setProperty( "P.universe.retrofit.server.location", "http://localhost:5320/" );
		}
		LogWrapper.enter();
	}
}