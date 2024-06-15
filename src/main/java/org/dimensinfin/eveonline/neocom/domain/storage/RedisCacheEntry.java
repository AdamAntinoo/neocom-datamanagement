package org.dimensinfin.eveonline.neocom.domain.storage;

import lombok.Builder;

@Builder(setterPrefix = "with")
public class RedisCacheEntry {
	private String key;
	private String payload;
	private Integer TTL;
}
