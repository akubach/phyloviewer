package org.iplantc.iptol.client;

import org.iplantc.iptol.client.events.DataBrowserNodeClickEvent;
import org.iplantc.iptol.client.events.DataBrowserNodeClickEventHandler;
import org.iplantc.iptol.client.models.DiskResource;

import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.shared.HandlerManager;

/**
 * 
 * @author sriram
 * A class that represents the application status bar. 
 * Application components can add/remove status. 
 */
public class ApplicationStatusBar extends ToolBar 
{
	private Status status_left;
	private Status status_center;
	private Status status_right;
	
	public ApplicationStatusBar(HandlerManager eventbus) 
	{
		super();

		status_left = new Status();
		status_center = new Status();
		status_right = new Status();
		
		assembleStatusBars();
		
		eventbus.addHandler(DataBrowserNodeClickEvent.TYPE, new DataBrowserNodeClickEventHandler() 
		{
			@Override
			public void onNodeClick(DataBrowserNodeClickEvent dbnce) 
			{
				String status = "";
				DiskResource resource = dbnce.getDiskResource();
				
				if(resource != null)
				{
					status = resource.getStatus();
				}
				
				status_left.setText(status);
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
