package org.iplantc.de.client.events.disk.mgmt;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines a handler for DiskResourceDeletedEvents
 *
 * @see org.iplantc.de.client.events.disk.mgmt.DiskResourceDeletedEvent
 */
public interface DiskResourceDeletedEventHandler extends EventHandler
{
	void onDeleted(DiskResourceDeletedEvent event);
}
