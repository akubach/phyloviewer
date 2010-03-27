package org.iplantc.iptol.client.views.widgets.tabs;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuBarItem;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;

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
	
	//////////////////////////////////////////
	//constructor
	protected WorkspaceTab(String idWorkspace,String caption,Type type)
	{
		this.idWorkspace = idWorkspace;
		this.type = type;
		
		setScrollMode(Scroll.AUTO);
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
	
	//////////////////////////////////////////
	protected MenuBarItem buildHelpMenu()
	{
		Menu menu = new Menu();  
	    
		MenuItem item = new MenuItem("About",new SelectionListener<MenuEvent>() 
		{
			@Override
			public void componentSelected(MenuEvent ce) 
			{
				doAboutDisplay();
			}
		});
		
		menu.add(item);
				
		return new MenuBarItem("Help",menu);
	}
	
	//////////////////////////////////////////
	//public methods
	public Type getType()
	{
		return type;
	}
}
