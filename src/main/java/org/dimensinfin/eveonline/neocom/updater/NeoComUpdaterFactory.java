package org.dimensinfin.eveonline.neocom.updater;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;

public class NeoComUpdaterFactory {
	public static NeoComUpdater buildUpdater( final ICollaboration model ) {
		if ( model instanceof Credential)
			return new CredentialUpdater((Credential) model);
//		if ( model instanceof Pilot)
//			return new PilotUpdater((Pilot) model);
//		if ( model instanceof ColonyPack)
//			return new ColonyPackUpdater((ColonyPack) model);
		return null;
	}
}
