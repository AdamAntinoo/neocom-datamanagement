package org.dimensinfin.eveonline.neocom.service;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.service.scheduler.domain.JobStatus;
import org.dimensinfin.eveonline.neocom.updater.NeoComUpdater;

public class UpdaterJobManagerTest {
	@Test
	public void submit_newjob() {
		final NeoComUpdater updater = Mockito.mock(NeoComUpdater.class);
		Mockito.when(updater.getIdentifier()).thenReturn("UPDATER-TEST-IDENTIFIER");
		final int size = UpdaterJobManager.clearJobs();
		UpdaterJobManager.submit(updater);

		Assert.assertEquals("The job size should be 0.", 0, size);
		Assert.assertEquals("The number of jobs in the queue should be 1.", 1, UpdaterJobManager.getPendingJobsCount());
	}

//	@Test
	public void submit_duplicatedjob() {
		final NeoComUpdater updater = Mockito.mock(NeoComUpdater.class);
		Mockito.when(updater.getIdentifier()).thenReturn("UPDATER-TEST-IDENTIFIER");
		Mockito.when(updater.getStatus()).thenReturn( JobStatus.SCHEDULED);
		final int size = UpdaterJobManager.clearJobs();
		UpdaterJobManager.submit(updater);

		Assert.assertEquals("The job size should be 0.", 0, size);
		Assert.assertEquals("The number of jobs in the queue should be 1.", 1, UpdaterJobManager.getPendingJobsCount());
		UpdaterJobManager.submit(updater);
		Assert.assertEquals("The number of jobs in the queue should be 1.", 1, UpdaterJobManager.getPendingJobsCount());
	}
}
