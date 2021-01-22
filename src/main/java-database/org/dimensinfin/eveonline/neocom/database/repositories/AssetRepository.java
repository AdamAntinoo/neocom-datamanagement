package org.dimensinfin.eveonline.neocom.database.repositories;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.NotNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import org.dimensinfin.eveonline.neocom.database.NeoComDatabaseService;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.service.DMServicesDependenciesModule;
import org.dimensinfin.logging.LogWrapper;

public class AssetRepository {
	private static final String OWNER_ID = "ownerId";
	private static final String ASSET_ID = "assetId";
	private static final String ASSETS_READ_LOG_MESSAGE = "Assets read: {0}";
	private final NeoComDatabaseService neoComDatabaseService;
	private Dao<NeoAsset, UUID> assetDao;

	// - C O N S T R U C T O R S
	@Inject
	public AssetRepository( @NotNull @Named(DMServicesDependenciesModule.NEOCOM_DATABASE_SERVICE) final NeoComDatabaseService neoComDatabaseService ) throws SQLException {
		this.neoComDatabaseService = neoComDatabaseService;
		this.assetDao = this.neoComDatabaseService.getAssetDao();
	}

	/**
	 * removes from the application database any asset and blueprint that contains the special -1 code as the
	 * owner identifier. Those records are from older downloads and have to be removed to avoid merging with the
	 * new download.
	 */
	public synchronized void clearInvalidRecords( final long pilotIdentifier ) {
		LogWrapper.enter( MessageFormat.format( "pilotIdentifier: {0}", pilotIdentifier ) );
		final ConnectionSource connection4Transaction = this.assetDao.getConnectionSource();
		try {
			TransactionManager.callInTransaction( connection4Transaction, () -> {
				// Remove all assets that do not have a valid owner.
				final DeleteBuilder<NeoAsset, UUID> deleteBuilder = assetDao.deleteBuilder();
				deleteBuilder.where().eq( OWNER_ID, (pilotIdentifier * -1) );
				int count = deleteBuilder.delete();
				LogWrapper.info( MessageFormat.format( "Invalid assets cleared for owner {}: {}",
						(pilotIdentifier * -1), count ) );
				return null;
			} );
		} catch (final SQLException sqle) {
			LogWrapper.error( sqle );
		} finally {
			LogWrapper.exit();
		}
	}

	/**
	 * Get the complete list of the assets that belong to this owner.
	 */
	public List<NeoAsset> findAllByOwnerId( final Integer ownerId ) {
		Objects.requireNonNull( ownerId );
		try {
			QueryBuilder<NeoAsset, UUID> queryBuilder = this.assetDao.queryBuilder();
			Where<NeoAsset, UUID> where = queryBuilder.where();
			where.eq( OWNER_ID, ownerId );
			final List<NeoAsset> assetList = assetDao.query( queryBuilder.prepare() );
			LogWrapper.info( MessageFormat.format( ASSETS_READ_LOG_MESSAGE, assetList.size() ) );
			return Stream.of( assetList )
					.map( this::assetReconstructor )
					.collect( Collectors.toList() );
		} catch (java.sql.SQLException sqle) {
			LogWrapper.error( sqle );
			return new ArrayList<>();
		}
	}

	public NeoAsset findAssetById( final Long assetId ) {
		Objects.requireNonNull( assetId );
		try {
			final List<NeoAsset> assetList = this.assetDao.queryForEq( ASSET_ID, assetId );
			LogWrapper.info( MessageFormat.format( ASSETS_READ_LOG_MESSAGE, assetList.size() ) );
			if (!assetList.isEmpty()) return assetList.get( 0 );
			else return null;
		} catch (java.sql.SQLException sqle) {
			LogWrapper.error( sqle );
			return null;
		}
	}

	public void persist( final NeoAsset record ) throws SQLException {
		if (null != record) {
			record.timeStamp();
			record.generateUid();
			this.assetDao.createOrUpdate( record );
			LogWrapper.info( MessageFormat.format( "Wrote asset to database id [{0}]", record.getAssetId() ) );
		}
	}

	/**
	 * Changes the owner id for all records from a new download with the id of the current character. This
	 * completes the download and the assignment of the resources to the character without interrupting the
	 * processing of data by the application.
	 */
	public synchronized void replaceAssets( final long pilotIdentifier ) {
		LogWrapper.enter( MessageFormat.format( "pilotIdentifier: {}", Long.toString( pilotIdentifier ) ) );
		final ConnectionSource connection4Transaction = this.assetDao.getConnectionSource();
		try {
			TransactionManager.callInTransaction( connection4Transaction, () -> {
				// Remove all assets from this owner before adding the new set.
				final DeleteBuilder<NeoAsset, UUID> deleteBuilder = this.assetDao.deleteBuilder();
				deleteBuilder.where().eq( OWNER_ID, pilotIdentifier );
				int count = deleteBuilder.delete();
				LogWrapper.info( MessageFormat.format(
						"-- [AndroidNeoComDBHelper.replaceAssets]> Invalid assets cleared for owner {0}: {1}",
						pilotIdentifier,
						count )
				);
				// Replace the owner to vake the assets valid.
				final UpdateBuilder<NeoAsset, UUID> updateBuilder = this.assetDao.updateBuilder();
				updateBuilder.updateColumnValue( OWNER_ID, pilotIdentifier )
						.where().eq( OWNER_ID, (pilotIdentifier * -1) );
				count = updateBuilder.update();
				LogWrapper.info( MessageFormat.format(
						"-- [AndroidNeoComDBHelper.replaceAssets]> Replace owner {} for assets: {}",
						pilotIdentifier, count )
				);
				return null;
			} );
		} catch (final SQLException sqle) {
			LogWrapper.error( sqle );
		} finally {
			LogWrapper.exit();
		}
	}

	private NeoAsset assetReconstructor( final NeoAsset target ) {
		LogWrapper.enter( "Reconstructing asset: " + target.getAssetId() );
		target.setItemDelegate( new NeoItem( target.getTypeId() ) );
		if (target.hasParentContainer()) { // Search for the parent asset. If not found then report a warning.
			final NeoAsset parent = this.findAssetById( target.getParentContainerId() );
			if (null != parent) target.setParentContainer( this.assetReconstructor( parent ) );
			else {
				LogWrapper.error( "Parent asset not found on post read action: " +
								target.getParentContainerId(),
						new NeoComRuntimeException( "If an asset has a parent identifier then it should have a matching asset " +
								"instance." ) );
			}
		}
		return target;
	}
}
