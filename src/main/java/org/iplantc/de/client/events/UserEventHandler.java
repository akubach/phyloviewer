package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface UserEventHandler extends EventHandler 
{
	void onEvent(UserEvent event);
}
