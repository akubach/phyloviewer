package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface FileEditorWindowDirtyEventHandler extends EventHandler 
{
	void onClean(FileEditorWindowDirtyEvent event);
	void onDirty(FileEditorWindowDirtyEvent event);
}
