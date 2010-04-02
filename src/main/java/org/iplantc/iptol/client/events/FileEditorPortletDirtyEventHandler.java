package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface FileEditorPortletDirtyEventHandler extends EventHandler 
{
	void onDirty(FileEditorPortletDirtyEvent event);
}
