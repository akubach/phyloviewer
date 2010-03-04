package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface LogoutEventHandler extends EventHandler 
{
	void onLogout(LogoutEvent event); 
}
