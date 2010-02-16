package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface FolderEventHandler extends EventHandler 
{
	void onRename(FolderEvent event);
	
	void onCreate(FolderEvent event);
}
