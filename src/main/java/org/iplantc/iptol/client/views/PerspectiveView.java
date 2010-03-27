package org.iplantc.iptol.client.views;

import java.util.ArrayList;
import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.events.GetDataEvent;
import org.iplantc.iptol.client.events.GetDataEventHandler;
import org.iplantc.iptol.client.events.SelectJobEvent;
import org.iplantc.iptol.client.events.SelectJobEventHandler;
import org.iplantc.iptol.client.events.ViewDataEvent;
import org.iplantc.iptol.client.events.ViewDataEventHandler;
import org.iplantc.iptol.client.events.ViewDataEvent.ViewType;
import org.iplantc.iptol.client.factories.WorkspaceTabFactory;
import org.iplantc.iptol.client.factories.WorkspaceTabFactory.TabType;
import org.iplantc.iptol.client.views.widgets.tabs.EditorTab;
import org.iplantc.iptol.client.views.widgets.tabs.IndependentContrastTab;
import org.iplantc.iptol.client.views.widgets.tabs.WorkspaceTab;
import com.extjs.gxt.ui.client.widget.TabPanel;

public class PerspectiveView extends TabPanel 
{
	//////////////////////////////////////////
	//private variables
	private String idWorkspace;	
	private ArrayList<WorkspaceTab> tabs = new ArrayList<WorkspaceTab>();
	
	//////////////////////////////////////////
	//constructor
	public PerspectiveView(String idWorkspace)
	{
		this.idWorkspace = idWorkspace;
		
		setMinTabWidth(115);  
		setResizeTabs(true);  
		setAnimScroll(true);  
		setTabScroll(true);
		initEventHandlers();
		initDefaultTabs();
	}
	
	//////////////////////////////////////////
	//private methods
	private WorkspaceTab findTab(WorkspaceTab.Type type)
	{
		WorkspaceTab ret = null;
		
		//find the desired tab
		for(WorkspaceTab tab : tabs)
		{
			if(tab.getType() == type)
			{
				ret = tab;
				break;
			}
		}
		
		return ret;
	}
	
	//////////////////////////////////////////
	private void handleViewEvent(ViewDataEvent event)
	{
		ViewDataEvent.ViewType type = event.getViewType();
		
		if(type == ViewType.Tree)
		{
			WorkspaceTab tab = findTab(WorkspaceTab.Type.VIEWER);

			//do we have this tab?
			if(tab != null)
			{
				setSelection(tab);
			}
			
		}
		else if (type == ViewType.Raw)
		{
			WorkspaceTab tab = findTab(WorkspaceTab.Type.DATA_MANAGEMENT);
			
			if(tab != null)
			{
				//ajm - aren't we doing this a different way?
			//	DataManagementTab tabData = (DataManagementTab)tab;
			//	tabData.displayRawData(event.getName());
			}			
		}
	}
	
	//////////////////////////////////////////
	private WorkspaceTab selectTab(WorkspaceTab.Type type)
	{
		WorkspaceTab ret = findTab(type);
		
		if(ret != null)
		{
			if(!ret.isVisible())
			{	
				ret.setVisible(true);
			}
			
			setSelection(ret);
		}
		
		return ret;			
	}
	
	//////////////////////////////////////////
	private void selectJob(String id)
	{
		WorkspaceTab tab = selectTab(WorkspaceTab.Type.INDEPENDANT_CONTRAST);
		
		if(tab != null)
		{
			//pass the job selection to our independent contrast tab
			if(tab instanceof IndependentContrastTab)
			{					
				IndependentContrastTab icTab = (IndependentContrastTab)tab;
				
				icTab.setVisible(true);
				icTab.selectJob(id);
			}			
		}
	}
	
	//////////////////////////////////////////
	private void displayData(GetDataEvent event)
	{
		WorkspaceTab tab = selectTab(WorkspaceTab.Type.VIEWER);
		
		if(tab != null)
		{
			if(tab instanceof EditorTab)
			{
				EditorTab eTab = (EditorTab)tab;
				eTab.showData(event);
			}
		}
	}
	
	//////////////////////////////////////////
	private void initEventHandlers()
	{
		EventBus eventbus = EventBus.getInstance();
		//register login handler
		eventbus.addHandler(ViewDataEvent.TYPE,new ViewDataEventHandler()
		{        	
			@Override
			public void onView(ViewDataEvent event) 
			{
				handleViewEvent(event);												
			}
		});
		
		//register select job handler
		eventbus.addHandler(SelectJobEvent.TYPE,new SelectJobEventHandler()
        {        	
			@Override
			public void onSelect(SelectJobEvent event) 
			{
				selectJob(event.getJobId());								
			}
        });	
		
		//get data handler
		eventbus.addHandler(GetDataEvent.TYPE,new GetDataEventHandler()
        {        	
			@Override
			public void onGet(GetDataEvent event) 
			{
				displayData(event);			
			}
        });	
	}

	//////////////////////////////////////////
	private void addTab(TabType type)
	{
		WorkspaceTab tab = WorkspaceTabFactory.getWorkspaceTab(idWorkspace,type);
			
		if(tab != null)
		{
			tabs.add(tab);	
			add(tab);
		}		
	}
	
	//////////////////////////////////////////
	private void initDefaultTabs()
	{
		addTab(TabType.DATA_MANAGEMENT);
		addTab(TabType.EDITOR);
		addTab(TabType.CONTRAST);
	}	
}
