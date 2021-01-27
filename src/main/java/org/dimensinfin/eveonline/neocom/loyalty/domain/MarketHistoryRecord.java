package org.dimensinfin.eveonline.neocom.loyalty.domain;

import java.util.Objects;

import org.joda.time.LocalDate;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class MarketHistoryRecord {
	private LocalDate recordDate;
	private int volume = 0;
	private int daysInRange = 15;

	// - C O N S T R U C T O R S
	private MarketHistoryRecord() {}

	// - G E T T E R S   &   S E T T E R S
	public int getVolume() {
		return this.volume;
	}

	public boolean isOnDateRange( final LocalDate endRange ) {
		return !this.recordDate.isBefore( endRange.minusDays( this.daysInRange ) );
	}

	// - B U I L D E R
	public static class Builder {
		private final MarketHistoryRecord onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new MarketHistoryRecord();
		}

		public MarketHistoryRecord build() {
			return this.onConstruction;
		}

		public MarketHistoryRecord.Builder withDateRange( final int daysInRange ) {
			this.onConstruction.daysInRange = daysInRange;
			return this;
		}

		public MarketHistoryRecord.Builder withRecordDate( final LocalDate recordDate ) {
			this.onConstruction.recordDate = Objects.requireNonNull( recordDate );
			return this;
		}

		public MarketHistoryRecord.Builder withVolume( final int volume ) {
			this.onConstruction.volume = volume;
			return this;
		}
	}
}