package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines a handler for UserEvents. 
 * 
 * @see org.iplantc.de.client.events.UserEvent
 */
public interface UserEventHandler extends EventHandler
{
	void onEvent(UserEvent event);
}
