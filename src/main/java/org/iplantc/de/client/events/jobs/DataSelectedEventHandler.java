package org.iplantc.de.client.events.jobs;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines a handler for DataSelectedEvents
 * 
 * @see org.iplantc.de.client.events.jobs.DataSelectedEvent
 * @author sriram
 */
public interface DataSelectedEventHandler extends EventHandler
{

	void onDataSelected(DataSelectedEvent dse);

}
