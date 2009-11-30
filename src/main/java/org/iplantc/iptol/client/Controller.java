package org.iplantc.iptol.client;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class Controller implements EntryPoint  {

	public void onModuleLoad() {
		//set the app layout
		ApplicationLayout layout = new ApplicationLayout();
		//this should be re-factored
		TreeFilesManager treeFilesManager = new TreeFilesManager();
		layout.center.setHeading("Upload your trees");
		layout.north.setHeading("Discovery Environment");
		layout.center.add(treeFilesManager);
		RootPanel.get().add(layout);
		layout.hideRegion(LayoutRegion.WEST);
		layout.hideRegion(LayoutRegion.EAST);
	}
	
}
