package org.iplantc.iptol.client;

import org.iplantc.iptol.client.images.Resources;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author sriram This class draws the layout for the discovery env.
 */
@SuppressWarnings("unused")
public class ApplicationLayout extends Viewport {

	IptolConstants constants = (IptolConstants) GWT
			.create(IptolConstants.class);

	ContentPanel north;
	ContentPanel west;
	ContentPanel center;
	ContentPanel east;
	ContentPanel south;

	BorderLayoutData northData;
	BorderLayoutData westData;
	BorderLayoutData centerData;
	BorderLayoutData eastData;
	BorderLayoutData southData;
	
	
	private ToolBar toolBar;

	private final BorderLayout layout;
	
	private HorizontalPanel headerPanel;
	
	private HorizontalPanel footerPanel;
	
	private ApplicationStatusBar statusBar;
	
	private HandlerManager eventbus;

	public ApplicationLayout(HandlerManager eventbus) {
		
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
	
	
	protected void assembleHeader() {
		drawHeader();
		north.add(headerPanel);
		// add to north panel
		north.add(toolBar);
	}
	
	protected void assembleFooter() {
		drawFooter();
		south.add(footerPanel);
		statusBar.setHeight("20px");
		south.add(statusBar);
	}
	
	private void drawFooter() {
		footerPanel = new HorizontalPanel();
		footerPanel.setBorders(false);
	}
	
	private void drawHeader() {
		// add our logo...This should be changed to DE logo later
		headerPanel = new HorizontalPanel();
		headerPanel.addStyleName("iptol_logo");
		headerPanel.setBorders(false);
		Image logo = new Image(constants.iplantLogo());
		logo.setHeight("90px");
		headerPanel.add(logo);
	}
	protected void drawNorth() {
		north.setHeaderVisible(false);
		north.setBodyStyleName("iptol_header");
		north.setBodyStyle("backgroundColor:#4B680C;");
		northData = new BorderLayoutData(LayoutRegion.NORTH, 125);
		northData.setCollapsible(false);
		northData.setFloatable(false);
		northData.setHideCollapseTool(true);
		northData.setSplit(false);
		northData.setMargins(new Margins(0, 0, 0, 0));
	}
	
	protected void drawSouth() {
		southData = new BorderLayoutData(LayoutRegion.SOUTH, 50);
		southData.setSplit(false);
		southData.setCollapsible(false);
		southData.setFloatable(false);
		southData.setMargins(new Margins(0, 0, 0, 0));
		south.setHeaderVisible(false);
		south.setBodyStyleName("iptol_footer");
	}
	
	protected void drawWest() {
		westData = new BorderLayoutData(LayoutRegion.WEST, 200);
		westData.setSplit(false);
		westData.setCollapsible(true);
		westData.setMargins(new Margins(5));
	}
	
	protected void drawEast() {
		eastData = new BorderLayoutData(LayoutRegion.EAST, 150);
		eastData.setSplit(false);
		eastData.setCollapsible(true);
		eastData.setMargins(new Margins(5));
	}
	
	
	protected void drawCenter() {
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
	
	protected void assembleToolbar() {
		// Add basic tool bar
		toolBar.setBorders(false);
		toolBar.setStyleName("iptol_toolbar");
		toolBar.setHeight("30px");
		Button logout = new Button();
		logout.setIcon(Resources.ICONS.user());
		logout.setText("Logout");
		toolBar.add(new FillToolItem());
		toolBar.add(logout);
	}

	public void assembleLayout() {
		drawNorth();
		drawSouth();
		drawWest();
		drawEast();
		drawCenter();
		add(north, northData);
		//add(west, westData);
		add(east, eastData);
		add(south, southData);
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
	public void hideRegion(LayoutRegion region) {
		layout.hide(region);
	}
	
	public void updateRegion(LayoutRegion region, Component component) {
		if(region == LayoutRegion.CENTER) {
			center.removeAll();
			this.add(component,centerData);
		} else if(region == LayoutRegion.WEST) {
			west.removeAll();
			this.add(component,westData);
		} else if(region == LayoutRegion.NORTH) {
			north.removeAll();
			this.add(component, northData);
		} else if(region == LayoutRegion.EAST) {
			east.removeAll();
			this.add(component,eastData);
		} else if(region == LayoutRegion.SOUTH) {
			south.removeAll();
			this.add(component,southData);
		}
			
	}

}
