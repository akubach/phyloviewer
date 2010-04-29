package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface FileEditorWindowSavedEventHandler extends EventHandler 
{
	void onSaved(FileEditorWindowSavedEvent event);
}
