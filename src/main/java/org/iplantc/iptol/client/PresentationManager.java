package org.iplantc.iptol.client;


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
	private String token = new String();
	private String cmd = new String();
	private String params = new String();
	
	private Presenter presenter = null;
	private HandlerManager eventbus = new HandlerManager(null);
	
	private final String[] validCommands = 
	{			
		"login",
		"welcome",
		"change_perspective",
		"workspace"
	};
		
	//////////////////////////////////////////
	//constructor
	public PresentationManager() 
	{
		// Add history listener
	    History.addValueChangeHandler(this);
	    
	    presenter = new WorkspacePresenter(eventbus);	    
	}

	//////////////////////////////////////////	
	//private methods
	private boolean isValidCommand(String cmd)
	{
		boolean ret = false;   //assume failure
		
		if(cmd != null)
		{
			//loop through our valid commands looking for a match
			for(int i = 0;i < validCommands.length;i++)
			{
				if(validCommands[i].equals(cmd))
				{
					//success!
					ret = true;
					break;
				}
			}
		}
		
		return ret;
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
	private void processNewCommand(int idxParams)
	{
		cmd = token.substring(0,idxParams);
		
		params = (idxParams == token.length()) ? "" : token.substring(idxParams + 1,token.length());

		displayPresenter();	
	}
			
	//////////////////////////////////////////
	private boolean isValidToken(String token)
	{
		return (token != null && token.length() > 0 && !this.token.equals(token));
	}
			
	//public methods
	public boolean handleToken(String token)
	{
		return handleToken(token,true);
	}
	
	//////////////////////////////////////////
	public boolean handleToken(String token,boolean addToHistory)
	{		
		if(isValidToken(token))
		{
			//save off our token
			this.token = token;
			
			int idx = token.indexOf("|");
			
			//if we do not have a vertical bar, we may still have a valid token (albeit one without parameters)
			if(idx == -1)
			{
				idx = token.length();
			}			
					
			//parse out the command and parameters portion of the token
			String cmd = token.substring(0,idx);
				
			if(isValidCommand(cmd))
			{				
				if(addToHistory)
				{
					//save our token in the history
					History.newItem(token);					
				}
						
				processNewCommand(idx);
				
			}
			
			return true;
		}
		
		return false;
	}
	
	//////////////////////////////////////////
	@Override
	public void onValueChange(ValueChangeEvent<String> event) 
	{
		handleToken((String)event.getValue(),false);
	}
}

