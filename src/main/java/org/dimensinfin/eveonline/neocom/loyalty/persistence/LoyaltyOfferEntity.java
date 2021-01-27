package org.dimensinfin.eveonline.neocom.loyalty.persistence;

import java.util.Objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.dimensinfin.eveonline.neocom.database.entities.UpdatableEntity;
import org.dimensinfin.eveonline.neocom.domain.EsiType;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
@DatabaseTable(tableName = "neocom.LoyaltyOffers")
public class LoyaltyOfferEntity extends UpdatableEntity {
	private static final long serialVersionUID = 6772112244501770693L;

	@DatabaseField(columnName = "offerId", id = true, index = true)
	private int offerId = -1;
	@DatabaseField(columnName = "typeId")
	private int typeId;
	@DatabaseField(columnName = "typeName")
	private String typeName;
	@DatabaseField(columnName = "corporationId")
	private int corporationId;
	@DatabaseField(columnName = "corporationName")
	private String corporationName;
	@DatabaseField(columnName = "lpValue")
	private int lpValue;
	@DatabaseField(columnName = "iskCost")
	private Long iskCost;
	@DatabaseField(columnName = "lpCost")
	private Integer lpCost;
	@DatabaseField(columnName = "quantity")
	private Integer quantity;
	@DatabaseField(columnName = "marketHubId")
	private Integer marketHubId;
	@DatabaseField(columnName = "price")
	private Double price;

	// - C O N S T R U C T O R S
	private LoyaltyOfferEntity() {}

	// - G E T T E R S   &   S E T T E R S
	public int getCorporationId() {
		return this.corporationId;
	}

	public String getCorporationName() {
		return this.corporationName;
	}

	public int getLpValue() {
		return this.lpValue;
	}

	public int getOfferId() {
		return this.offerId;
	}

	public int getTypeId() {
		return this.typeId;
	}

	public String getTypeName() {
		return this.typeName;
	}

	// - B U I L D E R
	public static class Builder {
		private final LoyaltyOfferEntity onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new LoyaltyOfferEntity();
		}

		public LoyaltyOfferEntity build() {
			return this.onConstruction;
		}

		public LoyaltyOfferEntity.Builder withId( final int id ) {
			this.onConstruction.offerId = id;
			return this;
		}

		public LoyaltyOfferEntity.Builder withIskCost( final long iskCost ) {
			this.onConstruction.iskCost = iskCost;
			return this;
		}

		public LoyaltyOfferEntity.Builder withLoyaltyCorporation( final int corporationId, final String corporationName ) {
			this.onConstruction.corporationId = corporationId;
			this.onConstruction.typeName = Objects.requireNonNull( corporationName );
			return this;
		}

		public LoyaltyOfferEntity.Builder withLpCost( final int lpCost ) {
			this.onConstruction.lpCost = lpCost;
			return this;
		}

		public LoyaltyOfferEntity.Builder withMarketHub( final int marketHubId ) {
			this.onConstruction.marketHubId = marketHubId;
			return this;
		}

		public LoyaltyOfferEntity.Builder withPrice( final double price ) {
			this.onConstruction.price = price;
			return this;
		}

		public LoyaltyOfferEntity.Builder withQuantity( final int quantity ) {
			this.onConstruction.quantity = quantity;
			return this;
		}

		public LoyaltyOfferEntity.Builder withType( final EsiType type ) {
			Objects.requireNonNull( type );
			this.onConstruction.typeId = type.getTypeId();
			this.onConstruction.typeName = type.getName();
			return this;
		}
	}
}