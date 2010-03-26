package org.iplantc.iptol.client.events.disk.mgmt;

import com.google.gwt.event.shared.EventHandler;

public interface DiskResourceDeletedEventHandler extends EventHandler 
{
	void onDeleted(DiskResourceDeletedEvent event);
}
