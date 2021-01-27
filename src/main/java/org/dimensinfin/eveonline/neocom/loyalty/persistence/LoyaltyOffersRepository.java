package org.dimensinfin.eveonline.neocom.loyalty.persistence;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import javax.validation.constraints.NotNull;

import com.google.inject.name.Named;
import com.j256.ormlite.dao.Dao;

import org.dimensinfin.eveonline.neocom.database.NeoComDatabaseService;
import org.dimensinfin.eveonline.neocom.service.DMServicesDependenciesModule;
import org.dimensinfin.logging.LogWrapper;

import static org.dimensinfin.eveonline.neocom.database.repositories.DatabaseFieldNames.CORPORATIONID_FIELDNAME;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class LoyaltyOffersRepository {
	private final Dao<LoyaltyOfferEntity, Integer> loyaltyOfferDao;

	// - C O N S T R U C T O R S
	public LoyaltyOffersRepository( @NotNull @Named(DMServicesDependenciesModule.NEOCOM_DATABASE_SERVICE) final NeoComDatabaseService neoComDatabaseService ) throws SQLException {
		this.loyaltyOfferDao = neoComDatabaseService.getLoyaltyOfferDao();
	}

	public void persist( final LoyaltyOfferEntity loyaltyOfferEntity ) throws SQLException {
		if (null != loyaltyOfferEntity) {
			loyaltyOfferEntity.timeStamp();
			this.loyaltyOfferDao.createOrUpdate( loyaltyOfferEntity );
			LogWrapper.info( MessageFormat.format( "Write/Update loyalty offer. Id: {0} - type {1}",
					loyaltyOfferEntity.getOfferId(),
					loyaltyOfferEntity.getTypeName() )
			);
		}
	}

	public List<LoyaltyOfferEntity> searchOffers4Corporation( final int corporationId ) throws SQLException {
		return this.loyaltyOfferDao.queryForEq( CORPORATIONID_FIELDNAME, corporationId );
	}
}