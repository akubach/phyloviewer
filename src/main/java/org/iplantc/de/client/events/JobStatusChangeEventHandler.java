package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines a handler for JobStatusChangeEvents.
 *  
 * @see org.iplantc.de.client.events.JobStatusChangeEvent
 */
public interface JobStatusChangeEventHandler extends EventHandler
{
	void onStatusChange(JobStatusChangeEvent event);
}
