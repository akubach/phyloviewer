package org.iplantc.iptol.client.views;

import org.iplantc.iptol.client.ApplicationLayout;
import org.iplantc.iptol.client.dialogs.panels.LoginPanel;
import org.iplantc.iptol.client.services.FolderServices;
import org.iplantc.iptol.client.views.widgets.DataBrowserTree;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class DefaultWorkspaceView extends View 
{
	//////////////////////////////////////////
	//private variables
	private ApplicationLayout layout;
	
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
	private void doWorkspaceDisplay(String idWorkspace)
	{
		layout.displaySystemButtons(true);
		
		DataBrowserTree dataBrowserTree = new DataBrowserTree(idWorkspace,eventbus);
		dataBrowserTree.assembleView();
		
		layout.replaceWestPanel(dataBrowserTree);
		layout.replaceCenterPanel(new PerspectiveView(idWorkspace,eventbus));
		layout.hideRegion(LayoutRegion.EAST);
	}
	
	//////////////////////////////////////////
	private String parseWorkspaceId(String json)
	{	
		JSONObject obj = (JSONObject)JSONParser.parse(json);
		
		return obj.get("workspaceId").isString().stringValue();
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
	public void displayWorkspace(String username) 
	{
		FolderServices.getUserInfo(username,new AsyncCallback<String>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				//TODO: handle failure
			}

			@Override
			public void onSuccess(String result)
			{
				String id = parseWorkspaceId(result);
				doWorkspaceDisplay(id);
			}
		});
	}
}
