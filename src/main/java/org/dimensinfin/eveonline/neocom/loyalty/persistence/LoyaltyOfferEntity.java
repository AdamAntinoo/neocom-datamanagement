package org.dimensinfin.eveonline.neocom.loyalty.persistence;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.dimensinfin.eveonline.neocom.database.entities.UpdatableEntity;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
@DatabaseTable(tableName = "neocom.LoyaltyOffers")
public class LoyaltyOfferEntity extends UpdatableEntity {
	private static final long serialVersionUID = 6772112244501770693L;

	@DatabaseField(columnName="offerId",id = true, index = true)
	private int offerId = -1;
	@DatabaseField(columnName="typeId")
	private int typeId;
	@DatabaseField(columnName="typeName")
	private String typeName;

	// - C O N S T R U C T O R S
	private LoyaltyOfferEntity() {}

	public String getTypeName() {
		return this.typeName;
	}

	public int getOfferId() {
		return this.offerId;
	}

	public int getTypeId() {
		return this.typeId;
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
	}
}