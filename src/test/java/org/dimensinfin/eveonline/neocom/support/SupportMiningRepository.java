package org.dimensinfin.eveonline.neocom.support;

import java.sql.SQLException;
import java.util.Objects;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;

public class SupportMiningRepository extends MiningRepository {
	private ConnectionSource connection4Transaction;

	public void deleteAll() throws SQLException {
		TableUtils.clearTable( this.connection4Transaction, MiningExtractionEntity.class );
	}

	// - B U I L D E R
	public static class Builder {
		private SupportMiningRepository onConstruction;

		public Builder() {
			this.onConstruction = new SupportMiningRepository();
		}

		public SupportMiningRepository build() {
			Objects.requireNonNull( this.onConstruction.miningExtractionDao );
			Objects.requireNonNull( this.onConstruction.locationCatalogService );
			return this.onConstruction;
		}

		public SupportMiningRepository.Builder withConnection4Transaction( final ConnectionSource connection ) {
			Objects.requireNonNull( connection );
			this.onConstruction.connection4Transaction = connection;
			return this;
		}

		public SupportMiningRepository.Builder withLocationCatalogService( final LocationCatalogService locationCatalogService ) {
			Objects.requireNonNull( locationCatalogService );
			this.onConstruction.locationCatalogService = locationCatalogService;
			return this;
		}

		public SupportMiningRepository.Builder withMiningExtractionDao( final Dao<MiningExtractionEntity, String> miningExtractionDao ) {
			Objects.requireNonNull( miningExtractionDao );
			this.onConstruction.miningExtractionDao = miningExtractionDao;
			return this;
		}
	}
}
