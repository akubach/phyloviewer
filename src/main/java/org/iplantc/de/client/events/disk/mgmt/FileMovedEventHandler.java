package org.iplantc.de.client.events.disk.mgmt;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines a handler for FileMovedEvents.
 *
 * @see org.iplantc.de.client.events.disk.mgmt.FileMovedEvent
 */
public interface FileMovedEventHandler extends EventHandler
{
	void onMoved(FileMovedEvent event);
}
