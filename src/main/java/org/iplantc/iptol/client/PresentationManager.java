package org.iplantc.iptol.client;

import org.iplantc.iptol.client.events.LoginEvent;
import org.iplantc.iptol.client.events.LoginEventHandler;
import org.iplantc.iptol.client.events.LogoutEvent;
import org.iplantc.iptol.client.events.LogoutEventHandler;
import org.iplantc.iptol.client.presentation.Presenter;
import org.iplantc.iptol.client.presentation.WorkspacePresenter;
import org.iplantc.iptol.client.services.ServiceCallWrapper;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PresentationManager implements ValueChangeHandler<String>
{
	//////////////////////////////////////////
	//private variables
	private String cmd = new String();
	private String params = new String();
	private Presenter presenter = null;

	//////////////////////////////////////////
	//constructor
	public PresentationManager() 
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
		
		//register login handler
		eventbus.addHandler(LoginEvent.TYPE,new LoginEventHandler()
        {        	
			@Override
			public void onLogin(LoginEvent event) 
			{
				doLogin(event);												
			}
        });
        
		//register logout handler
		eventbus.addHandler(LogoutEvent.TYPE,new LogoutEventHandler()
        {        	
			@Override
			public void onLogout(LogoutEvent event) 
			{				
				resetEventHandlers();
				handleToken(event.getHistoryToken());
			}
        });		
	}
	
	//////////////////////////////////////////
	private void doLogin(final LoginEvent event)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST,"http://" + Window.Location.getHostName() + ":14444/login","{\"userId\":\"" + event.getUsername() + "\"}");
		
		IptolServiceFacade.getInstance().getServiceData(wrapper,new AsyncCallback<String>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				handleToken("login");
			}

			@Override
			public void onSuccess(String result)
			{
				//if we succeed in logging in - handle token
				handleToken(event.getHistoryToken() + "|" + result);
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
}

