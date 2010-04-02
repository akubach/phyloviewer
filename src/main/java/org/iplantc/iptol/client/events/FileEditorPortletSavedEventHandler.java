package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface FileEditorPortletSavedEventHandler extends EventHandler 
{
	void onSaved(FileEditorPortletSavedEvent event);
}
