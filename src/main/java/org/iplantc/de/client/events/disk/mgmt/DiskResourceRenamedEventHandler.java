package org.iplantc.de.client.events.disk.mgmt;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines a handler for DiskResourceRenamedEvents.
 * 
 * @see org.iplantc.de.client.events.disk.mgmt.DiskResourceRenamedEvent
 */
public interface DiskResourceRenamedEventHandler extends EventHandler
{
	void onFolderRenamed(DiskResourceRenamedEvent event);

	void onFileRenamed(DiskResourceRenamedEvent event);
}
