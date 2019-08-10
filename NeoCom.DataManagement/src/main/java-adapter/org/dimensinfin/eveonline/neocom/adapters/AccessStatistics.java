package org.dimensinfin.eveonline.neocom.adapters;

public class AccessStatistics {
	private int accesses = 0;
	private int hits = 0;
	private int misses = 0;

	public int accountAccess( final boolean isHit ) {
		if (isHit) this.hits++;
		else this.misses++;
		this.accesses++;
		return this.accesses;
	}

//	public int getAccesses() {
//		return this.accesses;
//	}

	public int getHits() {
		return this.hits;
	}

//	public int getMisses() {
//		return this.misses;
//	}
}
