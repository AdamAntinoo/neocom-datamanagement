//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.fragment.core;

// - IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Vector;

import org.dimensinfin.android.mvc.activity.SafeStopActivity;
import org.dimensinfin.android.mvc.activity.TitledFragment;
import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.core.DataSourceAdapter;
import org.dimensinfin.android.mvc.interfaces.IMenuActionTarget;
import org.dimensinfin.core.model.CEventModel.ECoreModelEvents;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.constant.CVariant;
import org.dimensinfin.evedroid.constant.CVariant.EDefaultVariant;
import org.dimensinfin.evedroid.interfaces.IExtendedDataSource;
import org.dimensinfin.evedroid.model.NeoComCharacter;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

// - CLASS IMPLEMENTATION ...................................................................................
// REFACTOR Used this dependency just to maintain more code compatible with the new model.
public abstract class AbstractNewPagerFragment extends TitledFragment {
	//- CLASS IMPLEMENTATION ...................................................................................
	protected class CreatePartsTask extends AsyncTask<AbstractNewPagerFragment, Void, Void> {

		// - F I E L D - S E C T I O N ............................................................................
		private final AbstractNewPagerFragment fragment;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public CreatePartsTask(final AbstractNewPagerFragment fragment) {
			this.fragment = fragment;
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		/**
		 * The datasource is ready and the new hierarchy should be created from the current model. All the stages
		 * are executed at this time both the model contents update and the list of parts to be used on the
		 * ListView. First, the model is checked to be initialized and if not then it is created. Then the model
		 * is run from start to end to create all the visible elements and from this list then we create the full
		 * list of the parts with their right renders.<br>
		 * This is the task executed every time a datasource gets its model modified and hides all the update time
		 * from the main thread as it is recommended by Google.
		 */
		@Override
		protected Void doInBackground(final AbstractNewPagerFragment... arg0) {
			Log.i("NEOCOM", ">> CreatePartsTask.doInBackground");
			try {
				// Create the hierarchy structure to be used on the Adapter.
				_datasource.collaborate2Model();
				_datasource.createContentHierarchy();
			} catch (final RuntimeException rtex) {
				rtex.printStackTrace();
			}
			Log.i("NEOCOM", "<< CreatePartsTask.doInBackground");
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {
			Log.i("NEOCOM", ">> CreatePartsTask.onPostExecute");
			// Activate the display of the list to force a redraw. Stop user UI waiting.
			_adapter = new DataSourceAdapter(fragment, _datasource);
			_modelContainer.setAdapter(_adapter);

			_progressLayout.setVisibility(View.GONE);
			_modelContainer.setVisibility(View.VISIBLE);
			_container.invalidate();

			// Add the header parts once the display is initialized.
			ArrayList<AbstractAndroidPart> headerContents = _datasource.getHeaderParts();
			if (headerContents.size() > 0) {
				_headerContainer.removeAllViews();
				_headerContainer.invalidate();
				for (final AbstractAndroidPart part : headerContents) {
					fragment.addViewtoHeader(part);
				}
			}
			super.onPostExecute(result);
			Log.i("NEOCOM", "<< CreatePartsTask.onPostExecute");
		}
	}

	//- CLASS IMPLEMENTATION ...................................................................................
	protected class StructureChangeTask extends AsyncTask<AbstractNewPagerFragment, Void, Void> {

		// - F I E L D - S E C T I O N ............................................................................
		private final AbstractNewPagerFragment fragment;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public StructureChangeTask(final AbstractNewPagerFragment fragment) {
			this.fragment = fragment;
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		/**
		 * The datasource is ready and the new hierarchy should be created from the current model. All the stages
		 * are executed at this time both the model contents update and the list of parts to be used on the
		 * ListView. First, the model is checked to be initialized and if not then it is created. Then the model
		 * is run from start to end to create all the visible elements and from this list then we create the full
		 * list of the parts with their right renders.<br>
		 * This is the task executed every time a datasource gets its model modified and hides all the update time
		 * from the main thread as it is recommended by Google.
		 */
		@Override
		protected Void doInBackground(final AbstractNewPagerFragment... arg0) {
			Log.i("NEOCOM", ">> StructureChangeTask.doInBackground");
			try {
				// Create the hierarchy structure to be used on the Adapter.
				_datasource.createContentHierarchy();
			} catch (final RuntimeException rtex) {
				rtex.printStackTrace();
			}
			Log.i("NEOCOM", "<< StructureChangeTask.doInBackground");
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {
			Log.i("NEOCOM", ">> StructureChangeTask.onPostExecute");
			//			
			//			AbstractPagerFragment.this._adapter = new DataSourceAdapter(this.fragment, AbstractPagerFragment.this._datasource);
			//			AbstractPagerFragment.this._modelContainer.setAdapter(AbstractPagerFragment.this._adapter);

			_progressLayout.setVisibility(View.GONE);
			_modelContainer.setVisibility(View.VISIBLE);
			_container.invalidate();
			// Tell the adapter to refresh the contents.
			_adapter.notifyDataSetChanged();

			// Add the header parts once the display is initialized.
			ArrayList<AbstractAndroidPart> headerContents = _datasource.getHeaderParts();
			if (headerContents.size() > 0) {
				_headerContainer.removeAllViews();
				_headerContainer.invalidate();
				for (final AbstractAndroidPart part : headerContents) {
					fragment.addViewtoHeader(part);
				}
			}
			super.onPostExecute(result);
			Log.i("NEOCOM", "<< StructureChangeTask.onPostExecute");
		}
	}

	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	protected int																_fragmentID				= -1;
	protected IExtendedDataSource								_datasource				= null;
	protected DataSourceAdapter									_adapter					= null;
	// REFACTOR Set back to private after the PagerFragment is removed
	protected final Vector<AbstractAndroidPart>	_headerContents		= new Vector<AbstractAndroidPart>();
	private String															_variant					= CVariant
			.getName4Variant(EDefaultVariant.DEFAULT_VARIANT.hashCode());

	// - U I    F I E L D S
	protected ViewGroup													_container				= null;
	/** The view that handles the non scrolling header. */
	protected ViewGroup													_headerContainer	= null;
	/** The view that represent the list view and the space managed though the adapter. */
	protected ListView													_modelContainer		= null;
	protected ViewGroup													_progressLayout		= null;
	protected IMenuActionTarget									_listCallback			= null;
	private Bundle															_extras						= new Bundle();

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	public void addtoHeader(final AbstractAndroidPart target) {
		Log.i("NEOCOM", ">> AbstractNewPagerFragment.addtoHeader");
		_headerContents.add(target);
		Log.i("NEOCOM", "<< AbstractNewPagerFragment.addtoHeader");
	}

	public void clearHeader() {
		_headerContents.clear();
	}

	public Bundle getExtras() {
		return _extras;
	}

	/**
	 * If the user has set the identifier return the identifier set (This allows to use the Generic by code in
	 * multifragment activities) . Otherwise return the Id of the fragment that would be generated on the layout
	 * XML.
	 */
	@Deprecated
	public int getIdentifier() {
		if (_fragmentID > 0)
			return _fragmentID;
		else
			return this.getId();
	}

	public NeoComCharacter getPilot() {
		return AppModelStore.getSingleton().getPilot();
	}

	public String getPilotName() {
		return this.getPilot().getName();
	}

	@Override
	public abstract String getSubtitle();

	//	@Override
	//	public void propertyChange(final PropertyChangeEvent arg0) {
	//		// TODO Auto-generated method stub
	//
	//	}
	@Override
	public abstract String getTitle();

	public void notifyDataSetChanged() {
		if (null != _adapter) {
			_adapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		//		logger.info(">> ManufactureContextFragment.onContextItemSelected"); //$NON-NLS-1$
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final int menuItemIndex = item.getItemId();
		final AbstractAndroidPart part = (AbstractAndroidPart) info.targetView.getTag();
		if (part instanceof IMenuActionTarget)
			return ((IMenuActionTarget) part).onContextItemSelected(item);
		else
			return true;
	}

	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenuInfo menuInfo) {
		Log.i("NEOCOM", ">> PageFragment.onCreateContextMenu"); //$NON-NLS-1$
		// REFACTOR If we call the super then the fragment's parent activity gets called. So the listcallback and the Activity
		// have not to be the same
		super.onCreateContextMenu(menu, view, menuInfo);
		// Check parameters to detect the item selected for menu target.
		if (view == _headerContainer) {
			//			 Check if this fragment has the callback configured
			final AbstractAndroidPart part = _headerContents.firstElement();
			if (part instanceof IMenuActionTarget) {
				((IMenuActionTarget) part).onCreateContextMenu(menu, view, menuInfo);
			}
		}
		if (view == _modelContainer) {
			// Get the tag assigned to the selected view and if implements the callback interface send it the message.
			final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			// Check if the se4lected item is suitable for menu and select it depending on item part class.
			AbstractAndroidPart part = (AbstractAndroidPart) info.targetView.getTag();
			if (part instanceof IMenuActionTarget) {
				((IMenuActionTarget) part).onCreateContextMenu(menu, view, menuInfo);
			}
		}
		Log.i("NEOCOM", "<< PageFragment.onCreateContextMenu"); //$NON-NLS-1$
	}

	/**
	 * Creates the structures when the fragment is about to be shown. We have to check that the parent Activity
	 * is compatible with this kind of fragment. So the fragment has to check of it has access to a valid pilot
	 * before returning any UI element.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		Log.i("NEOCOM", ">> AbstractPageFragment.onCreateView");
		final View theView = super.onCreateView(inflater, container, savedInstanceState);
		try {
			//			if (!this._alreadyInitialized)
			_container = (ViewGroup) inflater.inflate(R.layout.fragment_base, container, false);
			_headerContainer = (ViewGroup) _container.findViewById(R.id.headerContainer);
			_modelContainer = (ListView) _container.findViewById(R.id.listContainer);
			_progressLayout = (ViewGroup) _container.findViewById(R.id.progressLayout);
			// Prepare the structures for the context menu.
			this.registerForContextMenu(_headerContainer);
			this.registerForContextMenu(_modelContainer);
		} catch (final RuntimeException rtex) {
			Log.e("NEOCOM", "RTEX> AbstractPageFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> AbstractPageFragment.onCreateView - " + rtex.getMessage()));
		}
		Log.i("NEOCOM", "<< AbstractPageFragment.onCreateView");
		return _container;
	}

	/**
	 * When the execution reaches this point to activate the fragment we have to check that all the elements
	 * required are defined, mainly the Data Source. If the DS is ready and valid then we launch the DS data
	 * loading code. If that was performed on a previous start and the DS is already loaded we can skip this
	 * step. This last approach will reduce CPU usage and give a better user feeling when activating and
	 * deactivating activities and fragments.
	 */
	@Override
	public void onStart() {
		Log.i("NEOCOM", ">> AbstractPageFragment.onStart");
		super.onStart();
		try {
			this.createParts();
			// Update the spinner counter on the actionbar.
			this.getActivity().invalidateOptionsMenu();
			// Add the header parts once the display is initialized.
			if (_headerContents.size() > 0) {
				_headerContainer.removeAllViews();
				for (final AbstractAndroidPart part : _headerContents) {
					this.addViewtoHeader(part);
				}
			}
		} catch (final Exception rtex) {
			Log.e("NEOCOM", "RTEX> AbstractPageFragment.onStart - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> AbstractPageFragment.onStart - " + rtex.getMessage()));
		}
		Log.i("NEOCOM", "<< AbstractPageFragment.onStart");
	}

	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equalsIgnoreCase(ECoreModelEvents.EVENT_EXPANDCOLLAPSENODE.name())) {
			new StructureChangeTask(this).execute();
		}
	}

	public void setDataSource(final IExtendedDataSource dataSource) {
		if (null != dataSource) {
			_datasource = dataSource;
		}
	}

	public AbstractNewPagerFragment setExtras(final Bundle extras) {
		_extras = extras;
		return this;
	}

	/**
	 * Stores the identifier used to register this fragment as a unique identifier for later retrieval. <br>
	 * Warning: I think I am not using this method or this identifier to locate back the fragments.
	 * 
	 * @param id
	 */
	@Deprecated
	public void setIdentifier(final int id) {
		_fragmentID = id;
	}

	public void setListCallback(final IMenuActionTarget callback) {
		if (null != callback) {
			_listCallback = callback;
		}
	}

	public AbstractNewPagerFragment setVariant(final String selectedVariant) {
		_variant = selectedVariant;
		return this;
	}

	protected boolean checkDSState() {
		if (null == _datasource)
			return true;
		else
			return false;
	}

	protected void createParts() {
		try {
			// Check the validity of the data source.
			if (null == _datasource) throw new RuntimeException("Datasource not defined.");
			Log.i("NEOCOM", "-- AbstractNewPagerFragment.createParts - Launching CreatePartsTask");
			new CreatePartsTask(this).execute();
		} catch (final Exception rtex) {
			Log.e("NEOCOM", "RTEX> AbstractNewPagerFragment.createParts - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> AbstractNewPagerFragment.createParts - " + rtex.getMessage()));
		}
	}

	protected String getVariant() {
		return _variant;
	}

	/**
	 * For really unrecoverable or undefined exceptions the application should go to a safe spot. That spot is
	 * defined by the application so this is another abstract method.
	 * 
	 * @param exception
	 */
	protected void stopActivity(final Exception exception) {
		final Intent intent = new Intent(this.getActivity(), SafeStopActivity.class);
		// Pass the user message to the activity for display.
		intent.putExtra(SystemWideConstants.extras.EXTRA_EXCEPTIONMESSAGE, exception.getMessage());
		//		EVEDroidApp.getSingletonApp().init();
		this.startActivity(intent);
	}

	private void addViewtoHeader(final AbstractAndroidPart target) {
		Log.i("NEOCOM", ">> AbstractPagerFragment.addViewtoHeader");
		try {
			final AbstractHolder holder = target.getHolder(this);
			holder.initializeViews();
			holder.updateContent();
			final View hv = holder.getView();
			//	_headerContainer.removeAllViews();
			_headerContainer.addView(hv);
			_headerContainer.setVisibility(View.VISIBLE);
		} catch (final RuntimeException rtex) {
			Log.e("PageFragment", "R> PageFragment.addViewtoHeader RuntimeException. " + rtex.getMessage());
			rtex.printStackTrace();
		}
		Log.i("NEOCOM", "<< AbstractPagerFragment.addViewtoHeader");
	}
}

// - UNUSED CODE ............................................................................................
