//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.BlueprintPart;
import org.dimensinfin.evedroid.part.MarketDataPart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class Blueprint4IndustryHeaderRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	public TextView	itemName					= null;
	public TextView	itemGroupCategory	= null;
	public TextView	blueprintCount		= null;
	public TextView	blueprintRuns			= null;
	public TextView	blueprintMETE			= null;
	public TextView	stackRuns					= null;
	public TextView	jobs							= null;
	public TextView	budget						= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Blueprint4IndustryHeaderRender(final MarketDataPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public BlueprintPart getPart() {
		return (BlueprintPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		itemName = (TextView) _convertView.findViewById(R.id.itemName);
		itemGroupCategory = (TextView) _convertView.findViewById(R.id.itemGroupCategory);
		blueprintCount = (TextView) _convertView.findViewById(R.id.blueprintCount);
		blueprintRuns = (TextView) _convertView.findViewById(R.id.blueprintRuns);
		blueprintMETE = (TextView) _convertView.findViewById(R.id.blueprintMETE);
		stackRuns = (TextView) _convertView.findViewById(R.id.stackRuns);
		jobs = (TextView) _convertView.findViewById(R.id.jobs);
		budget = (TextView) _convertView.findViewById(R.id.budget);
	}

	@Override
	public void updateContent() {
		super.updateContent();
		itemName.setText(getPart().getName());
		if (AppWideConstants.DEVELOPMENT)
			itemName.setText(getPart().getName() + " [#" + getPart().getTypeID() + "]");
		itemGroupCategory.setText(getPart().getGroupCategory());
		blueprintCount.setText(getPart().get_blueprintCount());
		blueprintRuns.setText("[" + qtyFormatter.format(getPart().getRuns()) + "]");
		blueprintMETE.setText(getPart().get_blueprintMETE());
		// Start to show calculated information.
		stackRuns.setText(getPart().get_stackRuns()); // The number of available runs / manufacturable runs.
		jobs.setText(qtyFormatter.format(getPart().getJobs()) + " jobs");
		// Check the budget value to set the right presentation.
		double budgetValue = getPart().getBudget();
		if (budgetValue > 0.0) {
			budget.setTextColor(getContext().getResources().getColor(R.color.redPrice));
			budget.setText(generatePriceString(budgetValue, true, true));
		} else
			budget.setText("- ISK");

		loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), getPart().getCastedModel().getTypeID());
		_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.blacktraslucent40));
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.blueprint4industryheader, null);
		_convertView.setTag(this);
	}
}
// - UNUSED CODE ............................................................................................
