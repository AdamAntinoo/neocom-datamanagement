package org.dimensinfin.eveonline.neocom.updater;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.core.domain.EEvents;
import org.dimensinfin.core.domain.EventEmitter;
import org.dimensinfin.core.domain.IntercommunicationEvent;
import org.dimensinfin.core.interfaces.IEventEmitter;
import org.dimensinfin.core.interfaces.IEventReceiver;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.service.UpdaterJobManager;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.JobStatus;

public abstract class NeoComUpdater<M> implements IEventEmitter {
	protected static final Logger logger = LoggerFactory.getLogger( NeoComUpdater.class );

	// - C O M P O N E N T S
	protected static ESIDataProvider esiDataProvider;

	public static void injectsEsiDataAdapter( final ESIDataProvider newesiDataProvider ) {
		esiDataProvider = newesiDataProvider;
	}

	protected DateTime startTime;
	private M model;
	private Exception lastException;
	private JobStatus status = JobStatus.READY;
	private EventEmitter eventEmitter = new EventEmitter();

	public NeoComUpdater( final M model ) {
		this.model = model;
	}

	public M getModel() {
		return this.model;
	}

	public JobStatus getStatus() {
		return this.status;
	}

	public void setStatus( final JobStatus status ) {
		this.status = status;
	}

	public Exception getLastException() {
		return this.lastException;
	}

	public abstract String getIdentifier();

	// - D E L E G A T E D
	@Override
	public void addEventListener( final IEventReceiver listener ) {
		this.eventEmitter.addEventListener( listener );
	}

	@Override
	public void removeEventListener( final IEventReceiver listener ) {
		this.eventEmitter.removeEventListener( listener );
	}

	@Override
	public boolean sendChangeEvent( final IntercommunicationEvent event ) {
		return this.eventEmitter.sendChangeEvent( event.getPropertyName() );
	}

	@Override
	public boolean sendChangeEvent( final String eventName ) {
		return this.eventEmitter.sendChangeEvent( eventName );
	}

	@Override
	public boolean sendChangeEvent( final String eventName, final Object origin ) {
		return this.eventEmitter.sendChangeEvent( eventName, origin );
	}

	@Override
	public boolean sendChangeEvent( final String eventName, final Object origin, final Object oldValue, final Object newValue ) {
		return this.eventEmitter.sendChangeEvent( eventName, origin, oldValue, newValue );
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.append( startTime )
				.append( lastException )
				.append( status )
				.toHashCode();
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (!(o instanceof NeoComUpdater)) return false;
		final NeoComUpdater<?> that = (NeoComUpdater<?>) o;
		return new EqualsBuilder()
				.append( startTime, that.startTime )
				.append( lastException, that.lastException )
				.append( status, that.status )
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "model", model )
				.append( "startTime", startTime )
				.append( "lastException", lastException )
				.append( "status", status )
				.toString();
	}

	// - N E O C O M U P D A T E R
	public abstract boolean needsRefresh();

	public void onComplete() {
		this.status = JobStatus.COMPLETED;
		this.eventEmitter.sendChangeEvent( EEvents.EVENT_REFRESHDATA.name() );
	}

	public void onException( final Exception exception ) {
		this.lastException = exception;
		this.status = JobStatus.EXCEPTION;
		NeoComLogger.error( exception );
	}

	public void onPrepare() {
		Objects.requireNonNull( esiDataProvider );
		this.status = JobStatus.RUNNING;
		this.startTime = DateTime.now();
	}
	// - C O R E

	public abstract void onRun();

	public void update( final boolean force ) {
		if ((force) || (this.needsRefresh())) UpdaterJobManager.submit( this );
	}

	public void update() {
		this.update( false );
	}
}
