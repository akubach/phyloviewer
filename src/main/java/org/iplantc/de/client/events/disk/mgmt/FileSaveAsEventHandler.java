package org.iplantc.de.client.events.disk.mgmt;

import com.google.gwt.event.shared.EventHandler;

public interface FileSaveAsEventHandler extends EventHandler 
{
	void onSaved(FileSaveAsEvent event);
}
