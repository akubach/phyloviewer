package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines a handler for LogoutEvents. 
 * 
 * @see org.iplantc.de.client.events.LogoutEvent
 */
public interface LogoutEventHandler extends EventHandler
{
	void onLogout(LogoutEvent event);
}
