package org.dimensinfin.eveonline.neocom.adapter;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.jakewharton.disklrucache.DiskLruCache;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.RecordProvider;
import com.nytimes.android.external.store3.base.RecordState;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;
import org.joda.time.DateTime;

import org.dimensinfin.eveonline.neocom.annotation.NeoComAdapter;
import org.dimensinfin.eveonline.neocom.core.StorageUnits;
import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;
import org.dimensinfin.logging.LogWrapper;

import io.reactivex.Maybe;
import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.Retrofit;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.CACHE_DIRECTORY_PATH;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.CACHE_STORE_ESI_ITEM_DATA;

/**
 * This class will deal with external storage of cached data. For that I will use already known libraries.
 * The caches should be externally configurable and other components may register new caches for their usage using
 * the external module as the cache element provider in case there is a miss on the cached data.
 */
@NeoComAdapter
public class StoreCacheManager {
	private static final String PATH_DELIMITER = "/";
	private static final int CACHE_VERSION = 151;
	private static final int CACHE_COUNTER = 2;
	private static final ObjectMapper jsonMapper = new ObjectMapper();

	static {
		jsonMapper.enable( SerializationFeature.INDENT_OUTPUT );
		jsonMapper.registerModule( new JodaModule() );
	}

	// - C O M P O N E N T S
	protected IConfigurationService configurationProvider;
	protected IFileSystem fileSystemAdapter;
	protected RetrofitFactory retrofitFactory;

	// - C A C H E S
	private Store<GetUniverseTypesTypeIdOk, Integer> esiItemStore;
	private Store<GetUniverseGroupsGroupIdOk, Integer> itemGroupStore;
	private Store<GetUniverseCategoriesCategoryIdOk, Integer> categoryStore;
	//	private Store<GetUniverseStationsStationIdOk, Integer> stationStore;
	private Store<GetUniverseSystemsSystemIdOk, Integer> systemsStoreCache;
	//	private Store<GetUniverseConstellationsConstellationIdOk, Integer> constellationsStoreCache;
	//	private Store<GetUniverseRegionsRegionIdOk, Integer> regionStoreCache;

	// - S T O R A G E S
	private DiskLruCache esiItemPersistentStore;
	//	private DiskLruCache systemsStoreCachePersistence;
	//	private DiskLruCache constellationsStoreCachePersistence;
	//	private DiskLruCache regionStoreCachePersistence;

	// - C O N S T R U C T O R S
	protected StoreCacheManager() { }

	public Single<GetUniverseCategoriesCategoryIdOk> accessCategory( final Integer categoryId ) {
		return this.categoryStore.get( categoryId );
	}

	public Single<GetUniverseGroupsGroupIdOk> accessGroup( final Integer groupId ) {
		return this.itemGroupStore.get( groupId );
	}

	// - C A C H E   E X P O R T E D   A P I
	public Single<GetUniverseTypesTypeIdOk> accessItem( final Integer itemId ) {
		return this.esiItemStore.get( itemId );
	}

	public Single<GetUniverseSystemsSystemIdOk> accessSolarSystem( final Integer solarSystemId ) {
		return this.systemsStoreCache.get( solarSystemId );
	}

	// - S T O R E S   C R E A T I O N
	private void createEsiItemStore() {
		try {
			final File cachedir = new File( this.fileSystemAdapter.accessResource4Path(
					this.configurationProvider.getResourceString( CACHE_DIRECTORY_PATH ) +
							PATH_DELIMITER +
							this.configurationProvider.getResourceString( CACHE_STORE_ESI_ITEM_DATA ) ) );
			this.esiItemPersistentStore = DiskLruCache.open( cachedir, CACHE_VERSION, CACHE_COUNTER,
					StorageUnits.GIGABYTES.toBytes( 2 ) );
			this.esiItemStore = StoreBuilder.<Integer, GetUniverseTypesTypeIdOk>key()
					.fetcher( new UniverseTypeFetcher( this.retrofitFactory.accessUniverseConnector() ) )
					.persister( new EsiItemPersister( esiItemPersistentStore ) )
					.networkBeforeStale()
					.open();
		} catch (final IOException ioe) {
			NeoComLogger.error( ioe );
			throw new NeoComRuntimeException( "Unable to create the item cache store." );
		}
	}

	private void createItemCategoryStore() {
		this.categoryStore = StoreBuilder.<Integer, GetUniverseCategoriesCategoryIdOk>key()
				.fetcher( new UniverseItemCategoryFetcher( this.retrofitFactory.accessUniverseConnector() ) )
				.open();
	}

	private void createItemGroupStore() {
		this.itemGroupStore = StoreBuilder.<Integer, GetUniverseGroupsGroupIdOk>key()
				.fetcher( new UniverseItemGroupFetcher( this.retrofitFactory.accessUniverseConnector() ) )
				.open();
	}

	private void createStores() {
		this.createEsiItemStore();
		this.createItemGroupStore();
		this.createItemCategoryStore();

		this.createSystemsStore();
	}

	private void createSystemsStore() {
		this.systemsStoreCache = StoreBuilder.<Integer, GetUniverseSystemsSystemIdOk>key()
				.fetcher( new UniverseSystemFetcher( this.retrofitFactory.accessUniverseConnector() ) )
				.open();
	}

	// - E S I   D A T A   A C C E S S
	// - B U I L D E R
	public static class Builder {
		private StoreCacheManager onConstruction;

// - C O N S T R U C T O R S
		/**
		 * This Builder declares the mandatory components to be linked on construction so the Null validation is done as soon as
		 * possible.
		 */
		public Builder() {
			this.onConstruction = new StoreCacheManager();
		}

		public Builder( final StoreCacheManager preInstance ) {
			if (null != preInstance) this.onConstruction = preInstance;
			else this.onConstruction = new StoreCacheManager();
		}

		public StoreCacheManager build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			Objects.requireNonNull( this.onConstruction.fileSystemAdapter );
			Objects.requireNonNull( this.onConstruction.retrofitFactory );
			this.onConstruction.createStores(); // Run the initialisation code.
			return this.onConstruction;
		}

		public StoreCacheManager.Builder withConfigurationProvider( final IConfigurationService configurationProvider ) {
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public StoreCacheManager.Builder withFileSystemAdapter( final IFileSystem fileSystem ) {
			this.onConstruction.fileSystemAdapter = fileSystem;
			return this;
		}

		public StoreCacheManager.Builder withRetrofitFactory( final RetrofitFactory retrofitFactory ) {
			Objects.requireNonNull( retrofitFactory );
			this.onConstruction.retrofitFactory = retrofitFactory;
			return this;
		}
	}

	// - U N I V E R S E T Y P E F E T C H E R
	public static class UniverseTypeFetcher implements Fetcher<GetUniverseTypesTypeIdOk, Integer> {
		private Retrofit neocomRetrofitNoAuth; // HTTP client to be used on not authenticated endpoints.

// - C O N S T R U C T O R S
		public UniverseTypeFetcher( final Retrofit neocomRetrofitNoAuth ) {
			this.neocomRetrofitNoAuth = neocomRetrofitNoAuth;
		}

		@Override
		public Single<GetUniverseTypesTypeIdOk> fetch( final Integer typeId ) {
			try {
				return Single.just( this.getUniverseTypeById( typeId ) );
			} catch (final IOException e) {
				return Single.just( null );
			}
		}

		/**
		 * Go for the item to the ESI server source. This endpoint means that the item is not found on the cache.
		 */
		private GetUniverseTypesTypeIdOk getUniverseTypeById( final int typeId ) throws IOException {
			final Response<GetUniverseTypesTypeIdOk> itemListResponse = this.neocomRetrofitNoAuth
					.create( UniverseApi.class )
					.getUniverseTypesTypeId( typeId,
							ESIDataProvider.DEFAULT_ACCEPT_LANGUAGE,
							ESIDataProvider.DEFAULT_ESI_SERVER, null, null )
					.execute();
			if (itemListResponse.isSuccessful()) {
				LogWrapper.info( MessageFormat.format( "Downloading: {0}-{1}",
						itemListResponse.body().getTypeId(),
						itemListResponse.body().getName() ) );
				return itemListResponse.body();
			}
			return null;
		}
	}

	// - U N I V E R S E I T E M G R O U P F E T C H E R
	public static class UniverseItemGroupFetcher implements Fetcher<GetUniverseGroupsGroupIdOk, Integer> {
		private Retrofit neocomRetrofitNoAuth; // HTTP client to be used on not authenticated endpoints.

// - C O N S T R U C T O R S
		public UniverseItemGroupFetcher( final Retrofit neocomRetrofitNoAuth ) {
			this.neocomRetrofitNoAuth = neocomRetrofitNoAuth;
		}

		@Override
		public Single<GetUniverseGroupsGroupIdOk> fetch( final Integer typeId ) {
			try {
				return Single.just( this.getUniverseGroupById( typeId ) );
			} catch (IOException e) {
				return Single.just( null );
			}
		}

		private GetUniverseGroupsGroupIdOk getUniverseGroupById( final Integer groupId ) throws IOException {
			final Response<GetUniverseGroupsGroupIdOk> groupResponse = this.neocomRetrofitNoAuth
					.create( UniverseApi.class )
					.getUniverseGroupsGroupId( groupId,
							ESIDataProvider.DEFAULT_ACCEPT_LANGUAGE,
							ESIDataProvider.DEFAULT_ESI_SERVER, null, null )
					.execute();
			if (groupResponse.isSuccessful())
				return groupResponse.body();
			return null;
		}
	}

	// - U N I V E R S E I T E M C A T E G O R Y F E T C H E R
	public static class UniverseItemCategoryFetcher implements Fetcher<GetUniverseCategoriesCategoryIdOk, Integer> {
		private Retrofit neocomRetrofitNoAuth; // HTTP client to be used on not authenticated endpoints.

// - C O N S T R U C T O R S
		public UniverseItemCategoryFetcher( final Retrofit neocomRetrofitNoAuth ) {
			this.neocomRetrofitNoAuth = neocomRetrofitNoAuth;
		}

		@Override
		public Single<GetUniverseCategoriesCategoryIdOk> fetch( final Integer typeId ) {
			try {
				return Single.just( this.getUniverseCategoryById( typeId ) );
			} catch (IOException e) {
				return Single.just( null );
			}
		}

		private GetUniverseCategoriesCategoryIdOk getUniverseCategoryById( final Integer categoryId ) throws IOException {
			final Response<GetUniverseCategoriesCategoryIdOk> groupResponse = this.neocomRetrofitNoAuth
					.create( UniverseApi.class )
					.getUniverseCategoriesCategoryId( categoryId,
							ESIDataProvider.DEFAULT_ACCEPT_LANGUAGE,
							ESIDataProvider.DEFAULT_ESI_SERVER, null, null )
					.execute();
			if (groupResponse.isSuccessful())
				return groupResponse.body();
			return null;
		}
	}

	// - E S I I T E M P E R S I S T E R
	public static class EsiItemPersister implements Persister<GetUniverseTypesTypeIdOk, Integer>, RecordProvider<Integer> {
		private static final long ITEM_CACHE_TIME = TimeUnit.DAYS.toMillis( 30 );
		private static final int DATA_CACHE_INDEX = 0;
		private static final int TIMESTAMP_CACHE_INDEX = 1;
		private DiskLruCache persistentStorage;

// - C O N S T R U C T O R S
		public EsiItemPersister( final DiskLruCache persistentStorage ) {
			this.persistentStorage = persistentStorage;
		}

		@Override
		public RecordState getRecordState( final Integer key ) {
			final Long timeStamp = this.accessRecordTimeStamp( key ); // Get the cache record for the timestamp.
			if (null == timeStamp) return RecordState.MISSING;
			if (timeStamp + ITEM_CACHE_TIME < DateTime.now().getMillis()) return RecordState.FRESH;
			return RecordState.STALE;
		}

		@Override
		public Maybe<GetUniverseTypesTypeIdOk> read( final Integer key ) {
			return Maybe.just( this.accessRecord( key ) );
		}

		@Override
		public Single<Boolean> write( final Integer key, final GetUniverseTypesTypeIdOk item ) {
			try {
				final String dataSerialized = jsonMapper.writeValueAsString( item );
				final DiskLruCache.Editor editor = this.persistentStorage.edit( key.toString() );
				editor.set( DATA_CACHE_INDEX, dataSerialized );
				editor.set( TIMESTAMP_CACHE_INDEX, Long.toString( DateTime.now().getMillis() ) );
				editor.commit();
				return Single.just( true );
			} catch (final IOException ioe) {
				return Single.just( false );
			}
		}

		protected GetUniverseTypesTypeIdOk accessRecord( final Integer key ) {
			try {
				if (null == key) return null;
				final DiskLruCache.Snapshot record = this.persistentStorage.get( key.toString() );
				if (null == record) return null;
				final String dataSerialized = record.getString( DATA_CACHE_INDEX );
				if (null == dataSerialized) return null;
				else {
					final GetUniverseTypesTypeIdOk data = jsonMapper.readValue( dataSerialized, GetUniverseTypesTypeIdOk.class );
					if (null != data) return data;
					else return null;
				}
			} catch (IOException e) {
				return null;
			}
		}

		protected Long accessRecordTimeStamp( final Integer key ) {
			if (null == key) return null;
			try {
				final DiskLruCache.Snapshot record = this.persistentStorage.get( key.toString() );
				if (null == record) return null;
				final Long timestamp = Long.valueOf( record.getString( TIMESTAMP_CACHE_INDEX ) );
				return timestamp;
			} catch (IOException ioe) {
				return null;
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
	}
}
