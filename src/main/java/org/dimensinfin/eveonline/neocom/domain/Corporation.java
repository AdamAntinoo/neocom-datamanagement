package org.dimensinfin.eveonline.neocom.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.joda.time.DateTime;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Corporation extends UpdatableNode {
	private static final long serialVersionUID = -2990989673090493394L;
	private static final long CORPORATION_CACHE_TIME = TimeUnit.HOURS.toMillis( 12 );

	private Integer corporationId;
	private transient GetCorporationsCorporationIdOk corporationPublicData;
	private Pilot ceoPilotData;
	private transient GetAlliancesAllianceIdOk alliance;

	// - C O N S T R U C T O R S
	private Corporation() {}

	// - V I R T U A L S
	public String getUrl4Icon() {
		return "http://image.eveonline.com/Corporation/" + this.corporationId + "_64.png";
	}

	// - I C O L L A B O R A T I O N

	// - G E T T E R S   &   S E T T E R S
	public Integer getCorporationId() {
		return corporationId;
	}

	public GetCorporationsCorporationIdOk getCorporationPublicData() {
		return this.corporationPublicData;
	}

	public Pilot getCeoPilotData() {
		return this.ceoPilotData;
	}

	public GetAlliancesAllianceIdOk getAlliance() {
		return this.alliance;
	}

	// - D E L E G A T E D
	public Integer getAllianceId() {
		if (null != this.corporationPublicData)
			return this.corporationPublicData.getAllianceId();
		else return -1; // This code is unreacheable.
	}

	/**
	 * Return the elements collaborated by this object. For a Character it depends on the implementation being a
	 * Pilot or a Corporation. For a Pilot the result depends on the variant received as the parameter
	 */
	@Override
	public List<ICollaboration> collaborate2Model( final String variant ) {
		return new ArrayList<>();
	}

	// - I U P D A T A B L E
	@Override
	public boolean needsRefresh() {
		return this.getLastUpdateTime().plus( CORPORATION_CACHE_TIME ).isBefore( DateTime.now() );
	}

	// - B U I L D E R
	public static class Builder {
		private Corporation onConstruction;

		public Builder() {
			this.onConstruction = new Corporation();
		}

		public Corporation build() {
			Objects.requireNonNull( this.onConstruction.corporationId );
			Objects.requireNonNull( this.onConstruction.corporationPublicData );
			Objects.requireNonNull( this.onConstruction.ceoPilotData );
			return this.onConstruction;
		}

		public Corporation.Builder optionslAlliance( final GetAlliancesAllianceIdOk alliance ) {
			this.onConstruction.alliance = alliance;
			return this;
		}

		public Corporation.Builder withCeoPilotData( final Pilot ceoPilotData ) {
			Objects.requireNonNull( ceoPilotData );
			this.onConstruction.ceoPilotData = ceoPilotData;
			return this;
		}

		public Corporation.Builder withCorporationId( final Integer corporationId ) {
			com.annimon.stream.Objects.requireNonNull( corporationId );
			this.onConstruction.corporationId = corporationId;
			return this;
		}

		public Corporation.Builder withCorporationPublicData( final GetCorporationsCorporationIdOk corporationPublicData ) {
			Objects.requireNonNull( corporationPublicData );
			this.onConstruction.corporationPublicData = corporationPublicData;
			return this;
		}
	}
}
