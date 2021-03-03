package org.dimensinfin.eveonline.neocom.service;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import org.dimensinfin.eveonline.neocom.character.service.CharacterService;
import org.dimensinfin.eveonline.neocom.loyalty.service.LoyaltyService;
import org.dimensinfin.eveonline.neocom.market.service.MarketService;

public class DMServicesDependenciesModule extends AbstractModule {
	public static final String RETROFIT_SERVICE = "RetrofitService";
	public static final String LOCATION_CATALOG_SERVICE = "LocationCatalogService";
	public static final String ESIDATA_SERVICE = "ESIDataService";
	public static final String RESOURCE_FACTORY = "ResourceFactory";
	public static final String LOYALTY_SERVICE = "LoyaltyService";
	public static final String MARKET_SERVICE = "MarketService";
	public static final String CHARACTER_SERVICE = "CharacterService";
	public static final String ICONFIGURATION_SERVICE = "IConfigurationService";
	public static final String IFILE_SYSTEM = "IFileSystem";
	public static final String ISTORE_CACHE = "IStoreCache";
	public static final String IDATA_STORE = "IDataStore";
	public static final String ISDE_DATABASE_SERVICE = "ISDEDatabaseService";
	public static final String INEOCOM_DATABASE_SERVICE = "INeoComDatabaseService";
	public static final String REDIS_DATABASE_URL = "RedisDatabaseURL";

	@Override
	protected void configure() {
		this.bind( RetrofitService.class )
				.annotatedWith( Names.named( RETROFIT_SERVICE ) )
				.to( RetrofitService.class )
				.in( Singleton.class );
		this.bind( LocationCatalogService.class )
				.annotatedWith( Names.named( LOCATION_CATALOG_SERVICE ) )
				.to( LocationCatalogService.class )
				.in( Singleton.class );
		this.bind( ESIDataService.class )
				.annotatedWith( Names.named( ESIDATA_SERVICE ) )
				.to( ESIDataService.class )
				.in( Singleton.class );
		this.bind( ResourceFactory.class )
				.annotatedWith( Names.named( RESOURCE_FACTORY ) )
				.to( ResourceFactory.class )
				.in( Singleton.class );
		this.bind( LoyaltyService.class )
				.annotatedWith( Names.named( LOYALTY_SERVICE ) )
				.to( LoyaltyService.class )
				.in( Singleton.class );
		this.bind( MarketService.class )
				.annotatedWith( Names.named( MARKET_SERVICE ) )
				.to( MarketService.class )
				.in( Singleton.class );
		this.bind( CharacterService.class )
				.annotatedWith( Names.named( CHARACTER_SERVICE ) )
				.to( CharacterService.class )
				.in( Singleton.class );
		//		this.bind( IDataStore.class )
		//				.annotatedWith( Names.named( IDATA_STORE ) )
		//				.to( RedisDataStoreImplementation.class )
		//				.in( Singleton.class );
	}
}