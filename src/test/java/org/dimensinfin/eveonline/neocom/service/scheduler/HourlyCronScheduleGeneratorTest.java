package org.dimensinfin.eveonline.neocom.service.scheduler;

import java.util.concurrent.TimeUnit;

import org.joda.time.DateTimeUtils;
import org.joda.time.LocalTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HourlyCronScheduleGeneratorTest {
	private HourlyCronScheduleGenerator generator4Test;

	@BeforeEach
	void beforeEach() {
		this.generator4Test = new HourlyCronScheduleGenerator.Builder().build();
	}

	@AfterEach
	void afterEach() {
		DateTimeUtils.setCurrentMillisSystem(); // Make sure to cleanup afterwards
	}

	@Test
	void buildContract() {
		final HourlyCronScheduleGenerator generator = new HourlyCronScheduleGenerator.Builder().build();
		Assertions.assertNotNull( generator );
	}

	@Test
	void matchEveryMinute() {
		final String schedule = "* - *";
		Assertions.assertTrue( this.generator4Test.match( schedule ) );
	}

	@Test
	void matchThisMinute() {
		final int minute = new LocalTime().getMinuteOfHour();
		final String schedule = minute + " - *";
		Assertions.assertTrue( this.generator4Test.match( schedule ) );
	}

	@Test
	void matchThisMinuteEvery5() {
		final int minute = new LocalTime().getMinuteOfHour();
		final String schedule = minute + "/5 - *";
		Assertions.assertTrue( this.generator4Test.match( schedule ) );
		DateTimeUtils.setCurrentMillisFixed( LocalTime.now().getMillisOfDay() + TimeUnit.MINUTES.toMillis( 5 ) );
		Assertions.assertTrue( this.generator4Test.match( schedule ) );
	}

	@Test
	void matchThisHour() {
		final int hour = new LocalTime().getHourOfDay();
		final String schedule = "* - " + hour;
		Assertions.assertTrue( this.generator4Test.match( schedule ) );
	}

	@Test
	void matchThisHourAndMinute() {
		final int hour = new LocalTime().getHourOfDay();
		final int minute = new LocalTime().getMinuteOfHour();
		final String schedule = minute + " - " + hour;
		Assertions.assertTrue( this.generator4Test.match( schedule ) );
	}

	@Test
	void notMatchThisHourAndMinute() {
		final int hour = new LocalTime().getHourOfDay() + 1;
		final int minute = new LocalTime().getMinuteOfHour() + 1;
		final String schedule = minute + " - " + hour;
		Assertions.assertFalse( this.generator4Test.match( schedule ) );
	}
}