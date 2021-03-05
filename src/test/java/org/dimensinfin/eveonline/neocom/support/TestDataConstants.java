package org.dimensinfin.eveonline.neocom.support;

import org.joda.time.DateTime;

import org.dimensinfin.eveonline.neocom.domain.IndustryGroup;
import org.dimensinfin.eveonline.neocom.esiswagger.model.CharacterscharacterIdfittingsItems;

import static org.dimensinfin.eveonline.neocom.provider.ESIDataProvider.DEFAULT_ESI_SERVER;

public class TestDataConstants {
	public static final class ESIDataServiceConstants {
		public static final Integer TEST_CHARACTER_IDENTIFIER = 91734031;
		public static final Integer TEST_ALLIANCE_IDENTIFIER = 117383987;
	}

	public static final class CredentialConstants {
		public static final Integer TEST_CREDENTIAL_ACCOUNT_ID = 91734031;
		public static final String TEST_CREDENTIAL_UNIQUE_ID = DEFAULT_ESI_SERVER.toLowerCase() + "." + TEST_CREDENTIAL_ACCOUNT_ID;
		public static final String TEST_CREDENTIAL_SCOPE = "publicData";
	}

	public static final class RetrofitFactoryConstants {
		public static final String TEST_RETROFIT_BASE_URL = "http://localhost";
		public static final String TEST_RETROFIT_AGENT = "-TEST_RETROFIT_AGENT-";
		public static final Integer TEST_RETROFIT_TIMEOUT = 10;
	}

	public static final class SpaceLocationConstants {
		public static final Integer TEST_REGION_ID = 1000043;
		public static final String TEST_REGION_NAME = "Domain";
		public static final Integer TEST_CONSTELLATION_ID = 20000322;
		public static final String TEST_CONSTELLATION_NAME = "Throne Worlds";
		public static final Integer TEST_SOLAR_SYSTEM_ID = 30002187;
		public static final String TEST_SOLAR_SYSTEM_NAME = "Amarr";
		public static final String TEST_SOLAR_SYSTEM_SECURITY_CLASS = "E1";
		public static final Float TEST_SOLAR_SYSTEM_SECURITY_STATUS = 0.5623334F;
		public static final Integer TEST_STATION_ID = 60008494;
		public static final String TEST_STATION_NAME = "Amarr VIII (Oris) - Emperor Family Academy";
		public static final Long TEST_STRUCTURE_ID = 60008494L;
		public static final Integer TEST_STRUCTURE_TYPE_ID = 60008494;
		public static final String TEST_STRUCTURE_NAME = "Amarr VIII (Oris) - Emperor Family Academy";
		public static final Integer TEST_CORPORATION_ID = 98384726;
		public static final String TEST_CORPORATION_NAME = "Industrias Machaque";
		public static final Integer TEST_CORPORATION_OWNER_ID = 98384726;
	}

	public static final class PilotPreferencesConstants {
		public static final Integer TEST_PILOT_PREFERENCE_PILOT_ID = 98384726;
		public static final String TEST_PILOT_PREFERENCE_NAME = "-PREFERENCE-";
		public static final String TEST_PILOT_PREFERENCE_STRING_VALUE = "Amarr VIII (Oris) - Emperor Family Academy";
		public static final Double TEST_PILOT_PREFERENCE_NUMERIC_VALUE = 87642.5623334;
	}

	public static final class SDERepositoryConstants {
		public static final Integer TEST_SDE_REPOSITORY_TYPE_ID = 32873;
	}

	public static final class EsiTypeConstants {
		public static final Integer TEST_ESITYPE_ID = 11535;
		public static final String TEST_ESITYPE_NAME = "Magnetometric Sensor Cluster";
		public static final String TEST_ESITYPE_GROUP_NAME = "Construction Components";
		public static final String TEST_ESITYPE_CATEGORY_NAME = "Commodity";
		public static final String TEST_ESITYPE_TYPEICON_URL = "https://image.eveonline.com/Type/11535_64.png";
		public static final IndustryGroup TEST_ESITYPE_INDUSTRYGROUP_NAME = IndustryGroup.COMMODITY;
		public static final String TEST_ESITYPE_HULLGROUP_NAME = "not-applies";
		public static final String TEST_ESITYPE_TECH = "Tech I";
		public static final Float TEST_ESITYPE_VOLUME = 0.1F;
	}

	public static final class ResourceConstants {
		public static final Integer TEST_RESOURCE_TYPE_ID = 16636;
		public static final Integer TEST_RESOURCE_QUANTITY = 543;
		public static final String TEST_RESOURCE_GROUP = "Moon Materials";
		public static final String TEST_RESOURCE_CATEGORY = "Material";
		public static final String TEST_RESOURCE_NAME = "Silicates";
		public static final Double TEST_RESOURCE_VOLUME = 0.5;
	}

	public static final class LoyaltyOfferEntityConstants {
		public static final Integer TEST_LOYALTY_OFFER_ID = 4104;
		public static final int TEST_LOYALTY_OFFER_CORPORATION_ID = 1000179;
		public static final String TEST_LOYALTY_OFFER_CORPORATION_NAME = "24th Imperial Crusade";
		public static final int TEST_LOYALTY_OFFER_LP_COST = 10875;
		public static final long TEST_LOYALTY_OFFER_ISK_COST = 10875000L;
		public static final int TEST_LOYALTY_OFFER_QUANTITY = 1;
		public static final int TEST_LOYALTY_MARKET_HUB_REGION = 10000030;
		public static final int TEST_LOYALTY_ITEM_ID = 10000030;
		public static final String TEST_LOYALTY_ITEM_ID_NAME = "-ITEM-NAME-";
		public static final Double TEST_LOYALTY_ITEM_SELL_PRICE = 123.45;
	}

	public static final class PilotConstants {
		public static final Integer TEST_PUBLIC_PILOT_ID = 91734031;
		public static final DateTime TEST_PUBLIC_PILOT_BIRTHDATE = DateTime.now();
		public static final Integer TEST_PILOT_LOCATION_SYSTEM_ID = 54323456;
		public static final Integer TEST_PUBLIC_PILOT_CORPORATION_ID = 43256785;
		public static final Integer TEST_PUBLIC_CORPORATION_CEO_ID = 91734031;
		public static final String TEST_PUBLIC_PILOT_DESCRIPTION = "-DESCRIPTION-";
		public static final Integer TEST_PUBLIC_PILOT_FACTION_ID = 32;
		public static final String TEST_PUBLIC_PILOT_NAME = "-NAME-";
		public static final Float TEST_PUBLIC_PILOT_SECURITY_STATUS = 0.1F;
		public static final String TEST_PUBLIC_PILOT_TITLE = "-TITLE-";
		public static final Long TEST_PILOT_SKILL_POINTS = 1436765L;
		public static final Double TEST_PILOT_WALLET_BALANCE = 432567.0;
	}

	public static final class PricedResourceConstants {
		public static final Integer TEST_RESOURCE_TYPE_ID = 16636;
		public static final Integer TEST_RESOURCE_QUANTITY = 543;
		public static final String TEST_RESOURCE_GROUP = "Moon Materials";
		public static final String TEST_RESOURCE_CATEGORY = "Material";
		public static final String TEST_RESOURCE_NAME = "Silicates";
		public static final Double TEST_RESOURCE_VOLUME = 0.5;
		public static final Double TEST_RESOURCE_PRICE = 76545.98;
	}

	public static final class MarketServiceConstants {
		public static final Integer TEST_MARKET_SERVICE_REGION_ID = 10000002;
		public static final Integer TEST_MARKET_SERVICE_TYPE_ID = 16636;
	}

	public static final class MarketDataConstants {
		public static final Double TEST_MARKET_DATA_BEST_SELL_PRICE = 345.98;
		public static final Double TEST_MARKET_DATA_BEST_BUY_PRICE = 164.09;
	}

	public static final class MarketOrderConstants {
		public static final Long TEST_MARKET_ORDER_ID = 4275798L;
		public static final Integer TEST_MARKET_ORDER_TYPE_ID = 16636;
		public static final Integer TEST_MARKET_ORDER_SYSTEM_ID = 60003760;
		public static final Double TEST_MARKET_ORDER_PRICE = 345.98;
		public static final Integer TEST_MARKET_ORDER_VOLUME_REMAIN = 100;
		public static final Integer TEST_MARKET_ORDER_VOLUME_TOTAL = 200;
	}

	public static final class FittingItemConstants {
		public static final CharacterscharacterIdfittingsItems.FlagEnum TEST_FITTING_ITEM_FLAG =
				CharacterscharacterIdfittingsItems.FlagEnum.HISLOT6;
		public static final Integer TEST_FITTING_ITEM_QUANTITY = 2;
		public static final Integer TEST_FITTING_ITEM_TYPE_ID = 12056;
		public static final String TEST_FITTING_ITEM_TYPE_NAME = "10MN Afterburner I";
	}

	public static final class CorporationsConstants {
		public static final Integer TEST_CORPORATION_ID = 1427661573;
		public static final Integer TEST_CORPORATION_CEO_ID = 93813310;

	}

	public static class ProcessedBlueprintConstants {
		public static final Integer TEST_PROCESSED_BLUEPRINT_ID = 31717;
	}
}
