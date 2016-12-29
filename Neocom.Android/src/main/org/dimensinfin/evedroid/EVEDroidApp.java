//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid;

// - IMPORT SECTION .........................................................................................
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.AndroidCacheConnector;
import org.dimensinfin.evedroid.core.AndroidDatabaseConnector;
import org.dimensinfin.evedroid.core.AndroidStorageConnector;
import org.dimensinfin.evedroid.interfaces.ICache;
import org.dimensinfin.evedroid.interfaces.IDateTimeComparator;
import org.dimensinfin.evedroid.interfaces.INamedPart;
import org.dimensinfin.evedroid.interfaces.IWeigthedNode;
import org.dimensinfin.evedroid.part.ApiKeyPart;
import org.dimensinfin.evedroid.part.AssetPart;
import org.dimensinfin.evedroid.part.BlueprintPart;
import org.dimensinfin.evedroid.part.ContainerPart;
import org.dimensinfin.evedroid.part.ResourcePart;
import org.dimensinfin.evedroid.part.ShipPart;
import org.dimensinfin.evedroid.service.PendingRequestEntry;
import org.dimensinfin.evedroid.service.TimeTickReceiver;
import org.dimensinfin.evedroid.storage.AppModelStore;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.connector.IConnector;
import org.dimensinfin.eveonline.neocom.connector.IDatabaseConnector;
import org.dimensinfin.eveonline.neocom.connector.IStorageConnector;
import org.dimensinfin.eveonline.neocom.core.INeoComModelStore;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.interfaces.INamed;
import org.dimensinfin.eveonline.neocom.model.JobQueue;
import org.dimensinfin.eveonline.neocom.model.NeoComApiKey;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class EVEDroidApp extends Application implements IConnector {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger							logger									= Logger.getLogger("EVEDroidApp");
	public static Typeface						daysFace								= null;
	private static DecimalFormat			pendingCounter					= new DecimalFormat("0.0##");
	private static EVEDroidApp				singleton								= null;
	private static boolean						firstTimeInitialization	= false;
	private static BroadcastReceiver	timeTickReceiver				= null;
	public static int									topCounter							= 0;
	public static int									marketCounter						= 0;

	//	private static AppModelStore			appModelStore						= null;

	public static boolean checkNetworkAccess() {
		final ConnectivityManager cm = (ConnectivityManager) EVEDroidApp.singleton
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if ((netInfo != null) && netInfo.isConnectedOrConnecting()) return true;
		return false;
	}

	@Deprecated
	public static Comparator<AbstractPropertyChanger> createComparator(final int code) {
		Comparator<AbstractPropertyChanger> comparator = new Comparator<AbstractPropertyChanger>() {
			public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
				return 0;
			}
		};
		switch (code) {
			case AppWideConstants.comparators.COMPARATOR_NAME:
				comparator = new Comparator<AbstractPropertyChanger>() {
					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
						String leftField = null;
						String rightField = null;
						if (left instanceof INamedPart) leftField = ((INamedPart) left).getName();
						if (right instanceof INamedPart) rightField = ((INamedPart) right).getName();
						if (left instanceof INamed) leftField = ((INamed) left).getOrderingName();
						if (right instanceof INamed) rightField = ((INamed) right).getOrderingName();

						if (null == leftField) return 1;
						if (null == rightField) return -1;
						if ("" == leftField) return 1;
						if ("" == rightField) return -1;
						return leftField.compareTo(rightField);
					}
				};
				break;
			case AppWideConstants.comparators.COMPARATOR_ASSET_COUNT:
				comparator = new Comparator<AbstractPropertyChanger>() {
					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
						long leftField = -1;
						long rightField = -1;
						if (left instanceof NeoComAsset) {
							final NeoComAsset intermediate = (NeoComAsset) left;
							leftField = intermediate.getQuantity();
						}

						if (right instanceof NeoComAsset) {
							final NeoComAsset intermediate = (NeoComAsset) right;
							rightField = intermediate.getQuantity();
						}
						if (leftField < rightField) return 1;
						if (leftField > rightField) return -1;
						return 0;
					}
				};
				break;
			case AppWideConstants.comparators.COMPARATOR_ITEM_TYPE:
				comparator = new Comparator<AbstractPropertyChanger>() {
					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
						int leftField = -1;
						int rightField = -1;
						// if (left instanceof BlueprintPart) leftField = 100;
						if (left instanceof AssetPart) leftField = 0;
						if (left instanceof ShipPart) leftField = 200;
						if (left instanceof ContainerPart) leftField = -300;

						// if (right instanceof BlueprintPart) rightField = 100;
						if (right instanceof AssetPart) rightField = 0;
						if (right instanceof ShipPart) rightField = 200;
						if (right instanceof ContainerPart) rightField = -300;

						if (leftField < rightField) return -1;
						if (leftField > rightField) return 1;
						return 0;
					}
				};
				break;
			case AppWideConstants.comparators.COMPARATOR_RESOURCE_TYPE:
				comparator = new Comparator<AbstractPropertyChanger>() {
					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
						int leftField = -1;
						int rightField = -1;
						if (left instanceof ResourcePart) {
							final Resource resource = ((ResourcePart) left).getCastedModel();
							leftField = 0;
							if (resource.getCategory().equalsIgnoreCase("Material")) leftField = -300;
							if (resource.getCategory().equalsIgnoreCase("Module")) leftField = -200;
							if (resource.getCategory().equalsIgnoreCase("Blueprint")) leftField = -100;
							if (resource.getName().contains("Datacore")) leftField = 100;
						}

						if (right instanceof ResourcePart) {
							final Resource resource = ((ResourcePart) left).getCastedModel();
							rightField = 0;
							if (resource.getCategory().equalsIgnoreCase("Material")) rightField = -300;
							if (resource.getCategory().equalsIgnoreCase("Module")) rightField = -200;
							if (resource.getCategory().equalsIgnoreCase("Blueprint")) rightField = -100;
							if (resource.getName().contains("Datacore")) rightField = 100;
						}

						if (leftField < rightField) return -1;
						if (leftField > rightField) return 1;
						return 0;
					}
				};
				break;
			case AppWideConstants.comparators.COMPARATOR_APIID_ASC:
				comparator = new Comparator<AbstractPropertyChanger>() {
					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
						long leftField = -1;
						long rightField = -1;
						if (left instanceof ApiKeyPart) {
							final NeoComApiKey intermediate = ((ApiKeyPart) left).getCastedModel();
							leftField = intermediate.getKey();
						}

						if (right instanceof ApiKeyPart) {
							final NeoComApiKey intermediate = ((ApiKeyPart) right).getCastedModel();
							rightField = intermediate.getKey();
						}
						if (leftField < rightField) return -1;
						if (leftField > rightField) return 1;
						return 0;
					}
				};
				break;
			case AppWideConstants.comparators.COMPARATOR_APIID_DESC:
				comparator = new Comparator<AbstractPropertyChanger>() {
					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
						long leftField = -1;
						long rightField = -1;
						if (left instanceof ApiKeyPart) {
							final NeoComApiKey intermediate = ((ApiKeyPart) left).getCastedModel();
							leftField = intermediate.getKey();
						}

						if (right instanceof ApiKeyPart) {
							final NeoComApiKey intermediate = ((ApiKeyPart) right).getCastedModel();
							rightField = intermediate.getKey();
						}
						if (leftField > rightField) return -1;
						if (leftField < rightField) return 1;
						return 0;
					}
				};
				break;
			case AppWideConstants.comparators.COMPARATOR_REQUEST_PRIORITY:
				comparator = new Comparator<AbstractPropertyChanger>() {
					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
						long leftField = -1;
						long rightField = -1;
						if (left instanceof PendingRequestEntry) {
							final PendingRequestEntry intermediate = (PendingRequestEntry) left;
							leftField = intermediate.getPriority();
						}

						if (right instanceof PendingRequestEntry) {
							final PendingRequestEntry intermediate = (PendingRequestEntry) right;
							rightField = intermediate.getPriority();
						}
						if (leftField < rightField) return -1;
						if (leftField > rightField) return 1;
						return 0;
					}
				};
				break;
			case AppWideConstants.comparators.COMPARATOR_PRIORITY:
				comparator = new Comparator<AbstractPropertyChanger>() {
					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
						int leftField = -1;
						int rightField = -1;
						if (left instanceof PendingRequestEntry) {
							final PendingRequestEntry intermediate = (PendingRequestEntry) left;
							leftField = intermediate.getPriority();
						}

						if (right instanceof PendingRequestEntry) {
							final PendingRequestEntry intermediate = (PendingRequestEntry) right;
							rightField = intermediate.getPriority();
						}
						if (leftField < rightField) return 1;
						if (leftField > rightField) return -1;
						return 0;
					}
				};
				break;
			case AppWideConstants.comparators.COMPARATOR_WEIGHT:
				comparator = new Comparator<AbstractPropertyChanger>() {
					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
						int leftField = -1;
						int rightField = -1;
						if (left instanceof IWeigthedNode) leftField = ((IWeigthedNode) left).getWeight();
						if (right instanceof IWeigthedNode) rightField = ((IWeigthedNode) right).getWeight();
						if (leftField < rightField) return -1;
						if (leftField > rightField) return 1;
						return 0;
					}
				};
				break;
			case AppWideConstants.comparators.COMPARATOR_TIMEPENDING:
				comparator = new Comparator<AbstractPropertyChanger>() {
					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
						int leftField = -1;
						int rightField = -1;
						if (left instanceof JobQueue) leftField = ((JobQueue) left).getTimeUsed();
						if (right instanceof JobQueue) rightField = ((JobQueue) right).getTimeUsed();

						if (leftField > rightField) return 1;
						if (leftField < rightField) return -1;
						return 0;
					}
				};
				break;
			case AppWideConstants.comparators.COMPARATOR_CARD_RATIO:
				comparator = new Comparator<AbstractPropertyChanger>() {
					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
						double leftField = 0.0;
						double rightField = 0.0;
						if (left instanceof BlueprintPart) leftField = ((BlueprintPart) left).getProfitIndex();
						if (right instanceof BlueprintPart) rightField = ((BlueprintPart) right).getProfitIndex();

						if (leftField > rightField)
							return -1;
						else if (leftField == rightField) return 0;
						return 1;
					}
				};
				break;
			case AppWideConstants.comparators.COMPARATOR_NEWESTDATESORT:
				comparator = new Comparator<AbstractPropertyChanger>() {
					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
						DateTime leftField = new DateTime(DateTimeZone.UTC);
						DateTime rightField = new DateTime(DateTimeZone.UTC);
						if (left instanceof IDateTimeComparator) leftField = ((IDateTimeComparator) left).getComparableDate();
						if (right instanceof IDateTimeComparator) rightField = ((IDateTimeComparator) right).getComparableDate();

						if (leftField.isAfter(rightField))
							return -1;
						else
							return 1;
					}
				};
				break;
			case AppWideConstants.comparators.COMPARATOR_OLDESTDATESORT:
				comparator = new Comparator<AbstractPropertyChanger>() {
					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
						DateTime leftField = new DateTime(DateTimeZone.UTC);
						DateTime rightField = new DateTime(DateTimeZone.UTC);
						if (left instanceof IDateTimeComparator) leftField = ((IDateTimeComparator) left).getComparableDate();
						if (right instanceof IDateTimeComparator) rightField = ((IDateTimeComparator) right).getComparableDate();

						if (leftField.isAfter(rightField))
							return 1;
						else
							return -1;
					}
				};
				break;
		}
		return comparator;
	}

	public static Comparator<IPart> createPartComparator(final int code) {
		Comparator<IPart> comparator = new Comparator<IPart>() {
			public int compare(final IPart left, final IPart right) {
				return 0;
			}
		};
		switch (code) {
			case AppWideConstants.comparators.COMPARATOR_NAME:
				comparator = new Comparator<IPart>() {
					public int compare(final IPart left, final IPart right) {
						String leftField = null;
						String rightField = null;
						if (left instanceof INamedPart) leftField = ((INamedPart) left).getName();
						if (right instanceof INamedPart) rightField = ((INamedPart) right).getName();
						if (left instanceof INamed) leftField = ((INamed) left).getOrderingName();
						if (right instanceof INamed) rightField = ((INamed) right).getOrderingName();

						if (null == leftField) return 1;
						if (null == rightField) return -1;
						if ("" == leftField) return 1;
						if ("" == rightField) return -1;
						return leftField.compareTo(rightField);
					}
				};
				break;
			case AppWideConstants.comparators.COMPARATOR_GROUPNAME:
				comparator = new Comparator<IPart>() {
					public int compare(final IPart left, final IPart right) {
						String leftField = null;
						String rightField = null;
						if (left instanceof AssetPart) leftField = ((AssetPart) left).getCastedModel().getGroupName();
						if (right instanceof AssetPart) rightField = ((AssetPart) right).getCastedModel().getGroupName();

						if (null == leftField) return 1;
						if (null == rightField) return -1;
						if ("" == leftField) return 1;
						if ("" == rightField) return -1;
						return leftField.compareTo(rightField);
					}
				};
				break;
			case AppWideConstants.comparators.COMPARATOR_ITEM_TYPE:
				comparator = new Comparator<IPart>() {
					public int compare(final IPart left, final IPart right) {
						int leftField = -1;
						int rightField = -1;
						// if (left instanceof BlueprintPart) leftField = 100;
						if (left instanceof AssetPart) leftField = 0;
						if (left instanceof ShipPart) leftField = 200;
						if (left instanceof ContainerPart) leftField = -300;

						// if (right instanceof BlueprintPart) rightField = 100;
						if (right instanceof AssetPart) rightField = 0;
						if (right instanceof ShipPart) rightField = 200;
						if (right instanceof ContainerPart) rightField = -300;

						if (leftField < rightField) return -1;
						if (leftField > rightField) return 1;
						return 0;
					}
				};
				break;
			case AppWideConstants.comparators.COMPARATOR_CARD_RATIO:
				comparator = new Comparator<IPart>() {
					public int compare(final IPart left, final IPart right) {
						double leftField = 0.0;
						double rightField = 0.0;
						if (left instanceof BlueprintPart) leftField = ((BlueprintPart) left).getProfitIndex();
						if (right instanceof BlueprintPart) rightField = ((BlueprintPart) right).getProfitIndex();

						if (leftField > rightField)
							return -1;
						else if (leftField == rightField) return 0;
						return 1;
					}
				};
				break;
			case AppWideConstants.comparators.COMPARATOR_NEWESTDATESORT:
				comparator = new Comparator<IPart>() {
					public int compare(final IPart left, final IPart right) {
						DateTime leftField = new DateTime(DateTimeZone.UTC);
						DateTime rightField = new DateTime(DateTimeZone.UTC);
						if (left instanceof IDateTimeComparator) leftField = ((IDateTimeComparator) left).getComparableDate();
						if (right instanceof IDateTimeComparator) rightField = ((IDateTimeComparator) right).getComparableDate();

						if (leftField.isAfter(rightField))
							return -1;
						else
							return 1;
					}
				};
				break;
		}
		return comparator;
	}

	/**
	 * Return the file that points to the application folder on the external (SDCARD) storage.
	 */
	public static File getAppDirectory() {
		return new File(Environment.getExternalStorageDirectory(), AppConnector.getResourceString(R.string.appfoldername));
	}

	/**
	 * Gets a reference to the main app global data store. If the store is not defined it will start the
	 * procedure to retrieve its contents, first from the persistence storage on the sdcard and if this fails
	 * then it will start the procedure to create a new set of data from the available keys.
	 * 
	 * @return
	 */
	@Deprecated
	public static AppModelStore getAppStore() {
		return AppModelStore.getSingleton();
	}

	public static boolean getBooleanPreference(final String preferenceName, final boolean defaultValue) {
		// Read the flag values from the preferences.
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(EVEDroidApp.getAppStore().getActivity());
		boolean pref = sharedPrefs.getBoolean(preferenceName, defaultValue);
		return pref;
	}

	public static EVEDroidApp getSingletonApp() {
		if (null == EVEDroidApp.singleton) new EVEDroidApp();
		return EVEDroidApp.singleton;
	}

	public static ICache getTheCacheConnector() {
		return EVEDroidApp.getSingletonApp().getCacheConnector();
	}

	public static boolean isFirstTimeInit() {
		return EVEDroidApp.firstTimeInitialization;
	}

	@Deprecated
	public static Element parseDOMDocument(final InputStream stream) throws IOException, ParserConfigurationException {
		EVEDroidApp.logger.info(">> EVEDroidApp.downloadDOMDocument");
		Element elementSingleton = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
				.getDocumentElement();
		if (null == stream) return elementSingleton;
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document doc = builder.parse(stream);
			elementSingleton = doc.getDocumentElement();
		} catch (final MalformedURLException me) {
			me.printStackTrace();
		} catch (final SAXException se) {
			se.printStackTrace();
		} catch (final ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		return elementSingleton;
	}

	//[01]
	public static void runTimer() {
		EVEDroidApp.timeTickReceiver.onReceive(EVEDroidApp.getSingletonApp().getApplicationContext(), null);
	}

	@Deprecated
	public static void setFirstInitalization(final boolean state) {
		EVEDroidApp.firstTimeInitialization = state;
	}

	public static void updateProgressSpinner() {
		if ((EVEDroidApp.marketCounter > 0) || (EVEDroidApp.topCounter > 0)) {
			double divider = 10.0;
			if (EVEDroidApp.topCounter > 10) divider = 100.0;
			EVEDroidApp.getSingletonApp()
					.startProgress(new Double(EVEDroidApp.marketCounter + new Double(EVEDroidApp.topCounter / divider)));
		} else
			EVEDroidApp.getSingletonApp().stopProgress();
	}

	// - F I E L D - S E C T I O N ............................................................................
	private AndroidStorageConnector		storage			= null;
	private AndroidDatabaseConnector	dbconnector	= null;
	private AndroidCacheConnector			cache				= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public EVEDroidApp() {
		EVEDroidApp.logger.info(">> [EVEDroidApp.<init>]");
		// Setup the referencing structures that will serve as proxy and global references.
		// If singleton already defined this is not a first time initialization.
		if (null == EVEDroidApp.singleton) {
			Log.i("EVEDroidApp", ".. First Time Initialization.");
			EVEDroidApp.singleton = this;
			AppConnector.setConnector(EVEDroidApp.getSingletonApp());
			EVEDroidApp.firstTimeInitialization = true;
		} else {
			Log.i("EVEDroidApp", ".. User reload requested.");
			EVEDroidApp.firstTimeInitialization = false;
		}
		EVEDroidApp.logger.info("<< [EVEDroidApp.<init>]");
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Puts in the processing queue a new request for the character received in the parameter. This method is
	 * required by the <code>AppConnector</code> implementation.
	 */
	public void addCharacterUpdateRequest(final long characterID) {
		this.getCacheConnector().addCharacterUpdateRequest(characterID);
	}

	/**
	 * Checks that the current parameter timestamp is still on the frame of the window.
	 * 
	 * @param timestamp
	 *          the current and last timestamp of the object.
	 * @param window
	 *          time span window in milliseconds.
	 */
	public boolean checkExpiration(final long timestamp, final long window) {
		// logger.info("-- Checking expiration for " + timestamp + ". Window " + window);
		if (0 == timestamp) return true;
		final long now = GregorianCalendar.getInstance().getTimeInMillis();
		final long endWindow = timestamp + window;
		if (now < endWindow)
			return false;
		else
			return true;
	}

	@Deprecated
	public void closeDB() {
		this.getDBConnector().closeDatabases();
	}

	/**
	 * Return the path to a file resource from the stand point of the application. We have to add the folder
	 * path where the user wishes to store the user application data. This directory is initially hardcoded but
	 * later may be changed on the configuration settings.
	 * 
	 * @param fileresourceid
	 * @return
	 */
	public String getAppFilePath(final int fileresourceid) {
		final String sdcarddir = this.getResourceString(R.string.appfoldername)
				+ this.getResourceString(R.string.app_versionsuffix);
		final String file = this.getResourceString(fileresourceid);
		return sdcarddir + "/" + file;
	}

	public String getAppFilePath(final String fileresourceName) {
		final String sdcarddir = this.getResourceString(R.string.appfoldername)
				+ this.getResourceString(R.string.app_versionsuffix);
		final String file = fileresourceName;
		return sdcarddir + "/" + file;
	}

	public ICache getCacheConnector() {
		if (null == cache) cache = new AndroidCacheConnector(this);
		return cache;
	}

	public IDatabaseConnector getDBConnector() {
		if (null == dbconnector) dbconnector = new AndroidDatabaseConnector(this);
		return dbconnector;
	}

	@Deprecated
	public INeoComModelStore getModelStore() {
		return AppModelStore.getSingleton();
	}

	public String getResourceString(final int reference) {
		EVEDroidApp.logger.fine("R>" + "Accessing resource: " + reference);
		return EVEDroidApp.getSingletonApp().getResources().getString(reference);
	}

	public IConnector getSingleton() {
		return EVEDroidApp.singleton;
	}

	public IStorageConnector getStorageConnector() {
		if (null == storage) storage = new AndroidStorageConnector(this);
		return storage;
	}

	@Deprecated
	public File getUserDataStorage() {
		return new File(Environment.getExternalStorageDirectory(),
				AppConnector.getResourceString(R.string.userdatamodelfilename));
	}

	public void init() {
		EVEDroidApp.logger.info(">> [EVEDroidApp.init]");
		// Close databases if they were left open.
		this.getDBConnector().closeDatabases();
		EVEDroidApp.singleton = this;
		// Remove any reference to old adapters.
		storage = null;
		dbconnector = null;
		cache = null;
		//		appModelStore = null;
		// this.appContext = null;
		// firstTimeInitialization = true;
		EVEDroidApp.logger.info("<< [EVEDroidApp.init]");
	}

	@Override
	public void onCreate() {
		EVEDroidApp.logger.info(">> [EVEDroidApp.onCreate]");
		super.onCreate();
		EVEDroidApp.daysFace = Typeface.createFromAsset(this.getApplicationContext().getAssets(), "fonts/Days.otf");
		// Remove the secure XML access and configure the ApiConnector.
		//		ApiConnector.setSecureXmlProcessing(false);
		EVEDroidApp.logger.info("<< [EVEDroidApp.onCreate]");
	}

	@Override
	public void onTerminate() {
		EVEDroidApp.logger.info(">> [EVEDroidApp.onTerminate]");
		this.unregisterReceiver(EVEDroidApp.timeTickReceiver);
		super.onTerminate();
		EVEDroidApp.logger.info("<< [EVEDroidApp.onTerminate]");
	}

	public void openDatabases() {
		this.getDBConnector().openCCPDataBase();
		this.getDBConnector().openAppDataBase();
	}

	/**
	 * This method checks if the application has access to the external disk (SDCARD) and if that access
	 * included the writing operations.<br>
	 * This method should be called before any expected access to the filesystem by the minor number of classes
	 * because it is a method strictly related to the Android OS. The execution may change the state of some
	 * external variables but taking on account that this state may change dynamically I would prefer to call
	 * repeatedly the method than storing the initial call results.
	 * 
	 * @return if the FS is writable. This also implies that the SDCARD is available.
	 */
	public boolean sdcardAvailable() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		final String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state))
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else
			// Something else is wrong. It may be one of many other states, but all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		return mExternalStorageWriteable;
	}

	/**
	 * Start the background broadcast receiver to intercept the minute tick and process the data structures
	 * checking elements that should be updated because they are obsolete. New updates will be launched as
	 * separate async tasks.
	 */
	public void startTimer() {
		if (null == EVEDroidApp.timeTickReceiver) {
			EVEDroidApp.timeTickReceiver = new TimeTickReceiver(this);
			this.registerReceiver(EVEDroidApp.timeTickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
		}
	}

	/**
	 * Gets the menu context and updates the background progress indicator with the counters.
	 * 
	 * @param countIndicator
	 */
	private void startProgress(final double countIndicator) {
		// Activate menu icon of progress.
		final Menu menu = EVEDroidApp.getAppStore().getAppMenu();
		if (null != menu) {
			final MenuItem updatingItem = menu.findItem(R.id.action_launchUpdate);
			final LayoutInflater mInflater = (LayoutInflater) EVEDroidApp.getAppStore().getActivity()
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			final View actionView = mInflater.inflate(R.layout.actionbar_indeterminateprogress, null);
			if (null != updatingItem) {
				updatingItem.setVisible(true);
				updatingItem.setActionView(actionView);
			}
			final TextView counter = (TextView) actionView.findViewById(R.id.progressCounter);
			if (null != counter) {
				counter.setVisibility(View.VISIBLE);
				// REFACTOR This should be configured on the XML and not on the
				// code. If the progress is set on a library
				// then it should be styled.
				counter.setTypeface(EVEDroidApp.daysFace);
				counter.setText(EVEDroidApp.pendingCounter.format(countIndicator));
			}
			actionView.invalidate();
		}
	}

	/**
	 * Hides the background progress indicator.
	 */
	private void stopProgress() {
		// Clear the update progress.
		final Menu menu = EVEDroidApp.getAppStore().getAppMenu();
		if (null != menu) {
			final MenuItem updatingItem = menu.findItem(R.id.action_launchUpdate);
			final LayoutInflater mInflater = (LayoutInflater) EVEDroidApp.getAppStore().getActivity()
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			final View actionView = mInflater.inflate(R.layout.actionbar_indeterminateprogress, null);
			if (null != updatingItem) {
				updatingItem.setActionView(null);
				updatingItem.setVisible(false);
			}
			final TextView counter = (TextView) actionView.findViewById(R.id.progressCounter);
			if (null != counter) counter.setVisibility(View.GONE);
			actionView.invalidate();
		}
	}
}
// - UNUSED CODE ............................................................................................
//[01]

// public static void setAppTheme(final int themeCode) {
// switch (themeCode) {
// case 1:
// appTheme = new RubiconRedTheme();
// break;
// case 2:
// appTheme = new CryosBlueTheme();
// break;
// default:
// appTheme = new RubiconRedTheme();
// }
//
// }
