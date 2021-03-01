package org.dimensinfin.eveonline.neocom.service.scheduler;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import org.jetbrains.annotations.NonNls;

import org.dimensinfin.eveonline.neocom.service.scheduler.converter.JobToJobRecordConverter;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.CronScheduleGenerator;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.Job;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.JobRecord;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.JobStatus;
import org.dimensinfin.logging.LogWrapper;

/**
 * The JobScheduler is a singleton instance. It should have a single instance on the whole system where the jobs are registered
 * and checked for the right time to be executed. The scheduler depends on an external time base so each platform can
 * implement its own timing base.
 *
 * It is expected that each minute the scheduler <code>runSchedule()</code> method is called to check from the list of
 * registered jobs which of them should be run on this minute.
 *
 * Being a singleton I should expect a default instance and the use of static calls to initiate the connection with the singleton.
 *
 * Registered jobs should avoid to record duplicates. Because of this I use Sets on the unique job identifier so jobs of the same class will use
 * their contents to identify possible different instances so a job with the same identifier is detected as already registered and the new
 * registration should replace the old registration. This allows to change job schedule parameters without removing old job first.
 *
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.19.0
 */
public class JobScheduler {
	private static final ExecutorService schedulerExecutor = Executors.newSingleThreadExecutor();
	private static JobScheduler singleton = new JobScheduler();

	public static JobScheduler getJobScheduler() {
		if (null == singleton) singleton = new JobScheduler();
		return singleton;
	}

	private final Map<Integer, Job> jobsRegistered = new HashMap<>();
	private CronScheduleGenerator cronScheduleGenerator = new HourlyCronScheduleGenerator.Builder().build();

	// - C O N S T R U C T O R S
	private JobScheduler() {}

	// - G E T T E R S   &   S E T T E R S
	public int getJobCount() {
		return this.jobsRegistered.size();
	}

	public List<JobRecord> getRegisteredJobs() {
		return Stream.of( this.jobsRegistered.values() )
				.map( job -> new JobToJobRecordConverter().convert( job ) )
				.collect( Collectors.toList() );
	}

	public void clear() {
		this.jobsRegistered.clear();
	}

	/**
	 * Register the job on the Scheduler registry and also execute the job a first time out of its schedule. The job will be added to the execution
	 * queue for a first time execution.
	 *
	 * @param job2Register the job instance to register and run.
	 * @return the number of jobs registered.
	 */
	public int registerAndRunJob( final Job job2Register ) {
		this.registerJob( job2Register );
		this.scheduleJob( job2Register );
		return this.jobsRegistered.size();
	}

	@NonNls
	public int registerJob( final Job job2Register ) {
		final Job registration = this.jobsRegistered.put( job2Register.getUniqueIdentifier(), job2Register );
		if (null == registration)
			LogWrapper.info( MessageFormat.format( "Registering job: (#{0}) - {1}",
					job2Register.getUniqueIdentifier() + "",
					job2Register.getClass().getSimpleName() )
			);
		return this.jobsRegistered.size();
	}

	public void removeJob( final Job jobInstance ) {
		this.jobsRegistered.remove( jobInstance.getUniqueIdentifier() );
	}

	/**
	 * Check each of the registered jobs to see if they should be launched on this HOUR:MINUTE.
	 */
	public void runSchedule() {
		for (final Job job : this.jobsRegistered.values()) {
			if (this.cronScheduleGenerator.match( job.getSchedule() ))
				this.scheduleJob( job );
		}
	}

	public void setCronScheduleGenerator( final CronScheduleGenerator cronScheduleGenerator ) {
		this.cronScheduleGenerator = cronScheduleGenerator;
	}

	public boolean wait4Completion() throws InterruptedException {
		LogWrapper.enter();
		schedulerExecutor.shutdown();
		try {
			schedulerExecutor.awaitTermination( Long.MAX_VALUE, TimeUnit.NANOSECONDS );
			return true;
		} finally {
			LogWrapper.exit();
		}
	}

	protected void scheduleJob( final Job job ) {
		job.setStatus( JobStatus.SCHEDULED );
		try {
			schedulerExecutor.submit( job );
		} catch (final RuntimeException neoe) {
			job.setStatus( JobStatus.EXCEPTION );
			LogWrapper.error( neoe );
		}
	}
}
