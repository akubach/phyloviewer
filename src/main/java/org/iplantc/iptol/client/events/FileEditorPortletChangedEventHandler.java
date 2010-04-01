package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface FileEditorPortletChangedEventHandler extends EventHandler 
{
	void onChanged(FileEditorPortletChangedEvent event);
}
