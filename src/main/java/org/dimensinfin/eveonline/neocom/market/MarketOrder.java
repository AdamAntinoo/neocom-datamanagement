package org.dimensinfin.eveonline.neocom.market;

import java.io.Serializable;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.domain.space.Station;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdOrders200Ok;

/**
 * MarketOrders represent the market entry data for a sell or buy action. Each market has the possibility to list orders to sell or buy items.
 * Orders have a set of attributes some of them are represented on this internal model class.
 *
 * Orders can be set on NPC stations or on player structures. Check this points because then the <code>station</code> field may not be of the right
 * type.
 *
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class MarketOrder implements Serializable {
	private static final long serialVersionUID = 2306455871561564504L;
	private Station station;
	private long orderId;
	private double price;
	private int typeId;
	private boolean isBuyOrder;
	private int volumeRemain;
	private int volumeTotal;

	// - C O N S T R U C T O R S
	private MarketOrder() {}

	// - G E T T E R S   &   S E T T E R S
	public long getOrderId() {
		return this.orderId;
	}

	public double getPrice() {
		return this.price;
	}

	public Station getStation() {
		return this.station;
	}

	public int getTypeId() {
		return this.typeId;
	}

	public int getVolumeRemain() {
		return this.volumeRemain;
	}

	public int getVolumeTotal() {
		return this.volumeTotal;
	}

	public boolean isBuyOrder() {
		return this.isBuyOrder;
	}

	// - B U I L D E R
	public static class Builder {
		private final MarketOrder onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new MarketOrder();
		}

		public MarketOrder build() {
			return this.onConstruction;
		}

		public MarketOrder.Builder withOrderData( final GetMarketsRegionIdOrders200Ok orderData ) {
			Objects.requireNonNull( orderData );
			this.onConstruction.orderId = orderData.getOrderId();
			this.onConstruction.price = orderData.getPrice();
			this.onConstruction.typeId = orderData.getTypeId();
			this.onConstruction.isBuyOrder = orderData.getIsBuyOrder();
			this.onConstruction.volumeRemain = orderData.getVolumeRemain();
			this.onConstruction.volumeTotal = orderData.getVolumeTotal();
			return this;
		}

		public MarketOrder.Builder withStation( final Station station ) {
			this.onConstruction.station = Objects.requireNonNull( station );
			return this;
		}
	}
}
