package org.iplantc.iptol.client.views;

import org.iplantc.iptol.client.ApplicationLayout;
import org.iplantc.iptol.client.dialogs.panels.LoginPanel;
import org.iplantc.iptol.client.view.widgets.DataBrowserTree;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.event.shared.HandlerManager;
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
	//private methods
	private void doLoginScreenDisplay()
	{	
		ContentPanel view = new ContentPanel();
		
		view.setScrollMode(Scroll.AUTOX);		
		view.add(new LoginPanel(eventbus));		
	    view.setBodyBorder(false);
	    view.setHeaderVisible(false);	    
	        
	    layout.replaceCenterPanel(view);
	    
	    layout.displaySystemButtons(false);
		layout.hideRegion(LayoutRegion.EAST);
		layout.hideRegion(LayoutRegion.WEST);
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
		doLoginScreenDisplay();
	}
	
	//////////////////////////////////////////
	public void displayWelcomeScreen(String username)
	{
	}
	
	//////////////////////////////////////////
	public void displayDefaultTab(String username,final int idxPerspective)
	{	
	}
	
	//////////////////////////////////////////
	public void displayWorkspace() 
	{
		layout.displaySystemButtons(true);
		
		DataBrowserTree dataBrowserTree = new DataBrowserTree(eventbus);
		dataBrowserTree.assembleView();
		
		layout.replaceWestPanel(dataBrowserTree);
		layout.hideRegion(LayoutRegion.EAST);
		layout.hideRegion(LayoutRegion.CENTER);	
	}
}
