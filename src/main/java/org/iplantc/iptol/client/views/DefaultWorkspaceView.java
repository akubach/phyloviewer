package org.iplantc.iptol.client.views;


import org.iplantc.iptol.client.ApplicationLayout;
import org.iplantc.iptol.client.DataBrowserTree;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class DefaultWorkspaceView extends View 
{
	//////////////////////////////////////////
	//protected variables
	protected ApplicationLayout layout;

	//////////////////////////////////////////
	//constructor
	public DefaultWorkspaceView(final HandlerManager eventbus) 
	{
		super(eventbus);
		layout = new ApplicationLayout(eventbus);
		layout.assembleLayout();
	}

	//////////////////////////////////////////
	//public methods
	@Override
	public Widget getDisplayWidget() 
	{
		return layout;
	}
	
	//////////////////////////////////////////
	public void displayLoginScreen()
	{
		
	}
	
	//////////////////////////////////////////
	public void displayWelcomeScreen(String username)
	{
		
	}
	
	//////////////////////////////////////////
	public void displayDefaultTab(String username,final int idxPerspective)
	{	
	}
	
	public void displayWorkspace() {
		DataBrowserTree dataBrowserTree = new DataBrowserTree(eventbus);
		dataBrowserTree.assembleView();
		layout.updateRegion(LayoutRegion.WEST, dataBrowserTree);
		RootPanel.get().add(layout);
		layout.hideRegion(LayoutRegion.EAST);
	}
}
