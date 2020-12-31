package org.dimensinfin.eveonline.neocom.service.scheduler.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.dimensinfin.eveonline.neocom.support.PojoTestUtils;

public class JobRecordTest {
	@Test
	public void accessorsContract() {
		PojoTestUtils.validateAccessors( JobRecord.class );
	}

	@Test
	public void buildComplete() {
		final JobRecord jobRecord = new JobRecord.Builder()
				.withJobName( "-NAME-" )
				.withStatus( JobStatus.READY )
				.withSchedule( "-SCHEDULE-" )
				.build();
		Assertions.assertNotNull( jobRecord );
	}

	@Test
	public void buildFailure() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			final JobRecord jobRecord = new JobRecord.Builder()
					.withJobName( null )
					.withStatus( JobStatus.READY )
					.withSchedule( "-SCHEDULE-" )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final JobRecord jobRecord = new JobRecord.Builder()
					.withJobName( "-NAME-" )
					.withStatus( JobStatus.READY )
					.withSchedule( null )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final JobRecord jobRecord = new JobRecord.Builder()
					.withStatus( JobStatus.READY )
					.withSchedule( "-SCHEDULE-" )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final JobRecord jobRecord = new JobRecord.Builder()
					.withJobName( "-NAME-" )
					.withStatus( JobStatus.READY )
					.build();
		} );
	}
}