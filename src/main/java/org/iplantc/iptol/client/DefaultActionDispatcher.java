package org.iplantc.iptol.client;

import org.iplantc.iptol.client.events.LogoutEvent;
import com.google.gwt.core.client.GWT;

public class DefaultActionDispatcher implements ActionDispatcher 
{
	//////////////////////////////////////////
	//private variables
	private static IptolClientConstants constants = (IptolClientConstants)GWT.create(IptolClientConstants.class);
	
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
