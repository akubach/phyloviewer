package org.iplantc.iptol.client.events.disk.mgmt;

import com.google.gwt.event.shared.EventHandler;

public interface FileRenamedEventHandler extends EventHandler 
{
	void onRenamed(FileRenamedEvent event);
}
