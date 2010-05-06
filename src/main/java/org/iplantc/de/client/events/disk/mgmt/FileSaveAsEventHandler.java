package org.iplantc.de.client.events.disk.mgmt;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines a handler for FileSaveAsEvents.
 * 
 * @see org.iplantc.de.client.events.disk.mgmt.FileSaveAsEvent
 */
public interface FileSaveAsEventHandler extends EventHandler
{
	void onSaved(FileSaveAsEvent event);
}
