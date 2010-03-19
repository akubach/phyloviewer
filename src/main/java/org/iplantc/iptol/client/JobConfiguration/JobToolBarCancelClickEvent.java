package org.iplantc.iptol.client.JobConfiguration;

import com.google.gwt.event.shared.GwtEvent;

/**
 * An event to notify user has clicked cancel. Currently not used.
 * 
 * @author sriram
 * 
 */
public class JobToolBarCancelClickEvent extends
		GwtEvent<JobToolBarCancelClickEventHandler> {

	public static final GwtEvent.Type<JobToolBarCancelClickEventHandler> TYPE = new GwtEvent.Type<JobToolBarCancelClickEventHandler>();

	private JobStep step;

	public void setStep(JobStep step) {
		this.step = step;
	}

	public JobStep getStep() {
		return step;
	}

	public JobToolBarCancelClickEvent(JobStep step) {
		this.setStep(step);
	}

	@Override
	public Type<JobToolBarCancelClickEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	@Override
	protected void dispatch(JobToolBarCancelClickEventHandler handler) {

	}

}
