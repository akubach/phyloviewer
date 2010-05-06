package org.iplantc.de.client.dispatchers;

import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.events.LogoutEvent;

import com.google.gwt.core.client.GWT;

/**
 * Defines the default implementation of an ActionDispatcher. 
 * 
 * Currently, the only action handled is "logout"
 */
public class DefaultActionDispatcher implements ActionDispatcher
{
	// ////////////////////////////////////////
	// private variables
	private static DEClientConstants constants = (DEClientConstants)GWT.create(DEClientConstants.class);

	// ////////////////////////////////////////
	// public methods
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
