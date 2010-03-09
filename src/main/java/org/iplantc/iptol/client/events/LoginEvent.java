package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class LoginEvent extends GwtEvent<LoginEventHandler> 
{
	//////////////////////////////////////////
	//protected variables
	protected String username = new String();
	protected String password = new String();
	
	//////////////////////////////////////////
	//type
	public static final GwtEvent.Type<LoginEventHandler> TYPE = new GwtEvent.Type<LoginEventHandler>();

	//////////////////////////////////////////
	//constructor
	public LoginEvent(String username,String password)
	{
		if(username != null)
		{
			this.username = username.trim();
		}
		
		if(password != null)
		{
			this.password = password.trim();
		}
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void dispatch(LoginEventHandler handler) 
	{		
		handler.onLogin(this);		
	}
	
	//////////////////////////////////////////
	//public methods
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<LoginEventHandler> getAssociatedType() 
	{
		return TYPE;
	}

	//////////////////////////////////////////
	public String getUsername()
	{
		return username;
	}

	//////////////////////////////////////////
	public String getPassword()
	{
		return password;
	}
	
	//////////////////////////////////////////
	public String getHistoryToken()
	{
		return "workspace";
	}	
}
