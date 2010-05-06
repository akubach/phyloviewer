package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines a handler for GetDataEvents
 * 
 * @see org.iplantc.de.client.events.GetDataEvent
 */
public interface GetDataEventHandler extends EventHandler
{
	void onGet(GetDataEvent event);
}
