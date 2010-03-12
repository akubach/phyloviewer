package org.iplantc.iptol.client.events.disk.mgmt;

import com.google.gwt.event.shared.EventHandler;

public interface FolderDeletedEventHandler extends EventHandler 
{
	void onDeleted(FolderDeletedEvent event);
}
