package org.dimensinfin.eveonline.neocom.character.domain;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
	private GetCorporationsCorporationIdOk corporationPublicData;
	private Integer ceoId;
	private GetAlliancesAllianceIdOk alliance;

	// - C O N S T R U C T O R S
	private PublicCorporationV1() {}

	// - G E T T E R S   &   S E T T E R S
	public GetAlliancesAllianceIdOk getAlliance() {
		return this.alliance;
	}

	public Integer getCeoId() {
		return this.ceoId;
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

	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 ).appendSuper( super.hashCode() ).append( this.corporationId ).append( this.corporationPublicData ).append(
				this.ceoId )
				.append( this.alliance ).toHashCode();
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (!(o instanceof PublicCorporationV1)) return false;
		final PublicCorporationV1 that = (PublicCorporationV1) o;
		return new EqualsBuilder().appendSuper( super.equals( o ) ).append( this.corporationId, that.corporationId )
				.append( this.corporationPublicData, that.corporationPublicData ).append( this.ceoId, that.ceoId ).append( this.alliance, that.alliance ).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "corporationId", this.corporationId )
				.append( "corporationPublicData", this.corporationPublicData )
				.append( "ceoId", this.ceoId )
				.append( "alliance", this.alliance )
				.toString();
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

		public PublicCorporationV1.Builder withCeoPilotId( final Integer ceoId ) {
			this.onConstruction.ceoId = Objects.requireNonNull( ceoId );
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