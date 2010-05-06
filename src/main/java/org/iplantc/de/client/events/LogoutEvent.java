package org.iplantc.de.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event represents a user logging out of the system. 
 */
public class LogoutEvent extends GwtEvent<LogoutEventHandler>
{
	// ////////////////////////////////////////
	// type
	public static final GwtEvent.Type<LogoutEventHandler> TYPE = new GwtEvent.Type<LogoutEventHandler>();

	// ////////////////////////////////////////
	// protected methods
	@Override
	protected void dispatch(LogoutEventHandler handler)
	{
		handler.onLogout(this);
	}

	// ////////////////////////////////////////
	// public methods
	@Override
	public Type<LogoutEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	// ////////////////////////////////////////
	public String getHistoryToken()
	{
		return "logout";
	}
}
