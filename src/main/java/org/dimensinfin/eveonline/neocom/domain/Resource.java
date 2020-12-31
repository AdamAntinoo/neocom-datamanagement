package org.dimensinfin.eveonline.neocom.domain;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.dimensinfin.eveonline.neocom.core.IAggregableItem;
import org.dimensinfin.eveonline.neocom.service.NeoItemFactory;

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
public class Resource extends NeoComNode implements IAggregableItem, IItemFacet {
	private static final long serialVersionUID = -1722630075425980171L;
	protected int baseQty;
	protected int stackSize = 1;

	protected transient NeoItem neoItemDelegate;

	// - C O N S T R U C T O R S
	public Resource( final int typeId ) {
		super();
		this.neoItemDelegate = NeoItemFactory.getSingleton().getItemById( typeId );
		this.baseQty = 0;
	}

	public Resource( final int typeId, final int newQty ) {
		this( typeId );
		this.baseQty = newQty;
	}

	public Resource( final int typeId, final int newQty, final int stackSize ) {
		this( typeId, newQty );
		this.stackSize = stackSize;
	}

	public int getBaseQuantity() {
		return this.baseQty;
	}

	public String getCategory() {
		return this.getItem().getCategoryName();
	}

	public String getGroupName() {
		return this.getItem().getGroupName();
	}

	public NeoItem getItem() {
		return Objects.requireNonNull( this.neoItemDelegate );
	}

	public String getName() {
		return this.getItem().getName();
	}

	@Override
	public String getURLForItem() {
		return this.getItem().getURLForItem();
	}

	public int getStackSize() {
		return this.stackSize;
	}

	public Resource setStackSize( final int stackSize ) {
		this.stackSize = stackSize;
		return this;
	}

	public int getTypeId() {
		return this.getItem().getTypeId();
	}

	/**
	 * Apply the manufacture formulas to get the correct value of the quantity for this user.
	 *
	 * @return my manufacture quantity
	 */
	public int getQuantity() {
		return this.getBaseQuantity() * stackSize;
	}

	public Resource setQuantity( final int newQuantity ) {
		this.baseQty = newQuantity;
		this.stackSize = 1;
		return this;
	}

	@Override
	public double getVolume() {
		return this.getItem().getVolume();
	}

	@Override
	public double getPrice() {
		return this.getItem().getPrice();
	}

	// - I A G G R E G A B L E I T E M
//	public void setAdaptiveStackSize( final int size ) {
//		this.setStackSize( size );
//		if (this.getItem().getCategoryName().equalsIgnoreCase( ModelWideConstants.eveglobal.Blueprint )) {
//			if (this.getItem().getTech().equalsIgnoreCase( ModelWideConstants.eveglobal.TechII )) {
//				final double stack = Math.ceil( size / 10.0 );
//				this.setStackSize( Math.max( (int) stack, 1 ) );
//			}
//			if (this.getItem().getTech().equalsIgnoreCase( ModelWideConstants.eveglobal.TechI )) {
//				final double stack = Math.ceil( size / 300.0 );
//				this.setStackSize( Math.max( (int) stack, 1 ) );
//			}
//		}
//		if (this.getItem().getCategoryName().equalsIgnoreCase( ModelWideConstants.eveglobal.Skill )) {
//			this.setStackSize( 1 );
//		}
//	}

	public int add( final int count ) {
		this.baseQty = this.getQuantity() + count;
		this.stackSize = 1;
		return this.baseQty;
	}

	/**
	 * Adds the quantities of two resources of the same type. On this moment the original resource losses the
	 * stack values and the equivalent quantity is calculated before adding the new quantity calculated exactly
	 * on the same way. The final result is the total quantity but with a stack size of one.
	 */
	public int addition( final Resource newResource ) {
		int newqty = this.getBaseQuantity() * this.getStackSize();
		newqty += newResource.getBaseQuantity() * newResource.getStackSize();
		this.baseQty = newqty;
		this.stackSize = 1;
		return this.baseQty;
	}

	// - C O R E
	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.appendSuper( super.hashCode() )
				.append( this.baseQty )
				.append( this.stackSize )
				.append( this.neoItemDelegate.getTypeId() )
				.toHashCode();
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final Resource resource = (Resource) o;
		return new EqualsBuilder()
				.appendSuper( super.equals( o ) )
				.append( this.baseQty, resource.baseQty )
				.append( this.stackSize, resource.stackSize )
				.append( this.neoItemDelegate.getTypeId(), resource.neoItemDelegate.getTypeId() )
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "baseQty", baseQty )
				.append( "stackSize", stackSize )
				.append( "name", getName() )
				.append( "typeId", getTypeId() )
				.append( "quantity", getQuantity() )
				.append( "volume", getVolume() )
				.append( "price", getPrice() )
				.append( "jsonClass", getJsonClass() )
				.toString();
	}

//	public String toStringJson() {
//		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
//				.append( "baseQty", baseQty )
//				.append( "stackSize", stackSize )
//				.append( "esiItem", neoItemDelegate )
//				.toString();
//	}
}
