package org.iplantc.iptol.client.views.widgets.tabs;

import org.iplantc.iptol.client.events.GetDataEvent;
import org.iplantc.iptol.client.views.widgets.panels.EditorPanel;

import com.extjs.gxt.ui.client.widget.menu.MenuBar;

public class EditorTab extends WorkspaceTab 
{
	//////////////////////////////////////////
	//private variables
	private EditorPanel panel;
	
	//////////////////////////////////////////
	//constructor
	public EditorTab(String idWorkspace) 
	{
		super(idWorkspace,displayStrings.editor(),Type.VIEWER);
	}

	//////////////////////////////////////////
	//private methods
	private MenuBar buildMenuBar()
	{
		MenuBar ret = new MenuBar();  
		ret.setBorders(true);  
		ret.setStyleAttribute("borderTop","none");  
		
		ret.add(buildHelpMenu());
		
		return ret;
	}
	
	//////////////////////////////////////////
	//protected methods	
	@Override
	protected void buildTabBody() 
	{
		add(buildMenuBar());
		panel = new EditorPanel(idWorkspace);
		add(panel);
	}	
	
	//////////////////////////////////////////
	//public methods
	public void showData(GetDataEvent event)
	{		
		panel.showData(event);
	}	
}
