package org.dimensinfin.eveonline.neocom.service.scheduler.domain;

import java.util.Objects;

public class JobRecord {
	private String jobName;
	private JobStatus status = JobStatus.READY;
	private String schedule;

	private JobRecord() {}

	public String getJobName() {
		return this.jobName;
	}

	public JobStatus getStatus() {
		return this.status;
	}

	public String getSchedule() {
		return this.schedule;
	}

	// - B U I L D E R
	public static class Builder {
		private JobRecord onConstruction;

		public Builder() {
			this.onConstruction = new JobRecord();
		}

		public JobRecord build() {
			Objects.requireNonNull( this.onConstruction.jobName );
			Objects.requireNonNull( this.onConstruction.schedule );
			return this.onConstruction;
		}

		public JobRecord.Builder withJobName( final String jobName ) {
			Objects.requireNonNull( jobName );
			this.onConstruction.jobName = jobName;
			return this;
		}

		public JobRecord.Builder withSchedule( final String schedule ) {
			Objects.requireNonNull( schedule );
			this.onConstruction.schedule = schedule;
			return this;
		}

		public JobRecord.Builder withStatus( final JobStatus status ) {
			this.onConstruction.status = status;
			return this;
		}
	}
}