package org.dimensinfin.eveonline.neocom.database.repositories;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import org.joda.time.LocalDate;

import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.exception.ErrorInfoCatalog;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.miningextraction.converter.MiningExtractionEntityToMiningExtractionConverter;
import org.dimensinfin.eveonline.neocom.miningextraction.domain.MiningExtraction;
import org.dimensinfin.eveonline.neocom.service.ResourceFactory;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

import static org.dimensinfin.eveonline.neocom.database.repositories.DatabaseFieldNames.ID_FIELDNAME;
import static org.dimensinfin.eveonline.neocom.database.repositories.DatabaseFieldNames.OWNERID_FIELDNAME;

/**
 * The mining repository isolates the repository fro mining data from the details related to the search and access to the records. Current
 * implementation expects the records persisted on a database repository.
 *
 * There is a class specific to store extractions since their contents are volatile and not stored on the repository. So methods now get the
 * records from the repository and <b>expand</b> the volatile contents to have again a full featured <code>MiningExtraction</code>.
 *
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.19.0
 */
public class MiningRepository {
	// - C O M P O N E N T S
	protected Dao<MiningExtractionEntity, String> miningExtractionDao;
	protected LocationCatalogService locationCatalogService;
	private ResourceFactory resourceFactory;

// - C O N S T R U C T O R S
	/**
	 * This other method does the same Mining Extractions processing but only for the records for the current date. The difference is that today
	 * records are aggregated by hour instead of by day. So we will have a record for one ore/system since the hour we did the extractions until
	 * the 23 hours. The first extraction will add to the hour until the next hour starts. Then the accounting for this new hour will show the new
	 * ore totals and so on hour after hour.
	 */
	protected MiningRepository() { }

	public MiningExtraction accessMiningExtractionFindById( final String recordIdentifier ) {
		try {
			final MiningExtractionEntity extraction = this.miningExtractionDao.queryForId( recordIdentifier );
			if (null != extraction) return new MiningExtractionEntityToMiningExtractionConverter( this.locationCatalogService, this.resourceFactory )
					.convert( this.miningExtractionDao.queryForId( recordIdentifier ) );
			else return null;
		} catch (final SQLException sqle) {
			NeoComLogger.error( ErrorInfoCatalog.MINING_EXTRACTION_BYID_SEARCH_FAILED.getErrorMessage( recordIdentifier ), sqle );
			throw new NeoComRuntimeException( ErrorInfoCatalog.MINING_EXTRACTION_BYID_SEARCH_FAILED.getErrorMessage( recordIdentifier ) );
		}
	}

	/**
	 * Get the list of Mining Extractions that are registered on the database. This can be a lot of records that need sorting and
	 * also
	 * grouping previously to rendering. This method can do the sorting but the grouping is not one of its features.
	 * The mining operations for a single day aggregate all the ore for a single type, but have different records for different
	 * systems
	 * and for different ores. So for a single day we can have around 6-8 records. The mining ledger information at the neocom
	 * database has
	 * to expiration time so the number of days is still not predetermined.
	 */
	public List<MiningExtraction> accessMiningExtractions4Pilot( final Credential credential ) {
		return Stream.of( this.queryMiningExtractions4Pilot( credential ) )
				.map( ( extraction ) -> new MiningExtractionEntityToMiningExtractionConverter( this.locationCatalogService, this.resourceFactory )
						.convert( extraction ) )
				.collect( Collectors.toList() );
	}

	public List<MiningExtraction> accessResources4Date( final Credential credential, final LocalDate filterDate ) {
		return Stream.of( this.queryResources4Date( credential, filterDate ) )
				.map( ( extraction ) -> new MiningExtractionEntityToMiningExtractionConverter( this.locationCatalogService, this.resourceFactory )
						.convert( extraction ) )
				.collect( Collectors.toList() );
	}

	public List<MiningExtraction> accessTodayMiningExtractions4Pilot( final Credential credential ) {
		return Stream.of( this.queryDatedMiningExtractions4Pilot( credential ) )
				.filter( this::filterOutNotTodayRecords )
				.map( ( extraction ) -> new MiningExtractionEntityToMiningExtractionConverter( this.locationCatalogService, this.resourceFactory )
						.convert( extraction ) )
				.collect( Collectors.toList() );
	}

	public List<MiningExtractionEntity> accessTodayMiningExtractions4PilotNotTransformed( final Credential credential ) {
		return Stream.of( this.queryDatedMiningExtractions4Pilot( credential ) )
				.filter( this::filterOutNotTodayRecords )
				.collect( Collectors.toList() );
	}

	public void persist( final MiningExtractionEntity record ) throws SQLException {
		if (null != record) {
			record.timeStamp();
			this.miningExtractionDao.createOrUpdate( record );
		}
	}

	/**
	 * Filter out all records not belonging to today.
	 *
	 * @param extraction the extraction to check for filtering
	 * @return true if the record should be kept because it has todays date.
	 */
	private boolean filterOutNotTodayRecords( final MiningExtractionEntity extraction ) {
		final String filterDate = LocalDate.now().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT );
		return extraction.getExtractionDateName().equalsIgnoreCase( filterDate );
	}

	private List<MiningExtractionEntity> queryDatedMiningExtractions4Pilot( final Credential credential ) {
		try {
			final QueryBuilder<MiningExtractionEntity, String> builder = this.miningExtractionDao.queryBuilder();
			final Where<MiningExtractionEntity, String> where = builder.where();
			where.eq( OWNERID_FIELDNAME, credential.getAccountId() );
			builder.orderBy( "extractionDateName", true );
			builder.orderBy( "extractionHour", true );
			builder.orderBy( "solarSystemId", true );
			builder.orderBy( "typeId", true );
			final PreparedQuery<MiningExtractionEntity> preparedQuery = builder.prepare();
			NeoComLogger.info( MessageFormat.format("SELECT: {0}", preparedQuery.getStatement() ));
			final List<MiningExtractionEntity> dataList = this.miningExtractionDao.query( preparedQuery );
			NeoComLogger.info( MessageFormat.format("Records read: {0}", dataList.size() ) );
			return dataList;
		} catch (SQLException sqle) {
			NeoComLogger.error( "SQL [MiningRepository.accessDatedMiningExtractions4Pilot]> SQL Exception: {}", sqle );
			return new ArrayList<>();
		}
	}

	private List<MiningExtractionEntity> queryMiningExtractions4Pilot( final Credential credential ) {
		try {
			final QueryBuilder<MiningExtractionEntity, String> builder = this.miningExtractionDao.queryBuilder();
			final Where<MiningExtractionEntity, String> where = builder.where();
			where.eq( OWNERID_FIELDNAME, credential.getAccountId() );
			builder.orderBy( ID_FIELDNAME, false );
			final PreparedQuery<MiningExtractionEntity> preparedQuery = builder.prepare();
			NeoComLogger.info( MessageFormat.format("SELECT: {0}", preparedQuery.getStatement() ));
			final List<MiningExtractionEntity> dataList = this.miningExtractionDao.query( preparedQuery );
			NeoComLogger.info( MessageFormat.format("Records read: {0}", dataList.size() ) );
			return dataList;
		} catch (final SQLException sqle) {
			NeoComLogger.error( sqle );
			return new ArrayList<>();
		}
	}

	private List<MiningExtractionEntity> queryResources4Date( final Credential credential, final LocalDate filterDate ) {
		try {
			final QueryBuilder<MiningExtractionEntity, String> builder = this.miningExtractionDao.queryBuilder();
			final Where<MiningExtractionEntity, String> where = builder.where();
			where.eq( OWNERID_FIELDNAME, credential.getAccountId() )
					.and()
					.eq( "extractionDateName", filterDate.toString( "YYYY-MM-dd" ) );
			builder.selectRaw( "\"typeId\"", "MAX(\"quantity\")" );
			builder.groupBy( "typeId" );
			NeoComLogger.info( MessageFormat.format("SELECT: {0}",
					builder.prepareStatementString()) );
			final GenericRawResults<String[]> dataList = this.miningExtractionDao.queryRaw( builder.prepareStatementString() );
			NeoComLogger.info( MessageFormat.format("Records read: {0}", dataList.getResults().size() ) );
			List<MiningExtractionEntity> results = new ArrayList<>();
			for (String[] record : dataList.getResults()) {
				try {
					results.add( this.miningExtractionDao.queryForId( record[0] ) );
				} catch (SQLException sqle) {
					NeoComLogger.error( sqle );
				}
			}
			return results;
		} catch (SQLException sqle) {
			NeoComLogger.error( "SQL Exception: {}", sqle );
			return new ArrayList<>();
		}
	}

	// - B U I L D E R
	public static class Builder {
		private MiningRepository onConstruction;

// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new MiningRepository();
		}

		public MiningRepository build() {
			Objects.requireNonNull( this.onConstruction.miningExtractionDao );
			Objects.requireNonNull( this.onConstruction.locationCatalogService );
			return this.onConstruction;
		}

		public MiningRepository.Builder withLocationCatalogService( final LocationCatalogService locationCatalogService ) {
			Objects.requireNonNull( locationCatalogService );
			this.onConstruction.locationCatalogService = locationCatalogService;
			return this;
		}

		public MiningRepository.Builder withMiningExtractionDao( final Dao<MiningExtractionEntity, String> miningExtractionDao ) {
			Objects.requireNonNull( miningExtractionDao );
			this.onConstruction.miningExtractionDao = miningExtractionDao;
			return this;
		}

		public MiningRepository.Builder withResourceFactory( final ResourceFactory resourceFactory ) {
			Objects.requireNonNull( resourceFactory );
			this.onConstruction.resourceFactory = resourceFactory;
			return this;
		}
	}
}
