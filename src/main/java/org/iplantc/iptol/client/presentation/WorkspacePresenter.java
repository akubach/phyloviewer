package org.iplantc.iptol.client.presentation;

import org.iplantc.iptol.client.views.DefaultWorkspaceView;

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
			
	}
	
	//////////////////////////////////////////
	private void doWelcomeDisplay(final String username)
	{
		
	}
	
	//////////////////////////////////////////
	private void doPerspectiveChange(final String params)
	{	
	}
	
	private void doWorkspace(final String params) {
		DefaultWorkspaceView workspaceView = (DefaultWorkspaceView)view;
		workspaceView.displayWorkspace();
	}
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void updateView(final String cmd,final String params) 
	{
		if(cmd.equals("login"))
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
		} else if (cmd.equals("workspace")) {
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
