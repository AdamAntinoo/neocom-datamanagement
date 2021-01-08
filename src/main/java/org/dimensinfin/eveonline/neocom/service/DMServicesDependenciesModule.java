package org.dimensinfin.eveonline.neocom.service;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class DMServicesDependenciesModule extends AbstractModule {
	public static final String RETROFIT_SERVICE = "RetrofitService";
	public static final String ESIDATA_SERVICE = "ESIDataService";
	public static final String RESOURCE_FACTORY = "ResourceFactory";
	public static final String ICONFIGURATION_SERVICE = "IConfigurationService";
	public static final String IFILE_SYSTEM = "IFileSystem";
	public static final String ISTORE_CACHE = "IStoreCache";
	public static final String LOCATION_CATALOG_SERVICE = "LocationCatalogService";
	public static final String ISDE_DATABASE_SERVICE = "ISDEDatabaseService";
	public static final String NEOCOM_DATABASE_SERVICE = "NeoComDatabaseService";

	@Override
	protected void configure() {
		bind( RetrofitService.class )
				.annotatedWith( Names.named( RETROFIT_SERVICE ) )
				.to( RetrofitService.class )
				.in( Singleton.class );
		bind( LocationCatalogService.class )
				.annotatedWith( Names.named( LOCATION_CATALOG_SERVICE ) )
				.to( LocationCatalogService.class )
				.in( Singleton.class );
		bind( ESIDataService.class )
				.annotatedWith( Names.named( ESIDATA_SERVICE ) )
				.to( ESIDataService.class )
				.in( Singleton.class );
		bind( ResourceFactory.class )
				.annotatedWith( Names.named( RESOURCE_FACTORY ) )
				.to( ResourceFactory.class )
				.in( Singleton.class );
	}
}