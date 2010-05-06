package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines a handler for ViewDataEvents
 * 
 * @see org.iplantc.de.client.events.ViewDataEvent
 */
public interface ViewDataEventHandler extends EventHandler
{
	void onView(ViewDataEvent event);
}
