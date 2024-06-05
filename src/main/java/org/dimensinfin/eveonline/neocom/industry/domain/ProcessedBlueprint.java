package org.dimensinfin.eveonline.neocom.industry.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.dimensinfin.eveonline.neocom.domain.EsiType;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.market.MarketData;
import org.dimensinfin.eveonline.neocom.utility.NeoObjects;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProcessedBlueprint implements Serializable {
	private static final long serialVersionUID = 1702676060995319018L;
	private int typeId;
	private EsiType blueprintItem;
	private SpaceLocation location;
	@Builder.Default
	private int materialEfficiency = 0;
	@Builder.Default
	private int timeEfficiency = 0;
	private int outputTypeId;
	private EsiType outputItem;
	private Double manufactureCost;
	private Double outputCost;
	private List<Resource> bom;
	@Builder.Default
	private Double index = 0.0;

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "typeId", typeId )
				.append( "blueprintItem", blueprintItem )
				.append( "location", location )
				.append( "materialEfficiency", materialEfficiency )
				.append( "timeEfficiency", timeEfficiency )
				.append( "outputTypeId", outputTypeId )
				.append( "outputItem", outputItem )
				.append( "bom", bom )
				.append( "index", index )
				.toString();
	}
}