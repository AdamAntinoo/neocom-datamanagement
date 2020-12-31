//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.
package org.dimensinfin.eveonline.neocom.constant;

// - IMPORT SECTION .........................................................................................

// - CLASS IMPLEMENTATION ...................................................................................
public class ModelWideConstants {
	// - M O D E L   V I E W   C O N T R O L L E R   V A R I A N T S
	//	public enum EVARIANT{
	//		DEFAULT_VARIANT
	//	}
	// - I N D U S T R Y   A C T I V I T I E S
//	public static final class activities {
//		public static final int	NO_ACTIVITY		= 0;
//		public static final int	MANUFACTURING	= 1;
//		public static final int	RESEARCH_EFI	= 3;
//		public static final int	RESEARCH_TIME	= 4;
//		public static final int	COPYING				= 5;
//		public static final int	INVENTION			= 8;
//	}

	// - S T A T I C - S E C T I O N ..........................................................................
	// - C O M P A R A T O R S
//	public static final class comparators {
//		public static final int	COMPARATOR_NAME							= 100;
//		public static final int	COMPARATOR_ASSET_COUNT			= comparators.COMPARATOR_NAME + 1;
//		public static final int	COMPARATOR_ITEM_TYPE				= comparators.COMPARATOR_NAME + 2;
//		public static final int	COMPARATOR_RESOURCE_TYPE		= comparators.COMPARATOR_NAME + 3;
//		public static final int	COMPARATOR_APIID_ASC				= comparators.COMPARATOR_NAME + 4;
//		public static final int	COMPARATOR_APIID_DESC				= comparators.COMPARATOR_NAME + 5;
//		public static final int	COMPARATOR_PRIORITY					= comparators.COMPARATOR_NAME + 6;
//		public static final int	COMPARATOR_WEIGHT						= comparators.COMPARATOR_NAME + 16;
//		public static final int	COMPARATOR_NEWESTDATESORT		= comparators.COMPARATOR_NAME + 7;
//		public static final int	COMPARATOR_OLDESTDATESORT		= comparators.COMPARATOR_NAME + 8;
//		public static final int	COMPARATOR_REQUEST_PRIORITY	= comparators.COMPARATOR_NAME + 17;
//		public static final int	COMPARATOR_CARD_RATIO				= comparators.COMPARATOR_NAME + 18;
//		// public static final int COMPARATOR_CREATIONDATE = COMPARATOR_NAME +
//		// 9;
//		public static final int	COMPARATOR_TIMEPENDING			= comparators.COMPARATOR_NAME + 10;
//	}

	// - L I T E R A L   C O N S T A N T S
	public static final class eveglobal {
		public static final class skillcodes {
			public static final int	MassProduction							= 3387;
			public static final int	AdvancedMassProduction			= 24625;
			public static final int	LaboratoryOperation					= 3406;
			public static final int	AdvancedLaboratoryOperation	= 24624;
		}

		public static final class skills {
			public static final String	INDUSTRY					= "Industry";
			public static final String	ADVANCEDINDUSTRY	= "Advanced Industry";
		}

		public static final String	TechI			= "Tech I";
		public static final String	TechII		= "Tech II";
		public static final String	TechIII		= "Tech III";
		public static final String	Blueprint	= "Blueprint";
		public static final String	Module		= "Module";
		public static final String	Mineral		= "Mineral";
		public static final String	Skill			= "Skill";
		public static final String	Commodity	= "Commodity";
		public static final String	Charge		= "Charge";
		public static final String	Ship			= "Ship";
		public static final String	Asteroid	= "Asteroid";
		public static final String	Datacores	= "Datacores";
	}

	// - J O B   S T A T U S
//	public static final class jobstatus {
//		public static final int	ACTIVE		= 1;
//		public static final int	PAUSED		= 2;
//		public static final int	READY			= 3;
//		public static final int	SCHEDULED	= 10;
//		public static final int	DELIVERED	= 101;
//		public static final int	CANCELLED	= 102;
//		public static final int	REVERTED	= 103;
//	}

	// - L O C A T I O N   R O L E S
	public static final class locationroles {
		public static final String	REFINE						= "REFINE";
		public static final String	ADVANCEDINDUSTRY	= "Advanced Industry";
	}

	// - M A R K E T   S I D E   I D E N T I F I E R S  -  E V E M A R K E T D A T A
//	public static final class marketSide {
//		public static final String	BUYER			= "BUY";
//		public static final String	SELLER		= "SELL";
//		public static final String	CALCULATE	= "CALCULATE";
//	}

	// - M A R K E T   O R D E R   S T A T E S
	public static final class orderstates {
		public static final int	OPEN			= 0;
		public static final int	CLOSED		= 1;
		public static final int	EXPIRED		= 2;
		public static final int	CANCELLED	= 3;
		public static final int	PENDING		= 4;
		public static final int	SCHEDULED	= 10;
	}

	// - M U L T I V A L U E   R E L A T I O N A L   T Y P E S
	public static final class property {
		public static final int	UNDEFINED	= -1;
		public static final int	USERLABEL	= 10;
	}

	// - E X P I R A T I O N   T I M E S
//	public static final int			NOW								= 0;
//	public static final int			SECONDS1					= 1 * 1000;
//	public static final int			SECONDS5					= 5 * ModelWideConstants.SECONDS1;
//	public static final int			MINUTES1					= 1 * 60 * ModelWideConstants.SECONDS1;
//	public static final int			MINUTES15					= 15 * ModelWideConstants.MINUTES1;
//	public static final int			MINUTES30					= 30 * ModelWideConstants.MINUTES1;
//	public static final int			MINUTES45					= 45 * ModelWideConstants.MINUTES1;
//	public static final int			MINUTES60					= 60 * ModelWideConstants.MINUTES1;
//	public static final int			HOURS1						= 1 * 60 * ModelWideConstants.MINUTES1;
//	public static final int			HOURS2						= 2 * ModelWideConstants.HOURS1;
//	public static final int			HOURS3						= 3 * ModelWideConstants.HOURS1;
//	public static final int			HOURS8						= 8 * ModelWideConstants.HOURS1;
//
//	public static final int			HOURS6						= 6 * ModelWideConstants.HOURS1;
//	public static final int			HOURS24						= 24 * ModelWideConstants.HOURS1;
	public static final String	STACKID_SEPARATOR	= "/";
}

// - UNUSED CODE ............................................................................................
