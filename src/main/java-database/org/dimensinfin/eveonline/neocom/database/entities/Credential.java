package org.dimensinfin.eveonline.neocom.database.entities;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Credentials are the block of data that stores the new authorization data for the ESI access to Eve Online data servers. The
 * change form CREST/XML has completed and I should not depend on any more data from that interfaces.
 *
 * Credentials are generated by the authorization flow through the CCP servers and connected to the Developer Application that
 * utterly authorizes point access. There are Credentials for the Tranquility production server and for the Singularity testing
 * service that are completely independent.
 *
 * This data should not be exported nor has more validity than to allow back ends to retrieve data on behalf for the user on a
 * periodic basis to speed up data access.
 *
 * There are two variants for using the Credentials. On on single user applications like the one developed for Android this
 * data allows to get repeated access without the need to repeat the login process. ON single user repositories there is no
 * problem sharing the information on different sessions. ONn contrary on the multi user applications like Infinity there
 * should be an isolation level and the Credential should only be used to update cache data.
 *
 * To keep Credential unique I will not use sequence generated ids but the identifier plus the server to use for the
 * authentication.
 * This way I can store credentials for the different eve online servers ans sill only keep one for each server using the pilot
 * unique identifier to isolate this. Using sequence will not avoid having duplicated entries for the same pilot.
 *
 * @author Adam Antinoo
 */
//@Entity(name = "Credentials")
@DatabaseTable(tableName = "neocom.Credentials")
@JsonIgnoreProperties
public class Credential extends UpdatableEntity {
	private static final long serialVersionUID = -4248173464157148843L;
	private static final String CREDENTIAL_IDENTIFIER_SEPARATOR = ".";

	public static String createUniqueIdentifier( final String server, final Integer identifier ) {
		return server.toLowerCase() + CREDENTIAL_IDENTIFIER_SEPARATOR + identifier.toString();
	}

	@DatabaseField(id = true, index = true)
	private String uniqueCredential = Credential.createUniqueIdentifier( "Tranquility".toLowerCase(), -2 );
	@DatabaseField
	private Integer accountId = -2;
	@DatabaseField
	private String accountName;
	@DatabaseField
	private int corporationId = -3; // Store the pilot's corporation identifier to be used on the UI.
	@DatabaseField
	private String dataSource = "Tranquility".toLowerCase();
	@DatabaseField(dataType = DataType.LONG_STRING)
	private String accessToken;
	@DatabaseField
	private String tokenType = "Bearer";
	@DatabaseField(dataType = DataType.LONG_STRING)
	private String scope = "publicData";
	@DatabaseField(dataType = DataType.LONG_STRING)
	private String refreshToken;
	@DatabaseField
	private int assetsCount = 0;
	@DatabaseField
	private Double walletBalance = 0.0;
	@DatabaseField
	private Double miningResourcesEstimatedValue = 0.0;
	@DatabaseField
	private String raceName;
	@DatabaseField
	private String jwtToken;

	// - C O N S T R U C T O R S

	/**
	 * WARNING - This constructor is required because this a repository entity.
	 */
	public Credential() {
		super();
	}

	private Credential( final Integer newAccountIdentifier ) {
		super();
		this.accountId = newAccountIdentifier;
		this.uniqueCredential = Credential.createUniqueIdentifier( this.dataSource, this.accountId );
	}

	// - G E T T E R S   &   S E T T E R S
	@JsonIgnore
	public boolean isValid() {
		if (StringUtils.isEmpty( this.dataSource )) return false;
		if (StringUtils.isEmpty( this.accessToken )) return false;
		return !StringUtils.isEmpty( this.refreshToken );
	}

	public String getUniqueCredential() {
		this.uniqueCredential = Credential.createUniqueIdentifier( this.dataSource, this.accountId );
		return this.uniqueCredential;
	}

	/**
	 * This is a virtual method which requirement is to have the right input/output api for the repository deserializers.
	 *
	 * @param newUniqueId the value to be set by the deserializer
	 * @return the self instance.
	 */
	public Credential setUniqueCredential( final String newUniqueId ) {
		this.uniqueCredential = newUniqueId;
		return this;
	}

	public Integer getAccountId() {
		return this.accountId;
	}

	public String getAccountName() {
		return this.accountName;
	}

	public int getCorporationId() {
		return this.corporationId;
	}

	public String getName() {
		return this.accountName;
	}

	public String getAccessToken() {
		return this.accessToken;
	}

	public Credential setAccessToken( final String accessToken ) {
		this.accessToken = accessToken;
		return this;
	}

	public String getJwtToken() {
		return this.jwtToken;
	}

	public Credential setJwtToken( final String jwtToken ) {
		this.jwtToken = jwtToken;
		return this;
	}

	public String getRefreshToken() {
		return this.refreshToken;
	}

	public String getDataSource() {
		return this.dataSource.toLowerCase();
	}

	public Double getWalletBalance() {
		return this.walletBalance;
	}

	public Credential setWalletBalance( final Double walletBalance ) {
		this.walletBalance = walletBalance;
		return this;
	}

	public int getAssetsCount() {
		return this.assetsCount;
	}

	public Credential setAssetsCount( final int assetsCount ) {
		this.assetsCount = assetsCount;
		return this;
	}

	public Double getMiningResourcesEstimatedValue() {
		return this.miningResourcesEstimatedValue;
	}

	public Credential setMiningResourcesEstimatedValue( final Double miningResourcesEstimatedValue ) {
		this.miningResourcesEstimatedValue = miningResourcesEstimatedValue;
		return this;
	}

	public String getRaceName() {
		return this.raceName;
	}

	public Credential setRaceName( final String raceName ) {
		this.raceName = raceName;
		return this;
	}

	public String getScope() {
		return this.scope;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.appendSuper( super.hashCode() )
				.append( this.accountId )
				.append( this.corporationId )
				.append( this.assetsCount )
				.append( this.accessToken )
				.append( this.tokenType )
				.append( this.dataSource )
				.append( this.scope )
				.append( this.uniqueCredential )
				.append( this.walletBalance )
				.append( this.refreshToken )
				.append( this.miningResourcesEstimatedValue )
				.append( this.accountName )
				.append( this.raceName )
				.append( this.jwtToken )
				.toHashCode();
	}

	// - C O R E
	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final Credential that = (Credential) o;
		return new EqualsBuilder()
				.appendSuper( super.equals( o ) )
				.append( this.accountId, that.accountId )
				.append( this.corporationId, that.corporationId )
				.append( this.assetsCount, that.assetsCount )
				.append( this.accessToken, that.accessToken )
				.append( this.tokenType, that.tokenType )
				.append( this.dataSource, that.dataSource )
				.append( this.scope, that.scope )
				.append( this.uniqueCredential, that.uniqueCredential )
				.append( this.walletBalance, that.walletBalance )
				.append( this.refreshToken, that.refreshToken )
				.append( this.miningResourcesEstimatedValue, that.miningResourcesEstimatedValue )
				.append( this.accountName, that.accountName )
				.append( this.raceName, that.raceName )
				.append( this.jwtToken, that.jwtToken )
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "jsonClass", this.getJsonClass() )
				.append( "uniqueCredential", this.uniqueCredential )
				.append( "walletBalance", this.walletBalance )
				.append( "assetsCount", this.assetsCount )
				.append( "miningResourcesEstimatedValue", this.miningResourcesEstimatedValue )
				.append( "accountId", this.accountId )
				.append( "accountName", this.accountName )
				.append( "raceName", this.raceName )
				.toString();
	}

	// - B U I L D E R
	public static class Builder {
		private Credential onConstruction;

		public Builder( final Integer accountId ) {
			Objects.requireNonNull( accountId );
			this.onConstruction = new Credential( accountId );
		}

		public Credential build() {
			Objects.requireNonNull( this.onConstruction.accountId );
			Objects.requireNonNull( this.onConstruction.accountName );
			return this.onConstruction;
		}

		public Builder withAccessToken( final String accessToken ) {
			if (null != accessToken) this.onConstruction.accessToken = accessToken;
			return this;
		}

		public Builder withAccountName( final String accountName ) {
			if (null != accountName) this.onConstruction.accountName = accountName;
			return this;
		}

		public Builder withCorporationId( final Integer corporationId ) {
			if (null != corporationId) this.onConstruction.corporationId = corporationId;
			return this;
		}

		public Builder withDataSource( final String dataSource ) {
			if (null != dataSource) this.onConstruction.dataSource = dataSource;
			return this;
		}

		public Builder withRefreshToken( final String refreshToken ) {
			if (null != refreshToken) this.onConstruction.refreshToken = refreshToken;
			return this;
		}

		public Builder withScope( final String scope ) {
			if (null != scope) this.onConstruction.scope = scope;
			return this;
		}

		public Builder withTokenType( final String tokenType ) {
			if (null != tokenType) this.onConstruction.tokenType = tokenType;
			return this;
		}
	}
}