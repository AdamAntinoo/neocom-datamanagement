package org.dimensinfin.eveonline.neocom.service.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalTime;

import org.dimensinfin.eveonline.neocom.service.scheduler.domain.CronScheduleGenerator;

/**
 * Uses a simplified cron pattern to detect if a job should be executed at this time.
 * The schedule generator expects to be called every minute to check which jobs have to be executed on this minute because their
 * schedule configuration pattern.
 *
 * The supported patterns are for minutes and hours.
 * <b>tick/every</b> - where tick is the initial minute and every is the difference with the next.
 * <b>tick,tick,tick</b> - the list of selected ticks
 * <b>*</b> - all ticks
 * minutes - hours
 */
public class HourlyCronScheduleGenerator implements CronScheduleGenerator {
	public HourlyCronScheduleGenerator() {}

	/**
	 * Checks if the cron pattern of the schedule matches to the HOUR:MINUTE combination of the current system time.
	 *
	 * @param schedule a reduced cron pattern with the minutes of execution and the hours of execution.
	 * @return true if the current time matches on this pattern.
	 */
	public boolean match( final String schedule ) {
		if (this.checkHourMatch( schedule, this.getHourlySchedule( schedule ) ))
			if (this.checkMinuteMatch( schedule, this.getMinuteSchedule( schedule ) )) return true;
			else return false;
		else return false;
	}

	private String getHourlySchedule( final String schedule ) {
		final String[] schedules = schedule.split( "-" );
		if (schedules.length > 1)
			return schedules[1].trim();
		else return "*";
	}

	private String getMinuteSchedule( final String schedule ) {
		final String[] schedules = schedule.split( "-" );
		if (schedules.length > 0)
			return schedules[0].trim();
		else return "*";
	}

	private boolean checkHourMatch( final String schedule, final String scheduleHour ) {
		final Integer hour = LocalTime.now().getHourOfDay();
		if (scheduleHour.startsWith( "*" )) return true;
		if (scheduleHour.contains( "/" )) {
			final String[] tickGenerator = scheduleHour.split( "/" );
			final List<Integer> ticks = this.generateHourTicks( Integer.parseInt( tickGenerator[0] ),
					Integer.parseInt( tickGenerator[1] ) );
			for (Integer tick : ticks)
				if (tick == hour) return true;
			return false;
		}
		final String[] ticks = scheduleHour.split( "," );
		for (int i = 0; i < ticks.length; i++) {
			if (hour == Integer.parseInt( ticks[i].trim() )) return true;
		}
		return false;
	}

	private boolean checkMinuteMatch( final String schedule, final String scheduleMinute ) {
		final Integer minute = LocalTime.now().getMinuteOfHour();
		if (scheduleMinute.startsWith( "*" )) return true;
		if (scheduleMinute.contains( "/" )) {
			final String[] tickGenerator = scheduleMinute.split( "/" );
			final List<Integer> ticks = this.generateMinuteTicks( Integer.parseInt( tickGenerator[0] ),
					Integer.parseInt( tickGenerator[1] ) );
			for (Integer tick : ticks)
				if (tick == minute) return true;
			return false;
		}
		final String[] ticks = scheduleMinute.split( "," );
		for (int i = 0; i < ticks.length; i++) {
			if (minute == Integer.parseInt( ticks[i].trim() )) return true;
		}
		return false;
	}

	private List<Integer> generateHourTicks( final Integer start, final Integer every ) {
		List<Integer> result = new ArrayList<>();
		result.add( start );
		int counter = 1;
		while (start + every * counter < 24) {
			result.add( start + every * counter );
		}
		return result;
	}

	private List<Integer> generateMinuteTicks( final Integer start, final Integer every ) {
		List<Integer> result = new ArrayList<>();
		result.add( start );
		int counter = 1;
		while ((start + every * counter) <= (start + 60)) {
			result.add( (start + every * counter) % 60 );
			counter++;
		}
		return result;
	}

	// - B U I L D E R
	public static class Builder {
		private HourlyCronScheduleGenerator onConstruction;

		public Builder() {
			this.onConstruction = new HourlyCronScheduleGenerator();
		}

		public HourlyCronScheduleGenerator build() {
			return this.onConstruction;
		}
	}
}