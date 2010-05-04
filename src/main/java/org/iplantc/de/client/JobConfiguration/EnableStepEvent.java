package org.iplantc.de.client.JobConfiguration;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author sriram An event to notify that a step must be enabled
 */
public class EnableStepEvent extends GwtEvent<EnableStepEventHandler> {

	private int stepno;
	private boolean enable;

	public static final GwtEvent.Type<EnableStepEventHandler> TYPE = new GwtEvent.Type<EnableStepEventHandler>();

	/**
	 * create a new EnableStepEvent
	 * 
	 * @param stepno
	 *            the step number
	 */
	public EnableStepEvent(int stepno, boolean enable) {
		this.setStepno(stepno);
		this.setEnable(enable);
	}

	@Override
	protected void dispatch(EnableStepEventHandler handler) {
		handler.enableStep(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<EnableStepEventHandler> getAssociatedType() {
		return TYPE;
	}

	public void setStepno(int stepno) {
		this.stepno = stepno;
	}

	public int getStepno() {
		return stepno;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public boolean isEnable() {
		return enable;
	}

}
