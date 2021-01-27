package org.dimensinfin.eveonline.neocom.database;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import org.dimensinfin.eveonline.neocom.loyalty.persistence.LoyaltyOffersRepository;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class DMDatabaseDependenciesModule extends AbstractModule {
	public static final String LOYALTYOFFERS_REPOSITORY = "LoyaltyOffersRepository";

	@Override
	protected void configure() {
		bind( LoyaltyOffersRepository.class )
				.annotatedWith( Names.named( LOYALTYOFFERS_REPOSITORY ) )
				.to( LoyaltyOffersRepository.class )
				.in( Singleton.class );
	}
}
