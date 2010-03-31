package org.iplantc.iptol.client.views.widgets.tabs;


import org.iplantc.iptol.client.JobConfiguration.JobConfigurationPanel;
import org.iplantc.iptol.client.views.widgets.panels.JobStatusPanel;

import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuBar;
import com.extjs.gxt.ui.client.widget.menu.MenuBarItem;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;

public class IndependentContrastTab extends WorkspaceTab 
{
	//////////////////////////////////////////
	//private variables
	private JobStatusPanel panelJobStatus;
		
	//////////////////////////////////////////
	//constructor
	public IndependentContrastTab(String idWorkspace) 
	{
		super(idWorkspace,"PIC (Phylip)",Type.INDEPENDANT_CONTRAST);
	}

	//////////////////////////////////////////
	private void createJob()
	{
		JobConfigurationPanel panel = new JobConfigurationPanel(idWorkspace);
		
		panel.assembleView();
	}

	//////////////////////////////////////////
	private MenuBarItem buildFileMenu()
	{
		Menu menu = new Menu();  
	
		//import menu item
		MenuItem item = new MenuItem("Add Job",new SelectionListener<MenuEvent>() 
		{
			@Override
			public void componentSelected(MenuEvent ce) 
			{
				createJob();
			}
		});
		
		menu.add(item);
			
		return new MenuBarItem("File",menu);
	}
	
	//////////////////////////////////////////
	private MenuBar buildMenuBar()
	{
		MenuBar ret = new MenuBar();  
		ret.setBorders(true);  
		ret.setStyleAttribute("borderTop", "none");  
		
		ret.add(buildFileMenu());
		ret.add(buildHelpMenu());
		
		return ret;
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void buildTabBody() 
	{
		VerticalPanel panel = new VerticalPanel();		
		panel.setSpacing(15);
	
		add(buildMenuBar());
		
		panelJobStatus = new JobStatusPanel("Contrast Jobs", idWorkspace);
		panel.add(panelJobStatus);
		add(panel);
	}

}
