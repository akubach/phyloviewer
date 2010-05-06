package org.iplantc.de.client.events.jobs;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines a handler for EnableStepEvents
 * 
 * @see org.iplantc.de.client.events.jobs.EnableStepEvent
 * @author sriram
 * 
 */
public interface EnableStepEventHandler extends EventHandler
{

	void enableStep(EnableStepEvent es);
}
