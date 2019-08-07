package org.dimensinfin.eveonline.neocom.database.repositories;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MiningRepository {
	private static Logger logger = LoggerFactory.getLogger(MiningRepository.class);
	private Dao<MiningExtraction, String> miningExtractionDao;

	/**
	 * This other method does the same Mining Extractions processing but only for the records for the current date. The difference is
	 * that today records are aggregated by hour instead of by day. So we will have a record for one ore/system since the hour we did the
	 * extractions until the 23 hours. The first extraction will add to the hour until the next hour starts. Then the accounting for this
	 * new hour will show the new ore totals and so on hour after hour.
	 */
	private List<MiningExtraction> extractions4Test = new ArrayList<>();

	protected MiningRepository() { }

	public List<MiningExtraction> accessTodayMiningExtractions4Pilot( final Credential credential ) {
		try {
			final QueryBuilder<MiningExtraction, String> builder = this.miningExtractionDao.queryBuilder();
			final Where<MiningExtraction, String> where = builder.where();
			where.eq("ownerId", credential.getAccountId());
			builder.orderBy("extractionDateName", true);
			builder.orderBy("extractionHour", true);
			builder.orderBy("solarSystemId", true);
			builder.orderBy("typeId", true);
			final PreparedQuery<MiningExtraction> preparedQuery = builder.prepare();
			logger.info("-- [MiningRepository.accessTodayMiningExtractions4Pilot]> SELECT: {}",preparedQuery.getStatement());
			final List<MiningExtraction> dataList = this.miningExtractionDao.query(preparedQuery);
			List<MiningExtraction> results = new ArrayList<>();
			final String filterDate = DateTime.now().toString("YYYY/MM/dd");
			// Filter out all records not belonging to today.
			for (MiningExtraction extraction : dataList) {
				final String date = extraction.getExtractionDateName().split(":")[0];
				if (date.equalsIgnoreCase(filterDate)) results.add(extraction);
			}
			return results;
		}catch (SQLException sqle){
			logger.error("");
			return new ArrayList<>();
		}
	}

	public MiningExtraction accessMiningExtractionFindById( final String recordIdentifier ) throws SQLException {
		return this.miningExtractionDao.queryForId(recordIdentifier);
	}

	public void persist( final MiningExtraction record ) throws SQLException {
		this.miningExtractionDao.createOrUpdate(record);
	}

	/**
	 * Get the list of Mining Extractions that are registered on the database. This can be a lot of records that need sorting and also
	 * grouping previously to rendering. This method can do the sorting but the grouping is not one of its features.
	 * The mining operations for a single day aggregate all the ore for a single type, but have different records for different systems
	 * and for different ores. So for a single day we can have around 6-8 records. The mining ledger information at the neocom database has
	 * to expiration time so the number of days is still not predetermined.
	 */
	public List<MiningExtraction> accessMiningExtractions4Pilot( final Credential credential ) throws SQLException {
		final QueryBuilder<MiningExtraction, String> builder = this.miningExtractionDao.queryBuilder();
		final Where<MiningExtraction, String> where = builder.where();
		where.eq("ownerId", credential.getAccountId());
		builder.orderBy("id", false);
		final PreparedQuery<MiningExtraction> preparedQuery = builder.prepare();
		return this.miningExtractionDao.query(preparedQuery);
	}

	// - B U I L D E R
	public static class Builder {
		private MiningRepository onConstruction;

		public Builder() {
			this.onConstruction = new MiningRepository();
		}

		public Builder withMiningExtractionDao( final Dao<MiningExtraction, String> miningExtractionDao ) {
			this.onConstruction.miningExtractionDao = miningExtractionDao;
			return this;
		}

		public MiningRepository build() {
			Objects.requireNonNull(this.onConstruction.miningExtractionDao);
			return this.onConstruction;
		}
	}
}
