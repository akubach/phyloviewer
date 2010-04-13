package org.iplantc.iptol.client.events.disk.mgmt;

import com.google.gwt.event.shared.EventHandler;

public interface FileMovedEventHandler extends EventHandler 
{
	void onMoved(FileMovedEvent event);
}
