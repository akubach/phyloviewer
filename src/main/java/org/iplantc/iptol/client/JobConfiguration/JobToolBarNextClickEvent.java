package org.iplantc.iptol.client.JobConfiguration;

import com.google.gwt.event.shared.GwtEvent;

/**
 * An event that notifies user has clicked next button in the toolbar.
 * @author sriram
 *
 */

public class JobToolBarNextClickEvent extends GwtEvent<JobToolBarNextClickEventHandler> {

	public static final GwtEvent.Type<JobToolBarNextClickEventHandler> TYPE = new GwtEvent.Type<JobToolBarNextClickEventHandler>();
	
	private JobStep step;
	
	public void setStep(JobStep step) {
		this.step = step;
	}

	public JobStep getStep() {
		return step;
	}
	
	public JobToolBarNextClickEvent(JobStep step) {
		this.setStep(step);
	}
	
	
	@Override
	protected void dispatch(JobToolBarNextClickEventHandler handler) {
		handler.onNextClick(this);
	}

	@Override
	public Type<JobToolBarNextClickEventHandler> getAssociatedType() {
		return TYPE;
	}

}
