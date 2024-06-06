package org.dimensinfin.eveonline.neocom.industry.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.dimensinfin.eveonline.neocom.domain.EsiType;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.service.IDataStore;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder(setterPrefix = "with")
public class ProcessedBlueprint implements Serializable {
	private static final long serialVersionUID = 1702676060995319018L;
	@NonNull
	private Integer typeId;
	@NonNull
	private EsiType blueprintItem;
	@NonNull
	private SpaceLocation location;
	@Builder.Default
	private int materialEfficiency = 0;
	@Builder.Default
	private int timeEfficiency = 0;
	private int outputTypeId;
	@NonNull
	private EsiType outputItem;
	private Double manufactureCost;
	private Double outputCost;
	@Builder.Default
	private List<Resource> bom=new ArrayList<>();
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
				.append( "manufactureCost", manufactureCost )
				.append( "outputCost", outputCost )
				.append( "bom", bom )
				.append( "index", index )
				.toString();
	}
	public String getStorageUniqueId(){
		return this.getLocation().getLocationId() + IDataStore.REDIS_SEPARATOR + this.getTypeId();
	}
}