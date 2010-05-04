package org.iplantc.de.client.events.disk.mgmt;

import com.google.gwt.event.shared.EventHandler;

public interface FolderRenamedEventHandler extends EventHandler 
{
	void onRenamed(FolderRenamedEvent event);
}
