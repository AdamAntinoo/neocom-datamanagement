package org.dimensinfin.eveonline.neocom.industry.domain;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.dimensinfin.eveonline.neocom.domain.EsiType;
import org.dimensinfin.eveonline.neocom.domain.IItemFacet;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;

/**
 * The class defines the basic stack of some type of item. It will allow the aggregation of more of the same
 * type items and differentiated from the asset in that it has no specified Location not owner. Includes the
 * methods for resource calculation on dependence of the character skills but that information has to be given
 * from outside if you desire to use it on the calculations.<br>
 * By default we consider a 5 on the Industry skills and that T2 blueprints have a ME=7 ME and TE=14 and that
 * other T1 blueprints have a 10 of ME and a 20 on TE.<br>
 * We also consider that T2 BPC have 10 runs while T1 BPC have 300.<br>
 * In short the blueprints will have the correct ME/TE/RUNS available so this values will be set from the
 * blueprint from where the resource was extracted.
 *
 * @author Adam Antinoo
 */
public class Resource extends EsiType implements IItemFacet {
	private static final long serialVersionUID = -1722630075425980171L;
	protected Integer quantity = 1;

	// - C O N S T R U C T O R S
	protected Resource() {}

	@Deprecated
	protected Resource( final int typeId ) {
		super();
		this.quantity = 1;
	}

	@Deprecated
	protected Resource( final int typeId, final int newQty ) {
		this( typeId );
		this.quantity = newQty;
	}

	@Deprecated
	protected Resource( final int typeId, final int newQty, final int stackSize ) {
		this( typeId, newQty * stackSize );
	}

	// - G E T T E R S   &   S E T T E R S
	public int getQuantity() {
		return this.quantity;
	}

	public Resource setQuantity( final int newQuantity ) {
		this.quantity = newQuantity;
		return this;
	}

	// - I A G G R E G A B L E I T E M
	@Deprecated
	public int getStackSize() {
		return 1;
	}

	public int add( final int count ) {
		this.quantity = this.getQuantity() + Math.max( 0, count );
		return this.quantity;
	}

	/**
	 * Adds the quantities of two resources of the same type. On this moment the original resource losses the
	 * stack values and the equivalent quantity is calculated before adding the new quantity calculated exactly
	 * on the same way. The final result is the total quantity but with a stack size of one.
	 */
	@Deprecated
	public int addition( final Resource newResource ) {
		int newqty = this.getBaseQuantity() * this.getStackSize();
		newqty += newResource.getBaseQuantity() * newResource.getStackSize();
		this.quantity = newqty;
		return this.quantity;
	}

	// - C O R E
	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.appendSuper( super.hashCode() )
				.append( this.quantity )
				.toHashCode();
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		final Resource resource = (Resource) o;
		return new EqualsBuilder()
				.appendSuper( super.equals( o ) )
				.append( this.quantity, resource.quantity )
				//				.append( this.stackSize, resource.stackSize )
				//				.append( this.neoItemDelegate.getTypeId(), resource.neoItemDelegate.getTypeId() )
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "baseQty", this.quantity )
				.append( "name", this.getName() )
				.append( "typeId", this.getTypeId() )
				.append( "quantity", this.getQuantity() )
				.append( "volume", this.getVolume() )
				.append( "jsonClass", this.getJsonClass() )
				.toString();
	}

	public int sub( final int count ) {
		if (count > this.quantity) this.quantity = 0;
		else this.quantity = this.getQuantity() - Math.max( 0, count );
		return this.quantity;
	}

	@Deprecated
	protected int getBaseQuantity() {
		return this.quantity;
	}

	// - B U I L D E R
	public static class Builder {
		private final Resource onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new Resource();
		}

		public Resource build() {
			Objects.requireNonNull( this.onConstruction.typeId );
			Objects.requireNonNull( this.onConstruction.type );
			Objects.requireNonNull( this.onConstruction.group );
			Objects.requireNonNull( this.onConstruction.category );
			return this.onConstruction;
		}

		public Resource.Builder withCategory( final GetUniverseCategoriesCategoryIdOk category ) {
			this.onConstruction.category = Objects.requireNonNull( category );
			return this;
		}

		public Resource.Builder withGroup( final GetUniverseGroupsGroupIdOk group ) {
			this.onConstruction.group = Objects.requireNonNull( group );
			return this;
		}

		public Resource.Builder withItemType( final GetUniverseTypesTypeIdOk item ) {
			this.onConstruction.type = Objects.requireNonNull( item );
			return this;
		}

		public Resource.Builder withQuantity( final Integer quantity ) {
			if (null != quantity) this.onConstruction.quantity = quantity;
			return this;
		}

		@Deprecated
		public Resource.Builder withTypeId( final Integer typeId ) {
			this.onConstruction.typeId = Objects.requireNonNull( typeId );
			return this;
		}
	}
}
