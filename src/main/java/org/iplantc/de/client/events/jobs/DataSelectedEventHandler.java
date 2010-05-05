package org.iplantc.de.client.events.jobs;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author sriram Event handler for DataSelectedEvent
 */
public interface DataSelectedEventHandler extends EventHandler {

	void onDataSelected(DataSelectedEvent dse);

}
