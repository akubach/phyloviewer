package org.iplantc.iptol.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.History;

public class Controller implements EntryPoint 
{
	public void onModuleLoad() 
	{
		PresentationManager mgr = new PresentationManager();
		String token = History.getToken();
		
		if(token == null || token.length() == 0)
		{	// "login" is being skipped because Shibboleth is handling that 
			// now, so we skip into the workspace assuming our gatekeeper   
			// has let them through. 
			mgr.handleToken("workspace");			
		}
		else
		{
			mgr.processCommand(token);
		}	
	}
}
