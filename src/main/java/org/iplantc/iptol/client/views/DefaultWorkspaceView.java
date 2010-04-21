package org.iplantc.iptol.client.views;

import org.iplantc.iptol.client.ApplicationLayout;
import org.iplantc.iptol.client.ErrorHandler;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.dialogs.panels.LoginPanel;
import org.iplantc.iptol.client.services.FolderServices;
import org.iplantc.iptol.client.views.widgets.NavigationMenu;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class DefaultWorkspaceView implements View 
{
	//////////////////////////////////////////
	//private variables
	private ApplicationLayout layout;
	private IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);
	
	//////////////////////////////////////////
	//constructor
	public DefaultWorkspaceView() 
	{	
		layout = new ApplicationLayout();
		layout.assembleLayout();
	}

	//////////////////////////////////////////
	//private methods
	private void doLoginScreenDisplay()
	{	
		ContentPanel view = new ContentPanel();
		
		view.setScrollMode(Scroll.AUTOX);		
		view.add(new LoginPanel());		
	    view.setBodyBorder(false);
	    view.setHeaderVisible(false);	    
	        
	    layout.replaceCenterPanel(view);
	    layout.replaceWestPanel(null);
	    layout.displaySystemButtons(false);		
	}
	
	//////////////////////////////////////////
	private void doWorkspaceDisplay(String idWorkspace)
	{
		layout.displaySystemButtons(true);
		
		NavigationMenu nav = new NavigationMenu(idWorkspace);
		layout.replaceWestPanel(nav);
		
		layout.replaceCenterPanel(new PerspectiveView(idWorkspace));
		layout.initEventHandlers();
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
	public void displayWorkspace() 
	{
		FolderServices.getUserInfo(new AsyncCallback<String>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				ErrorHandler.post(errorStrings.retrieveUserInfoFailed());	
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
