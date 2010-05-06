package org.iplantc.de.client.events.disk.mgmt;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines a handler for FileUploadEvents. 
 * 
 * @see org.iplantc.de.client.events.disk.mgmt.FileUploadEvent
 */
public interface FileUploadedEventHandler extends EventHandler
{
	void onUploaded(FileUploadedEvent event);
}
