package org.iplantc.de.client.events.disk.mgmt;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines a handler for FolderCreatedEvents. 
 * 
 * @see org.iplantc.de.client.events.disk.mgmt.FolderCreatedEvent
 */
public interface FolderCreatedEventHandler extends EventHandler
{
	void onCreated(FolderCreatedEvent event);
}
