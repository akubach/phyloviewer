package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines a handler for SelectJobEvents.
 * 
 * @see org.iplantc.de.client.events.SelectJobEvent
 */
public interface SelectJobEventHandler extends EventHandler
{
	void onSelect(SelectJobEvent event);
}
