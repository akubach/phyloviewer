package org.iplantc.iptol.client.presentation;

import org.iplantc.iptol.client.views.DefaultWorkspaceView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;

public class WorkspacePresenter extends Presenter 
{
	//////////////////////////////////////////
	//private variables
	private boolean firstPass = true;
	
	//////////////////////////////////////////
	//constructor
	public WorkspacePresenter(final HandlerManager eventbus) 
	{
		super(new DefaultWorkspaceView(eventbus));	
	}	

	//////////////////////////////////////////
	//private methods
	private void doLoginDisplay()
	{
		GWT.runAsync(new RunAsyncCallback() 
		{
			@Override
			public void onSuccess() 
			{
				DefaultWorkspaceView workspaceView = (DefaultWorkspaceView)view;
				
				workspaceView.displayLoginScreen();		
			}
			
			@Override
			public void onFailure(Throwable reason) 
			{
				// TODO: handle failure				
			}
		});	
	}
	
	//////////////////////////////////////////
	private void doWelcomeDisplay(final String username)
	{
		
	}
	
	//////////////////////////////////////////
	private void doPerspectiveChange(final String params)
	{	
	}
	
	private void doWorkspace(final String params) 
	{
		GWT.runAsync(new RunAsyncCallback() 
		{
			@Override
			public void onSuccess() 
			{
				DefaultWorkspaceView workspaceView = (DefaultWorkspaceView)view;
				workspaceView.displayWorkspace(params);
			}
			
			@Override
			public void onFailure(Throwable reason) 
			{
				// TODO: handle failure				
			}
		});	
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void updateView(final String cmd,final String params) 
	{
		if(cmd.equals("login") || cmd.equals("logout"))
		{				
			doLoginDisplay();			
		}
		else if(cmd.equals("welcome"))
		{
			doWelcomeDisplay(params);
		}		
		else if(cmd.equals("change_perspective"))
		{
			doPerspectiveChange(params);
		} 
		else if(cmd.equals("workspace")) 
		{
			doWorkspace(params);
		}
		
		//if this is our first pass... we need to add this to our root panel
		if(firstPass)
		{
			firstPass = false;
			RootPanel.get().add(view.getDisplayWidget());
		}
	}	
}
