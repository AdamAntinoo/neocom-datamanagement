package org.dimensinfin.eveonline.neocom.domain;

import java.util.ArrayList;
import java.util.List;

import org.dimensinfin.core.interfaces.ICollaboration;

public class ExpandableContainer<M extends ICollaboration> extends NeoComExpandableNode {
	private static final long serialVersionUID = -3759714638563369946L;
	private List<M> contents = new ArrayList<>();

	public ExpandableContainer() {}

	public int getContentCount() {
		return this.contents.size();
	}

	public List<M> getContents() {
		return this.contents;
	}

	// - I E X P A N D A B L E
	@Override
	public boolean isEmpty() {
		return this.contents.isEmpty();
	}

	// -  C O N T E N T
	public int addContent( final M item ) {
		this.contents.add( item );
		return this.contents.size();
	}

	public void clear() {
		this.contents.clear();
	}

	// - I C O L L A B O R A T I O N
	@Override
	public List<ICollaboration> collaborate2Model( final String variant ) {
		final ArrayList<ICollaboration> collaboration = new ArrayList<>();
		collaboration.addAll( this.contents );
		return collaboration;
	}
}
