//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.market;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.List;

import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dimensinfin.eveonline.neocom.enums.EMarketSide;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.model.ANeoComEntity;
import org.dimensinfin.eveonline.neocom.model.EveLocation;

// - CLASS IMPLEMENTATION ...................................................................................

/**
 * This class stores the module and the market information and any other data for manufacturing calculations
 * and module classification. This is the equivalent to the Market card on the excel file and should be able
 * to calculate and generate any manufacturing information usable on the User Interface.<br>
 * <p>
 * Because on some cases the information may not be available and on Android the update is delegated to a
 * background task the class should be able to cope with NO DATA environments and generate dummy data that
 * will indicate the UI of such state.
 * @author Adam Antinoo
 */
public class MarketDataSet extends ANeoComEntity implements Serializable {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -2976488566617309014L;
	private static Logger logger = LoggerFactory.getLogger("MarketDataSet");
	private static final int HIGH_SECURITY = 1;
	private static final int LOW_SECURITY = 2;
	private static final int NULL_SECURITY = 3;

	// - F I E L D - S E C T I O N ............................................................................
	/**
	 * The data structures of the price and orders pending on the different markets for this single module.
	 */
	private List<MarketDataEntry> dataOnMarketHub = null;
	private MarketDataEntry bestmarketHigh = null;
	private MarketDataEntry bestmarketLow = null;
	private MarketDataEntry bestmarketNull = null;
	private Instant timeStamp = new Instant(0);
	private int id = -2;
	private EMarketSide side = EMarketSide.SELLER;
	/**
	 * Special indicator to report invalid entries that should not be cached. Only true when filled from market
	 * real data.
	 */
	private boolean valid = false;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public MarketDataSet() {
	}

	/**
	 * When creating the new data we have to access the base information that is the item and then copy the
	 * price from the base price of the item to the price of the card. This will allow use the card without
	 * problems while the background processes update the information from the network provider.
	 * @param id item id to store and to get info for.
	 */
	public MarketDataSet( final int id, final EMarketSide side ) {
		this.id = id;
		this.side = side;
		double baseprice = 0.0;
		try {
			baseprice = accessGlobal().searchItem4Id(id).getBaseprice();
		} catch ( NeoComRuntimeException neoe ) {
			baseprice = 0.0;
		}
		bestmarketHigh = bestmarketLow = bestmarketNull = new MarketDataEntry(new EveLocation());
		bestmarketHigh.setPrice(baseprice);
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	//--- G E T T E R S   &   S E T T E R S
	public List<MarketDataEntry> getDataOnMarketHub() {
		return this.dataOnMarketHub;
	}

	public MarketDataSet setDataOnMarketHub( final List<MarketDataEntry> dataOnMarketHub ) {
		this.dataOnMarketHub = dataOnMarketHub;
		return this;
	}

	public MarketDataEntry getBestmarketHigh() {
		return this.bestmarketHigh;
	}

	public MarketDataSet setBestmarketHigh( final MarketDataEntry bestmarketHigh ) {
		this.bestmarketHigh = bestmarketHigh;
		return this;
	}

	public MarketDataEntry getBestmarketLow() {
		return this.bestmarketLow;
	}

	public MarketDataSet setBestmarketLow( final MarketDataEntry bestmarketLow ) {
		this.bestmarketLow = bestmarketLow;
		return this;
	}

	public MarketDataEntry getBestmarketNull() {
		return this.bestmarketNull;
	}

	public MarketDataSet setBestmarketNull( final MarketDataEntry bestmarketNull ) {
		this.bestmarketNull = bestmarketNull;
		return this;
	}

	public Instant getTimeStamp() {
		return this.timeStamp;
	}

	public MarketDataSet setTimeStamp( final Instant timeStamp ) {
		this.timeStamp = timeStamp;
		return this;
	}

	public EMarketSide getSide() {
		return this.side;
	}

	public MarketDataSet setSide( final EMarketSide side ) {
		this.side = side;
		return this;
	}

	public boolean isValid() {
		return this.valid;
	}

	public MarketDataSet setValid( final boolean valid ) {
		this.valid = valid;
		return this;
	}

	//--- N O N   E X P O R T A B L E   F I E L D S
//	@JsonIgnore
	public MarketDataEntry getBestMarket() {
		return bestmarketHigh;
	}

	@JsonIgnore
	public Instant getTS() {
		return timeStamp;
	}

	@JsonIgnore
	public void markUpdate() {
		this.timeStamp = new Instant();
	}

	@JsonIgnore
	public void setData( final List<MarketDataEntry> hubData ) {
		dataOnMarketHub = hubData;
		this.updateBestMarket();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("MarketDataSet [");
		buffer.append(side).append(" ");
		if ( null != bestmarketHigh ) buffer.append(bestmarketHigh.toString());
		buffer.append("]");
		return buffer.toString();
	}

	/**
	 * Iterates over the market information to get the market data with the better price depending on the market
	 * direction.<br>
	 * If the list is empty there is no best market information so we can set the default item price
	 * information.
	 */
	@JsonIgnore
	public synchronized void updateBestMarket() {
		if ( side == EMarketSide.SELLER ) {
			if ( (null == dataOnMarketHub) || (dataOnMarketHub.size() < 1) ) {
				double baseprice = 0.0;
				try {
					baseprice = accessGlobal().searchItem4Id(id).getBaseprice();
				} catch ( NeoComRuntimeException neoe ) {
					baseprice = 0.0;
				}
				bestmarketHigh = bestmarketLow = bestmarketNull = new MarketDataEntry(new EveLocation());
				bestmarketHigh.setPrice(baseprice);
				//				MarketDataSet.logger.info("-- MarketDataSet.updateBestMarket - using default price: " + baseprice); //$NON-NLS-1$
			} else {
				// Scan the list of entries to store the best one for each of the categories.
				bestmarketHigh = null;
				for ( MarketDataEntry mde : dataOnMarketHub ) {
					//	MarketDataSet.logger.info("-- MarketDataSet.updateBestMarket - processing MDE: " + mde); //$NON-NLS-1$
					int sec = this.getSecurityCategory(mde.getSecurity());
					if ( sec == MarketDataSet.HIGH_SECURITY ) {
						if ( null == bestmarketHigh ) {
							bestmarketHigh = mde;
						} else if ( bestmarketHigh.getPrice() > mde.getPrice() ) {
							bestmarketHigh = mde;
							//							MarketDataSet.logger.info("-- MarketDataSet.updateBestMarket - setting a better SELLER: " + mde); //$NON-NLS-1$
						}
					}
				}
				// Check for empty process. For example on blueprints.
				if ( null == bestmarketHigh ) {
					bestmarketHigh = new MarketDataEntry(new EveLocation());
				}
			}
		}
		if ( side == EMarketSide.BUYER ) {
			if ( (null == dataOnMarketHub) || (dataOnMarketHub.size() < 1) ) {
				double baseprice = 0.0;
				try {
					baseprice = accessGlobal().searchItem4Id(id).getBaseprice();
				} catch ( NeoComRuntimeException neoe ) {
					baseprice = 0.0;
				}
				bestmarketHigh = bestmarketLow = bestmarketNull = new MarketDataEntry(new EveLocation());
				bestmarketHigh.setPrice(baseprice);
				//				MarketDataSet.logger.info("-- MarketDataSet.updateBestMarket - using default price: " + baseprice); //$NON-NLS-1$
			} else {
				// Scan the list of entries to store the best one for each of the categories.
				bestmarketHigh = null;
				for ( MarketDataEntry mde : dataOnMarketHub ) {
					int sec = this.getSecurityCategory(mde.getSecurity());
					if ( sec == MarketDataSet.HIGH_SECURITY ) {
						if ( null == bestmarketHigh ) {
							bestmarketHigh = mde;
						} else if ( bestmarketHigh.getPrice() < mde.getPrice() ) {
							bestmarketHigh = mde;
							//							MarketDataSet.logger.info("-- MarketDataSet.updateBestMarket - setting a better BUYER: " + mde); //$NON-NLS-1$
						}
					}
				}
				// Check for empty process. For example on blueprints.
				if ( null == bestmarketHigh ) {
					bestmarketHigh = new MarketDataEntry(new EveLocation());
				}
			}
		}
	}

	private int getSecurityCategory( final String security ) {
		try {
			double sec = Double.parseDouble(security);
			if ( sec >= 0.5 ) return MarketDataSet.HIGH_SECURITY;
			if ( sec >= 0.0 ) return MarketDataSet.LOW_SECURITY;
		} catch ( RuntimeException rtex ) {
			return MarketDataSet.NULL_SECURITY;
		}
		return MarketDataSet.NULL_SECURITY;
	}
}

// - UNUSED CODE ............................................................................................