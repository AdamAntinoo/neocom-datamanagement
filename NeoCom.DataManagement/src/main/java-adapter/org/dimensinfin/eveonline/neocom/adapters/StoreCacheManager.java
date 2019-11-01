package org.dimensinfin.eveonline.neocom.adapters;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;

import io.reactivex.Maybe;
import io.reactivex.Single;
import okio.BufferedSource;

/**
 * This class will deal with external storage of cached data. For that I will use already known libraries.
 * The caches should be externally configurable and other components may register new caches for their usage using
 * the external module as the cache element provider in case there is a miss on the cached data.
 */
@NeoComAdapter
public class StoreCacheManager {
	private static final int CACHE_VERSION = 151;
	private static final int CACHE_COUNTER = 2;
	private static final ObjectMapper jsonMapper = new ObjectMapper();

	static {
		jsonMapper.enable( SerializationFeature.INDENT_OUTPUT );
		jsonMapper.registerModule( new JodaModule() );
	}

	// - C O M P O N E N T S
	private IConfigurationProvider configurationProvider;
	private IFileSystem fileSystem;
	private ESIUniverseDataProvider esiUniverseDataProvider;
	private ESIDataAdapter esiDataAdapter;

	// - C A C H E S
//	private String cacheRegistry;

	private Store<GetUniverseTypesTypeIdOk, Integer> esiItemStore;
	private Store<GetUniverseGroupsGroupIdOk, Integer> itemGroupStore;
	private Store<GetUniverseCategoriesCategoryIdOk, Integer> categoryStore;
	//	private Store<GetUniverseCategoriesCategoryIdOk, Integer> categoryStore;
	private Store<GetUniverseSystemsSystemIdOk, Integer> systemsStoreCache;
	private Store<GetUniverseConstellationsConstellationIdOk, Integer> constellationsStoreCache;
	private Store<GetUniverseRegionsRegionIdOk, Integer> regionStoreCache;

	// - S T O R A G E S
	private DiskLruCache esiItemPersistentStore;
	private DiskLruCache systemsStoreCachePersistence;
	private DiskLruCache constellationsStoreCachePersistence;
	private DiskLruCache regionStoreCachePersistence;

	// - C O N S T R U C T O R S
	private StoreCacheManager() { }

//	public void registerCache() {
//
//	}

	private void createStores() {
		this.createEsiItemStore();
		this.createItemGroupStore();
		this.createItemCategoryStore();

		this.createSystemsStore();
	}

	private void createSystemsStore() {
		this.systemsStoreCache = StoreBuilder.<Integer, BufferedSource, GetUniverseSystemsSystemIdOk>parsedWithKey()
				.fetcher( (Fetcher) new UniverseSystemFetcher( this.esiUniverseDataProvider ) )
				.open();
	}

	private void createEsiItemStore() {
		try {
			final File cachedir = new File( this.fileSystem.accessResource4Path(
					this.configurationProvider.getResourceString( "P.cache.directory.path" ) +
							"/" + this.configurationProvider.getResourceString( "P.cache.directory.store.esiitem" ) ) );
			this.esiItemPersistentStore = DiskLruCache.open( cachedir, CACHE_VERSION, CACHE_COUNTER, 2 * StorageUnits.GIGABYTES );
			this.esiItemStore = StoreBuilder.<Integer, GetUniverseTypesTypeIdOk>key()
					.fetcher( typeId -> Single.just( this.esiDataAdapter.getUniverseTypeById( typeId ) ) )
					//										.fetcher(new UniverseTypeFetcher(esiDataAdapter))
					.persister( new EseItemPersistent( esiItemPersistentStore ) )
					.networkBeforeStale()
					.open();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new NeoComRuntimeException( "Unable to create the item cache store." );
		}
	}

	private void createItemGroupStore() {
		this.itemGroupStore = StoreBuilder.<Integer, BufferedSource, GetUniverseGroupsGroupIdOk>parsedWithKey()
				.fetcher( (Fetcher) new ItemGroupFetcher( this.esiDataAdapter ) )
				.open();
	}

	private void createItemCategoryStore() {
		this.categoryStore = StoreBuilder.<Integer, BufferedSource, GetUniverseCategoriesCategoryIdOk>parsedWithKey()
				.fetcher( (Fetcher) new ItemCategoryFetcher( this.esiDataAdapter ) )
				.open();
	}

	// - C A C H E   E X P O R T E D   A P I
	public Single<GetUniverseTypesTypeIdOk> accessItem( final Integer itemId ) {
		return this.esiItemStore.get( itemId );
	}

	public Single<GetUniverseGroupsGroupIdOk> accessGroup( final Integer groupId ) {
		return this.itemGroupStore.get( groupId );
	}

	public Single<GetUniverseCategoriesCategoryIdOk> accessCategory( final Integer categoryId ) {
		return this.categoryStore.get( categoryId );
	}

	// - B U I L D E R
	public static class Builder {
		private StoreCacheManager onConstruction;

		/**
		 * This Builder declares the mandatory components to be linked on construction so the Null validation is done as soon as
		 * possible.
		 */
		public Builder() {
			this.onConstruction = new StoreCacheManager();
		}

		public Builder withEsiDataAdapter( final ESIDataAdapter esiDataAdapter ) {
			this.onConstruction.esiDataAdapter = esiDataAdapter;
			return this;
		}

		public Builder withConfigurationProvider( final IConfigurationProvider configurationProvider ) {
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public Builder withFileSystem( final IFileSystem fileSystem ) {
			this.onConstruction.fileSystem = fileSystem;
			return this;
		}

		public StoreCacheManager build() {
			Objects.requireNonNull( this.onConstruction.esiDataAdapter );
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			Objects.requireNonNull( this.onConstruction.fileSystem );
			this.onConstruction.createStores(); // Run the initialisation code.
			return this.onConstruction;
		}
	}

	// - E V E I T E M F E T C H E R
	public static class UniverseTypeFetcher implements Fetcher<GetUniverseTypesTypeIdOk, Integer> {
		private ESIDataAdapter esiDataAdapter;

		public UniverseTypeFetcher( final ESIDataAdapter esiDataAdapter ) {
			this.esiDataAdapter = esiDataAdapter;
		}

		@Override
		public Single<GetUniverseTypesTypeIdOk> fetch( final Integer typeId ) {
			return Single.just( this.esiDataAdapter.getUniverseTypeById( typeId ) );
		}
	}

	// - I T E M G R O U P F E T C H E R
	public static class ItemGroupFetcher implements Fetcher<GetUniverseGroupsGroupIdOk, Integer> {
		private ESIDataAdapter esiDataAdapter;

		public ItemGroupFetcher( final ESIDataAdapter esiDataAdapter ) {
			this.esiDataAdapter = esiDataAdapter;
		}

		@Override
		public Single<GetUniverseGroupsGroupIdOk> fetch( final Integer typeId ) {
			return Single.just( this.esiDataAdapter.getUniverseGroupById( typeId ) );
		}
	}

	// - I T E M C A T E G O R Y F E T C H E R
	public static class ItemCategoryFetcher implements Fetcher<GetUniverseCategoriesCategoryIdOk, Integer> {
		private ESIDataAdapter esiDataAdapter;

		public ItemCategoryFetcher( final ESIDataAdapter esiDataAdapter ) {
			this.esiDataAdapter = esiDataAdapter;
		}

		@Override
		public Single<GetUniverseCategoriesCategoryIdOk> fetch( final Integer typeId ) {
			return Single.just( this.esiDataAdapter.getUniverseCategoryById( typeId ) );
		}
	}

	public static class EseItemPersistent implements Persister<GetUniverseTypesTypeIdOk, Integer>, RecordProvider<Integer> {
		private static final long ITEM_CACHE_TIME = TimeUnit.DAYS.toMillis( 1 );
		private static final int DATA_CACHE_INDEX = 0;
		private static final int TIMESTAMP_CACHE_INDEX = 1;
		private DiskLruCache persistentStorage;

		public EseItemPersistent( final DiskLruCache persistentStorage ) {
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
				editor.set( TIMESTAMP_CACHE_INDEX, Long.valueOf( DateTime.now().getMillis() ).toString() );
				editor.commit();
				return Single.just( true );
			} catch (JsonProcessingException jpe) {
				return Single.just( false );
			} catch (IOException ioe) {
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
