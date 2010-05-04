package org.iplantc.de.client;

import org.iplantc.de.client.events.LogoutEvent;
import org.iplantc.de.client.events.LogoutEventHandler;
import org.iplantc.de.client.presentation.Presenter;
import org.iplantc.de.client.presentation.WorkspacePresenter;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;

public class HistoryManager implements ValueChangeHandler<String>
{
	//////////////////////////////////////////
	//private constants
	static private final String SHIBBOLETH_LOGOUT_URL = "/Shibboleth.sso/Logout";

	//////////////////////////////////////////
	//private variables
	private String cmd = new String();
	private String params = new String();
	private Presenter presenter = null;

	//////////////////////////////////////////
	//constructor
	public HistoryManager() 
	{
		// Add history listener
	    History.addValueChangeHandler(this);
	    
	    presenter = new WorkspacePresenter();	
	    initEventHandlers();
	}

	//////////////////////////////////////////	
	//private methods	
	private void initEventHandlers()
	{
		EventBus eventbus = EventBus.getInstance();
		     
		//register logout handler
		eventbus.addHandler(LogoutEvent.TYPE,new LogoutEventHandler()
        {        	
			@Override
			public void onLogout(LogoutEvent event) 
			{
				resetEventHandlers();
				handleToken(event.getHistoryToken());
				redirectToLogoutPage();
			}
        });		
	}
	
	//////////////////////////////////////////
	private void resetEventHandlers()
	{		
		EventBus eventbus = EventBus.getInstance();
		eventbus.clearHandlers();
		
		initEventHandlers();
	}
	
	//////////////////////////////////////////
	private void displayPresenter()
	{
		if(presenter != null)
		{
			presenter.display(cmd,params);
		}
	}
				
	//////////////////////////////////////////
	private boolean isValidToken(String token)
	{
		return (token != null && token.length() > 0);
	}
			
	//////////////////////////////////////////
	//public methods	
	public void processCommand(String token)
	{
		int idx = token.indexOf("|");
		
		//if we do not have a vertical bar, we may still have a valid token (albeit one without parameters)
		if(idx == -1)
		{
			idx = token.length();
		}
		
		cmd = token.substring(0,idx);
		params = (idx == token.length()) ? "" : token.substring(idx + 1,token.length());

		displayPresenter();	
	}
	
	//////////////////////////////////////////
	@Override
	public void onValueChange(ValueChangeEvent<String> event) 
	{
		processCommand((String)event.getValue());	
	}
	
	//////////////////////////////////////////
	public boolean handleToken(String token)
	{		
		boolean ret = false; //assume failure
		
		if(isValidToken(token))
		{
			History.newItem(token);		
			
			ret = true;
		}
		
		return ret;
	}
	
	//////////////////////////////////////////
	private void redirectToLogoutPage()
	{
		Window.Location.assign("https://" + Window.Location.getHost() + SHIBBOLETH_LOGOUT_URL);
	}
}
