package org.iplantc.de.client;

import org.iplantc.de.client.events.LogoutEvent;
import com.google.gwt.core.client.GWT;

public class DefaultActionDispatcher implements ActionDispatcher 
{
	//////////////////////////////////////////
	//private variables
	private static DEClientConstants constants = (DEClientConstants)GWT.create(DEClientConstants.class);
	
	//////////////////////////////////////////
	//public methods
	@Override
	public void dispatchAction(String tag) 
	{
		if(tag.equals(constants.logoutTag()))
		{
			EventBus eventbus = EventBus.getInstance();
			LogoutEvent event = new LogoutEvent();
			eventbus.fireEvent(event);
		}
	}
}
