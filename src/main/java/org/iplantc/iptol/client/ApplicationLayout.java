package org.iplantc.iptol.client;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
 
/**
 * 
 * @author sriram
 * This class draws the layout for the discovery env.
 */
@SuppressWarnings("unused")
public class ApplicationLayout extends Viewport {
	
	IptolConstants constants = (IptolConstants) GWT
	.create(IptolConstants.class);
	
	 ContentPanel north ;  
	 ContentPanel west;  
	 ContentPanel center;
	 ContentPanel east;  
	 ContentPanel south;
	 
	 BorderLayoutData northData;
	 BorderLayoutData westData;
	 BorderLayoutData centerData;
	 BorderLayoutData eastData;
	 BorderLayoutData southData;
	 
	 private final BorderLayout layout;
	
     
    public ApplicationLayout() {
    	 
		//build top level layout
    	 layout  = new BorderLayout();  
	     setLayout(layout);  
	     
	     north = new ContentPanel();  
	     west = new ContentPanel();  
	     center = new ContentPanel();
	     east = new ContentPanel();  
	     south = new ContentPanel();
	     
	     northData = new BorderLayoutData(LayoutRegion.NORTH, 155);  
	     northData.setCollapsible(false);  
	     northData.setFloatable(false);  
	     northData.setHideCollapseTool(false);  
	     northData.setSplit(false);  
	     northData.setMargins(new Margins(0, 0, 0, 0)); 
	     north.setHeaderVisible(false);
	     north.setBodyStyleName("iptol_header");
	     north.setBodyStyle("backgroundColor:#4B680C;");
	   
	     westData = new BorderLayoutData(LayoutRegion.WEST, 150);  
	     westData.setSplit(false);  
	     westData.setCollapsible(true);  
	     westData.setMargins(new Margins(5));  
	    
	     centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	     centerData.setMargins(new Margins(5, 0, 5, 0)); 
	     center.setBodyStyle("padding: 25px");
	     center.setScrollMode(Scroll.AUTOX); 
	     center.setHeaderVisible(false);
	     centerData.setCollapsible(false);  
	     centerData.setFloatable(false);  
	     centerData.setHideCollapseTool(false);  
	     centerData.setSplit(false);  
	     
	     eastData = new BorderLayoutData(LayoutRegion.EAST, 150);  
	     eastData.setSplit(false);  
	     eastData.setCollapsible(true);  
	     eastData.setMargins(new Margins(5));  
	   
	     southData = new BorderLayoutData(LayoutRegion.SOUTH, 50);  
	     southData.setSplit(false);  
	     southData.setCollapsible(false);  
	     southData.setFloatable(false);  
	     southData.setMargins(new Margins(0, 0, 0, 0)); 
	     south.setHeaderVisible(false);
	     south.setBodyStyleName("iptol_footer");
	     
	     add(north, northData);  
	     add(west, westData);  
	     add(east, eastData);  
	     add(south, southData);
	     
	     //Add basic tool bar
	     ToolBar toolBar = new ToolBar(); 
	     toolBar.setBorders(false);
	        
	     Button upload = new Button("<b>Upload</b>");
	     
	     //create basic menu and menu item
//	     Menu menu = new Menu();
//	     MenuItem tree_data= new MenuItem("Tree Data");
//	     tree_data.addSelectionListener(new SelectionListener<MenuEvent>() {
//			@Override
//			public void componentSelected(MenuEvent ce) {
//				Window.alert("==> tree data clicked" );
//			}
//		});
//	     menu.add(tree_data);
//	     
//	     upload.setMenu(menu);
//	     toolBar.add(upload);
	     toolBar.setStyleName("iptol_toolbar");
	     toolBar.setHeight("30px");
	     
	     //add our logo...This should be changed to DE logo later
	     HorizontalPanel headerPanel = new HorizontalPanel();
	     headerPanel.addStyleName("iptol_logo");
	     Image logo = new Image(constants.iplantLogo());
	     logo.setHeight("125px");
	     headerPanel.add(logo);
	     north.add(headerPanel);
	     //add to north panel
	     north.add(toolBar);
	     
	}
	
    /**
     * 
     * @param region a region with-in the border layout
     * Hide a particular region from displaying
     */
	public void hideRegion(LayoutRegion region) {
		layout.hide(region);
	}
	
	/**
	 * 
	 * @param portal portal to add to the layout
	 * @param data data for the selected layout
	 * Add the portal to the center of layout
	 */
	public void addPortal(Widget portal, BorderLayoutData data) {
		add(portal,data);
	}
	
}
