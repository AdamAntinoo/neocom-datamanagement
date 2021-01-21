package org.dimensinfin.eveonline.neocom.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.dimensinfin.eveonline.neocom.core.EveGlobalConstants;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.utility.GlobalWideConstants;

public class EsiType extends NeoComNode {
	public static final String ESI_ICON_URL_PREFIX = "https://image.eveonline.com/Type/";
	public static final String ESI_ICON_URL_SUFFIX = "_64.png";
	private static final long serialVersionUID = 1430130141655722687L;
	private static final Map<String, String> hullGroupMap = new HashMap<>();

	static {
		hullGroupMap.put( "Assault Frigate", "frigate" );
		hullGroupMap.put( "Attack Battlecruiser", "battlecruiser" );
		hullGroupMap.put( "Battleship", "battleship" );
		hullGroupMap.put( "Blockade Runner", "battlecruiser" );
		hullGroupMap.put( "Combat Battlecruiser", "battlecruiser" );
		hullGroupMap.put( "Combat Recon Ship", "battleship" );
		hullGroupMap.put( "Command Destroyer", "destroyer" );
		hullGroupMap.put( "Corvette", "shuttle" );
		hullGroupMap.put( "Cruiser", "cruiser" );
		hullGroupMap.put( "Deep Space Transport", "industrial" );
		hullGroupMap.put( "Destroyer", "destroyer" );
		hullGroupMap.put( "Exhumer", "miningBarge" );
		hullGroupMap.put( "Frigate", "frigate" );
		hullGroupMap.put( "Heavy Assault Cruiser", "cruiser" );
		hullGroupMap.put( "Industrial", "industrial" );
		hullGroupMap.put( "Industrial Command Ship", "industrial" );
		hullGroupMap.put( "Interceptor", "frigate" );
		hullGroupMap.put( "Interdictor", "frigate" );
		hullGroupMap.put( "Logistics", "cruiser" );
		hullGroupMap.put( "Mining Barge", "miningBarge" );
		hullGroupMap.put( "Shuttle", "shuttle" );
		hullGroupMap.put( "Stealth Bomber", "cruiser" );
		hullGroupMap.put( "Strategic Cruiser", "cruiser" );
		hullGroupMap.put( "Tactical Destroyer", "destroyer" );

	}

	protected int typeId = -1;
	protected IndustryGroup industryGroup = IndustryGroup.UNDEFINED;
	protected transient GetUniverseTypesTypeIdOk type;
	protected transient GetUniverseGroupsGroupIdOk group;
	protected transient GetUniverseCategoriesCategoryIdOk category;

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
		if (this.getIndustryGroup() == IndustryGroup.HULL)
			return hullGroupMap.getOrDefault( this.getGroupName(), "not-applies" );
		return "not-applies";
	}

	public IndustryGroup getIndustryGroup() {
		if (this.industryGroup == IndustryGroup.UNDEFINED) {
			this.classifyIndustryGroup();
		}
		return this.industryGroup;
	}

	public String getName() {
		return this.type.getName();
	}

	/**
	 * This method evaluates the item name since the technology level is not stored on any repository (up to the moment). The assumption is that
	 * all items have Tech I until otherwise detected.
	 * Tech detection is done on the type name end string.
	 *
	 * @return the expected tech level for the item.
	 */
	public String getTech() {
		if (this.getName().endsWith( "III" )) return "Tech III";
		if (this.getName().endsWith( "II" )) return "Tech II";
		return "Tech I";
	}

	public GetUniverseTypesTypeIdOk getType() {
		return this.type;
	}

	public String getTypeIconURL() {
		return ESI_ICON_URL_PREFIX + this.getTypeId() + ESI_ICON_URL_SUFFIX;
	}

	public int getTypeId() {
		return this.typeId;
	}

	public double getVolume() {return this.type.getVolume();}

	// - C O R E
	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.appendSuper( super.hashCode() )
				.append( this.typeId )
				.append( this.industryGroup ).toHashCode();
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (!(o instanceof EsiType)) return false;
		final EsiType esiType = (EsiType) o;
		return new EqualsBuilder().appendSuper( super.equals( o ) )
				.append( this.typeId, esiType.typeId )
				.append( this.industryGroup, esiType.industryGroup ).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "typeId", this.typeId )
				.append( "industryGroup", this.industryGroup )
				.append( "type", this.type )
				.append( "group", this.group )
				.append( "category", this.category )
				.toString();
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
		if (this.getCategoryName().equalsIgnoreCase( GlobalWideConstants.EveGlobal.BLUEPRINT )) {
			this.industryGroup = IndustryGroup.BLUEPRINT;
		}
		if (this.getCategoryName().equalsIgnoreCase( GlobalWideConstants.EveGlobal.SKILL )) {
			this.industryGroup = IndustryGroup.SKILL;
		}
		if (this.getGroupName().equalsIgnoreCase( GlobalWideConstants.EveGlobal.MINERAL )) {
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

	// - B U I L D E R
	public static class Builder {
		private final EsiType onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new EsiType();
		}

		public EsiType build() {
			if (this.onConstruction.typeId < 0) throw new NullPointerException( "EsiType itemId should not be left unassigned." );
			Objects.requireNonNull( this.onConstruction.type );
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
			this.onConstruction.type = Objects.requireNonNull( item );
			return this;
		}

		public EsiType.Builder withTypeId( final Integer typeId ) {
			this.onConstruction.typeId = Objects.requireNonNull( typeId );
			return this;
		}
	}
}
