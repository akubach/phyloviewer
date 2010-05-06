package org.iplantc.de.client.events.disk.mgmt;

import com.google.gwt.event.shared.EventHandler;

public interface DiskResourceRenamedEventHandler extends EventHandler 
{
	void onFolderRenamed(DiskResourceRenamedEvent event);
	void onFileRenamed(DiskResourceRenamedEvent event);
}
