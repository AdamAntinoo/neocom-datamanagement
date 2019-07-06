package org.dimensinfin.eveonline.neocom.database.entities;

import java.util.Objects;
import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import jdk.nashorn.internal.ir.annotations.Immutable;

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
 * To keep Credential unique I will not use sequence generated ids but the identifier plus the server to use for the authentication.
 * This way I can store credentials for the different eve online servers ans sill only keep one for each server using the pilot
 * unique identifier to isolate this. Using sequence will not avoid having duplicated entries for the same pilot.
 *
 * @author Adam Antinoo
 */
@Immutable
@Entity(name = "Credentials")
@DatabaseTable(tableName = "Credentials")
public class Credential extends UpdatableEntity {
	private static final long serialVersionUID = -4248173464157148843L;

	public static String createUniqueIdentifier( final String server, final int identifier ) {
		return server.toLowerCase() + "/" + identifier;
	}

	@DatabaseField(id = true, index = true)
	private String uniqueCredential = Credential.createUniqueIdentifier("Tranquility".toLowerCase(), -1);
	@DatabaseField
	private int accountId = -2;
	@DatabaseField
	private String accountName;
	@DatabaseField
	private String dataSource = "Tranquility".toLowerCase();
	@DatabaseField
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
	private String raceName;

	// - C O N S T R U C T O R S
	public Credential() {
		super();
	}

	private Credential( final int newAccountIdentifier ) {
		super();
		this.accountId = newAccountIdentifier;
		this.uniqueCredential = Credential.createUniqueIdentifier(this.dataSource, this.accountId);
	}

	// - G E T T E R S   &   S E T T E R S
	public boolean isValid() {
		if (StringUtils.isEmpty(this.dataSource)) return false;
		if (StringUtils.isEmpty(this.accessToken)) return false;
		if (StringUtils.isEmpty(this.refreshToken)) return false;
		return true;
	}

	public String getUniqueId() {
		return this.uniqueCredential;
	}

	public int getAccountId() {
		return accountId;
	}

	public Credential setAccountId( final int accountId ) {
		this.accountId = accountId;
		this.uniqueCredential = Credential.createUniqueIdentifier(this.dataSource, this.accountId);
		return this;
	}

	public String getAccountName() {
		return accountName;
	}

	public String getName() {
		return this.accountName;
	}

	public String getAccessToken() {
		return this.accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getDataSource() {
		return dataSource.toLowerCase();
	}

	public void setDataSource( final String dataSource ) {
		if (null != dataSource) this.dataSource = dataSource.toLowerCase();
	}

	public Double getWalletBalance() {
		return walletBalance;
	}

	public Credential setWalletBalance( final Double walletBalance ) {
		this.walletBalance = walletBalance;
		return this;
	}

	public String getScope() {
		return scope;
	}

	public int getAssetsCount() {
		return assetsCount;
	}

	public Credential setAssetsCount( final int assetsCount ) {
		this.assetsCount = assetsCount;
		return this;
	}

	public String getRaceName() {
		return this.raceName;
	}

	public Credential setRaceName( final String raceName ) {
		this.raceName = raceName;
		return this;
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final Credential that = (Credential) o;
		return new EqualsBuilder()
				       .appendSuper(super.equals(o))
				       .append(accountId, that.accountId)
				       .append(assetsCount, that.assetsCount)
				       .append(accessToken, that.accessToken)
				       .append(tokenType, that.tokenType)
				       .append(dataSource, that.dataSource)
				       .append(scope, that.scope)
				       .append(uniqueCredential, that.uniqueCredential)
				       .append(walletBalance, that.walletBalance)
				       .append(refreshToken, that.refreshToken)
				       .append(accountName, that.accountName)
				       .append(raceName, that.raceName)
				       .isEquals();
	}

	// - C O R E
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				       .appendSuper(super.hashCode())
				       .append(accessToken)
				       .append(tokenType)
				       .append(dataSource)
				       .append(scope)
				       .append(uniqueCredential)
				       .append(accountId)
				       .append(walletBalance)
				       .append(assetsCount)
				       .append(refreshToken)
				       .append(accountName)
				       .append(raceName)
				       .toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				       .append("jsonClass", this.getJsonClass())
				       .append("uniqueCredential", uniqueCredential)
				       .append("walletBalance", walletBalance)
				       .append("assetsCount", assetsCount)
				       .append("accountName", accountName)
				       .append("raceName", raceName)
				       .toString();
	}

	// - B U I L D E R
	public static class Builder {
		private Credential onConstruction;

		public Builder( final int account ) {
			this.onConstruction = new Credential(account);
		}

		public Builder withAccountId( final Integer accountId ) {
			if (null != accountId) this.onConstruction.setAccountId(accountId);
			return this;
		}

		public Builder withAccountName( final String accountName ) {
			if (null != accountName) this.onConstruction.accountName = accountName;
			return this;
		}

		public Builder withDataSource( final String dataSource ) {
			if (null != dataSource) this.onConstruction.dataSource = dataSource;
			return this;
		}

		public Builder withTokenType( final String tokenType ) {
			if (null != tokenType) this.onConstruction.tokenType = tokenType;
			return this;
		}

		public Builder withAccessToken( final String accessToken ) {
			if (null != accessToken) this.onConstruction.accessToken = accessToken;
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

		public Builder withWalletBalance( final Double walletBalance ) {
			if (null != walletBalance) this.onConstruction.walletBalance = walletBalance;
			return this;
		}

		public Builder withAssetsCount( final Integer assetsCount ) {
			if (null != assetsCount) this.onConstruction.assetsCount = assetsCount;
			return this;
		}

		public Builder withRaceName( final String raceName ) {
			if (null != raceName) this.onConstruction.raceName = raceName;
			return this;
		}

		public Credential build() {
			Objects.requireNonNull(this.onConstruction.accountName);
			return this.onConstruction;
		}
	}
}
