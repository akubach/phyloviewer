package org.iplantc.de.client;

import org.iplantc.de.client.events.DataBrowserNodeClickEvent;
import org.iplantc.de.client.events.DataBrowserNodeClickEventHandler;
import org.iplantc.de.client.models.DiskResource;

import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.shared.HandlerRegistration;

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
	private HandlerRegistration handlerNodeClick;
	
	public ApplicationStatusBar() 
	{
		super();

		status_left = new Status();
		status_center = new Status();
		status_right = new Status();
		
		initEventHandlers();
		assembleStatusBars();		
	}
	
	private void assembleStatusBars() 
	{
		add(status_left);
		add(status_center);
		add(status_right);
	}
	
	public void initEventHandlers()
	{
		EventBus eventbus = EventBus.getInstance();
		
		//ensure we only have one event handler registered
		if(handlerNodeClick != null)
		{
			eventbus.removeHandler(handlerNodeClick);
		}
		
		//register our handler
		handlerNodeClick = eventbus.addHandler(DataBrowserNodeClickEvent.TYPE, new DataBrowserNodeClickEventHandler() 
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
		
	public void resetStatus()
	{
		status_left.setText("");
	}
}
