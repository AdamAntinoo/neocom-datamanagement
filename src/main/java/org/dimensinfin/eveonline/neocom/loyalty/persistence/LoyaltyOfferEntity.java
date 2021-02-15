package org.dimensinfin.eveonline.neocom.loyalty.persistence;

import java.util.Objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import org.dimensinfin.eveonline.neocom.database.entities.UpdatableEntity;
import org.dimensinfin.eveonline.neocom.domain.EsiType;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
@DatabaseTable(tableName = "neocom.LoyaltyOffers")
public class LoyaltyOfferEntity extends UpdatableEntity {
	private static final long serialVersionUID = 6772112244501770693L;

	private static String generateOfferIdentifier( final Integer offerId, final Integer marketRegionId ) {
		return "UID:" + offerId + ":" + marketRegionId;
	}

	/**
	 * The unique id is composed of the offer identifier and the market region identifier so multiple regions can be scanned and persisted.
	 */
	@DatabaseField(columnName = "id", id = true, index = true)
	private String id;
	@DatabaseField(columnName = "offerId")
	private Integer offerId;
	@DatabaseField(columnName = "typeId")
	private int typeId;
	@DatabaseField(columnName = "typeName")
	private String typeName;
	@DatabaseField(columnName = "corporationId")
	private int corporationId;
	@DatabaseField(columnName = "corporationName")
	private String corporationName;
	@DatabaseField(columnName = "lpValue")
	private Integer lpValue;
	@DatabaseField(columnName = "iskCost")
	private Long iskCost;
	@DatabaseField(columnName = "lpCost")
	private Integer lpCost;
	@DatabaseField(columnName = "quantity")
	private Integer quantity = 1;
	@DatabaseField(columnName = "marketRegionId")
	private Integer marketRegionId;
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

	public String getId() {
		return this.id;
	}

	public long getIskCost() {
		return this.iskCost;
	}

	public int getLpCost() {
		return this.lpCost;
	}

	public int getLpValue() {
		return this.lpValue;
	}

	public int getMarketRegionId() {
		return this.marketRegionId;
	}

	public int getOfferId() {
		return this.offerId;
	}

	public double getPrice() {
		return this.price;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public int getTypeId() {
		return this.typeId;
	}

	public String getTypeName() {
		return this.typeName;
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (!(o instanceof LoyaltyOfferEntity)) return false;
		final LoyaltyOfferEntity entity = (LoyaltyOfferEntity) o;
		return new EqualsBuilder().appendSuper( super.equals( o ) ).append( typeId, entity.typeId )
				.append( corporationId, entity.corporationId ).append( id, entity.id ).append( offerId, entity.offerId )
				.append( typeName, entity.typeName ).append( corporationName, entity.corporationName ).append( lpValue, entity.lpValue )
				.append( iskCost, entity.iskCost ).append( lpCost, entity.lpCost ).append( quantity, entity.quantity )
				.append( marketRegionId, entity.marketRegionId ).append( price, entity.price ).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 ).appendSuper( super.hashCode() ).append( id ).append( offerId ).append( typeId ).append( typeName )
				.append( corporationId ).append( corporationName ).append( lpValue ).append( iskCost ).append( lpCost ).append( quantity )
				.append( marketRegionId ).append( price ).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this )
				.append( "id", id )
				.append( "typeName", typeName )
				.append( "corporationName", corporationName )
				.append( "lpValue", lpValue )
				.append( "iskCost", iskCost )
				.append( "lpCost", lpCost )
				.append( "price", price )
				.toString();
	}

	// - B U I L D E R
	public static class Builder {
		private final LoyaltyOfferEntity onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new LoyaltyOfferEntity();
		}

		public LoyaltyOfferEntity build() {
			Objects.requireNonNull( this.onConstruction.offerId );
			Objects.requireNonNull( this.onConstruction.marketRegionId );
			this.onConstruction.id = generateOfferIdentifier( this.onConstruction.offerId, this.onConstruction.marketRegionId );
			this.onConstruction.lpValue = (int) Math
					.round( ((this.onConstruction.quantity * this.onConstruction.price) - this.onConstruction.iskCost) /
							this.onConstruction.lpCost );
			return this.onConstruction;
		}

		public LoyaltyOfferEntity.Builder withIskCost( final long iskCost ) {
			this.onConstruction.iskCost = iskCost;
			return this;
		}

		public LoyaltyOfferEntity.Builder withLoyaltyCorporation( final int corporationId, final String corporationName ) {
			this.onConstruction.corporationId = corporationId;
			this.onConstruction.corporationName = Objects.requireNonNull( corporationName );
			return this;
		}

		public LoyaltyOfferEntity.Builder withLpCost( final int lpCost ) {
			this.onConstruction.lpCost = lpCost;
			return this;
		}

		public LoyaltyOfferEntity.Builder withMarketRegionId( final int marketRegionId ) {
			this.onConstruction.marketRegionId = marketRegionId;
			return this;
		}

		public LoyaltyOfferEntity.Builder withOfferId( final int offerId ) {
			this.onConstruction.offerId = offerId;
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