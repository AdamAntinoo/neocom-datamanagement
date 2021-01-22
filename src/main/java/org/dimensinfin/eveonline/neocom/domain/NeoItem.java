package org.dimensinfin.eveonline.neocom.domain;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.dimensinfin.eveonline.neocom.annotation.RequiresNetwork;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOkDogmaAttributes;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.utility.GlobalWideConstants;
import org.dimensinfin.logging.LogWrapper;

@JsonIgnoreProperties
@Deprecated
public class NeoItem extends NeoComNode implements IItemFacet {
	public enum IndustryGroup {
		UNDEFINED, OUTPUT, SKILL, BLUEPRINT, COMPONENTS, HULL, CHARGE, DATACORES, DATAINTERFACES, DECRIPTORS, ITEMS, MINERAL,
		PLANETARYMATERIALS, REACTIONMATERIALS, REFINEDMATERIAL, SALVAGEDMATERIAL, OREMATERIALS, COMMODITY}
	private static final long serialVersionUID = -2548296399305221197L;
	private static ESIUniverseDataProvider esiUniverseDataProvider;
	protected int id = -1;
	private transient GetUniverseTypesTypeIdOk item;
	private transient GetUniverseGroupsGroupIdOk group;
	private transient GetUniverseCategoriesCategoryIdOk category;
	/**
	 * This is the ESI returned price from the global market data service. This is the price shown on the game UI for item values
	 * and it is not tied to any specific market place.
	 */
	private double price = -1.0;
	private String tech = GlobalWideConstants.EveGlobal.TECH_I;
	// - A D D I T I O N A L   F I E L D S
	private transient IndustryGroup industryGroup = IndustryGroup.UNDEFINED;

	// - C O N S T R U C T O R S
	@Deprecated
	public NeoItem() {
		super();
	}
	@Deprecated
	public NeoItem( final GetUniverseTypesTypeIdOk sdeItem ) {
		super();
		this.item = sdeItem;
	}

	@Deprecated
	@RequiresNetwork
	public NeoItem( final int typeId ) {
		this.id = typeId;
		this.loadup();
	}

	@Deprecated
	public static void injectEsiUniverseDataAdapter( final ESIUniverseDataProvider newEsiUniverseDataProvider ) {
		esiUniverseDataProvider = newEsiUniverseDataProvider;
	}

	@Deprecated
	@RequiresNetwork
	private void loadup() {
		try {
			this.item = esiUniverseDataProvider.searchEsiItem4Id(this.id);
			Objects.requireNonNull(this.item);
			this.group = esiUniverseDataProvider.searchItemGroup4Id(this.item.getGroupId());
			Objects.requireNonNull(this.group);
			this.category = esiUniverseDataProvider.searchItemCategory4Id(this.group.getCategoryId());
			Objects.requireNonNull(this.category);
		} catch (final RuntimeException rtex) {
			LogWrapper.info( MessageFormat.format(
					"RT [NeoItem.loadup]> Error downloading the NeoItem data for code {0}. Not able to complete the instantiation.",this.id
			));
			LogWrapper.error( rtex);
		}
	}

	// - G E T T E R S   &   S E T T E R S
	@Deprecated
	public int getItemId() {
		return this.id;
	}

	public int getTypeId() {
		return this.id;
	}

	/**
	 * This is the key method used when instantiating an NeoItem to set the eve item identifier for the esi underlying object. After setting this
	 * value we can post the download or caches access to the delegated esi data.
	 * Also we later will used this entry point to locate the extended market information to be used when calculating prices. This price
	 * data will be located inside a new delegete instance.
	 *
	 * @param typeId the eve game unique type identifier.
	 */
	public NeoItem setTypeId( final int typeId ) {
		this.id = typeId;
		this.loadup();
//		futureBuyerData = this.retrieveMarketData(getTypeId(), EMarketSide.BUYER);
//		futureSellerData = this.retrieveMarketData(getTypeId(), EMarketSide.SELLER);
		return this;
	}

	@RequiresNetwork
	public String getName() {
		if (null == this.item) this.loadup();
		return this.item.getName();
	}

	/**
	 * Return the ESI api market set price for this item. Sometimes there is another price markets as the average price that I am
	 * not using now.
	 */
	@RequiresNetwork
	public double getPrice() {
		if (this.price < 0.0)
			this.price = esiUniverseDataProvider.searchSDEMarketPrice(this.getTypeId());
		return this.price;
	}

	public void setPrice( final double price ) {
		this.price = price;
	}

	@RequiresNetwork
	public int getGroupId() {
		if (null == this.group) this.loadup();
		return this.group.getGroupId();
	}

	@RequiresNetwork
	public int getCategoryId() {
		if (null == this.category) this.loadup();
		return this.category.getCategoryId();
	}

	/**
	 * Some items have tech while others don't. Tech information has to be calculated for some items when I
	 * download them as assets or blueprints. Set it to a default value that by now I can consider valid.
	 */
	public String getTech() {
		return this.tech;
	}

	public void setTech( final String tech ) {
		this.tech = tech;
	}

	@RequiresNetwork
	public double getVolume() {
		if (null == this.item) this.loadup();
		return this.item.getVolume();
	}

	public boolean isBlueprint() {
		if (this.getCategoryName().equalsIgnoreCase( GlobalWideConstants.EveGlobal.BLUEPRINT))
			return true;
		else
			return false;
	}

	public IndustryGroup getIndustryGroup() {
		if (this.industryGroup == IndustryGroup.UNDEFINED) {
			this.classifyIndustryGroup();
		}
		return this.industryGroup;
	}

	public Float getCapacity() {
		return this.item.getCapacity();
	}

	public List<GetUniverseTypesTypeIdOkDogmaAttributes> getDogmaAttributes() {
		return this.item.getDogmaAttributes();
	}

	// - V I R T U A L   A C C E S S O R S
	public String getHullGroup() {
		if (getIndustryGroup() == IndustryGroup.HULL) {
			if (getGroupName().equalsIgnoreCase("Assault Frigate")) return "frigate";
			if (getGroupName().equalsIgnoreCase("Attack Battlecruiser")) return "battlecruiser";
			if (getGroupName().equalsIgnoreCase("Battleship")) return "battleship";
			if (getGroupName().equalsIgnoreCase("Blockade Runner")) return "battlecruiser";
			if (getGroupName().equalsIgnoreCase("Combat Battlecruiser")) return "battlecruiser";
			if (getGroupName().equalsIgnoreCase("Combat Recon Ship")) return "battleship";
			if (getGroupName().equalsIgnoreCase("Command Destroyer")) return "destroyer";
			if (getGroupName().equalsIgnoreCase("Corvette")) return "shuttle";
			if (getGroupName().equalsIgnoreCase("Cruiser")) return "cruiser";
			if (getGroupName().equalsIgnoreCase("Deep Space Transport")) return "industrial";
			if (getGroupName().equalsIgnoreCase("Destroyer")) return "destroyer";
			if (getGroupName().equalsIgnoreCase("Exhumer")) return "miningBarge";
			if (getGroupName().equalsIgnoreCase("Frigate")) return "frigate";
			if (getGroupName().equalsIgnoreCase("Heavy Assault Cruiser")) return "cruiser";
			if (getGroupName().equalsIgnoreCase("Industrial")) return "industrial";
			if (getGroupName().equalsIgnoreCase("Industrial Command Ship")) return "industrial";
			if (getGroupName().equalsIgnoreCase("Interceptor")) return "frigate";
			if (getGroupName().equalsIgnoreCase("Interdictor")) return "frigate";
			if (getGroupName().equalsIgnoreCase("Logistics")) return "cruiser";
			if (getGroupName().equalsIgnoreCase("Mining Barge")) return "miningBarge";
			if (getGroupName().equalsIgnoreCase("Shuttle")) return "shuttle";
			if (getGroupName().equalsIgnoreCase("Stealth Bomber")) return "cruiser";
			if (getGroupName().equalsIgnoreCase("Strategic Cruiser")) return "cruiser";
			if (getGroupName().equalsIgnoreCase("Tactical Destroyer")) return "destroyer";
		}
		return "not-applies";
	}

	public NeoItem setHullGroup( final String dummy ) {
		return this;
	}

//	/**
//	 * Try to get the best price for this element. There are two sets of prices, those for selling an item
//	 * (highest buyers) and those to buy the same item (lower seller). The default price that is the one from
//	 * the SDE database if sometimes far from close to the real market price.
//	 * The item declared price can also be obtained from the market but this will require a location to get
//	 * access to t the CCP API. The item closest price will be obtained from the best buyer so that price
//	 * represent the income I will obtain in case I sell that item.
//	 * For simple item it is not an important point since the interface allows to get the original data to any
//	 * higher level model object.
//	 */
//	public MarketDataEntry getLowestSellerPrice() throws ExecutionException, InterruptedException {
//		return this.getSellerMarketData().getBestMarket();
//	}
//
//	public NeoItem setLowestSellerPrice( final MarketDataEntry dummy ) {
//		return this;
//	}
//
//	public MarketDataEntry getHighestBuyerPrice() throws ExecutionException, InterruptedException {
//		return this.getBuyerMarketData().getBestMarket();
//	}
//
//	public NeoItem setHighestBuyerPrice( final MarketDataEntry dummy ) {
//		return this;
//	}

	public String getCategoryName() {
		if (null == this.category) this.loadup();
		return this.category.getName();
	}

//	public NeoItem setCategoryName( final String dummy ) {
//		return this;
//	}

	@RequiresNetwork
	public String getGroupName() {
		if (null == this.group) this.loadup();
		return this.group.getName();
	}

//	public NeoItem setGroupName( final String dummy ) {
//		return this;
//	}

	// - I I T E M F A C E T
	public String getTypeIconURL() {
		return "https://image.eveonline.com/Type/" + this.getTypeId() + "_64.png";
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				       .appendSuper(super.hashCode())
				       .append(this.id)
				       .append(this.item)
				       .append(this.group)
				       .append(this.category)
				       .append(this.price)
				       .append(this.tech)
				       .append(this.industryGroup)
				       .toHashCode();
	}

	// - C O R E

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final NeoItem neoItem = (NeoItem) o;
		return new EqualsBuilder()
				       .appendSuper(super.equals(o))
				       .append(this.id, neoItem.id)
				       .append(this.price, neoItem.price)
				       .append(this.item, neoItem.item)
				       .append(this.group, neoItem.group)
				       .append(this.category, neoItem.category)
				       .append(this.tech, neoItem.tech)
				       .append(this.industryGroup, neoItem.industryGroup)
				       .isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				       .append("id", this.id)
				       .append("item", this.item)
				       .append("group", this.group)
				       .append("category", this.category)
				       .append("price", this.price)
				       .append("tech", this.tech)
				       .append("industryGroup", this.industryGroup)
				       .toString();
	}

	// - P R I V A T E   S E C T I O N
	protected void classifyIndustryGroup() {
		if ((this.getGroupName().equalsIgnoreCase("Composite")) && (this.getCategoryName().equalsIgnoreCase("Material"))) {
			industryGroup = IndustryGroup.REACTIONMATERIALS;
		}
		if (this.getCategoryName().equalsIgnoreCase("Asteroid")) {
			industryGroup = IndustryGroup.OREMATERIALS;
		}
		if ((this.getGroupName().equalsIgnoreCase("Mining Crystal")) && (this.getCategoryName().equalsIgnoreCase("Charge"))) {
			industryGroup = IndustryGroup.ITEMS;
		}
		if (this.getCategoryName().equalsIgnoreCase("Charge")) {
			industryGroup = IndustryGroup.CHARGE;
		}
		if (this.getGroupName().equalsIgnoreCase("Tool")) {
			industryGroup = IndustryGroup.ITEMS;
		}
		if (this.getCategoryName().equalsIgnoreCase("Commodity")) {
			industryGroup = IndustryGroup.COMMODITY;
		}
		if (this.getCategoryName().equalsIgnoreCase(GlobalWideConstants.EveGlobal.BLUEPRINT)) {
			industryGroup = IndustryGroup.BLUEPRINT;
		}
		if (this.getCategoryName().equalsIgnoreCase(GlobalWideConstants.EveGlobal.SKILL)) {
			industryGroup = IndustryGroup.SKILL;
		}
		if (this.getGroupName().equalsIgnoreCase(GlobalWideConstants.EveGlobal.MINERAL)) {
			industryGroup = IndustryGroup.REFINEDMATERIAL;
		}
		if (this.getCategoryName().equalsIgnoreCase("Module")) {
			industryGroup = IndustryGroup.COMPONENTS;
		}
		if (this.getCategoryName().equalsIgnoreCase("Drone")) {
			industryGroup = IndustryGroup.ITEMS;
		}
		if (this.getCategoryName().equalsIgnoreCase("Planetary Commodities")) {
			industryGroup = IndustryGroup.PLANETARYMATERIALS;
		}
		if (this.getGroupName().equalsIgnoreCase("Datacores")) {
			industryGroup = IndustryGroup.DATACORES;
		}
		if (this.getGroupName().equalsIgnoreCase("Salvaged Materials")) {
			industryGroup = IndustryGroup.SALVAGEDMATERIAL;
		}
		if (this.getCategoryName().equalsIgnoreCase("Ship")) {
			industryGroup = IndustryGroup.HULL;
		}
	}

//	/**
//	 * Submits a <code>Callable</code> request to the background threads to retrieve the data into the <code>Future</code>. In
//	 * the case the market data is accessed and the Future was not completed the thread should wait until the market data access
//	 * completes. Most of the calls will execute fast because the data being cached continuously py the scheduled submitted jobs.
//	 *
//	 * @param itemId the items id to search market data.
//	 * @param side   if we should search buy orders or sell orders.
//	 * @return a <code>Future</code> with the whole market data values.
//	 */
//	private Future<MarketDataSet> retrieveMarketData( final int itemId, final EMarketSide side ) {
//		return esiDataProvider.searchMarketData(itemId, side);
//	}
//
//	/**
//	 * Search on the market data provider for the market registers for this particular item. This will search on
//	 * the market data cache and then if not found on the market data service that will call the corresponding
//	 * parsers to extract the information.<br>
//	 * This method can fail by some causes. First of them because there are no connection to the sources of by
//	 * errors during the parsing of the information. In such cases I should be ready to get the price
//	 * information from other sources like the default price information.
//	 */
//	public MarketDataSet getBuyerMarketData() throws ExecutionException, InterruptedException {
//		if (null == futureBuyerData) {
//			futureBuyerData = retrieveMarketData(getTypeId(), EMarketSide.BUYER);
//		}
//		return futureBuyerData.get();
//	}
//
//	/**
//	 * Search on the market data provider for the market registers for this particular item. This will search on
//	 * the market data cache and then if not found on the market data service that will call the corresponding
//	 * parsers to extract the information.<br>
//	 * This method can fail by some causes. First of them because there are no connection to the sources of by
//	 * errors during the parsing of the information. In such cases I should be ready to get the price
//	 * information from other sources like the default price information.
//	 */
//	public MarketDataSet getSellerMarketData() throws ExecutionException, InterruptedException {
//		if (null == futureBuyerData) {
//			futureBuyerData = retrieveMarketData(getTypeId(), EMarketSide.SELLER);
//		}
//		return futureBuyerData.get();
//	}

	public enum ItemTechnology {
		Tech_1("Tech I"), Tech_2("Tech II"), Tech_3("Tech III");

		// - I N V E R S E   L O O K U P   T A B L E
		private static final Map<String, ItemTechnology> lookup = new HashMap<>();

		static {
			for (ItemTechnology env : ItemTechnology.values()) {
				lookup.put(env.getName(), env);
			}
		}

		private String label;

		ItemTechnology( String newlabel ) {
			this.label = newlabel;
		}

		/** Return the item tech from the label string by matching it to the enum label. */
		public static ItemTechnology lookupLabel( String label ) {
			return lookup.get(label);
		}

		public String getName() {
			return this.label;
		}
	}
}
