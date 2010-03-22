package org.iplantc.iptol.client.views.widgets.tabs;

import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.events.GetDataEvent;
import org.iplantc.iptol.client.views.widgets.panels.EditorPanel;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuBar;
import com.extjs.gxt.ui.client.widget.menu.MenuBarItem;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

public class EditorTab extends WorkspaceTab 
{
	//////////////////////////////////////////
	//private variables
	private EditorPanel panel;
	private static IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);
	
	//////////////////////////////////////////
	//constructor
	public EditorTab(String idWorkspace,HandlerManager eventbus) 
	{
		super(idWorkspace,displayStrings.editor(),eventbus,Type.VIEWER);
	}

	//////////////////////////////////////////
	//private methods
	private void doSave()
	{
		MessageBox.alert("Coming soon!","Save functionality.",null);
	}
	
	//////////////////////////////////////////
	private void doSaveAs()
	{
		MessageBox.alert("Coming soon!","Save As functionality.",null);	
	}
	
	//////////////////////////////////////////
	private MenuBarItem buildFileMenu()
	{
		Menu menu = new Menu();  
			
		//save menu item
		menu.add(new MenuItem(displayStrings.save(),new SelectionListener<MenuEvent>() 
		{
			@Override
			public void componentSelected(MenuEvent ce) 
			{
				doSave();
			}
		}));
		
		//Save As menu item
		menu.add(new MenuItem(displayStrings.saveAs(),new SelectionListener<MenuEvent>() 
		{
			@Override
			public void componentSelected(MenuEvent ce) 
			{
				doSaveAs();
			}
		}));
					
		return new MenuBarItem(displayStrings.file(),menu);
	}
	
	//////////////////////////////////////////
	private MenuBar buildMenuBar()
	{
		MenuBar ret = new MenuBar();  
		ret.setBorders(true);  
		ret.setStyleAttribute("borderTop","none");  
		
		ret.add(buildFileMenu());
		ret.add(buildHelpMenu());
		
		return ret;
	}
	
	//////////////////////////////////////////
	//protected methods	
	@Override
	protected void buildTabBody() 
	{
		add(buildMenuBar());
		panel = new EditorPanel(idWorkspace,eventbus);
		add(panel);
	}	
	
	//////////////////////////////////////////
	//public methods
	public void showData(GetDataEvent event)
	{		
		panel.showData(event);
	}	
}
