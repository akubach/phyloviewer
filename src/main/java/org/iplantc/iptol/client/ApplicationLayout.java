package org.iplantc.iptol.client;

import org.iplantc.iptol.client.images.Resources;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Image;

/**
 * 
 * @author sriram This class draws the layout for the discovery env.
 */
@SuppressWarnings("unused")
public class ApplicationLayout extends Viewport 
{
	private IptolClientConstants constants = (IptolClientConstants)GWT.create(IptolClientConstants.class);
	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);

	private ContentPanel north;
	private ContentPanel west;
	private ContentPanel center;
	private ContentPanel east;
	private ContentPanel south;

	private BorderLayoutData northData;
	private BorderLayoutData westData;
	private BorderLayoutData centerData;
	private BorderLayoutData eastData;
	private BorderLayoutData southData;
	
	private ToolBar toolBar;

	private final BorderLayout layout;
	
	private HorizontalPanel headerPanel;
	
	private HorizontalPanel footerPanel;
	
	private ApplicationStatusBar statusBar;
	
	private HandlerManager eventbus;
	
	public ApplicationLayout(HandlerManager eventbus) 
	{
		this.eventbus = eventbus;
	
		// build top level layout
		layout = new BorderLayout();
		setLayout(layout);
		
		north = new ContentPanel();
		west = new ContentPanel();	
		center = new ContentPanel();
		east = new ContentPanel();
		south = new ContentPanel();
		toolBar = new ToolBar();
		statusBar = new ApplicationStatusBar(eventbus);
	}	
	
	protected void assembleHeader() 
	{
		drawHeader();
		north.add(headerPanel);
	
		//add tool bar to north panel
		north.add(toolBar);
	}
	
	protected void assembleFooter() 
	{
		drawFooter();
		south.add(footerPanel);
	
		statusBar.setHeight("22px");
		south.add(statusBar);
	}
	
	private void drawFooter() 
	{
		footerPanel = new HorizontalPanel();
		footerPanel.setBorders(false);
	}
	
	private void drawHeader() 
	{
		// add our logo...This should be changed to DE logo later
		headerPanel = new HorizontalPanel();
		headerPanel.addStyleName("iptol_logo");
		headerPanel.setBorders(false);
	
		Image logo = new Image(constants.iplantLogo());
		logo.setHeight("85px");
	
		headerPanel.add(logo);
	}
	
	protected void drawNorth() 
	{
		north.setHeaderVisible(false);
		north.setBodyStyleName("iptol_header");
		north.setBodyStyle("backgroundColor:#4B680C;");
		
		northData = new BorderLayoutData(LayoutRegion.NORTH, 115);
		northData.setCollapsible(false);
		northData.setFloatable(false);
		northData.setHideCollapseTool(true);
		northData.setSplit(false);
		northData.setMargins(new Margins(0, 0, 0, 0));
	}
	
	protected void drawSouth() 
	{
		southData = new BorderLayoutData(LayoutRegion.SOUTH, 47);
		southData.setSplit(false);
		southData.setCollapsible(false);
		southData.setFloatable(false);
		southData.setMargins(new Margins(0, 0, 0, 0));
	
		south.setHeaderVisible(false);
		south.setBodyStyleName("iptol_footer");
	}
	
	protected void drawWest() 
	{
		westData = new BorderLayoutData(LayoutRegion.WEST, 200);
		westData.setSplit(false);
		westData.setCollapsible(true);
		westData.setMargins(new Margins(5));
	}
	
	protected void drawEast() 
	{
		eastData = new BorderLayoutData(LayoutRegion.EAST, 150);
		eastData.setSplit(false);
		eastData.setCollapsible(true);
		eastData.setMargins(new Margins(5));
	}
		
	protected void drawCenter() 
	{
		centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins(5, 0, 5, 0));
		
		center.setBodyStyle("padding: 25px");
		center.setScrollMode(Scroll.AUTOX);
		center.setHeaderVisible(false);
		
		centerData.setCollapsible(false);
		centerData.setFloatable(false);
		centerData.setHideCollapseTool(false);
		centerData.setSplit(false);
	}
	
	protected void assembleToolbar() 
	{
		// Add basic tool bar
		toolBar.setBorders(false);
		toolBar.setStyleName("iptol_toolbar");
		toolBar.setHeight("28px");
		
		Button logout = new Button();
		logout.setHeight("20px");
		logout.setIcon(Resources.ICONS.user());
		logout.setText(displayStrings.logout());
		
		toolBar.add(new FillToolItem());
		toolBar.add(logout);
	}

	public void assembleLayout() 
	{
		drawNorth();
		drawSouth();
		drawWest();
		drawEast();
		drawCenter();
		
		add(north,northData);
		add(east,eastData);
		add(south,southData);
		
		assembleToolbar();
		assembleHeader();
		assembleFooter();
	}

	/**
	 * 
	 * @param region
	 * a region with-in the border layout Hide a particular region
	 * from displaying
	 */
	public void hideRegion(LayoutRegion region) 
	{
		layout.hide(region);
	}
	
	public void updateRegion(LayoutRegion region, Component component) 
	{
		if(region == LayoutRegion.CENTER) 
		{
			center.removeAll();
			add(component,centerData);
		} 
		else if(region == LayoutRegion.WEST) 
		{
			west.removeAll();
			add(component,westData);		
		} 
		else if(region == LayoutRegion.NORTH) 
		{
			north.removeAll();
			add(component, northData);
		} 
		else if(region == LayoutRegion.EAST) 
		{
			east.removeAll();
			add(component,eastData);
		} 
		else if(region == LayoutRegion.SOUTH) 
		{
			south.removeAll();
			add(component,southData);
		}			
	}
}
