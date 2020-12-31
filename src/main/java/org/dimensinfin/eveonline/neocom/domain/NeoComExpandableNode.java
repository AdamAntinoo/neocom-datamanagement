package org.dimensinfin.eveonline.neocom.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.dimensinfin.core.interfaces.IExpandable;

/**
 * This model class will serve as the base placeholder for the NeoCom application nodes. Will define the
 * common methods and implement the default behavior for nodes.
 */
public abstract class NeoComExpandableNode extends NeoComNode implements IExpandable {
	private static final long serialVersionUID = -5222015207026531184L;
	protected boolean expanded = false;

	// - C O N S T R U C T O R S
	public NeoComExpandableNode() {
		super();
	}

	// - I E X P A N D A B L E   I N T E R F A C E
	@Override
	public boolean collapse() {
		this.expanded = false;
		return false;
	}

	@Override
	public boolean expand() {
		this.expanded = true;
		return true;
	}

	@Override
	public boolean isExpanded() {
		return this.expanded;
	}

	@Override
	public boolean toggleExpand() {
		this.expanded = !this.expanded;
		return this.expanded;
	}

	// - C O R E
	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.appendSuper( super.hashCode() )
				.append( this.expanded )
				.toHashCode();
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final NeoComExpandableNode that = (NeoComExpandableNode) o;
		return new EqualsBuilder()
				.appendSuper( super.equals( o ) )
				.append( this.expanded, that.expanded )
				.isEquals();
	}
}
