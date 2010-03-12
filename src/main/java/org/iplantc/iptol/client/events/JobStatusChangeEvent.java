package org.iplantc.iptol.client.events;

import java.util.List;
import org.iplantc.iptol.client.Job;
import com.google.gwt.event.shared.GwtEvent;

public class JobStatusChangeEvent extends GwtEvent<JobStatusChangeEventHandler> 
{
	//////////////////////////////////////////
	//private variables
	private List<Job> jobs;
	
	//////////////////////////////////////////
	//type
	public static final GwtEvent.Type<JobStatusChangeEventHandler> TYPE = new GwtEvent.Type<JobStatusChangeEventHandler>();
	
	//////////////////////////////////////////
	//constructor
	public JobStatusChangeEvent(List<Job> jobs)
	{
		this.jobs = jobs;
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void dispatch(JobStatusChangeEventHandler handler) 
	{
		handler.onStatusChange(this);	
	}

	//////////////////////////////////////////
	//public methods
	@Override
	public Type<JobStatusChangeEventHandler> getAssociatedType() 
	{
		return TYPE;
	}
	
	//////////////////////////////////////////
	public List<Job> getJobs()
	{
		return jobs;
	}
}
