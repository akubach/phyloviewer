package org.iplantc.de.client.events.disk.mgmt;

import com.google.gwt.event.shared.EventHandler;

public interface FileUploadedEventHandler extends EventHandler 
{
	void onUploaded(FileUploadedEvent event);
}
