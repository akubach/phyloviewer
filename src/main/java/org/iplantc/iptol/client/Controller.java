package org.iplantc.iptol.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class Controller implements EntryPoint  {

	public void onModuleLoad() {
		//set the app layout
		ApplicationLayout layout = new ApplicationLayout();
		//this should be re-factored
		TreeFilesManager treeFilesManager = new TreeFilesManager();
		layout.center.setHeading("Upload your trees");
		layout.center.add(treeFilesManager);
		RootPanel.get().add(layout);
	}
	
}
