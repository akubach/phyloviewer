package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines a handler for FileEditorWindowDirtyEvents
 * 
 * @see org.iplantc.de.client.events.FileEditorWindowDirtyEvent
 */
public interface FileEditorWindowDirtyEventHandler extends EventHandler
{
	void onClean(FileEditorWindowDirtyEvent event);

	void onDirty(FileEditorWindowDirtyEvent event);
}
