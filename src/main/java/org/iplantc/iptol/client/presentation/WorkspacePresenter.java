package org.iplantc.iptol.client.presentation;

import org.iplantc.iptol.client.ErrorHandler;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.views.DefaultWorkspaceView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class WorkspacePresenter extends Presenter 
{
	//////////////////////////////////////////
	//private variables
	private boolean firstPass = true;
	private IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);
	
	//////////////////////////////////////////
	//constructor
	public WorkspacePresenter() 
	{
		super(new DefaultWorkspaceView());	
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
				ErrorHandler.post(errorStrings.loginFailed());		
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
	
	private void doWorkspaceDisplay() 
	{
		GWT.runAsync(new RunAsyncCallback() 
		{
			@Override
			public void onSuccess() 
			{
				DefaultWorkspaceView workspaceView = (DefaultWorkspaceView)view;
				workspaceView.displayWorkspace();
			}
			
			@Override
			public void onFailure(Throwable reason) 
			{
				ErrorHandler.post(errorStrings.unableToBuildWorkspace());			
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
			doWorkspaceDisplay();
		}
		
		//if this is our first pass... we need to add this to our root panel
		if(firstPass)
		{
			firstPass = false;
			RootPanel.get().add(view.getDisplayWidget());
		}
	}	
}
