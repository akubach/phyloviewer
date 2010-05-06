package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines a handler for JobSavedEvents
 * 
 * @see org.iplantc.de.client.events.JobSavedEvent
 */
public interface JobSavedEventHandler extends EventHandler
{

	void onJobSaved(JobSavedEvent jse);

}
