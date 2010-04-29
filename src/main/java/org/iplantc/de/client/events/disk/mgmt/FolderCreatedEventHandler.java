package org.iplantc.de.client.events.disk.mgmt;

import com.google.gwt.event.shared.EventHandler;

public interface FolderCreatedEventHandler extends EventHandler 
{
	void onCreated(FolderCreatedEvent event);
}
