package org.iplantc.iptol.client.factories;

import org.iplantc.iptol.client.views.widgets.tabs.DataManagementTab;
import org.iplantc.iptol.client.views.widgets.tabs.EditorTab;
import org.iplantc.iptol.client.views.widgets.tabs.IndependentContrastTab;
import org.iplantc.iptol.client.views.widgets.tabs.WorkspaceTab;
import com.google.gwt.event.shared.HandlerManager;

public class WorkspaceTabFactory 
{
	public enum TabType
	{
		DATA_MANAGEMENT,
		EDITOR,
		CONTRAST
	}
	
	static public WorkspaceTab getWorkspaceTab(String idWorkspace,TabType type,HandlerManager eventbus)
	{
		WorkspaceTab ret = null;
		
		switch(type)
		{
			case DATA_MANAGEMENT:		
				ret = new DataManagementTab(idWorkspace,eventbus);
				break;

			case EDITOR:
				ret = new EditorTab(idWorkspace,eventbus);
				break;
				
			case CONTRAST:
				ret = new IndependentContrastTab(idWorkspace,eventbus);
				break;
				
			default: 
				break;
		}
		
		return ret;
	}
}
