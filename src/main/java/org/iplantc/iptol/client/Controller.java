package org.iplantc.iptol.client;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.fx.Draggable;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class Controller implements EntryPoint {
	// name of application
	public static final String APPLICATION_TITLE = "Discovery Environment";
	public static final String BROWSE_TREES = "Data Browser";

	public void onModuleLoad() {
		// set the app layout
		ApplicationLayout layout = new ApplicationLayout();
		layout.assembleLayout();

		// create a portal
		WorkspacePortal portal = new WorkspacePortal(3);
		portal.setStyleName("iptol_portal");
		portal.setColumnWidth(0, .33);
		portal.setColumnWidth(1, .33);
		portal.setColumnWidth(2, .33);
		portal.setBorders(false);
		
		// this should be re-factored
		TreeFilesManager treeFilesManager = new TreeFilesManager();
		treeFilesManager.assembleComponents();
		Portlet portlet = new Portlet();
		portlet.add(treeFilesManager);
		portlet.setHeading(BROWSE_TREES);
		portlet.setCollapsible(true);
		portlet.setAutoWidth(true);
		portlet.setAutoHeight(true);
		portlet.setScrollMode(Scroll.AUTO);
		//portlet.setBodyStyle("font-family:'Verdana';font-size:5px;");
		portal.add(portlet, 0);
		layout.addPortal(portal, layout.centerData);
		layout.north.setHeading(APPLICATION_TITLE);
		RootPanel.get().add(layout);
		layout.hideRegion(LayoutRegion.WEST);
		layout.hideRegion(LayoutRegion.EAST);
	}

}
