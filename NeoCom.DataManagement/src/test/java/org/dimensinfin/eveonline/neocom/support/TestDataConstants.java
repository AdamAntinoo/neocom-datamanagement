package org.dimensinfin.eveonline.neocom.support;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;

public class TestDataConstants {
	public static class ESIDataServiceConstants {
		public static final Integer TEST_CHARACTER_IDENTIFIER = 92223647;
	}

	public static class CredentialConstants {
		public static final Integer TEST_CREDENTIAL_ACCOUNT_ID = 92223647;
	}

	public static class RetrofitFactoryConstants {
		public static final String TEST_RETROFIT_BASE_URL = "http://localhost";
		public static final String TEST_RETROFIT_AGENT = "-TEST_RETROFIT_AGENT-";
		public static final Integer TEST_RETROFIT_TIMEOUT = 10;
	}

	public static class EsiUniverseTestDataConstants {
		public static final GetCharactersCharacterIdOk TEST_ESI_CHARACTER_DATA = new GetCharactersCharacterIdOk();
		public static final String TEST_ESI_CHARACTER_NAME = "-TEST_ESI_CHARACTER_NAME-";
		public static final String TEST_ESI_CHARACTER_DESCRIPTION = "-TEST_ESI_CHARACTER_DESCRIPTION-";
		public static final Integer TEST_ESI_CHARACTER_CORPORATION_IDENTIFIER = 98384726;
		public static final Integer TEST_ESI_CHARACTER_RACE_IDENTIFIER = 100;
		public static final Integer TEST_ESI_CHARACTER_ANCESTRY_IDENTIFIER = 200;
		public static final Integer TEST_ESI_CHARACTER_BLOODLINE_IDENTIFIER = 300;
		public static final Float TEST_ESI_CHARACTER_SECURITY_STATUS = 0.5F;

		static {
			TEST_ESI_CHARACTER_DATA.setName( TEST_ESI_CHARACTER_NAME );
			TEST_ESI_CHARACTER_DATA.setCorporationId( TEST_ESI_CHARACTER_CORPORATION_IDENTIFIER );
			//			TEST_ESI_CHARACTER_DATA.setBirthday( new DateTime() );
			TEST_ESI_CHARACTER_DATA.setRaceId( TEST_ESI_CHARACTER_RACE_IDENTIFIER );
			TEST_ESI_CHARACTER_DATA.setAncestryId( TEST_ESI_CHARACTER_ANCESTRY_IDENTIFIER );
			TEST_ESI_CHARACTER_DATA.setBloodlineId( TEST_ESI_CHARACTER_BLOODLINE_IDENTIFIER );
			TEST_ESI_CHARACTER_DATA.setDescription( TEST_ESI_CHARACTER_DESCRIPTION );
			TEST_ESI_CHARACTER_DATA.setGender( GetCharactersCharacterIdOk.GenderEnum.MALE );
			TEST_ESI_CHARACTER_DATA.setSecurityStatus( TEST_ESI_CHARACTER_SECURITY_STATUS );
		}
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
	public static class PilotPreferencesConstants{
		public static final Integer TEST_PILOT_PREFERENCE_PILOT_ID =98384726;
		public static final String TEST_PILOT_PREFERENCE_NAME = "-PREFERENCE-";
		public static final String TEST_PILOT_PREFERENCE_STRING_VALUE = "Amarr VIII (Oris) - Emperor Family Academy";
		public static final Double TEST_PILOT_PREFERENCE_NUMERIC_VALUE =87642.5623334;
	}
}
