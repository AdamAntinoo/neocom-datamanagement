package org.dimensinfin.eveonline.neocom.support;

import org.dimensinfin.eveonline.neocom.domain.IndustryGroup;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;

import static org.dimensinfin.eveonline.neocom.provider.ESIDataProvider.DEFAULT_ESI_SERVER;

public class TestDataConstants {
	public static class ESIDataServiceConstants {
		public static final Integer TEST_CHARACTER_IDENTIFIER = 91734031;
	}

	public static class CredentialConstants {
		public static final Integer TEST_CREDENTIAL_ACCOUNT_ID = 91734031;
		public static final String TEST_CREDENTIAL_UNIQUE_ID = DEFAULT_ESI_SERVER.toLowerCase() + "." + TEST_CREDENTIAL_ACCOUNT_ID;
		public static final String TEST_CREDENTIAL_SCOPE = "publicData";
	}

	public static class RetrofitFactoryConstants {
		public static final String TEST_RETROFIT_BASE_URL = "http://localhost";
		public static final String TEST_RETROFIT_AGENT = "-TEST_RETROFIT_AGENT-";
		public static final Integer TEST_RETROFIT_TIMEOUT = 10;
	}


	public static class SpaceLocationConstants {
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

	public static class PilotPreferencesConstants {
		public static final Integer TEST_PILOT_PREFERENCE_PILOT_ID = 98384726;
		public static final String TEST_PILOT_PREFERENCE_NAME = "-PREFERENCE-";
		public static final String TEST_PILOT_PREFERENCE_STRING_VALUE = "Amarr VIII (Oris) - Emperor Family Academy";
		public static final Double TEST_PILOT_PREFERENCE_NUMERIC_VALUE = 87642.5623334;
	}

	public static class SDERepositoryConstants {
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
	}

	public static final class ResourceConstants {
		public static final Integer TEST_RESOURCE_TYPE_ID = 16636;
		public static final Integer TEST_QUANTITY = 543;
		public static final String TEST_RESOURCE_GROUP = "Moon Materials";
		public static final String TEST_RESOURCE_CATEGORY = "Material";
		public static final String TEST_RESOURCE_NAME = "Silicates";
		public static final Double TEST_RESOURCE_VOLUME = 0.5;
	}
}
