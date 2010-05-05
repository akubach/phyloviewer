package org.iplantc.de.client.events.jobs;

import com.google.gwt.event.shared.GwtEvent;

/**
 * An event to notify user has clicked cancel. Currently not used.
 * 
 * @author sriram
 * 
 */
public class JobToolBarSaveClickEvent extends
		GwtEvent<JobToolBarSaveClickEventHandler> {

	public static final GwtEvent.Type<JobToolBarSaveClickEventHandler> TYPE = new GwtEvent.Type<JobToolBarSaveClickEventHandler>();

	private String jobName;
	
	public JobToolBarSaveClickEvent(String jobname) {
		setJobName(jobname);
	}

	@Override
	public Type<JobToolBarSaveClickEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(JobToolBarSaveClickEventHandler handler) {
		handler.onSave(this);
	}

	public void setJobName(String name) {
		this.jobName = name;
	}

	public String getJobName() {
		return jobName;
	}
}
