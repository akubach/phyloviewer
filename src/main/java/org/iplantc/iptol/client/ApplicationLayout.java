package org.iplantc.iptol.client;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;

public class ApplicationLayout extends Viewport {
	
	 ContentPanel north = new ContentPanel();  
     ContentPanel west = new ContentPanel();  
     ContentPanel center = new ContentPanel();
     ContentPanel east = new ContentPanel();  
     ContentPanel south = new ContentPanel(); 
     
	public ApplicationLayout() {
		//build top level layout
		 final BorderLayout layout = new BorderLayout();  
	     setLayout(layout);  
	   
	     center.setScrollMode(Scroll.AUTOX);  
	   
	     BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 100);  
	     northData.setCollapsible(true);  
	     northData.setFloatable(true);  
	     northData.setHideCollapseTool(true);  
	     northData.setSplit(true);  
	     northData.setMargins(new Margins(5, 5, 0, 5));  
	   
	     BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 150);  
	     westData.setSplit(true);  
	     westData.setCollapsible(true);  
	     westData.setMargins(new Margins(5));  
	   
	     BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	     centerData.setMargins(new Margins(5, 0, 5, 0));  
	   
	     BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 150);  
	     eastData.setSplit(true);  
	     eastData.setCollapsible(true);  
	     eastData.setMargins(new Margins(5));  
	   
	     BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 100);  
	     southData.setSplit(true);  
	     southData.setCollapsible(true);  
	     southData.setFloatable(true);  
	     southData.setMargins(new Margins(0, 5, 5, 5));  
	   
	     add(north, northData);  
	     add(west, westData);  
	     add(center, centerData);  
	     add(east, eastData);  
	     add(south, southData); 
	  
	}
}
