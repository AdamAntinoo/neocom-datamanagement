package org.dimensinfin.eveonline.neocom.planetary;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.domain.IItemFacet;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOkContents;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOkDogmaAttributes;
import org.joda.time.DateTime;

import java.util.List;

public interface IPlanetaryFacility extends ICollaboration, IItemFacet {
	String getName();

	PlanetType getPlanetType();

	int getGroupId();

	PlanetaryFacilityType getFacilityType();

	int getIconReferenceId();

	int getIconColorReference();

	FacilityGeoPosition getGeoPosition();

	Integer getSchematicId();

	DateTime getLastCycleStart();

	List<GetCharactersCharacterIdPlanetsPlanetIdOkContents> getContents();

	int getCpuUsage();

	int getPowerUsage();

	FacilityGeoPosition getCommandCenterPosition();

	void setCommandCenterPosition( final FacilityGeoPosition commandCenterPosition );

	GetUniverseTypesTypeIdOkDogmaAttributes getDogmaAttributeById( final int attributeId );
}
