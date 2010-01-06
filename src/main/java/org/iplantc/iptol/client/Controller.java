package org.iplantc.iptol.client;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.fx.Draggable;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;

public class Controller implements EntryPoint {
	// name of application
	public static final String APPLICATION_TITLE = "Discovery Environment";

	public void onModuleLoad() {
		
		PresentationManager mgr = new PresentationManager();
		String token = History.getToken();
		
		if(!mgr.handleToken(token,false))
		{
			mgr.handleToken("workspace");
		}	
		
		// set the app layout
//		ApplicationLayout layout = new ApplicationLayout();
//		layout.assembleLayout();
//		DataBrowserTree dataManagerTree = new DataBrowserTree();
//		dataManagerTree.assembleView();
//		layout.add(dataManagerTree, layout.westData);
//		RootPanel.get().add(layout);
//		layout.hideRegion(LayoutRegion.EAST);
	}

}
