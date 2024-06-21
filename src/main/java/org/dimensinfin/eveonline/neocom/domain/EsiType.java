package org.dimensinfin.eveonline.neocom.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jetbrains.annotations.NonNls;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.utility.GlobalWideConstants;

@NonNls
public class EsiType extends NeoComNode implements Serializable {
	public static final String ESI_ICON_URL_PREFIX = "https://image.eveonline.com/Type/";
	public static final String ESI_ICON_URL_SUFFIX = "_64.png";
	private static final long serialVersionUID = 1430130141655722687L;
	@NonNls
	private static final Map<String, String> hullGroupMap = new HashMap<>();
	@NonNls
	private static final String FRIGATE = "frigate";
	@NonNls
	private static final String BATTLECRUISER = "battlecruiser";
	@NonNls
	private static final String BATTLESHIP = "battleship";
	@NonNls
	private static final String DESTROYER = "destroyer";
	@NonNls
	private static final String CRUISER = "cruiser";
	@NonNls
	private static final String INDUSTRIAL = "industrial";
	@NonNls
	private static final String MININGBARGE = "miningBarge";
	@NonNls
	private static final String SHUTTLE = "shuttle";

	static {
		hullGroupMap.put( "Assault Frigate", FRIGATE );
		hullGroupMap.put( "Attack Battlecruiser", BATTLECRUISER );
		hullGroupMap.put( "Battleship", BATTLESHIP );
		hullGroupMap.put( "Blockade Runner", BATTLECRUISER );
		hullGroupMap.put( "Combat Battlecruiser", BATTLECRUISER );
		hullGroupMap.put( "Combat Recon Ship", BATTLESHIP );
		hullGroupMap.put( "Command Destroyer", DESTROYER );
		hullGroupMap.put( "Corvette", SHUTTLE );
		hullGroupMap.put( "Cruiser", CRUISER );
		hullGroupMap.put( "Deep Space Transport", INDUSTRIAL );
		hullGroupMap.put( "Destroyer", DESTROYER );
		hullGroupMap.put( "Exhumer", MININGBARGE );
		hullGroupMap.put( "Frigate", FRIGATE );
		hullGroupMap.put( "Heavy Assault Cruiser", CRUISER );
		hullGroupMap.put( "Industrial", INDUSTRIAL );
		hullGroupMap.put( "Industrial Command Ship", INDUSTRIAL );
		hullGroupMap.put( "Interceptor", FRIGATE );
		hullGroupMap.put( "Interdictor", FRIGATE );
		hullGroupMap.put( "Logistics", CRUISER );
		hullGroupMap.put( "Mining Barge", MININGBARGE );
		hullGroupMap.put( "Shuttle", "shuttle" );
		hullGroupMap.put( "Stealth Bomber", CRUISER );
		hullGroupMap.put( "Strategic Cruiser", CRUISER );
		hullGroupMap.put( "Tactical Destroyer", DESTROYER );

	}

	protected int typeId = -1;
	protected IndustryGroup industryGroup = IndustryGroup.UNDEFINED;
	protected GetUniverseTypesTypeIdOk type;
	protected GetUniverseGroupsGroupIdOk group;
	protected GetUniverseCategoriesCategoryIdOk category;

	// - C O N S T R U C T O R S
	protected EsiType() {}

	// - G E T T E R S   &   S E T T E R S
	public GetUniverseCategoriesCategoryIdOk getCategory() {
		return this.category;
	}

	public String getCategoryName() {
		return this.category.getName();
	}

	public GetUniverseGroupsGroupIdOk getGroup() {
		return this.group;
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
	 * This method evaluates the type name since the technology level is not stored on any repository (up to the moment). The assumption is that
	 * all items have Tech I until otherwise detected.
	 * Tech detection is done on the type name end string.
	 *
	 * @return the expected tech level for the type.
	 */
	public String getTech() {
		if (this.getName().endsWith( "III" )) return "Tech III";
		if (this.getName().endsWith( "II" )) return "Tech II";
		return "Tech I";
	}

	public GetUniverseTypesTypeIdOk getType() {
		return this.type;
	}

	@Deprecated
	public String getTypeIconURL() {
		return ESI_ICON_URL_PREFIX + this.getTypeId() + ESI_ICON_URL_SUFFIX;
	}

	public int getTypeId() {
		return this.typeId;
	}

	public double getVolume() {return this.type.getVolume();}

	public boolean isBlueprint() {
		return this.getCategoryName().equalsIgnoreCase( GlobalWideConstants.EveGlobal.BLUEPRINT );
	}

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
