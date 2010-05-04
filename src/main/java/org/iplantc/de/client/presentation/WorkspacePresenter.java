package org.iplantc.de.client.presentation;

import org.iplantc.de.client.DEErrorStrings;
import org.iplantc.de.client.ErrorHandler;
import org.iplantc.de.client.views.DefaultWorkspaceView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class WorkspacePresenter extends Presenter 
{
	//////////////////////////////////////////
	//private variables
	private boolean firstPass = true;
	private DEErrorStrings errorStrings = (DEErrorStrings) GWT.create(DEErrorStrings.class);
	
	//////////////////////////////////////////
	//constructor
	public WorkspacePresenter() 
	{
		super(new DefaultWorkspaceView());	
	}	

	//////////////////////////////////////////
	private void doWelcomeDisplay(final String username)
	{
		
	}
	
	//////////////////////////////////////////
	private void doPerspectiveChange(final String params)
	{	
	}
	
	//////////////////////////////////////////
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
			Window.Location.assign(Window.Location.getPath() + "/jsp/login.jsp");
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
