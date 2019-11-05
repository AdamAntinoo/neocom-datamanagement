package org.dimensinfin.eveonline.neocom.service.scheduler.domain;

import java.util.UUID;
import java.util.concurrent.Callable;

public class Job implements Callable<Boolean> {
	private UUID identifier;
	private String schedule;
	private JobStatus status = JobStatus.READY;

	private Job() {}

	public UUID getIdentifier() {
		return this.identifier;
	}

	public String getSchedule() {
		return schedule;
	}

	public JobStatus getStatus() {
		return status;
	}

	public Job setStatus( final JobStatus status ) {
		this.status = status;
		return this;
	}

	@Override
	public Boolean call() throws Exception {
		return true;
	}

	// - B U I L D E R
	public static class Builder {
		private Job onConstruction;

		public Builder() {
			this.onConstruction = new Job();
		}

		public Job.Builder addCronSchedule( final String cronPattern ) {
			this.onConstruction.schedule = cronPattern;
			return this;
		}

		public Job build() {
			return this.onConstruction;
		}
	}
}