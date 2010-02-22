package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface FolderEventHandler extends EventHandler 
{	
	void onCreate(FolderEvent event);
	
	void onRename(FolderEvent event);
	
	void onDelete(FolderEvent event);	
}
