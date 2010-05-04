package org.iplantc.de.client.views.windows;

import org.iplantc.de.client.JobConfiguration.JobConfigurationPanel;
import org.iplantc.de.client.views.panels.JobStatusPanel;

import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuBar;
import com.extjs.gxt.ui.client.widget.menu.MenuBarItem;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.Element;

public class JobsManagementWindow extends IPlantWindow 
{
	//////////////////////////////////////////
	//private variables
	private String idWorkspace;
	
	//////////////////////////////////////////
	//constructor
	public JobsManagementWindow(String tag, String idWorkspace) 
	{
		super(tag);
		this.idWorkspace = idWorkspace;
		
		setHeading(displayStrings.myJobs());
		setClosable(true);
		setResizable(false);
		setMaximizable(false);
		setMinimizable(true);
		setWidth(570);
		setHeight(440);
		
		setIcon(IconHelper.createStyle("bogus"));
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
	private void doAboutDisplay()
	{
		com.google.gwt.user.client.Window.open("help/about.html",displayStrings.about(),null);
	}
	
	//////////////////////////////////////////
	private void doHelpContentDisplay()  
	{
		com.google.gwt.user.client.Window.open("help/mydata.html",displayStrings.help(),null);		
	}
	
	//////////////////////////////////////////
	private MenuBarItem buildHelpMenu()
	{
		Menu menu = new Menu();  
		
		menu.add(new MenuItem(displayStrings.helpContent(),new SelectionListener<MenuEvent>() 
		{
			@Override
			public void componentSelected(MenuEvent ce) 
			{
				doHelpContentDisplay();
			}
		}));
		
		menu.add(new MenuItem(displayStrings.about(),new SelectionListener<MenuEvent>() 
		{
			@Override
			public void componentSelected(MenuEvent ce) 
			{
				doAboutDisplay();
			}
		}));
				
		return new MenuBarItem(displayStrings.help(),menu);
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
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
		
		VerticalPanel panel = new VerticalPanel();		
		panel.setSpacing(15);
	
		add(buildMenuBar());
		
		JobStatusPanel panelJobStatus = new JobStatusPanel(displayStrings.contrastJobs(),idWorkspace);
		panel.add(panelJobStatus);

		add(panel);
	}
	
	//////////////////////////////////////////
	//public methods
	@Override
	public void cleanup() 
	{
		//we have no cleanup		
	}
}
