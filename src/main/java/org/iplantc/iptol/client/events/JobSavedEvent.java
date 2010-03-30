package org.iplantc.iptol.client.events;

import java.util.ArrayList;

import org.iplantc.iptol.client.JobConfiguration.Job;

import com.google.gwt.event.shared.GwtEvent;

public class JobSavedEvent extends GwtEvent<JobSavedEventHandler>{

	private ArrayList<Job> jobs;
	
	public static final GwtEvent.Type<JobSavedEventHandler> TYPE = new GwtEvent.Type<JobSavedEventHandler>();

	/**
	 * create a new instance of JobSavedEvent
	 */
	public JobSavedEvent(ArrayList<Job> jobs) {
		this.setJobs(jobs);
	}

	@Override
	public Type<JobSavedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(JobSavedEventHandler handler) {
		handler.onJobSaved(this);
	}

	public void setJobs(ArrayList<Job> jobs) {
		this.jobs = jobs;
	}

	public ArrayList<Job> getJobs() {
		return jobs;
	}

	
	
}
