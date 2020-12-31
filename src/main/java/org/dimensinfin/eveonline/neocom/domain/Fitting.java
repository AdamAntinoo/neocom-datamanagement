package org.dimensinfin.eveonline.neocom.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.annotation.RequiresNetwork;
import org.dimensinfin.eveonline.neocom.esiswagger.model.CharacterscharacterIdfittingsItems;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdFittings200Ok;

public class Fitting extends NeoComNode {
	private static final long serialVersionUID = 2267335283642321303L;

	private GetCharactersCharacterIdFittings200Ok fittingDescription;
	private List<FittingItem> items = new ArrayList<>();
	private transient NeoItem shipItem = null;

	// - C O N S T R U C T O R S
	private Fitting() {}

	// - G E T T E R S   &   S E T T E R S
	public String getDescription() {return this.fittingDescription.getDescription();}

	public Integer getFittingId() {return this.fittingDescription.getFittingId();}

	public String getName() {return this.fittingDescription.getName();}

	public NeoItem getShipItem() {
		return this.shipItem;
	}

	public Integer getShipTypeId() {return this.fittingDescription.getShipTypeId();}

	@RequiresNetwork
	public String getGroupName() {return this.shipItem.getGroupName();}

	public String getHullGroup() {return this.shipItem.getHullGroup();}

	public String getURLForItem() {return this.shipItem.getURLForItem();}

	public List<FittingItem> getItems() {
		return this.items;
	}

	/**
	 * During the transformation this method will be called with the original list of items that are encoded in location and in
	 * type. During the assignment we should process that list and expand them to a full list of enumerated ship locations and
	 * full eve items type.
	 *
	 * @param fittingData original ESI item data.
	 */
	@RequiresNetwork
	private void downloadFittingItems( final GetCharactersCharacterIdFittings200Ok fittingData ) {
		this.items.clear();
		for (CharacterscharacterIdfittingsItems item : fittingData.getItems()) {
			final FittingItem newitem = new FittingItem.Builder().withFittingItem( item ).build();
			this.items.add( newitem );
		}
	}

	@RequiresNetwork
	private void downloadHullData( final Integer shipTypeId ) {
		Objects.requireNonNull( shipTypeId );
		this.shipItem = new NeoItem( shipTypeId );
		Objects.requireNonNull( this.shipItem );
	}

	// - B U I L D E R
	public static class Builder {
		private Fitting onConstruction;

		public Builder() {
			this.onConstruction = new Fitting();
		}

		public Fitting build() {
			Objects.requireNonNull( this.onConstruction.fittingDescription );
			return this.onConstruction;
		}

		public Fitting.Builder withFittingData( final GetCharactersCharacterIdFittings200Ok fittingData ) {
			Objects.requireNonNull( fittingData );
			this.onConstruction.fittingDescription = fittingData;
			// Download the hull eve item data.
			this.onConstruction.downloadHullData( this.onConstruction.fittingDescription.getShipTypeId() );
			// Download the items that are used on this fitting.
			this.onConstruction.downloadFittingItems( fittingData );
			return this;
		}
	}
}
