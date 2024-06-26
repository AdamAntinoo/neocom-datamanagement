package org.dimensinfin.eveonline.neocom.utility;

public class GlobalWideConstants {
	public static final String STACKID_SEPARATOR = "/";
	public static final String REDIS_SEPARATOR=":";

	// - C O N S T R U C T O R S
	private GlobalWideConstants() {}

	public static final class EveGlobal {
		public static final String TECH_I = "Tech I";
		public static final String TECH_II = "Tech II";
		public static final String TECH_III = "Tech III";
		public static final String BLUEPRINT = "Blueprint";
		public static final String MODULE = "Module";
		public static final String MINERAL = "Mineral";
		public static final String SKILL = "Skill";
		public static final String COMMODITY = "Commodity";
		public static final String CHARGE = "Charge";
		public static final String SHIP = "Ship";
		public static final String ASTEROID = "Asteroid";
		public static final String DATACORES = "Datacores";

		public static final class IndustrySkillIdentifiers {
			public static final String INDUSTRY = "Industry";
			public static final String ADVANCED_INDUSTRY = "Advanced Industry";
			public static final int MASS_PRODUCTION = 3387;
			public static final int ADVANCED_MASS_PRODUCTION = 24625;
			public static final int LABORATORY_OPERATION = 3406;
			public static final int ADVANCED_LABORATORY_OPERATION = 24624;
		}
	}

	// - L O C A T I O N   R O L E S
	public static final class LocationRoles {
		public static final String REFINE = "REFINE";
		public static final String ADVANCEDINDUSTRY = "Advanced Industry";
	}

	// - D A T A    S T O R E   K E Y S
	public static final class DataStoreKeys {
		@Deprecated
		public static final String LOWEST_SELL_ORDER_MAP = "LSO";
		@Deprecated
		public static final Integer LOWEST_SELL_ORDER_TTL = 300;
		// - Universe ESI Types
		public static final String ESI_UNIVERSE_TYPE_KEY_NAME="UTY";
		public static final Integer ESI_UNIVERSE_TYPE_KEY_TTL = -1;
		// - ESI Types
		public static final String ESI_TYPE_KEY_NAME="TYP";
		public static final Integer ESI_TYPE_KEY_TTL = 48;
		// - Locations
		public static final String SPACE_LOCATIONS_KEY_NAME = "SPL";
		public static final Integer SPACE_LOCATIONS_CACHE_TTL = 12;
		// - Extended Blueprints
		public static final String EXTENDED_BLUEPRINTS_KEY_NAME = "ECM";
		public static final Integer EXTENDED_BLUEPRINTS_TTL = 12;
	}
}
