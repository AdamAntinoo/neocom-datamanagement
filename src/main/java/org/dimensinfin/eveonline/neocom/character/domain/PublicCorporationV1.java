package org.dimensinfin.eveonline.neocom.character.domain;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.domain.NeoComNode;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class PublicCorporationV1 extends NeoComNode {
	public static final String CORPORATION_ICON_URL_PREFIX = "http://image.eveonline.com/Corporation/";
	public static final String CORPORATION_ICON_URL_SUFFIX = "_64.png";

	private static final long serialVersionUID = 981323756290749600L;

	private Integer corporationId;
	private transient GetCorporationsCorporationIdOk corporationPublicData;
	private PublicPilotV1 ceoPilotData;
	private transient GetAlliancesAllianceIdOk alliance;

	// - C O N S T R U C T O R S
	private PublicCorporationV1() {}

	// - G E T T E R S   &   S E T T E R S
	public GetAlliancesAllianceIdOk getAlliance() {
		return this.alliance;
	}

	public PublicPilotV1 getCeoPilotData() {
		return this.ceoPilotData;
	}

	public Integer getCorporationId() {
		return this.corporationId;
	}

	public GetCorporationsCorporationIdOk getCorporationPublicData() {
		return this.corporationPublicData;
	}

	// - V I R T U A L S
	public String getUrl4Icon() {
		return CORPORATION_ICON_URL_PREFIX + this.corporationId + CORPORATION_ICON_URL_SUFFIX;
	}

	// - B U I L D E R
	public static class Builder {
		private final PublicCorporationV1 onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new PublicCorporationV1();
		}

		public PublicCorporationV1 build() {
			return this.onConstruction;
		}

		public PublicCorporationV1.Builder withCeoPilotData( final PublicPilotV1 ceoPilotData ) {
			this.onConstruction.ceoPilotData = Objects.requireNonNull( ceoPilotData );
			return this;
		}

		public PublicCorporationV1.Builder withCorporationId( final Integer corporationId ) {
			this.onConstruction.corporationId = Objects.requireNonNull( corporationId );
			return this;
		}

		public PublicCorporationV1.Builder withCorporationPublicData( final GetCorporationsCorporationIdOk corporationPublicData ) {
			this.onConstruction.corporationPublicData = Objects.requireNonNull( corporationPublicData );
			return this;
		}
	}
}