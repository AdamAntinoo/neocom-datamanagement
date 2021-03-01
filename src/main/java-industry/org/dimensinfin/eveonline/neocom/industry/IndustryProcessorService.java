package org.dimensinfin.eveonline.neocom.industry;

import java.util.List;
import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.dimensinfin.eveonline.neocom.industry.domain.ActionPreference;
import org.dimensinfin.eveonline.neocom.industry.domain.Resource;
import org.dimensinfin.eveonline.neocom.industry.services.IndustrySession;

public class IndustryProcessorService {
	@Inject
	@Named("IndustrySession")
	private final IndustrySession industrySession;

	// - C O N S T R U C T O R S
	private IndustryProcessorService( final @NotNull IndustrySession industrySession ) {
		this.industrySession = industrySession;
	}

	/**
	 * Processing can accept multiple types of parameters but the result on all cases should be a list of actions related to resources.
	 * The method accepts items. Blueprints will be processed on another method because will require additional properties like the number of
	 * copies to process and that is an information that can be related to the blueprint characteristics.
	 *
	 * The processing will take on account the <code>ActionPreference</code> list of preferences loaded to the service with the
	 * <code>IndustrySession</code>. Any asset management will be done to the local list of assets also managed by the IndustrySession.
	 *
	 * @param item the eve item and the quantity to be processed. <code>Resources</code> represent game stacks and have a reference to a esi item
	 *             and to a count identical copies of the same item on a stack liek structure.
	 * @return the list of industry/logistics actions that should be executed to get the requested result, or an additional information action
	 * 		indicating if the whole count cannot be processed.
	 */
	public List<ActionPreference> process( final Resource item ) {
		return null;
	}
}