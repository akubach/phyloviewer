package org.iplantc.iptol.client;

import org.iplantc.iptol.client.events.DataBrowserNodeClickEvent;
import org.iplantc.iptol.client.events.DataBrowserNodeClickEventHandler;

import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
/**
 * 
 * @author sriram
 * A class that represents the application status bar. 
 * Application components can add/remove status. 
 */
public class ApplicationStatusBar extends ToolBar {

	private HandlerManager eventbus;
	private Status status_left;
	private Status status_center;
	private Status status_right;
	
	public ApplicationStatusBar(HandlerManager eventbus) {
		super();
		this.eventbus = eventbus;
		status_left = new Status();
		status_center = new Status();
		status_right = new Status();
		assembleStatusBars();
		eventbus.addHandler(DataBrowserNodeClickEvent.TYPE, new DataBrowserNodeClickEventHandler() {
			@Override
			public void onNodeClick(DataBrowserNodeClickEvent dbnce) {
				File file = dbnce.getFile();
				if(file instanceof Folder) {
					status_left.setText(file.getChildren().size() + " file(s)");
				} else {
					status_left.setText(dbnce.getFile().getInfo().getType() + "-" + dbnce.getFile().getInfo().getUploaded());
				}
			}
		});
	}

	
	private void assembleStatusBars() {
		this.add(status_left);
		this.add(status_center);
		this.add(status_right);
	}
	
	
	
	
}
