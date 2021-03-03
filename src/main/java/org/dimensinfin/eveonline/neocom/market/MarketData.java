package org.dimensinfin.eveonline.neocom.market;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

import org.dimensinfin.eveonline.neocom.domain.space.Station;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdOrders200Ok;
import org.dimensinfin.eveonline.neocom.market.service.MarketService;

import static org.dimensinfin.eveonline.neocom.market.service.MarketService.MARKET_DEEP_RANGE;

/**
 * Converts ESI market data into a suitable data container that has meaning on the front end.
 * Best sell and best buy orders can be empty if the market selected has no player operations. So prices in such cases do not have value and that
 * supposes a problem at the serialization step or storage.
 *
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class MarketData implements Serializable {
	private static final long serialVersionUID = 2265789863342148968L;
	private Integer typeId;
	private Station regionHub;
	@Nullable
	private MarketOrder bestSellOrder;
	@Nullable
	private MarketOrder bestBuyOrder;
	private List<GetMarketsRegionIdOrders200Ok> sellOrders = new ArrayList<>();
	private int sellDeep = 0;
	private double sellAverage = 0.0;
	private double marketWidth = 0.0;

	// - C O N S T R U C T O R S
	private MarketData() {}

	// - G E T T E R S   &   S E T T E R S
	@Nullable
	public MarketOrder getBestBuyOrder() {
		return this.bestBuyOrder;
	}

	public double getBestBuyPrice() {
		if (null == this.bestBuyOrder) return 0.0;
		else return this.bestBuyOrder.getPrice();
	}

	@Nullable
	public MarketOrder getBestSellOrder() {
		return this.bestSellOrder;
	}

	public double getBestSellPrice() {
		if (null == this.bestSellOrder) return 0.0;
		else return this.bestSellOrder.getPrice();
	}

	public double getMarketWidth() {
		return this.marketWidth;
	}

	public double getSellAverage() {
		return this.sellAverage;
	}

	public int getSellDeep() {
		return this.sellDeep;
	}

	public List<GetMarketsRegionIdOrders200Ok> getSellOrders() {
		return this.sellOrders;
	}

	public int getSellRegionId() {
		if (null != this.regionHub) return this.regionHub.getRegionId();
		else return MarketService.PREDEFINED_MARKET_REGION_ID;
	}

	public int getTypeId() {
		return this.typeId;
	}

	// - B U I L D E R
	public static class Builder {
		private final MarketData onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new MarketData();
		}

		public MarketData build() {
			Objects.requireNonNull( this.onConstruction.typeId );
			Objects.requireNonNull( this.onConstruction.regionHub );
			this.onConstruction.marketWidth = this.calculateMarketWidth( this.onConstruction.bestSellOrder, this.onConstruction.bestBuyOrder );
			return this.onConstruction;
		}

		public MarketData.Builder withBestBuyOrder( final MarketOrder bestBuyOrder ) {
			this.onConstruction.bestBuyOrder = bestBuyOrder;
			return this;
		}

		public MarketData.Builder withBestSellOrder( final MarketOrder bestSellOrder ) {
			this.onConstruction.bestSellOrder = bestSellOrder;
			return this;
		}

		public MarketData.Builder withRegionHub( final Station regionHub ) {
			this.onConstruction.regionHub = Objects.requireNonNull( regionHub );
			return this;
		}

		public MarketData.Builder withSellOrders( final List<GetMarketsRegionIdOrders200Ok> sellOrders ) {
			if (null != sellOrders) {
				this.onConstruction.sellOrders = sellOrders;
				this.onConstruction.sellDeep = this.calculateSellDeep( this.onConstruction.sellOrders );
				this.onConstruction.sellAverage = this.calculateSellAverage( this.onConstruction.sellOrders );
			}
			return this;
		}

		public Builder withTypeId( final int itemId ) {
			this.onConstruction.typeId = itemId;
			return this;
		}

		/**
		 * Calculates the market width that is the difference between the lowest sell price and the highest buy price.
		 */
		private double calculateMarketWidth( final MarketOrder sellOrder, final MarketOrder buyOrder ) {
			if (null == sellOrder) return -1.0;
			if (null == buyOrder) return -1.0;
			return Math.abs( sellOrder.getPrice() - buyOrder.getPrice() );
		}

		private double calculateSellAverage( final List<GetMarketsRegionIdOrders200Ok> sellOrders ) {
			if (!sellOrders.isEmpty()) {
				final double amount = sellOrders.stream().mapToDouble( order -> order.getPrice() * order.getVolumeRemain() ).sum();
				final double count = sellOrders.stream().mapToInt( GetMarketsRegionIdOrders200Ok::getVolumeRemain ).sum();
				if (amount > 0.0) return amount / count;
				else return 0.0;
			} else return 0.0;
		}

		/**
		 * Calculate the sell market deep that is the accumulated volume for all orders inside a price range. By default the price range is set to the
		 * 5% of the best sell value.
		 *
		 * @param sellOrders the orders where to search for the data values.
		 * @return the volume of sell orders for this price range.
		 */
		private int calculateSellDeep( final List<GetMarketsRegionIdOrders200Ok> sellOrders ) {
			if (!sellOrders.isEmpty()) {
				final double priceLimit = sellOrders.get( 0 ).getPrice() * MARKET_DEEP_RANGE;
				return sellOrders.stream()
						.filter( order -> order.getPrice() <= priceLimit )
						.mapToInt( GetMarketsRegionIdOrders200Ok::getVolumeRemain )
						.sum();
			} else return 0;
		}
	}
}