package org.iplantc.de.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;

public class DiscoveryEnvironment implements EntryPoint 
{
	public void onModuleLoad() 
	{
		setEntryPointTitle();
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

	/**
	 * Set the title element of the root page/entry point. 
	 * 
	 * Enables i18n of the root page.
	 */
	private void setEntryPointTitle() {
		DEDisplayStrings displayStrings = (DEDisplayStrings) GWT.create(DEDisplayStrings.class);
		Window.setTitle(displayStrings.rootTitle());
	}
}
