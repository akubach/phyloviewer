package org.iplantc.de.client.events;

import java.util.ArrayList;

import org.iplantc.de.client.models.Job;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event representation of a user saving an analysis job. 
 */
public class JobSavedEvent extends GwtEvent<JobSavedEventHandler>
{
	public static final GwtEvent.Type<JobSavedEventHandler> TYPE = new GwtEvent.Type<JobSavedEventHandler>();
	
	private ArrayList<Job> jobs;

	/**
	 * create a new instance of JobSavedEvent
	 */
	public JobSavedEvent(ArrayList<Job> jobs)
	{
		this.setJobs(jobs);
	}

	@Override
	public Type<JobSavedEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(JobSavedEventHandler handler)
	{
		handler.onJobSaved(this);
	}

	public void setJobs(ArrayList<Job> jobs)
	{
		this.jobs = jobs;
	}

	public ArrayList<Job> getJobs()
	{
		return jobs;
	}

}
