package org.iplantc.iptol.client.views.widgets.tabs;

import org.iplantc.iptol.client.IptolDisplayStrings;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuBarItem;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;

public abstract class WorkspaceTab extends TabItem 
{
	//////////////////////////////////////////
	//public types
	public static enum Type
	{
		DATA_MANAGEMENT,
		VIEWER,
		INDEPENDANT_CONTRAST,
		COMING_SOON,
	}	
	
	//////////////////////////////////////////
	//protected variables
	protected Type type;
	protected String idWorkspace;
	protected static IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);
	
	//////////////////////////////////////////
	//constructor
	protected WorkspaceTab(String idWorkspace,String caption,Type type)
	{
		this.idWorkspace = idWorkspace;
		this.type = type;
		
		setScrollMode(Scroll.AUTOY);
		setText(caption);  
		addStyleName("pad-text");	
		buildTabBody();	
	}
	
	//////////////////////////////////////////
	//abstract methods
	protected abstract void buildTabBody();
	
	//////////////////////////////////////////
	//protected methods
	protected void doAboutDisplay()
	{
		MessageBox.alert("Coming soon!","About information.",null);
	}
	
	protected void doHelpContentDisplay()  {
		
		MessageBox.alert("Coming soon!","Help information.",null);
	}
	
	//////////////////////////////////////////
	protected MenuBarItem buildHelpMenu()
	{
		Menu menu = new Menu();  
	    
		
		MenuItem helpContent = new MenuItem(displayStrings.helpContent(),new SelectionListener<MenuEvent>() 
				{
					@Override
					public void componentSelected(MenuEvent ce) 
					{
						doHelpContentDisplay();
					}
				});
		
		MenuItem item = new MenuItem(displayStrings.about(),new SelectionListener<MenuEvent>() 
		{
			@Override
			public void componentSelected(MenuEvent ce) 
			{
				doAboutDisplay();
			}
		});
		
		menu.add(helpContent);
		menu.add(item);
				
		return new MenuBarItem(displayStrings.help(),menu);
	}
	
	//////////////////////////////////////////
	//public methods
	public Type getType()
	{
		return type;
	}
}
