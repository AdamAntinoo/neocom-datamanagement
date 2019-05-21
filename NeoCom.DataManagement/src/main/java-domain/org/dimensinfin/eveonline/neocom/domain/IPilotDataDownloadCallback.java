package org.dimensinfin.eveonline.neocom.domain;

import org.dimensinfin.eveonline.neocom.entities.Credential;

public interface IPilotDataDownloadCallback {
	Credential getCredential();

	void signalCompletion( PilotDataSections section, Object publicData );

	int getRaceId();
}
