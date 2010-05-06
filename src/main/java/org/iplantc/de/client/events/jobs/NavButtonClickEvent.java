package org.iplantc.de.client.events.jobs;

import org.iplantc.de.client.models.JobStep;

import com.google.gwt.event.shared.GwtEvent;

/**
 * An event to notify the navigation button was clicked.
 * 
 * @author sriram 
 */
public class NavButtonClickEvent extends GwtEvent<NavButtonEventClickEventHandler>
{

	public static final GwtEvent.Type<NavButtonEventClickEventHandler> TYPE = new GwtEvent.Type<NavButtonEventClickEventHandler>();

	private JobStep step;

	public NavButtonClickEvent(JobStep step)
	{
		this.setStep(step);
	}
	
	public void setStep(JobStep step)
	{
		this.step = step;
	}

	public JobStep getStep()
	{
		return step;
	}

	@Override
	protected void dispatch(NavButtonEventClickEventHandler handler)
	{
		handler.onClick(this);
	}

	@Override
	public Type<NavButtonEventClickEventHandler> getAssociatedType()
	{
		return TYPE;
	}

}
