package org.dimensinfin.eveonline.neocom.updater;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.core.domain.EEvents;
import org.dimensinfin.core.domain.IntercommunicationEvent;
import org.dimensinfin.core.interfaces.IEventReceiver;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.JobStatus;
import org.dimensinfin.eveonline.neocom.support.PojoTestUtils;

import nl.jqno.equalsverifier.EqualsVerifier;
import static org.mockito.ArgumentMatchers.any;

public class NeoComUpdaterTest {
	private ESIDataProvider esiDataProvider;
	private UpdaterUnderTest updaterUnderTest;

	@Before
	public void setUp() throws Exception {
		this.esiDataProvider = Mockito.mock( ESIDataProvider.class );
		NeoComUpdater.injectsEsiDataAdapter( this.esiDataProvider );
		this.updaterUnderTest = new UpdaterUnderTest( new TestPayload() );
		Assert.assertNotNull( updaterUnderTest );
	}

	@Test
	public void accessorContract() {
		PojoTestUtils.validateAccessors( UpdaterUnderTest.class );
	}

	//	@Test
	public void equalsContract() {
		EqualsVerifier.forClass( UpdaterUnderTest.class ).verify();
	}

	@Test
	public void toStringContract() {
		final UpdaterUnderTest updater = new UpdaterUnderTest( new TestPayload() );
		Assert.assertEquals( "{\"model\":{},\"startTime\":null,\"lastException\":null,\"status\":\"READY\"}",
				updater.toString() );
	}

	@Test
	public void onPrepare() {
		final UpdaterUnderTest updater = new UpdaterUnderTest( new TestPayload() );
		Assert.assertNotNull( updater );
		Assert.assertTrue( updater.needsRefresh() );
		Assert.assertEquals( JobStatus.READY, updater.getStatus() );
		updater.onPrepare();
		Assert.assertEquals( JobStatus.RUNNING, updater.getStatus() );
		Assert.assertFalse( updater.needsRefresh() );
	}

	@Test(expected = NullPointerException.class)
	public void onPrepareFailure() {
		NeoComUpdater.injectsEsiDataAdapter( null );
		final UpdaterUnderTest updater = new UpdaterUnderTest( new TestPayload() );
		Assert.assertNotNull( updater );
		Assert.assertTrue( updater.needsRefresh() );
		Assert.assertEquals( JobStatus.READY, updater.getStatus() );
		updater.onPrepare();
	}

	@Test
	public void onException() {
		final UpdaterUnderTest updater = new UpdaterUnderTest( new TestPayload() );
		Assert.assertNotNull( updater );
		updater.onException( new NeoComRuntimeException( "Updater test exception." ) );
		Assert.assertNotNull( updater.getLastException() );
		Assert.assertEquals( JobStatus.EXCEPTION, updater.getStatus() );
	}

	@Test
	public void onComplete() {
		final UpdaterUnderTest updater = new UpdaterUnderTest( new TestPayload() );
		Assert.assertNotNull( updater );
		final TestEventReceiver receiver = new TestEventReceiver();
		final TestEventReceiver spyReceiver = Mockito.spy( receiver );

		updater.addEventListener( spyReceiver );
		updater.onComplete();
		Assert.assertEquals( JobStatus.COMPLETED, updater.getStatus() );
		Mockito.verify( spyReceiver, Mockito.atLeastOnce() ).receiveEvent( any( IntercommunicationEvent.class ) );
	}

	@Test
	public void getModel() {
		Assert.assertNotNull( this.updaterUnderTest.getModel() );
		Assert.assertEquals( "{}", this.updaterUnderTest.getModel().toString() );
	}

	@Test
	public void setStatus() {
		Assert.assertEquals( JobStatus.READY, this.updaterUnderTest.getStatus() );
		this.updaterUnderTest.setStatus( JobStatus.SCHEDULED );
		Assert.assertEquals( JobStatus.SCHEDULED, this.updaterUnderTest.getStatus() );
	}

	@Test
	public void update() {
		final UpdaterUnderTest updateTest = new UpdaterUnderTest( new TestPayload() );
		updateTest.update();
		Assert.assertEquals( JobStatus.READY, this.updaterUnderTest.getStatus() );
	}

	public static class TestEventReceiver implements IEventReceiver {

		@Override
		public void receiveEvent( final IntercommunicationEvent event ) {
			Assert.assertEquals( EEvents.EVENT_REFRESHDATA.name(), event.getEventName() );
		}
	}

	public static class TestPayload {
		@Override
		public String toString() {
			return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
					.toString();
		}
	}

	public static class UpdaterUnderTest extends NeoComUpdater<TestPayload> {

		public UpdaterUnderTest( final TestPayload model ) {
			super( model );
		}

		@Override
		public boolean needsRefresh() {
			if (null == this.startTime) return true;
			if (this.startTime.plus( TimeUnit.SECONDS.toMillis( 2 ) ).isBeforeNow()) return true;
			return false;
		}

		@Override
		public String getIdentifier() {
			return "-TEST-IDENTIFIER-";
		}

		@Override
		public void onRun() {

		}
	}
}