package org.iplantc.de.client.events;

import java.util.List;

import org.iplantc.de.client.models.Job;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event represents a change in a user's analysis job status.
 */
public class JobStatusChangeEvent extends GwtEvent<JobStatusChangeEventHandler>
{
	// ////////////////////////////////////////
	// type
	public static final GwtEvent.Type<JobStatusChangeEventHandler> TYPE = new GwtEvent.Type<JobStatusChangeEventHandler>();

	// ////////////////////////////////////////
	// private variables
	private List<Job> jobs;

	// ////////////////////////////////////////
	// constructor
	public JobStatusChangeEvent(List<Job> jobs)
	{
		this.jobs = jobs;
	}

	// ////////////////////////////////////////
	// protected methods
	@Override
	protected void dispatch(JobStatusChangeEventHandler handler)
	{
		handler.onStatusChange(this);
	}

	// ////////////////////////////////////////
	// public methods
	@Override
	public Type<JobStatusChangeEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	// ////////////////////////////////////////
	public List<Job> getJobs()
	{
		return jobs;
	}
}
