package org.iplantc.iptol.client.JobConfiguration;

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
	private String createdOn;
	
	public JobToolBarSaveClickEvent(String jobname,String createdon) {
		this.jobName = jobname;
		this.createdOn = createdon;
	}

	@Override
	public Type<JobToolBarSaveClickEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(JobToolBarSaveClickEventHandler handler) {
		handler.onSave(this);
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobName() {
		return jobName;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getCreatedOn() {
		return createdOn;
	}

}
