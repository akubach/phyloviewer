package org.iplantc.iptol.client;

import org.iplantc.iptol.client.events.DataBrowserNodeClickEvent;
import org.iplantc.iptol.client.events.DataBrowserNodeClickEventHandler;

import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

/**
 * 
 * @author sriram
 * A class that represents the application status bar. 
 * Application components can add/remove status. 
 */
public class ApplicationStatusBar extends ToolBar 
{
	private HandlerManager eventbus;
	
	private Status status_left;
	private Status status_center;
	private Status status_right;
	
	private IptolConstants constants = (IptolConstants)GWT.create(IptolConstants.class);
	
	public ApplicationStatusBar(HandlerManager eventbus) 
	{
		super();
		this.eventbus = eventbus;

		status_left = new Status();
		status_center = new Status();
		status_right = new Status();
		
		assembleStatusBars();
		
		eventbus.addHandler(DataBrowserNodeClickEvent.TYPE, new DataBrowserNodeClickEventHandler() 
		{
			@Override
			public void onNodeClick(DataBrowserNodeClickEvent dbnce) 
			{
				File file = dbnce.getFile();
				if(file instanceof Folder) 
				{
					status_left.setText(file.getChildren().size() + " " + constants.files());
				} 
				else 					
				{
					String filename = dbnce.getFile().getInfo().getName();
					status_left.setText(filename + " - " + dbnce.getFile().getInfo().getUploaded());
				}
			}
		});
	}
	
	private void assembleStatusBars() 
	{
		add(status_left);
		add(status_center);
		add(status_right);
	}
}
