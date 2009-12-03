package org.iplantc.iptol.client;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class Controller implements EntryPoint  {
	//name of application 
public static final String APPLICATION_TITLE = "Discovery Environment";
public static final String BROWSE_TREES = "Browse Trees";
	public void onModuleLoad() {
		//set the app layout
		ApplicationLayout layout = new ApplicationLayout();

		//create a portal
		DiscoveryPortal portal = new DiscoveryPortal(3);
		//portal.setColumnWidth(0, .33);  
		//portal.setColumnWidth(1, .33);  
		//portal.setColumnWidth(2, .33); 
//		
		//this should be re-factored
		TreeFilesManager treeFilesManager = new TreeFilesManager();
		Portlet portlet = new Portlet();
		portlet.setHeight(400);
		portlet.setWidth(400);
		portlet.add(treeFilesManager);
		portlet.setHeading(BROWSE_TREES);
		portlet.setCollapsible(true);
		portal.add(portlet, 0);
		layout.addPortal(portal,layout.centerData);
		layout.north.setHeading(APPLICATION_TITLE);
		RootPanel.get().add(layout);
		layout.hideRegion(LayoutRegion.WEST);
		layout.hideRegion(LayoutRegion.EAST);
	}
	
}
