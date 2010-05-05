package org.iplantc.de.client.views.widgets.tabs;

import org.iplantc.de.client.jobs.JobConfigurationPanel;
import org.iplantc.de.client.views.widgets.panels.JobStatusPanel;

import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuBar;
import com.extjs.gxt.ui.client.widget.menu.MenuBarItem;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.Window;

public class IndependentContrastTab extends WorkspaceTab 
{
	//////////////////////////////////////////
	//private variables
	private JobStatusPanel panelJobStatus;
		
	//////////////////////////////////////////
	//constructor
	public IndependentContrastTab(String idWorkspace) 
	{
		super(idWorkspace,displayStrings.picPhylip(),Type.INDEPENDANT_CONTRAST);
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
		MenuItem item = new MenuItem(displayStrings.addJob(),new SelectionListener<MenuEvent>() 
		{
			@Override
			public void componentSelected(MenuEvent ce) 
			{
				createJob();
			}
		});
		
		menu.add(item);
			
		return new MenuBarItem(displayStrings.file(),menu);
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
		
		panelJobStatus = new JobStatusPanel(displayStrings.contrastJobs(),idWorkspace);
		panel.add(panelJobStatus);

		add(panel);
	}

	//////////////////////////////////////////
	@Override
	protected void doAboutDisplay()
	{
		Window.open("help/about.html",displayStrings.about(),null);
	}
	
	//////////////////////////////////////////
	@Override
	protected void doHelpContentDisplay()  
	{
		Window.open("help/ic.html",displayStrings.help(),null);		
	}
}
