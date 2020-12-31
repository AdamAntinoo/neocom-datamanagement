package org.dimensinfin.eveonline.neocom.service.scheduler.domain;

import java.util.Objects;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Defines the skeleton of a schedule job. The main points is that it have to be able to be called, and that should have a unique identifier that will
 * be used to do not launch multiple times the same task.
 * Implementors should use a hash generator (may be the default instance hashCode()) to generate a unique identifier that should depend on each job
 * particular parameters and contents.
 * The name is some information that will help to identify a job or set of jobs by human operators.
 *
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.19.0
 */
public abstract class Job implements Callable<Boolean> {
	private String schedule = "* - *";
	private JobStatus status = JobStatus.READY;

	protected Job() {}

	public String getSchedule() {
		return this.schedule;
	}

	protected void setSchedule( final String schedule ) {
		this.schedule = schedule;
	}

	public abstract int getUniqueIdentifier();

	public JobStatus getStatus() {
		return this.status;
	}

	public Job setStatus( final JobStatus status ) {
		this.status = status;
		return this;
	}

	// - C O R E
	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.append( this.schedule )
				.toHashCode();
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final Job job = (Job) o;
		return new EqualsBuilder()
				.append( this.schedule, job.schedule )
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "schedule", this.schedule )
				.append( "status", this.status )
				.toString();
	}

	// - B U I L D E R
	public abstract static class Builder<T extends Job, B extends Job.Builder<T, B>> {
		protected B actualClassBuilder;

		public Builder() {
			this.actualClassBuilder = this.getActualBuilder();
		}

		protected abstract T getActual();

		protected abstract B getActualBuilder();

		public B addCronSchedule( final String cronPattern ) {
			Objects.requireNonNull( cronPattern );
			this.getActual().setSchedule( cronPattern );
			return this.actualClassBuilder;
		}

		public T build() {
			Objects.requireNonNull( this.getActual().getSchedule() );
			return this.getActual();
		}
	}
}
