package org.dimensinfin.eveonline.neocom.domain;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.core.EveGlobalConstants;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;

public class EsiType extends NeoComNode {
	private static final long serialVersionUID = 1430130141655722687L;
	public static final String ESI_ICON_URL_PREFIX="https://image.eveonline.com/Type/";
	public static final String ESI_ICON_URL_SUFFIX="_64.png";

	protected int typeId = -1;
	protected IndustryGroup industryGroup = IndustryGroup.UNDEFINED;
	protected GetUniverseTypesTypeIdOk item;
	protected GetUniverseGroupsGroupIdOk group;
	protected GetUniverseCategoriesCategoryIdOk category;

	// - C O N S T R U C T O R S
	protected EsiType() {}

	// - G E T T E R S   &   S E T T E R S
	public GetUniverseCategoriesCategoryIdOk getCategory() {
		return category;
	}

	public String getCategoryName() {
		return this.category.getName();
	}

	public GetUniverseGroupsGroupIdOk getGroup() {
		return group;
	}

	public String getGroupName() {
		return this.group.getName();
	}

	// - V I R T U A L   A C C E S S O R S
	public String getHullGroup() {
		if (this.getIndustryGroup() == IndustryGroup.HULL) {
			if (this.getGroupName().equalsIgnoreCase( "Assault Frigate" )) return "frigate";
			if (this.getGroupName().equalsIgnoreCase( "Attack Battlecruiser" )) return "battlecruiser";
			if (this.getGroupName().equalsIgnoreCase( "Battleship" )) return "battleship";
			if (this.getGroupName().equalsIgnoreCase( "Blockade Runner" )) return "battlecruiser";
			if (this.getGroupName().equalsIgnoreCase( "Combat Battlecruiser" )) return "battlecruiser";
			if (this.getGroupName().equalsIgnoreCase( "Combat Recon Ship" )) return "battleship";
			if (this.getGroupName().equalsIgnoreCase( "Command Destroyer" )) return "destroyer";
			if (this.getGroupName().equalsIgnoreCase( "Corvette" )) return "shuttle";
			if (this.getGroupName().equalsIgnoreCase( "Cruiser" )) return "cruiser";
			if (this.getGroupName().equalsIgnoreCase( "Deep Space Transport" )) return "industrial";
			if (this.getGroupName().equalsIgnoreCase( "Destroyer" )) return "destroyer";
			if (this.getGroupName().equalsIgnoreCase( "Exhumer" )) return "miningBarge";
			if (this.getGroupName().equalsIgnoreCase( "Frigate" )) return "frigate";
			if (this.getGroupName().equalsIgnoreCase( "Heavy Assault Cruiser" )) return "cruiser";
			if (this.getGroupName().equalsIgnoreCase( "Industrial" )) return "industrial";
			if (this.getGroupName().equalsIgnoreCase( "Industrial Command Ship" )) return "industrial";
			if (this.getGroupName().equalsIgnoreCase( "Interceptor" )) return "frigate";
			if (this.getGroupName().equalsIgnoreCase( "Interdictor" )) return "frigate";
			if (this.getGroupName().equalsIgnoreCase( "Logistics" )) return "cruiser";
			if (this.getGroupName().equalsIgnoreCase( "Mining Barge" )) return "miningBarge";
			if (this.getGroupName().equalsIgnoreCase( "Shuttle" )) return "shuttle";
			if (this.getGroupName().equalsIgnoreCase( "Stealth Bomber" )) return "cruiser";
			if (this.getGroupName().equalsIgnoreCase( "Strategic Cruiser" )) return "cruiser";
			if (this.getGroupName().equalsIgnoreCase( "Tactical Destroyer" )) return "destroyer";
		}
		return "not-applies";
	}

	public IndustryGroup getIndustryGroup() {
		if (this.industryGroup == IndustryGroup.UNDEFINED) {
			this.classifyIndustryGroup();
		}
		return this.industryGroup;
	}

	public GetUniverseTypesTypeIdOk getItem() {
		return this.item;
	}

	public String getName() {
		return this.item.getName();
	}

	public int getTypeId() {
		return this.typeId;
	}
@Deprecated
	public String getURLForItem() {
		return ESI_ICON_URL_PREFIX + this.getTypeId() + ESI_ICON_URL_SUFFIX;
	}
	public String getTypeIconURL ()  {
		return ESI_ICON_URL_PREFIX + this.getTypeId() + ESI_ICON_URL_SUFFIX;
	}

	protected void classifyIndustryGroup() {
		if ((this.getGroupName().equalsIgnoreCase( "Composite" )) && (this.getCategoryName().equalsIgnoreCase( "Material" ))) {
			this.industryGroup = IndustryGroup.REACTIONMATERIALS;
		}
		if (this.getCategoryName().equalsIgnoreCase( "Asteroid" )) {
			this.industryGroup = IndustryGroup.OREMATERIALS;
		}
		if ((this.getGroupName().equalsIgnoreCase( "Mining Crystal" )) && (this.getCategoryName().equalsIgnoreCase( "Charge" ))) {
			this.industryGroup = IndustryGroup.ITEMS;
		}
		if (this.getCategoryName().equalsIgnoreCase( "Charge" )) {
			this.industryGroup = IndustryGroup.CHARGE;
		}
		if (this.getGroupName().equalsIgnoreCase( "Tool" )) {
			this.industryGroup = IndustryGroup.ITEMS;
		}
		if (this.getCategoryName().equalsIgnoreCase( "Commodity" )) {
			this.industryGroup = IndustryGroup.COMMODITY;
		}
		if (this.getCategoryName().equalsIgnoreCase( EveGlobalConstants.Blueprint )) {
			this.industryGroup = IndustryGroup.BLUEPRINT;
		}
		if (this.getCategoryName().equalsIgnoreCase( EveGlobalConstants.Skill )) {
			this.industryGroup = IndustryGroup.SKILL;
		}
		if (this.getGroupName().equalsIgnoreCase( EveGlobalConstants.Mineral )) {
			this.industryGroup = IndustryGroup.REFINEDMATERIAL;
		}
		if (this.getCategoryName().equalsIgnoreCase( "Module" )) {
			this.industryGroup = IndustryGroup.COMPONENTS;
		}
		if (this.getCategoryName().equalsIgnoreCase( "Drone" )) {
			this.industryGroup = IndustryGroup.ITEMS;
		}
		if (this.getCategoryName().equalsIgnoreCase( "Planetary Commodities" )) {
			this.industryGroup = IndustryGroup.PLANETARYMATERIALS;
		}
		if (this.getGroupName().equalsIgnoreCase( "Datacores" )) {
			this.industryGroup = IndustryGroup.DATACORES;
		}
		if (this.getGroupName().equalsIgnoreCase( "Salvaged Materials" )) {
			this.industryGroup = IndustryGroup.SALVAGEDMATERIAL;
		}
		if (this.getCategoryName().equalsIgnoreCase( "Ship" )) {
			this.industryGroup = IndustryGroup.HULL;
		}
	}

	public double getVolume() {return this.item.getVolume();}

	// - B U I L D E R
	public static class Builder {
		private final EsiType onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new EsiType();
		}

		public EsiType build() {
			Objects.requireNonNull( this.onConstruction.typeId );
			Objects.requireNonNull( this.onConstruction.item );
			Objects.requireNonNull( this.onConstruction.group );
			Objects.requireNonNull( this.onConstruction.category );
			return this.onConstruction;
		}

		public EsiType.Builder withCategory( final GetUniverseCategoriesCategoryIdOk category ) {
			this.onConstruction.category = Objects.requireNonNull( category );
			return this;
		}

		public EsiType.Builder withGroup( final GetUniverseGroupsGroupIdOk group ) {
			this.onConstruction.group = Objects.requireNonNull( group );
			return this;
		}

		public EsiType.Builder withItemType( final GetUniverseTypesTypeIdOk item ) {
			this.onConstruction.item = Objects.requireNonNull( item );
			return this;
		}

		public EsiType.Builder withTypeId( final Integer typeId ) {
			this.onConstruction.typeId = Objects.requireNonNull( typeId );
			return this;
		}
	}
}
