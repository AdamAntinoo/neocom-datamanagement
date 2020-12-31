package org.dimensinfin.eveonline.neocom.provider;

public interface IConfigurationService {
	int contentCount();

	boolean getResourceBoolean( final String key );

	boolean getResourceBoolean( final String key, final Boolean defaultValue );

	Integer getResourceInteger( final String key );

	Integer getResourceInteger( final String key, final Integer defaultValue );

	String getResourceString( final String key );

	String getResourceString( final String key, final String defaultValue );
}
