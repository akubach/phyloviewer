package org.iplantc.iptol.client.JobConfiguration;



import com.google.gwt.event.shared.GwtEvent;
/**
 * An event that notifies user has clicked previous button in the toolbar.
 * @author sriram
 *
 */
public class JobToolBarPrevClickEvent extends GwtEvent<JobToolBarPrevClickEventHandler> {

	public static final GwtEvent.Type<JobToolBarPrevClickEventHandler> TYPE = new GwtEvent.Type<JobToolBarPrevClickEventHandler>();

	private JobStep step;
	
	public void setStep(JobStep step) {
		this.step = step;
	}


	public JobStep getStep() {
		return step;
	}
	
	public JobToolBarPrevClickEvent(JobStep step) {
		this.setStep(step);	
	}
	
	
	@Override
	protected void dispatch(JobToolBarPrevClickEventHandler handler) {
		handler.onPrevClick(this);
	}

	@Override
	public Type<JobToolBarPrevClickEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

}
