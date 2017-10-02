//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.connector;

import java.util.GregorianCalendar;

import org.dimensinfin.eveonline.neocom.interfaces.INeoComModelStore;
import org.joda.time.Duration;
import org.joda.time.Instant;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class role is to allow the Model to use external environment functions that can change depending on
 * the final implementing platform like Android or Sprint Boot. Allows the coding of the model outside from
 * application of other external dependencies such as file systems or file locations. This is a proxy that
 * will send the messages to the real connector supplied by the application at runtime.
 * 
 * @author Adam Antinoo
 */
public class AppConnector {
	// - P R E F E R E N C E S
	public static final class preference {
		public static final String	PREF_APPTHEMES						= "prefkey_appthemes";
		public static final String	PREF_LOCATIONSLIMIT				= "prefkey_locationsLimit";
		public static final String	PREF_ALLOWMOVEREQUESTS		= "prefkey_AllowMoveRequests";
		public static final String	PREF_CALCULATEASSETVALUE	= "prefkey_AssetValueCalculation";
		public static final String	PREF_BLOCKDOWNLOAD				= "prefkey_BlockDownloads";
		public static final String	PREF_BLOCKMARKET					= "prefkey_BlockMarket";
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static IConnector	connection	= null;
	private static Instant		chrono			= null;

	public static void addCharacterUpdateRequest(final long characterID) {
		if (null != AppConnector.connection) {
			AppConnector.connection.addCharacterUpdateRequest(characterID);
		} else
			throw new RuntimeException(
					"Application connector not defined. Functionality 'addCharacterUpdateRequest' disabled.");
	}

	public static boolean checkExpiration(final Instant timestamp, final long window) {
		if (null == timestamp) return true;
		return AppConnector.checkExpiration(timestamp.getMillis(), window);
	}

	/**
	 * Checks that the current parameter timestamp is still on the frame of the window.
	 * 
	 * @param timestamp
	 *          the current and last timestamp of the object.
	 * @param window
	 *          time span window in milliseconds.
	 */
	public static boolean checkExpiration(final long timestamp, final long window) {
		if (0 == timestamp) return true;
		final long now = GregorianCalendar.getInstance().getTimeInMillis();
		final long endWindow = timestamp + window;
		if (now < endWindow)
			return false;
		else
			return true;
	}

	public static IConnector getAppSingleton() {
		if (null != AppConnector.connection)
			return AppConnector.connection.getAppSingleton();
		else
			throw new RuntimeException("Application connector not defined. Functionality 'getAppSingleton' disabled.");
	}

	public static ICacheConnector getCacheConnector() {
		if (null != AppConnector.connection)
			return AppConnector.connection.getCacheConnector();
		else
			throw new RuntimeException("Application connector not defined. Functionality 'getCacheConnector' disabled.");
	}

	public static ICCPDatabaseConnector getCCPDBConnector() {
		if (null != AppConnector.connection)
			return AppConnector.connection.getCCPDBConnector();
		else
			throw new RuntimeException("Application connector not defined. Functionality 'getCCPDBConnector' disabled.");
	}

	public static IDatabaseConnector getDBConnector() {
		if (null != AppConnector.connection)
			return AppConnector.connection.getDBConnector();
		else
			throw new RuntimeException("Application connector not defined. Functionality 'getDBConnector' disabled.");
	}

	public static INeoComModelStore getModelStore() {
		if (null != AppConnector.connection)
			return AppConnector.connection.getModelStore();
		else
			throw new RuntimeException("Application connector not defined. Functionality 'getModelStore' disabled.");
	}

	public static IStorageConnector getStorageConnector() {
		if (null != AppConnector.connection)
			return AppConnector.connection.getStorageConnector();
		else
			throw new RuntimeException("Application connector not defined. Functionality 'getStorageConnector' disabled.");
	}

	public static void setConnector(final IConnector androidApp) {
		if (null == androidApp) throw new RuntimeException("Required connector is not properly defined.");
		AppConnector.connection = androidApp;
	}

	public static void startChrono() {
		AppConnector.chrono = new Instant();
	}

	public static Duration timeLapse() {
		return new Duration(AppConnector.chrono, new Instant());
	}

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AppConnector() {
	}

	// - F I E L D - S E C T I O N ............................................................................
	// - M E T H O D - S E C T I O N ..........................................................................
}

// - UNUSED CODE ............................................................................................
