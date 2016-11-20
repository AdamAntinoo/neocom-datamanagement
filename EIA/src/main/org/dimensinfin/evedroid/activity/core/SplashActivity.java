//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.activity.core;

// - IMPORT SECTION .........................................................................................
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Logger;

import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.PilotListActivity;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.model.APIKey;
import org.dimensinfin.evedroid.model.EveChar;
import org.dimensinfin.evedroid.model.EveCharCore;
import org.dimensinfin.evedroid.model.NeoComApiKey;
import org.dimensinfin.evedroid.storage.AppModelStore;

import com.beimin.eveapi.parser.ApiAuthorization;
import com.beimin.eveapi.parser.account.ApiKeyInfoParser;
import com.beimin.eveapi.parser.account.CharactersParser;
import com.beimin.eveapi.response.account.ApiKeyInfoResponse;
import com.beimin.eveapi.response.account.CharactersResponse;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class SplashActivity extends Activity {
	/**
	 * Class to perform the update of the user information as a background task. If the data is not present or
	 * if the information is obsolete we launch a task to update and download the pilot information.
	 * 
	 * @author Adam Antinoo
	 */
	//- CLASS IMPLEMENTATION ...................................................................................
	private class EveDroidInitialization extends AsyncTask<Void, Void, Boolean> {
		private static final boolean	showProgress	= false;
		// - F I E L D - S E C T I O N ............................................................................
		//		private EVEDroidApp						app						= null;
		private Activity							_activity			= null;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public EveDroidInitialization(final Activity act) {
			_activity = act;
		}

		@Override
		protected Boolean doInBackground(final Void... entry) {
			// Just check that the files exist and if not load the Api list and their characters.
			updateStateLabel("Checking application database...");
			// STEP 03. Check required files availability on app directory.
			createAppDir();
			createCacheDirectories();
			logger.info(">> EveDroidInitialization.STEP 03. App Directories created");
			// STEP 04. Check existence of required files.
			if (!checkAppFile(AppConnector.getResourceString(R.string.ccpdatabasefilename))) {
				// Initial item database is not on place. Copy one from the assets.
				copyFromAssets(R.string.ccpdatabasefilename);
			}
			if (AppWideConstants.DEVELOPMENT) if (!checkAppFile(AppConnector.getResourceString(R.string.apikeysfilename))) {
				// Initial item database is not on place. Copy one from the assets.
				copyFromAssets(R.string.apikeysfilename);
			}
			logger.info(">> EveDroidInitialization.STEP 04. Required files on place");

			updateStateLabel("Loading user data...");
			// STEP 08. Check API file.
			if (!checkAppFile(AppConnector.getResourceString(R.string.apikeysfilename))) {
				logger.info("-- EveDroidInitialization.STEP 06. No API List. Needs API introduction");
				startActivity(new Intent(_activity, AddNewAPIActivity.class));
				return false;
			}
			AppModelStore store = EVEDroidApp.getAppStore();
			//				// Try to read the data. If this fails we have to force an update or leave the user to do that.
			//				status = EVEDroidApp.getSingletonApp().getAppStore().restore();
			//				logger.info(">> EveDroidInitialization.STEP 06. Reload of user data [" + status + "]");
			//			}
			logger.info(">> EveDroidInitialization.STEP 08. API list available");
			updateStateLabel("Downloading data from CCP...");
			// STEP 09. Refresh data from CCP.	
			readApiKeys();
			logger.info(">> EveDroidInitialization.STEP 09. API list refreshed");
			EVEDroidApp.getSingletonApp().startTimer();
			return true;
		}

		protected Boolean doInBackgroundOld(final Void... entry) {
			logger.info(">> EveDroidInitialization.doInBackground");
			// Be sure the application is created and connected to the App adapter.
			//			app = EVEDroidApp.getSingletonApp();

			// STEP 01. Check if this is a first time initialization of the startup was already executed before.
			if (!EVEDroidApp.isFirstTimeInit()) {
				// The app was initialized before. We can skip that until we found code that need to be executed every time.
				// But anyway check the state of the SDCARD later.
				logger.info(">> EveDroidInitialization.STEP 01. FirstTimeInit FALSE");
				// If menu forced update active.
				if (true) {
					logger.info(">> EveDroidInitialization.STEP 01. Fullreload requested. Updating API Keys");
					updateStateLabel("Downloading data from CCP...");
					//					EVEDroidApp.setFullReload(false);
					readApiKeys();
				}
				//					return true;
				//				} else
				//					return true;
			}
			logger.info(">> EveDroidInitialization.STEP 01. FirstTimeInit TRUE");

			// STEP 02. Check SDCARD access. If no access to SDCARD then app cannot go.
			if (!AppConnector.sdcardAvailable()) {
				// Stop on a safe point until the storage is available again.
				//				app.startTimer();
				startActivity(new Intent(_activity, StopNOSDActivity.class));
				//				return false;
			}
			logger.info(">> EveDroidInitialization.STEP 02. SDCARD available");
			// STEP 03. Check required files availability on app directory.
			//		updateStateLabel("Creating configuration files...");
			createAppDir();
			createCacheDirectories();
			logger.info(">> EveDroidInitialization.STEP 03. App Directories created");

			// STEP 04. Check existence of required files.
			if (!checkAppFile(AppConnector.getResourceString(R.string.ccpdatabasefilename))) {
				// Initial item database is not on place. Copy one from the assets.
				copyFromAssets(R.string.ccpdatabasefilename);
			}
			if (!checkAppFile(AppConnector.getResourceString(R.string.appdatabasefilename))) {
				// Initial item database is not on place. Copy one from the assets.
				copyFromAssets(R.string.appdatabasefilename);
			}
			if (!checkAppFile(AppConnector.getResourceString(R.string.apikeysfilename))) {
				// Initial item database is not on place. Copy one from the assets.
				copyFromAssets(R.string.apikeysfilename);
			}
			logger.info(">> EveDroidInitialization.STEP 04. Required files on place");

			//			// STEP 05. Check user database version. To be implemented
			//			AppConnector.getDBConnector().openCCPDataBase();
			//			AppConnector.getDBConnector().openAppDataBase();
			//			AppConnector.getDBConnector().openDAO();
			//			logger.info(">> EveDroidInitialization.STEP 05. Databases open");
			updateStateLabel("Loading user data...");
			doInitStepB();

			//			// TODO After proper initialization clean this code. Clear caches and user data copies.
			//			JobManager.clearCache();
			//			ArrayList<EveChar> chars = EVEDroidApp.getAppModel().getActiveCharacters();
			//			for (EveChar eveChar : chars)
			//				eveChar.clean();
			return true;
		}

		protected boolean doInitStepB() {
			// STEP 06. Check user data information and storage file.
			boolean status = false;
			if (EVEDroidApp.getSingletonApp().getAppStore().needsRestore()) {
				// Try to read the data. If this fails we have to force an update or leave the user to do that.
				status = EVEDroidApp.getSingletonApp().getAppStore().restore();
				logger.info(">> EveDroidInitialization.STEP 06. Reload of user data [" + status + "]");
			}

			// STEP 07. Check is there are characters available
			logger.info(">> EveDroidInitialization.STEP 07. Checking API keys availability");
			if (status) {
				ArrayList<EveChar> pilots = EVEDroidApp.getSingletonApp().getAppStore().getActiveCharacters();
				if (pilots.size() < 1) {
					// There are no characters. Check for api list.
					// STEP 08. Check API file.
					if (!checkAppFile(AppConnector.getResourceString(R.string.apikeysfilename))) {
						logger.info("-- EveDroidInitialization.STEP 06. No API List. Needs API introduction");
						startActivity(new Intent(_activity, AddNewAPIActivity.class));
						return false;
					}
					logger.info(">> EveDroidInitialization.STEP 08. API list available");
					updateStateLabel("Downloading data from CCP...");
					// STEP 09. Refresh data from CCP.	
					readApiKeys();
					logger.info(">> EveDroidInitialization.STEP 09. API list refreshed");
				}
			} else {
				// User data file is not compatible with current version. Go to info page.
				startActivity(new Intent(_activity, NewVersionWarningActivity.class));
				return false;
			}

			//			// STEP 08. Process Fittings information stored on the internal app data.
			//			logger.info(">> EveDroidInitialization.STEP 08. Processing Fittings information");
			//			// STEP 09. Load user selected Theme.
			//			updateStateLabel("Instantiating user selected theme...");
			//			logger.info(">> EveDroidInitialization.STEP 09. Load User selected theme");
			//			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(_activity);
			//			String themeCode = sharedPrefs.getString(AppWideConstants.preference.PREF_APPTHEMES, "-1");
			//			//		int themeCode1 = sharedPrefs.getInt(AppWideConstants.preference.PREF_APPTHEMES, -1);
			//			EVEDroidApp.setAppTheme(-1);

			//			app.startTimer();
			EVEDroidApp.setFirstInitalization(false);
			logger.info("<< EveDroidInitialization.doInBackground");
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean result) {
			// Check if we have pilots to show or we have to go to the AddAPI page.
			if (result) {
				startActivity(new Intent(_activity, PilotListActivity.class));
			}
		}

		private boolean checkAppFile(final String resourceString) {
			return AppConnector.getStorageConnector()
					.checkStorageResource(AppConnector.getStorageConnector().accessAppStorage(null), resourceString);
		}

		/**
		 * Copies the parameter files from the assets installation directory to the user application directory.
		 * Current contents are the static database and the initial API list if found.
		 * 
		 * @param resourceId
		 */
		private void copyFromAssets(final int resourceId) {
			InputStream istream = null;
			OutputStream ostream = null;
			String resourceName = AppConnector.getResourceString(resourceId);
			//		final Context context = singleton.getApplicationContext();
			try {
				istream = _activity.getAssets().open(resourceName);
				final File destination = AppConnector.getStorageConnector().accessAppStorage(resourceName);
				destination.createNewFile();
				ostream = new FileOutputStream(destination);
				final byte[] buffer = new byte[8192];
				int length;
				while ((length = istream.read(buffer)) > 0) {
					ostream.write(buffer, 0, length);
				}
				ostream.flush();
				logger.info("-- Copied resource from assets [" + resourceName + "]");
			} catch (final Exception e) {
				e.printStackTrace();
				logger.severe("E> Failed to copy resource: " + resourceName);
			} finally {
				try {
					if (ostream != null) {
						ostream.close();
					}
					if (istream != null) {
						istream.close();
					}
				} catch (final IOException e) {
				}
			}
		}

		/**
		 * Creates the application directory if it already does not exist on the sdcard.
		 */
		private boolean createAppDir() {
			// Create file application directory if it doesn't exist
			final File sdcarddir = new File(Environment.getExternalStorageDirectory(),
					AppConnector.getResourceString(R.string.appfoldername)
							+ AppConnector.getResourceString(R.string.app_versionsuffix));
			final boolean existence = sdcarddir.exists();
			if (!sdcarddir.exists()) {
				sdcarddir.mkdir();
			}
			return !existence;
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		private void createCacheDirectories() {
			// Create additional cache folders.
			File cachedir = AppConnector.getStorageConnector().getCacheStorage();
			cachedir.mkdir();
			File alternatedir = new File(cachedir, AppConnector.getResourceString(R.string.drawablecachefoldername));
			alternatedir.mkdir();
			alternatedir = new File(cachedir, AppConnector.getResourceString(R.string.marketdatacachefoldername));
			alternatedir.mkdir();
		}

		private void instantiateKey() {
			// Get the complete api information for CCP through the eveapi.
			ApiAuthorization authorization = new ApiAuthorization(apiKey.getKeyID(), apiKey.getVerificationCode());
			ApiKeyInfoParser parser = new ApiKeyInfoParser();
			ApiKeyInfoResponse response = parser.getResponse(authorization);
			if (null != response) {
				apiKey.updateAllParameters(response);
				// Get access to all the pilots and update the information on the ApiKey.
				final CharactersParser pilotParser = new CharactersParser();
				final CharactersResponse pilotResponse = pilotParser.getResponse(authorization);
				if (null != pilotResponse) {
					//			 HashSet<Character> characterList = new HashSet<Character>();
					Set<Character> characterList = pilotResponse.getAll();
					for (final Character evechar : characterList) {
						EveCharCore newChar = processCharacter(evechar);
						//Add the credentials
						//	newChar.setApiKeyID(2889577);
						newChar.setKeyID(apiKey.getKeyID());
						newChar.setVerificationCode(apiKey.getVerificationCode());
						// Add more information to the character using mode CCP calls.
						// CharacterInfo
						//				updateCharacterInfo(newChar);
						apiKey.addCharacter(newChar);
					}
				}
				_modelRoot.add(apiKey);
			}

		}

		private void readApiKeys() {
			logger.info(">> EveDroidInitialization.readApiKeys");
			try {
				// Read the contents of the character information.
				final File characterFile = AppConnector.getStorageConnector()
						.accessAppStorage(AppConnector.getResourceString(R.string.apikeysfilename));
				InputStream is = new BufferedInputStream(new FileInputStream(characterFile));
				BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				String line = br.readLine();
				while (null != line) {
					try {
						String[] parts = line.split(":");
						String key = parts[0];
						String validationcode = parts[1];
						int keynumber = Integer.parseInt(key);
						logger.info("-- Inserting API key " + keynumber);
						//						APIKey api = new APIKey(keynumber, validationcode);
						APIKey api = NeoComApiKey.build(keynumber, validationcode);
						EVEDroidApp.getAppStore().addApiKey(api);
					} catch (NumberFormatException nfex) {
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					line = br.readLine();
				}
				if (null != br) {
					br.close();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private void updateStateLabel(final String newLabel) {
			runOnUiThread(new Runnable() {
				public void run() {
					stateLabel.setText(newLabel);
					stateLabel.invalidate();
				}
			});
		}
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger			logger			= Logger.getLogger("SplashActivity");
	protected static Typeface	daysFace		= Typeface
			.createFromAsset(EVEDroidApp.getSingletonApp().getApplicationContext().getAssets(), "fonts/Days.otf");
	// - F I E L D - S E C T I O N ............................................................................
	private final boolean			_lock				= false;
	private TextView					stateLabel	= null;

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * This is almost the first android code to be executed. We will only check that core initialization was
	 * performed. We will prepare the splash screen and do any other thing that has to be done only a single
	 * time during the life of the application. Activities may get destroyed on memory so we have to save any
	 * permanent state.<br>
	 * Currently there are no permanent state that is not stored on the Static and User models.<br>
	 * The new code also adds the Action bar and start to play with it as a resource for better navigation.
	 * 
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		logger.info(">> SplashActivity.onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		stateLabel = (TextView) findViewById(R.id.stateLabel);
		stateLabel.setTypeface(daysFace);
		logger.info("<< SplashActivity.onCreate");
	}

	/**
	 * This code is performed anytime this activity is created so that means sometimes. The code has to be
	 * strong to detect things already done and do not try to repeat. Part of the action is coded inside
	 * background activities implemented as an AsyncTask or set of tasks that will serialize any call to those
	 * background activities.<br>
	 * The content is to open the display, start the waiting animation while the background tasks perform the
	 * download of the data and then finish. There is a wait link connected to the EVENT detection for the
	 * property changer so when the background task ends it will send an event that would allow interception or
	 * even the launch from the last task.<br>
	 * The splash activity will have a set of image buttons to enter the different functions of the application,
	 * mainly the addition of new keys that is a must to be on working state and the selection of a pilot to
	 * work with.
	 */
	@Override
	protected void onStart() {
		logger.info(">> SplashActivity.onStart");
		super.onStart();
		//	stateLabel.setText("Instantiating user selected theme...");
		stateLabel.setText("Checking application database...");
		stateLabel.invalidate();
		new EveDroidInitialization(this).execute();
		//		performLogoAnimations();
		logger.info("<< SplashActivity.onStart");
	}

	private void performLogoAnimations() {
		logger.info(">> Entering SplashActivity.performLogoAnimations");
		// Launch the animation while the application initialization code is being executed.
		final Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
		animation.setDuration(5 * 1000);
		logger.info("-- Starting logo EVE animation");
		final ImageView logoEve = (ImageView) findViewById(R.id.logoDroid);
		animation.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(final Animation animation) {
				logoEve.setVisibility(View.INVISIBLE);
			}

			public void onAnimationRepeat(final Animation arg0) {
			}

			public void onAnimationStart(final Animation arg0) {
			}
		});
		logoEve.startAnimation(animation);
		logger.info("<< Exiting SplashActivity.performLogoAnimations");
	}
}
//- UNUSED CODE ............................................................................................
