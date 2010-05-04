package org.iplantc.de.client.JobConfiguration;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author sriram An event to notify the navigation button was clicked
 */
public class NavButtonClickEvent extends
		GwtEvent<NavButtonEventClickEventHandler> {

	public static final GwtEvent.Type<NavButtonEventClickEventHandler> TYPE = new GwtEvent.Type<NavButtonEventClickEventHandler>();

	private JobStep step;

	public void setStep(JobStep step) {
		this.step = step;
	}

	public JobStep getStep() {
		return step;
	}

	public NavButtonClickEvent(JobStep step) {
		this.setStep(step);
	}

	@Override
	protected void dispatch(NavButtonEventClickEventHandler handler) {
		handler.onClick(this);
	}

	@Override
	public Type<NavButtonEventClickEventHandler> getAssociatedType() {
		return TYPE;
	}

}
