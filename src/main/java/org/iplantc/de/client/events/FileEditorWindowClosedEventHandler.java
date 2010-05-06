package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines a handler for FileEditorWindowClosedEvents.
 *
 * @see org.iplantc.de.client.events.FileEditorWindowClosedEvent
 */
public interface FileEditorWindowClosedEventHandler extends EventHandler
{
	public void onClosed(FileEditorWindowClosedEvent event);
}
