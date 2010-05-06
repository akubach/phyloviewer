package org.iplantc.de.client.events.jobs;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event represents a step in the job configuration process need to be enabled.
 * 
 * @author sriram
 */
public class EnableStepEvent extends GwtEvent<EnableStepEventHandler>
{
	public static final GwtEvent.Type<EnableStepEventHandler> TYPE = new GwtEvent.Type<EnableStepEventHandler>();
	
	private int stepno;
	private boolean enable;

	/**
	 * create a new EnableStepEvent
	 * 
	 * @param stepno the step number
	 */
	public EnableStepEvent(int stepno, boolean enable)
	{
		this.setStepno(stepno);
		this.setEnable(enable);
	}

	@Override
	protected void dispatch(EnableStepEventHandler handler)
	{
		handler.enableStep(this);
	}

	@Override
	public Type<EnableStepEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	public void setStepno(int stepno)
	{
		this.stepno = stepno;
	}

	public int getStepno()
	{
		return stepno;
	}

	public void setEnable(boolean enable)
	{
		this.enable = enable;
	}

	public boolean isEnable()
	{
		return enable;
	}

}
