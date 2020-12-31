package org.dimensinfin.eveonline.neocom.service.scheduler.domain;

public interface CronScheduleGenerator {
	boolean match( final String schedule );
}
