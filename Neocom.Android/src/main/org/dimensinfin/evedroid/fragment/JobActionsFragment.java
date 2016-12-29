//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.fragment;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.datasource.IndustryJobActionsDataSource;
import org.dimensinfin.evedroid.fragment.core.AbstractPagerFragment;
import org.dimensinfin.evedroid.part.BlueprintPart;
import org.dimensinfin.evedroid.storage.AppModelStore;
import org.dimensinfin.eveonline.neocom.model.NeoComBlueprint;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// - CLASS IMPLEMENTATION ...................................................................................
public class JobActionsFragment extends AbstractPagerFragment {

	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private AppModelStore	_store	= null;
	private Bundle				_extras	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	@Override
	public String getSubtitle() {
		return "Industry - Job Actions";
	}

	@Override
	public String getTitle() {
		return this.getPilotName();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		Log.i("NEOCOM", ">> JobActionsFragment.onCreateView");
		final View theView = super.onCreateView(inflater, container, savedInstanceState);
		try {
			this.setIdentifier(AppWideConstants.fragment.FRAGMENT_INDUSTRYJOBACTIONS);
		} catch (final RuntimeException rtex) {
			Log.e("NEOCOM", "RTEX> JobActionsFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> JobActionsFragment.onCreateView - " + rtex.getMessage()));
		}
		Log.i("NEOCOM", "<< JobActionsFragment.onCreateView");
		return theView;
	}

	@Override
	public void onStart() {
		Log.i("NEOCOM", ">> JobActionsFragment.onStart");
		try {
			if (!_alreadyInitialized) {
				final long bpassetid = _extras.getLong(AppWideConstants.EExtras.EXTRA_BLUEPRINTID.name());
				final int activity = _extras.getInt(AppWideConstants.EExtras.EXTRA_BLUEPRINTACTIVITY.name());
				final NeoComBlueprint blueprint = _store.getPilot().getAssetsManager().searchBlueprintByID(bpassetid);
				// Create the key element for this activity. All data to be shown comes from this single element.
				final BlueprintPart bppart = new BlueprintPart(blueprint);
				bppart.setActivity(activity);

				// First create the data fragment that contains the model.
				final IndustryJobActionsDataSource ds = new IndustryJobActionsDataSource(_store);
				ds.setBlueprint(bppart);
				this.setDataSource(ds);

				// This fragment has a header. Populate it with the datasource header contents.
				ArrayList<AbstractAndroidPart> headerData = ds.getHeaderPartHierarchy();
				for (AbstractAndroidPart headerPart : headerData)
					this.addtoHeader(headerPart);
			}
		} catch (final RuntimeException rtex) {
			Log.e("NEOCOM", "RTEX> JobActionsFragment.onStart - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> JobActionsFragment.onStart - " + rtex.getMessage()));
		}
		super.onStart();
		Log.i("NEOCOM", "<< JobActionsFragment.onStart");
	}

	//	private IDataSource getDataSource() {
	//		return _datasource;
	//	}

	public AbstractPagerFragment setExtras(final Bundle extras) {
		_extras = extras;
		return this;
	}

	public void setStore(final AppModelStore store) {
		_store = store;
	}
}
// - UNUSED CODE ............................................................................................
