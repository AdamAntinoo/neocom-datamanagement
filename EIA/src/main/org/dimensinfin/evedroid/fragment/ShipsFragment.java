//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.fragment;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractCorePart;
import org.dimensinfin.android.mvc.interfaces.IEditPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.AppWideConstants.EFragment;
import org.dimensinfin.evedroid.datasource.DataSourceLocator;
import org.dimensinfin.evedroid.datasource.ShipsDataSource;
import org.dimensinfin.evedroid.datasource.SpecialDataSource;
import org.dimensinfin.evedroid.factory.PartFactory;
import org.dimensinfin.evedroid.fragment.core.AbstractNewPagerFragment;
import org.dimensinfin.evedroid.model.EveLocation;
import org.dimensinfin.evedroid.model.Region;
import org.dimensinfin.evedroid.model.Separator;
import org.dimensinfin.evedroid.part.GroupPart;
import org.dimensinfin.evedroid.part.LocationIndustryPart;
import org.dimensinfin.evedroid.part.RegionPart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// - CLASS IMPLEMENTATION ...................................................................................
public class ShipsFragment extends AbstractNewPagerFragment {

	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	@Override
	public String getSubtitle() {
		String st = "";
		if (getVariant() == AppWideConstants.EFragment.SHIPS_BYLOCATION) {
			st = "Ships - by Location";
		}
		if (getVariant() == AppWideConstants.EFragment.SHIPS_BYCLASS) {
			st = "Ships - by Class";
		}
		return st;
	}

	@Override
	public String getTitle() {
		return getPilotName();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Creates the structures when the fragment is about to be shown. It will inflate the layout where the
	 * generic fragment will be layered to show the content. It will get the Activity functionality for single
	 * page activities.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		Log.i("NEOCOM", ">> ShipsFragment.onCreateView");
		final View theView = super.onCreateView(inflater, container, savedInstanceState);
		try {
			setIdentifier(_variant.hashCode());
		} catch (final RuntimeException rtex) {
			Log.e("NEOCOM", "RTEX> ShipsFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> ShipsFragment.onCreateView - " + rtex.getMessage()));
		}
		Log.i("NEOCOM", "<< ShipsFragment.onCreateView");
		return theView;
	}

	@Override
	public void onStart() {
		Log.i("NEOCOM", ">> ShipsFragment.onStart");
		try {
			// Check the datasource status and create a new one if still does not exists.
			if (checkDSState()) {
				registerDataSource();
			}
		} catch (final RuntimeException rtex) {
			Log.e("NEOCOM", "RTEX> ShipsFragment.onStart - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> ShipsFragment.onStart - " + rtex.getMessage()));
		}
		super.onStart();
		Log.i("NEOCOM", "<< ShipsFragment.onStart");
	}

	@Override
	protected void registerDataSource() {
		Log.i("NEOCOM", ">> ShipsFragment.registerDataSource");
		DataSourceLocator locator = new DataSourceLocator().addIdentifier(getPilotName()).addIdentifier(_variant.name());
		SpecialDataSource ds = new ShipsDataSource(locator, new ShipPartFactory(_variant));
		ds.setVariant(_variant);
		ds.addParameter(AppWideConstants.EExtras.CAPSULEERID.name(), getPilot().getCharacterID());
		setDataSource(EVEDroidApp.getAppStore().getDataSourceConector().registerDataSource(ds));
		Log.i("NEOCOM", "<< ShipsFragment.registerDataSource");
	}
}

//- CLASS IMPLEMENTATION ...................................................................................
final class ShipPartFactory extends PartFactory implements IPartFactory {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ShipPartFactory(final EFragment _variant) {
		super(_variant);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The method should create the matching part for the model received but there is no other place where we
	 * should create the next levels of the hierarchy. So we will create the part trasnformationes here.
	 */
	@Override
	public IEditPart createPart(final IGEFNode node) {
		if (node instanceof Region) {
			AbstractCorePart part = new RegionPart((Region) node);
			((AbstractAndroidPart) part).setRenderMode(AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION);
			part.setFactory(this);
			return part;
		}
		if (node instanceof EveLocation) {
			AbstractCorePart part = new LocationIndustryPart((EveLocation) node);
			((AbstractAndroidPart) part).setRenderMode(AppWideConstants.rendermodes.RENDER_BLUEPRINTINDUSTRY);
			part.setFactory(this);
			return part;
		}
		if (node instanceof Separator) {
			AbstractCorePart part = new GroupPart((Separator) node);
			//			((AbstractAndroidPart) part).setRenderMode(AppWideConstants.rendermodes.FRAGMENT_ASS);
			part.setFactory(this);
			return part;
		}
		return null;
	}
}

// - UNUSED CODE ............................................................................................
