//	PROJECT:        POC (POC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Java 1.7.
//	DESCRIPTION:		Projects for Proof Of Concept desings.
package org.dimensinfin.eveonline.poc.planetary;

import java.util.HashMap;
import java.util.Vector;

import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.model.Schematics;
import org.dimensinfin.eveonline.neocom.model.Schematics.ESchematicDirection;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * Stores the data to transform a set of resources into a new set where some of them are processed from a Tier
 * to a higher Tier. It uses the CCP database information to generate the quantity conversions from the input
 * data to the result data. <br>
 * Also stores the number of cycles required for the complete transformation of the resources and then the
 * total time to perfrom the transformation.
 * 
 * @author Adam Antinoo
 */
public class ProcessingAction {

	// - S T A T I C - S E C T I O N ..........................................................................
	//	private static Logger								logger									= Logger.getLogger("ProcessingAction");
	//	private static int									RAW2TIER1_TRANSFORMQTY	= 3000;
	//	private static final int						MINUTES_RAWCYCLE				= 30;
	//	private static final int						RAWOUTPUT_MULTIPLIER		= 20;

	// - F I E L D - S E C T I O N ............................................................................
	private int													targetId				= 0;
	private Vector<Schematics>					schematics			= new Vector<Schematics>();
	private Vector<Schematics>					inputList				= new Vector<Schematics>();
	private Schematics									output					= null;
	private HashMap<Integer, Resource>	actionResources	= new HashMap<Integer, Resource>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	/**
	 * Instance a new <code>ProcessingAction</code> and set the target product id to be produced by this action.
	 * This should get the schematics information so the action can process the quantities and the length of the
	 * cycles.
	 * 
	 * @param targetId
	 */
	public ProcessingAction(int targetId) {
		this.targetId = targetId;
		// Get the schematics information.
		schematics = AppConnector.getDBConnector().searchSchematics4Output(targetId);
		// Store the inputs into another list.
		for (Schematics sche : schematics) {
			if (sche.getDirection() == ESchematicDirection.INPUT) inputList.add(sche);
			if (sche.getDirection() == ESchematicDirection.OUTPUT) output = sche;
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void addResource(Resource resource) {
		stockResource(resource);
	}

	/**
	 * Return the list of resources left and new from the action processing following the current schematics.
	 * This is the result of substracting from the input resources the input quantity multiplied by the cycles
	 * and adding to the result the outpur resource quantity multiplied by the same cycles.
	 * 
	 * @return
	 */
	public Vector<Resource> getActionResults() {
		int cycles = getPossibleCycles();
		Vector<Resource> results = new Vector<Resource>();
		if (cycles > 0) {
			for (Schematics sche : inputList) {
				results.add(processResource(sche, cycles));
			}
			// Add the output
			results.add(new Resource(output.getTypeId(), output.getQty() * cycles));
		}
		return results;
	}
	//[01]

	public Vector<Schematics> getInputs() {
		return inputList;
	}

	/**
	 * Return the number of cycles that can be run with the current quantities of input resources.
	 * 
	 * @return
	 */
	public int getPossibleCycles() {
		int cycles = Integer.MAX_VALUE;
		for (Schematics schematics : inputList) {
			int inputType = schematics.getTypeId();
			Resource resource = actionResources.get(inputType);
			if (null == resource)
				return 0;
			else
				cycles = Math.min(cycles, resource.getQuantity() / schematics.getQty());
		}
		return cycles;
	}

	/**
	 * Returns the list of resources at the end of the transformations.
	 * 
	 * @return
	 */
	public HashMap<Integer, Resource> getResources() {
		return actionResources;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("ProcessingAction [");
		buffer.append(inputList).append(" ").append(output).append(" ");
		buffer.append("resources: ").append(actionResources).append(" ");
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	protected ProcessingAction clone() throws CloneNotSupportedException {
		ProcessingAction clone = new ProcessingAction(this.targetId);
		for (Integer resource : actionResources.keySet()) {
			clone.addResource(new Resource(resource, actionResources.get(resource).getQuantity()));
		}
		return clone;
	}

	/**
	 * Processes one resource from the number of cycles indicated for the schematic received.
	 * 
	 * @param sche
	 * @return
	 */
	private Resource processResource(Schematics sche, int cycles) {
		// Get the resource from the list of available resources.
		Resource res = actionResources.get(sche.getTypeId());
		if (null != res) {
			return new Resource(sche.getTypeId(), res.getQuantity() - (sche.getQty() * cycles));
		} else
			return null;
	}

	//	/**
	//	 * Removes the resource with the ID indicated from any of the lists of Planetary Resources.
	//	 * 
	//	 * @param typeid
	//	 *          item type id to remove.
	//	 */
	//	private void removeResource(int typeid) {
	//		for (PlanetaryResource res : rawResources) {
	//			if (res.getResource().getTypeID() == typeid) rawResources.remove(res);
	//		}
	//		for (PlanetaryResource res : t1Resources) {
	//			if (res.getResource().getTypeID() == typeid) rawResources.remove(res);
	//		}
	//		for (PlanetaryResource res : t2Resources) {
	//			if (res.getResource().getTypeID() == typeid) rawResources.remove(res);
	//		}
	//		for (PlanetaryResource res : t3Resources) {
	//			if (res.getResource().getTypeID() == typeid) rawResources.remove(res);
	//		}
	//		for (PlanetaryResource res : t4Resources) {
	//			if (res.getResource().getTypeID() == typeid) rawResources.remove(res);
	//		}
	//	}
	/**
	 * Adds a new PlanetaryResource to the list of current resources stacking it to an already existing
	 * resource. If the resource is not already in the list then we put it on the aaction resource list.
	 * 
	 * @param typeid
	 *          the resource item id
	 * @param quantity
	 *          the quantity of the resource to stack.
	 */
	private void stockResource(Resource newResource) {
		//		logger.info(">> [ProcessingAction.stockResource]");
		Resource hit = actionResources.get(newResource.getTypeID());
		if (null == hit)
			actionResources.put(newResource.getTypeID(), newResource);
		else {
			hit.setQuantity(hit.getQuantity() + newResource.getQuantity());
		}
	}
	//	/**
	//	 * Adds a new PlanetaryResource to the list of current resources stacking it to an already existing
	//	 * resource. If the resource is not already in the list then we put it on the right one.
	//	 * 
	//	 * @param typeid
	//	 *          the resource item id
	//	 * @param quantity
	//	 *          the quantity of the resource to stack.
	//	 */
	//	private void stockResource(PlanetaryResource newResource) {
	//		// Get the list where I should stock the resource.
	//		Vector<PlanetaryResource> targetList = null;
	//		switch (newResource.getType()) {
	//			case RAW:
	//				targetList = rawResources;
	//				break;
	//			case TIER1:
	//				targetList = t1Resources;
	//				break;
	//			case TIER2:
	//				targetList = t2Resources;
	//				break;
	//			case TIER3:
	//				targetList = t3Resources;
	//				break;
	//			case TIER4:
	//				targetList = t4Resources;
	//				break;
	//
	//			default:
	//				break;
	//		}
	//		boolean found = false;
	//		for (PlanetaryResource pr : targetList) {
	//			if (pr.getResource().getTypeID() == newResource.getResource().getTypeID()) {
	//				pr.setQuantity(pr.getQuantity() + newResource.getQuantity());
	//				found = true;
	//			}
	//		}
	//		if (!found) {
	//			// Add the new resource to the list.
	//			targetList.add(newResource);
	//		}
	//	}

}

// - UNUSED CODE ............................................................................................
//[01]
//	public boolean checkActionActive(int cycles) {
//
//		return false;
//	}

//	/**
//	 * Converts all the RAW resources into Tier 1 resources. Packs the cycles into the number of possible cycles
//	 * and stores the time and the resulting Planetary Resources.
//	 */
//	public void doRawTransformation() {
//		for (PlanetaryResource resource : rawResources) {
//			if (resource.getType() == EPlanetaryTypes.RAW) {
//				int typeid = resource.getResource().getTypeID();
//				int qty = resource.getQuantity();
//				int outResource = resource.getOutputId();
//
//				// Calculate the maximum number of cycles.
//				int cycles = Math.floorDiv(resource.getQuantity(), RAW2TIER1_TRANSFORMQTY);
//				int totalTime = cycles * MINUTES_RAWCYCLE;
//				// Get the resulting resources.
//				removeResource(typeid);
//				addResource(new Resource(outResource, RAWOUTPUT_MULTIPLIER * cycles));
//				// Add the rest of the RAW that was not processed.
//				addResource(new Resource(typeid, qty - (RAWOUTPUT_MULTIPLIER * cycles)));
//			}
//		}
//	}
