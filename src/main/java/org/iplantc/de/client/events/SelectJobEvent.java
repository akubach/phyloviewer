package org.iplantc.de.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event represents a user selecting a job. 
 */
public class SelectJobEvent extends GwtEvent<SelectJobEventHandler>
{
	// ////////////////////////////////////////
	// private variables
	private String idJob;

	// ////////////////////////////////////////
	// type
	public static final GwtEvent.Type<SelectJobEventHandler> TYPE = new GwtEvent.Type<SelectJobEventHandler>();

	// ////////////////////////////////////////
	// constructor
	public SelectJobEvent(String idJob)
	{
		this.idJob = idJob;
	}

	// ////////////////////////////////////////
	// protected methods
	@Override
	protected void dispatch(SelectJobEventHandler handler)
	{
		handler.onSelect(this);
	}

	// ////////////////////////////////////////
	// public methods
	@Override
	public GwtEvent.Type<SelectJobEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	// ////////////////////////////////////////
	public String getJobId()
	{
		return idJob;
	}
}
