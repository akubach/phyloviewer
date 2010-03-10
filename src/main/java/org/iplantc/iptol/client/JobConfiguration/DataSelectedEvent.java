package org.iplantc.iptol.client.JobConfiguration;

import com.google.gwt.event.shared.GwtEvent;
/**
 * An event to notify that data is selected at each step.
 * @author sriram
 *
 */
public class DataSelectedEvent extends GwtEvent<DataSelectedEventHandler> {

	private boolean selected;
	private int step;
	
	public static final GwtEvent.Type<DataSelectedEventHandler> TYPE = new GwtEvent.Type<DataSelectedEventHandler>();
	
	public DataSelectedEvent(int step, boolean selected) {
		this.setSelected(selected);
		this.setStep(step);
	}
	
	@Override
	protected void dispatch(DataSelectedEventHandler handler) {
		handler.onDataSelected(this);
	}

	@Override
	public Type<DataSelectedEventHandler> getAssociatedType() {
		return TYPE;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getStep() {
		return step;
	}

}
