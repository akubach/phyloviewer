package org.iplantc.de.client.events.jobs;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines a handler for NavButtonEventClickEvents.
 */
public interface NavButtonEventClickEventHandler extends EventHandler
{

	void onClick(NavButtonClickEvent navButton);
}
