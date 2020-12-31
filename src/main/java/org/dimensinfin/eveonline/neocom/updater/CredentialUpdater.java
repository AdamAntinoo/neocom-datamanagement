package org.dimensinfin.eveonline.neocom.updater;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;

public class CredentialUpdater extends NeoComUpdater<Credential> {
	private static final long CREDENTIAL_CACHE_TIME = TimeUnit.MINUTES.toMillis( 15 );

	public CredentialUpdater( final Credential model ) {
		super( model );
	}

	// - N E O C O M U P D A T E R
	@Override
	public boolean needsRefresh() {
		if (this.getModel().getLastUpdateTime().plus( CREDENTIAL_CACHE_TIME ).isBefore( DateTime.now() ))
			return true;
		return false;
	}

	@Override
	public String getIdentifier() {
		return this.getModel().getJsonClass().toUpperCase() + ":" + this.getModel().getAccountId();
	}

	/**
	 * Execute the specific tasks that need to be completed to update the Credential additional information.
	 * There are two tasks: one to update the number of assets and another to get the wallet amount.
	 */
	@Override
	public void onRun() {
		if (null != esiDataProvider) {
			if (this.getModel().isValid()) {
				// Count the number of assets.
				final List<GetCharactersCharacterIdAssets200Ok> assetList = esiDataProvider.getCharactersCharacterIdAssets(
						this.getModel() );
				this.getModel().setAssetsCount( assetList.size() );
				// Get the wallet balance.
				final Double walletBalance = esiDataProvider.getCharactersCharacterIdWallet( this.getModel() );
				if (walletBalance > 0.0) this.getModel().setWalletBalance( walletBalance );

				// Get the race name. This needs access to the pilot.
				final GetCharactersCharacterIdOk pilotPublicData = esiDataProvider
						.getCharactersCharacterId( this.getModel().getAccountId() );
				final GetUniverseRaces200Ok raceData = esiDataProvider.searchSDERace( pilotPublicData.getRaceId() );
				if (null != raceData) this.getModel().setRaceName( raceData.getName() );
			}
			this.getModel().timeStamp(); // Mark the model as updated.
		}
	}
}
