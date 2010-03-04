package org.iplantc.iptol.client;


import org.iplantc.iptol.client.events.LoginEvent;
import org.iplantc.iptol.client.events.LoginEventHandler;
import org.iplantc.iptol.client.events.LogoutEvent;
import org.iplantc.iptol.client.events.LogoutEventHandler;
import org.iplantc.iptol.client.presentation.Presenter;
import org.iplantc.iptol.client.presentation.WorkspacePresenter;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;

public class PresentationManager implements ValueChangeHandler<String>
{
	//////////////////////////////////////////
	//private variables
	private String cmd = new String();
	private String params = new String();
	private Presenter presenter = null;
	private HandlerManager eventbus = new HandlerManager(null);
		
	//////////////////////////////////////////
	//constructor
	public PresentationManager() 
	{
		// Add history listener
	    History.addValueChangeHandler(this);
	    
	    presenter = new WorkspacePresenter(eventbus);	
	    initEventHandlers();
	}

	//////////////////////////////////////////	
	//private methods	
	private void initEventHandlers()
	{
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
				handleToken(event.getHistoryToken());								
			}
        });		
	}
	
	//////////////////////////////////////////
	private void doLogin(final LoginEvent event)
	{
		//TODO: call service
		handleToken(event.getHistoryToken());
		
		/*
		RPCFacade.login(event.getUsername(),event.getPassword(),new AsyncCallback<String>()
		{
			@Override
			public void onSuccess(String result) 
			{
				if(result.equals("success"))
				{
					handleToken(event.getHistoryToken());
				}
			}

			@Override
			public void onFailure(Throwable caught) 
			{
				// TODO Auto-generated method stub			
			}
		});*/	
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

