package org.dimensinfin.eveonline.neocom.constant;

@Deprecated
public class ModelWideConstants {
	public static final String STACKID_SEPARATOR = "/";

	@Deprecated
	public static final class eveglobal {
		public static final String TechI = "Tech I";
		public static final String TechII = "Tech II";
		public static final String TechIII = "Tech III";
		public static final String Blueprint = "Blueprint";
		public static final String Module = "Module";
		public static final String Mineral = "Mineral";
		public static final String Skill = "Skill";
		public static final String Commodity = "Commodity";
		public static final String Charge = "Charge";
		public static final String Ship = "Ship";
		public static final String Asteroid = "Asteroid";
		public static final String Datacores = "Datacores";

		public static final class skillcodes {
			public static final int MassProduction = 3387;
			public static final int AdvancedMassProduction = 24625;
			public static final int LaboratoryOperation = 3406;
			public static final int AdvancedLaboratoryOperation = 24624;
		}

		public static final class skills {
			public static final String INDUSTRY = "Industry";
			public static final String ADVANCEDINDUSTRY = "Advanced Industry";
		}
	}

	// - L O C A T I O N   R O L E S
	public static final class locationroles {
		public static final String REFINE = "REFINE";
		public static final String ADVANCEDINDUSTRY = "Advanced Industry";
	}

	// - M A R K E T   O R D E R   S T A T E S
	public static final class orderstates {
		public static final int OPEN = 0;
		public static final int CLOSED = 1;
		public static final int EXPIRED = 2;
		public static final int CANCELLED = 3;
		public static final int PENDING = 4;
		public static final int SCHEDULED = 10;
	}

	// - M U L T I V A L U E   R E L A T I O N A L   T Y P E S
	public static final class property {
		public static final int UNDEFINED = -1;
		public static final int USERLABEL = 10;
	}
}
