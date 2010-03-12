package org.iplantc.iptol.client.events.disk.mgmt;

import com.google.gwt.event.shared.EventHandler;

public interface FileDeletedEventHandler extends EventHandler 
{
	void onDeleted(FileDeletedEvent event);
}
