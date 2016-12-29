//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.activity;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.activity.PagerFragment;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.core.SettingsActivity;
import org.dimensinfin.evedroid.activity.core.SplashActivity;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.factory.DataSourceFactory;
import org.dimensinfin.evedroid.interfaces.IDirector;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class DirectorsBoardActivity extends Activity {
	// - S T A T I C - S E C T I O N ..........................................................................
	enum EDirectorCode {
		ASSETDIRECTOR, SHIPDIRECTOR, INDUSTRYDIRECTOR, MARKETDIRECTOR, JOBDIRECTOR, MININGDIRECTOR, FITDIRECTOR
	}

	private static Logger									logger							= Logger.getLogger("DirectorsBoardActivity");
	private static final EDirectorCode[]	activeDirectors			= { EDirectorCode.ASSETDIRECTOR, EDirectorCode.SHIPDIRECTOR,
			EDirectorCode.INDUSTRYDIRECTOR, EDirectorCode.FITDIRECTOR };
	//	private static final EDirectorCode[]	activeDirectors			= { EDirectorCode.FITDIRECTOR };
	protected static Typeface							daysFace						= Typeface
			.createFromAsset(EVEDroidApp.getSingletonApp().getApplicationContext().getAssets(), "fonts/Days.otf");

	// - F I E L D - S E C T I O N ............................................................................
	private ActionBar											_actionBar					= null;
	private ImageView											_back								= null;
	private ViewGroup											_directorContainer	= null;
	private ViewGroup											_fragmentContainer	= null;
	private AppModelStore									_store							= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public DirectorsBoardActivity() {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.eiabasemenu, menu);
		EVEDroidApp.getAppStore().setAppMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.action_settings:
				this.startActivity(new Intent(this, SettingsActivity.class));
				return true;
			case R.id.action_fullreload:
				// EVEDroidApp.setFullReload(true);
				this.startActivity(new Intent(this, SplashActivity.class));
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		DirectorsBoardActivity.logger.info(">> DirectorsBoardActivity.onCreate");
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_directorsboard);
		try {
			// Process the parameters into the context.
			final Bundle extras = this.getIntent().getExtras();
			if (null == extras) throw new RuntimeException(
					"RT DirectorsBoardActivity.onCreate - Unable to continue. Required parameters not defined on Extras.");
			// Instantiate the pilot from the characterID.
			final long characterid = extras.getLong(AppWideConstants.EExtras.EXTRA_CAPSULEERID.name());
			Log.i("DirectorsBoardActivity", "-- DirectorsBoardActivity.onCreate -- Detected "
					+ AppWideConstants.EExtras.EXTRA_CAPSULEERID.name() + "=" + characterid);
			if (characterid > 0) {
				_store = AppModelStore.getSingleton();
				_store.activatePilot(characterid);
				_store.activateActivity(this);
			} else
				throw new RuntimeException(
						"RT DirectorsBoardActivity.onCreate - Unable to continue. Required parameters not defined on Extras.");

			// Gets the activity's default ActionBar
			_actionBar = this.getActionBar();
			_actionBar.show();
			_actionBar.setDisplayHomeAsUpEnabled(true);

			// Change the title and the background of the activity.
			_actionBar.setTitle(_store.getPilot().getName());

			// Locate the elements of the page and store in global data.
			_directorContainer = (ViewGroup) this.findViewById(R.id.neocomContainer);
			_fragmentContainer = (ViewGroup) this.findViewById(R.id.fragmentContainer);
			_back = (ImageView) this.findViewById(R.id.backgroundFrame);
			// Check page structure.
			if (null == _directorContainer) {
				this.stopActivity(new RuntimeException("UNXER. Expected UI element not found."));
			}
			if (null == _fragmentContainer) {
				this.stopActivity(new RuntimeException("UNXER. Expected UI element not found."));
			}
			if (null == _back) {
				this.stopActivity(new RuntimeException("UNXER. Expected UI element not found."));
			}
		} catch (final Exception rtex) {
			DirectorsBoardActivity.logger
					.severe("R> Runtime Exception on DirectorsBoardActivity.onCreate." + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(rtex);
		}

		// Compose the page adding the fragments.
		try {
			// Add the fragments to the fragment container if they are not
			// already there.
			this.addFragment("PilotInformation", AppWideConstants.fragment.FRAGMENT_PILOTINFO_INFO);

			// final FragmentManager manager = getFragmentManager();
		} catch (final Exception rtex) {
			DirectorsBoardActivity.logger
					.severe("R> Runtime Exception on DirectorsBoardActivity.onCreate." + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(
					new RuntimeException("R> Runtime Exception on DirectorsBoardActivity.onCreate." + rtex.getMessage()));
		}
		DirectorsBoardActivity.logger.info("<< DirectorsBoardActivity.onCreate");
	}

	//	/**
	//	 * Save the store to their persistent file before releasing the control to another activity that will then
	//	 * be able to make use of that data structures.
	//	 */
	//	@Override
	//	protected void onPause() {
	//		Log.i("DirectorsBoardActivity", ">> DirectorsBoardActivity.onPause");
	//		// Check store state and update cache on disk if it has changed.
	//		if (_store.isDirty()) {
	//			_store.save();
	//		}
	//		super.onPause();
	//		Log.i("DirectorsBoardActivity", "<< DirectorsBoardActivity.onPause");
	//	}

	/**
	 * When the Activity is activated and accessible to the user this event is signaled to report that we are
	 * back. On the list of managers we have to check manager by manager if they should activate and if they are
	 * or not visible.<br>
	 * This is done with a manager factory that will generate the view items for the managers if they should be
	 * present.
	 */
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		DirectorsBoardActivity.logger.info(">> DirectorsBoardActivity.onResume");
		super.onResume();
		try {
			// // Clean user data structures before querying.
			// getPilot().clean();
			// Before activating the directors make sure the user has declared
			// the MANUFACTURE location.
			// final EveLocation location =
			// _store.getPilot().getFunctionLocation("MANUFACTURE");
			// EDirectorCode[] directors;
			// if (null != location)
			final EDirectorCode[] directors = DirectorsBoardActivity.activeDirectors;
			// else
			// directors = passiveDirectors;
			final DirectorsBoardActivity parentActivity = this;
			for (final EDirectorCode directorCode : directors) {
				ImageView activator = null;
				switch (directorCode) {
					case ASSETDIRECTOR:
						final IDirector adirector = new AssetsDirectorActivity();
						if (adirector.checkActivation(_store.getPilot())) {
							DirectorsBoardActivity.logger.info("-- DirectorsBoardActivity.onResume - activated " + directorCode);
							activator = (ImageView) this.findViewById(R.id.assetsDirectorIcon);
							activator.setImageDrawable(this.getDrawable(R.drawable.assetsdirector));
							activator.setClickable(true);
							activator.setOnClickListener(new View.OnClickListener() {
								public void onClick(final View view) {
									Log.i("DirectorsBoardActivity", ">> DirectorsBoardActivity.ASSETDIRECTOR.onClick");
									// Activate the manager.
									final Intent intent = new Intent(parentActivity, adirector.getClass());
									// Send the pilot id and transfer it to the next
									// Activity
									intent.putExtra(AppWideConstants.EExtras.EXTRA_CAPSULEERID.name(),
											_store.getPilot().getCharacterID());
									DirectorsBoardActivity.this.startActivity(intent);
									Log.i("DirectorsBoardActivity", "<< DirectorsBoardActivity.ASSETDIRECTOR.onClick");
								}
							});
							final TextView label = (TextView) this.findViewById(R.id.assetsDirectorLabel);
							label.setTypeface(DirectorsBoardActivity.daysFace);
							activator.invalidate();
						}
					case SHIPDIRECTOR:
						final IDirector sdirector = new ShipDirectorActivity();
						if (sdirector.checkActivation(_store.getPilot())) {
							DirectorsBoardActivity.logger.info("-- DirectorsBoardActivity.onResume - activated " + directorCode);
							activator = (ImageView) this.findViewById(R.id.shipsDirectorIcon);
							activator.setImageDrawable(this.getDrawable(R.drawable.shipsdirector));
							activator.setClickable(true);
							activator.setOnClickListener(new View.OnClickListener() {
								public void onClick(final View view) {
									Log.i("DirectorsBoardActivity", ">> DirectorsBoardActivity.SHIPDIRECTOR.onClick");
									// Activate the manager.
									final Intent intent = new Intent(parentActivity, sdirector.getClass());
									// Send the pilot id and transfer it to the next
									// Activity
									intent.putExtra(AppWideConstants.EExtras.EXTRA_CAPSULEERID.name(),
											_store.getPilot().getCharacterID());
									DirectorsBoardActivity.this.startActivity(intent);
									Log.i("DirectorsBoardActivity", "<< DirectorsBoardActivity.ASSETDIRECTOR.onClick");
								}
							});
							final TextView label = (TextView) this.findViewById(R.id.shipsDirectorLabel);
							label.setTypeface(DirectorsBoardActivity.daysFace);
							activator.invalidate();
						}
					case INDUSTRYDIRECTOR:
						final IDirector thedirector = new IndustryDirectorActivity();
						if (thedirector.checkActivation(_store.getPilot())) {
							DirectorsBoardActivity.logger.info("-- DirectorsBoardActivity.onResume - activated " + directorCode);
							activator = (ImageView) this.findViewById(R.id.industryDirectorIcon);
							activator.setImageDrawable(this.getDrawable(R.drawable.industrydirector));
							activator.setClickable(true);
							activator.setOnClickListener(new View.OnClickListener() {
								public void onClick(final View view) {
									Log.i("DirectorsBoardActivity", ">> DirectorsBoardActivity.INDUSTRYDIRECTOR.onClick");
									// Activate the manager.
									final Intent intent = new Intent(parentActivity, thedirector.getClass());
									// Send the pilot id and transfer it to the next
									// Activity
									intent.putExtra(AppWideConstants.EExtras.EXTRA_CAPSULEERID.name(),
											_store.getPilot().getCharacterID());
									DirectorsBoardActivity.this.startActivity(intent);
									Log.i("DirectorsBoardActivity", "<< DirectorsBoardActivity.INDUSTRYDIRECTOR.onClick");
								}
							});
							final TextView label = (TextView) this.findViewById(R.id.industryDirectorLabel);
							label.setTypeface(DirectorsBoardActivity.daysFace);
							activator.invalidate();
						}
						break;
					case JOBDIRECTOR:
						final IDirector jdirector = new FittingActivity();
						if (jdirector.checkActivation(_store.getPilot())) {
							DirectorsBoardActivity.logger.info("-- DirectorsBoardActivity.onResume - activated " + directorCode);
							activator = (ImageView) this.findViewById(R.id.jobDirectorIcon);
							activator.setImageDrawable(this.getDrawable(R.drawable.jobdirector));
							activator.setClickable(true);
							activator.setOnClickListener(new View.OnClickListener() {
								public void onClick(final View view) {
									// Activate the manager.
									final Intent intent = new Intent(parentActivity, jdirector.getClass());
									// Send the pilot id and transfer it to the next
									// Activity
									intent.putExtra(AppWideConstants.EExtras.EXTRA_CAPSULEERID.name(),
											_store.getPilot().getCharacterID());
									DirectorsBoardActivity.this.startActivity(intent);
								}
							});
							activator.invalidate();
						}
						break;
					case MARKETDIRECTOR:
						final IDirector director = new MarketDirectorActivity();
						if (director.checkActivation(_store.getPilot())) {
							DirectorsBoardActivity.logger.info("-- DirectorsBoardActivity.onResume - activated " + directorCode);
							activator = (ImageView) this.findViewById(R.id.marketDirectorIcon);
							activator.setImageDrawable(this.getDrawable(R.drawable.marketdirector));
							activator.setClickable(true);
							activator.setOnClickListener(new View.OnClickListener() {
								public void onClick(final View view) {
									// Activate the manager.
									final Intent intent = new Intent(parentActivity, director.getClass());
									// Send the pilot id and transfer it to the next
									// Activity
									intent.putExtra(AppWideConstants.EExtras.EXTRA_CAPSULEERID.name(),
											_store.getPilot().getCharacterID());
									DirectorsBoardActivity.this.startActivity(intent);
								}
							});
							activator.invalidate();
						}
						break;
					case FITDIRECTOR:
						final IDirector fdirector = new FittingListActivity();
						if (fdirector.checkActivation(_store.getPilot())) {
							DirectorsBoardActivity.logger.info("-- DirectorsBoardActivity.onResume - activated " + directorCode);
							activator = (ImageView) this.findViewById(R.id.fitDirectorIcon);
							activator.setImageDrawable(this.getDrawable(R.drawable.fitsdirector));
							activator.setClickable(true);
							activator.setOnClickListener(new View.OnClickListener() {
								public void onClick(final View view) {
									// Activate the manager.
									final Intent intent = new Intent(parentActivity, fdirector.getClass());
									// Send the pilot id and transfer it to the next Activity
									intent.putExtra(AppWideConstants.EExtras.EXTRA_CAPSULEERID.name(),
											_store.getPilot().getCharacterID());
									DirectorsBoardActivity.this.startActivity(intent);
								}
							});
							activator.invalidate();
						}
						break;
					//[02]
				}
			}
		} catch (final Exception rtex) {
			rtex.printStackTrace();
			this.stopActivity(
					new RuntimeException("R> Runtime Exception on DirectorsBoardActivity.onResume." + rtex.getMessage()));
		}
		DirectorsBoardActivity.logger.info("<< DirectorsBoardActivity.onResume");
	}

	@Override
	protected void onSaveInstanceState(final Bundle savedInstanceState) {
		Log.i("DirectorsBoardActivity", ">> DirectorsBoardActivity.onSaveInstanceState"); //$NON-NLS-1$
		super.onSaveInstanceState(savedInstanceState);
		// Add current model data dependencies. EVECHARACTERID
		savedInstanceState.putLong(AppWideConstants.EExtras.EXTRA_CAPSULEERID.name(), _store.getPilot().getCharacterID());
		// _store.save();
		Log.i("DirectorsBoardActivity", "<< DirectorsBoardActivity.onSaveInstanceState"); //$NON-NLS-1$
	}

	/**
	 * Clear the state of all the Directors to a dimmed and inactive state. It will be recalculated while on the
	 * onResume phase.
	 */
	@Override
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@SuppressWarnings("deprecation")
	protected void onStart() {
		DirectorsBoardActivity.logger.info(">> DirectorsBoardActivity.onStart");
		super.onStart();
		ImageView directorButton = (ImageView) this.findViewById(R.id.assetsDirectorIcon);
		directorButton.setImageDrawable(this.getDrawable(R.drawable.assetsdimmed));
		directorButton.setClickable(false);

		directorButton = (ImageView) this.findViewById(R.id.industryDirectorIcon);
		directorButton.setImageDrawable(this.getDrawable(R.drawable.industrydirectordimmed));
		directorButton.setClickable(false);

		directorButton = (ImageView) this.findViewById(R.id.marketDirectorIcon);
		directorButton.setImageDrawable(this.getDrawable(R.drawable.marketdirectordimmed));
		directorButton.setClickable(false);
		// directorButton = (ImageView) findViewById(R.id.nmarketDirectorIcon);
		// directorButton.setImageDrawable(getDrawable(R.drawable.marketdirectordimmed));
		// directorButton.setClickable(false);

		directorButton = (ImageView) this.findViewById(R.id.jobDirectorIcon);
		directorButton.setImageDrawable(this.getDrawable(R.drawable.jobdirectordimmed));
		directorButton.setClickable(false);

		// directorButton = (ImageView) findViewById(R.id.taskDirectorIcon);
		// directorButton.setImageDrawable(getDrawable(R.drawable.taskdirectordimmed));
		// directorButton.setClickable(false);

		directorButton = (ImageView) this.findViewById(R.id.fitDirectorIcon);
		directorButton.setImageDrawable(this.getDrawable(R.drawable.fitsdirector));
		directorButton.setClickable(true);
		DirectorsBoardActivity.logger.info("<< DirectorsBoardActivity.onStart");
	}

	/**
	 * For really unrecoverable or undefined exceptions the application should go to a safe spot. That spot is
	 * defined by the application so this is another abstract method.
	 * 
	 * @param exception
	 */
	protected void stopActivity(final Exception exception) {
		final Intent intent = new Intent(this, PilotListActivity.class);
		// Pass the user message to the activity for display.
		intent.putExtra(AppWideConstants.extras.EXTRA_EXCEPTIONMESSAGE, exception.getMessage());
		this.startActivity(intent);
	}

	//	private void addExpandableFragment(final String label, final int fragmentIdentifier) {
	//		final FragmentManager manager = getFragmentManager();
	//		if (null != manager) if (null == manager.findFragmentByTag(label)) {
	//			// Create a new Fragment to be placed in the activity layout
	//			final ExpandableFragment thefrag = new ExpandableFragment();
	//			thefrag.setLabel(label);
	//			thefrag.setIdentifier(fragmentIdentifier);
	//			thefrag.setDataSource(DataSourceFactory.createDataSource(fragmentIdentifier));
	//			final Bundle args = new Bundle();
	//			args.putString("TAG", Integer.valueOf(fragmentIdentifier).toString());
	//			thefrag.setArguments(args);
	//
	//			// Add the fragment to the 'fragmentContainer' Layout
	//			getFragmentManager().beginTransaction().add(R.id.fragmentContainer, thefrag).commit();
	//		}
	//	}

	/**
	 * Adds a new fragment to the fragment manager in case that fragment is not already registered. Fragment
	 * registration uses the fragment identifier converted to an string as the unique locator. If the fragment
	 * is not registered the methods creates a new instance and initializes its structures.
	 * 
	 * @param label
	 *          string that is used for fragment tagging and registration
	 * @param fragmentIdentifier
	 *          fragment unique id to identify the corresponding data source.
	 */
	private void addFragment(final String label, final int fragmentIdentifier) {
		final FragmentManager manager = this.getFragmentManager();
		if (null != manager) if (null == manager.findFragmentByTag(label)) {
			// Create a new Fragment to be placed in the activity layout
			final PagerFragment thefrag = new PagerFragment();
			thefrag.setIdentifier(fragmentIdentifier);
			thefrag.setDataSource(DataSourceFactory.createDataSource(fragmentIdentifier));
			final Bundle args = new Bundle();
			args.putString("TAG", Integer.valueOf(fragmentIdentifier).toString());
			thefrag.setArguments(args);

			// Add the fragment to the 'fragmentContainer' Layout
			this.getFragmentManager().beginTransaction().add(R.id.fragmentContainer, thefrag).commit();
		}
	}

	private Activity getActivity() {
		return this;
	}

	@SuppressLint("Override")
	private Drawable getDrawable(final int reference) {
		// ContextCompat.getDrawable(getActivity(), reference);
		return this.getActivity().getResources().getDrawable(reference);
	}

	// private EveChar getPilot() {
	// return _store.getPilot();
	// }
}

// - UNUSED CODE ............................................................................................
//[02]
// case MININGDIRECTOR:
// final IDirector mdirector = new MiningSessionActivity();
// if (mdirector.checkActivation(getPilot())) {
// logger.info("-- DirectorsBoardActivity.onResume - activated "
// + directorCode);
// activator = (ImageView)
// findViewById(R.id.miningDirectorIcon);
// activator.setImageDrawable(getDrawable(R.drawable.miningdirector));
// activator.setClickable(true);
// activator.setOnClickListener(new View.OnClickListener() {
// public void onClick(final View view) {
// // Activate the manager.
// final Intent intent = new Intent(parentActivity,
// mdirector.getClass());
// // Send the pilot id and transfer it to the next Activity
// intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
// parentActivity.getPilot()
// .getCharacterID());
// startActivity(intent);
// }
// });
// activator.invalidate();
// }
// break;
// case TASKDIRECTOR:
// final IDirector tdirector = new TasksDirectorActivity();
// if (tdirector.checkActivation(getPilot())) {
// logger.info("-- DirectorsBoardActivity.onResume - activated "
// + directorCode);
// activator = (ImageView) findViewById(R.id.taskDirectorIcon);
// activator.setImageDrawable(getDrawable(R.drawable.taskdirector));
// activator.setClickable(true);
// activator.setOnClickListener(new View.OnClickListener() {
// public void onClick(final View view) {
// // Activate the manager.
// final Intent intent = new Intent(parentActivity,
// tdirector.getClass());
// // Send the pilot id and transfer it to the next Activity
// intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
// parentActivity.getPilot()
// .getCharacterID());
// startActivity(intent);
// }
// });
// }
// break;
// case FITDIRECTOR:
// // final IDirector fdirector = new FitsActivity();
// // if (fdirector.checkActivation(getPilot())) {
// // logger.info("-- DirectorsBoardActivity.onResume -
// activated " + directorCode);
// activator = (ImageView) findViewById(R.id.fitDirectorIcon);
// activator.setImageDrawable(getDrawable(R.drawable.fitsdirector));
// activator.setClickable(true);
// activator.setOnClickListener(new View.OnClickListener() {
// public void onClick(final View view) {
// // Activate the manager.
// final Intent intent = new Intent(parentActivity,
// FittingActivity.class);
// // Send the pilot id and transfer it to the next Activity
// intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
// _store.getPilot().getCharacterID());
// startActivity(intent);
// }
// });
// // }
// break;
// [01]
