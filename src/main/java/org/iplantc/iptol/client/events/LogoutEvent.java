package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class LogoutEvent extends GwtEvent<LogoutEventHandler> 
{
	//////////////////////////////////////////
	//type
	public static final GwtEvent.Type<LogoutEventHandler> TYPE = new GwtEvent.Type<LogoutEventHandler>();
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void dispatch(LogoutEventHandler handler)
	{
		handler.onLogout(this);	
	}

	//////////////////////////////////////////
	//public methods
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<LogoutEventHandler> getAssociatedType() 
	{
		return TYPE;
	}
	
	//////////////////////////////////////////
	public String getHistoryToken()
	{
		return "logout";
	}
}
