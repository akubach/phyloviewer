package org.iplantc.iptol.client;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.Image;

public class ApplicationLayout extends Viewport {
	
	 ContentPanel north = new ContentPanel();  
     ContentPanel west = new ContentPanel();  
     ContentPanel center = new ContentPanel();
     ContentPanel east = new ContentPanel();  
     ContentPanel south = new ContentPanel(); 
     private final BorderLayout layout;
	
     public ApplicationLayout() {
		//build top level layout
    	 layout  = new BorderLayout();  
	     setLayout(layout);  
	     BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 175);  
	     northData.setCollapsible(false);  
	     northData.setFloatable(false);  
	     northData.setHideCollapseTool(false);  
	     northData.setSplit(false);  
	     northData.setMargins(new Margins(0, 0, 0, 0)); 
	     north.setHeaderVisible(false);
	   
	     BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 150);  
	     westData.setSplit(false);  
	     westData.setCollapsible(true);  
	     westData.setMargins(new Margins(5));  
	    
	     BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	     centerData.setMargins(new Margins(5, 0, 5, 0)); 
	     center.setBodyStyle("padding: 25px");
	     center.setScrollMode(Scroll.AUTOX); 
	     center.setHeaderVisible(true);
	     centerData.setCollapsible(false);  
	     centerData.setFloatable(false);  
	     centerData.setHideCollapseTool(false);  
	     centerData.setSplit(false);  
	     
	     BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 150);  
	     eastData.setSplit(false);  
	     eastData.setCollapsible(true);  
	     eastData.setMargins(new Margins(5));  
	   
	     BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 50);  
	     southData.setSplit(false);  
	     southData.setCollapsible(false);  
	     southData.setFloatable(false);  
	     southData.setMargins(new Margins(0, 0, 0, 0)); 
	     south.setHeaderVisible(false);
	   
	     add(north, northData);  
	     add(west, westData);  
	     add(center, centerData);  
	     add(east, eastData);  
	     add(south, southData);
	     
	     //Add basic tool bar
	     ToolBar toolBar = new ToolBar(); 
	     toolBar.setBorders(true);
	        
	     Button upload = new Button("<b>Upload</b>");
	     
	     //create basic menu and menu item
	     Menu menu = new Menu();
	     menu.add(new MenuItem("Tree Data"));
	     
	     upload.setMenu(menu);
	     toolBar.add(upload);
	     
	     //add our logo...This should be changed to DE logo later
	     Image logo = new Image("/discoveryenvironment/images/iplant62008-long.jpg");
	     logo.setHeight("145px");
	     north.add(logo);
	     // add to north panel
	     north.add(toolBar);
	     
	}
	
	public void hideRegion(LayoutRegion region) {
		layout.hide(region);
	}
}
