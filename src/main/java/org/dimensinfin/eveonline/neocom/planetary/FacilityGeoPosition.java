package org.dimensinfin.eveonline.neocom.planetary;

public class FacilityGeoPosition {
	private float latitude = 0.0F;
	private float longitude = 0.0F;

	public float getLatitude() {
		return this.latitude;
	}

	public FacilityGeoPosition setLatitude( final float latitude ) {
		this.latitude = latitude;
		return this;
	}

	public FacilityGeoPosition setLatitude( final double latitude ) {
		this.latitude = (float) latitude;
		return this;
	}

	public float getLongitude() {
		return this.longitude;
	}

	public FacilityGeoPosition setLongitude( final float longitude ) {
		this.longitude = longitude;
		return this;
	}

	public FacilityGeoPosition setLongitude( final double longitude ) {
		this.longitude = (float) longitude;
		return this;
	}
}
