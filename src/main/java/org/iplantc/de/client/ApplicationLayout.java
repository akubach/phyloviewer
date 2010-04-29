package org.iplantc.de.client;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.de.client.events.LogoutEvent;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BorderLayoutEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;

/**
 * 
 * @author sriram This class draws the layout for the discovery env.
 */
public class ApplicationLayout extends Viewport 
{
	private DEClientConstants constants = (DEClientConstants)GWT.create(DEClientConstants.class);
	private DEDisplayStrings displayStrings = (DEDisplayStrings) GWT.create(DEDisplayStrings.class);

	private ContentPanel north;
	private Component west;
	private Component center;
	private ContentPanel south;
	
	private ToolBar toolBar;

	private final BorderLayout layout;
	
	private HorizontalPanel headerPanel;
	private HorizontalPanel footerPanel;
	
	private ApplicationStatusBar statusBar;
		
	private ArrayList<Button> buttonsSystem = new ArrayList<Button>();
		
	class ListBoxPanel extends ContentPanel
	{
		private ListBox lb = new ListBox();
				
		public ListBoxPanel(List<String> items,int width)
		{
			setBorders(false);
			setHeaderVisible(false);
			setLayout(new FitLayout());
			setBodyStyle("backgroundColor:#0B9B9D;");			
			initListBox(items,width);		
		}
		
		private void initListBox(List<String> items,int width)
		{
			String widthAsString = width + "px";
			lb.setWidth(widthAsString);
			
			for(String item : items)
			{
				lb.addItem(item);
			}
			
			lb.setSelectedIndex(0);	
		}
		
		@Override  
		protected void onRender(Element parent,int index) 
		{  
			super.onRender(parent,index);
			
			add(lb);
		}
	}
	
	public ApplicationLayout() 
	{	
		// build top level layout
		layout = new BorderLayout();
		
		//make sure we re-draw when a panel expands
		layout.addListener(Events.Expand,new Listener<BorderLayoutEvent>() 
		{
			public void handleEvent(BorderLayoutEvent be) 
			{
				layout();
			}
		});
		
		setLayout(layout);
		
		north = new ContentPanel();
		south = new ContentPanel();
		toolBar = new ToolBar();
		statusBar = new ApplicationStatusBar();
	}	
	
	private void assembleHeader() 
	{
		drawHeader();
		north.add(headerPanel);
	
		//add tool bar to north panel
		north.add(toolBar);
	}
	
	private void assembleFooter() 
	{
		drawFooter();
		south.add(footerPanel);
	
		statusBar.setHeight("22px");
		//south.add(statusBar);
		south.addText(displayStrings.copyright());
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
		headerPanel.addStyleName("iplantc-logo");
		headerPanel.setBorders(false);
	
		Image logo = new Image(constants.iplantLogo());
		logo.setHeight("85px");
	
		headerPanel.add(logo);
	}
	
	private void drawNorth() 
	{
		north.setHeaderVisible(false);
		north.setBodyStyleName("iplantc-header");
		north.setBodyStyle("backgroundColor:#4B680C;");
		
		BorderLayoutData data = new BorderLayoutData(LayoutRegion.NORTH, 115);
		data.setCollapsible(false);
		data.setFloatable(false);
		data.setHideCollapseTool(true);
		data.setSplit(false);
		data.setMargins(new Margins(0, 0, 0, 0));
		
		add(north,data);
	}
		
	private void drawSouth() 
	{
		BorderLayoutData data = new BorderLayoutData(LayoutRegion.SOUTH, 47);
		data.setSplit(false);
		data.setCollapsible(false);
		data.setFloatable(false);
		data.setMargins(new Margins(0, 0, 0, 0));
	
		south.setHeaderVisible(false);
		south.setBodyStyleName("iplantc-footer");
				
		add(south,data);
	}
	
	private void doLogout()
	{		
		statusBar.resetStatus();
		EventBus eventbus = EventBus.getInstance();
		LogoutEvent event = new LogoutEvent();
		eventbus.fireEvent(event);	
	}
	
	private Button buildButton(String caption,ArrayList<Button> dest,SelectionListener<ButtonEvent> event,int position)
	{
		Button ret = new Button(caption,event);
		
		ret.setStyleAttribute("padding-right","5px");
		ret.setIcon(IconHelper.createPath("./images/User.png"));  
		ret.setHeight("20px");
        
		ret.hide();
		
		dest.add(position,ret);
		    
		return ret;
	}
	
	private Button buildButton(String caption,ArrayList<Button> dest,SelectionListener<ButtonEvent> event)
	{
		return buildButton(caption,dest,event,dest.size());
	}
	
	private List<String> buildPerspectiveNames()
	{
		List<String> ret = new ArrayList<String>();
		
		ret.add("Trait Evolution");
		ret.add("Ultra HT Sequencing");
		
		return ret;
	}
		
	private List<String> buildWorkflowNames()
	{
		List<String> ret = new ArrayList<String>();
		
		ret.add("Trait Evolution Workflow 1");
		ret.add("Trait Evolution Workflow 2");
		
		return ret;
	}
		
	private void assembleToolbar() 
	{
		// Add basic tool bar
		toolBar.setBorders(false);
		toolBar.setStyleName("iplantc-toolbar");
		toolBar.setHeight("28px");
				
		Button btn = buildButton(displayStrings.logout(),buttonsSystem,new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				doLogout();				
			}			
		});
				
		toolBar.add(new ListBoxPanel(buildPerspectiveNames(),180));
		toolBar.add(new ListBoxPanel(buildWorkflowNames(),240));
		toolBar.add(new FillToolItem());
		toolBar.add(btn);
	}
	
	//////////////////////////////////////////
	private void displayButtons(boolean show,ArrayList<Button> buttons)
	{
		for(Button btn : buttons)
		{
			btn.setVisible(show);
		}
	}

	public void displaySystemButtons(boolean show)
	{
		displayButtons(show,buttonsSystem);
	}
	
	public void assembleLayout() 
	{
		drawNorth();
		drawSouth();
		
		assembleToolbar();
		assembleHeader();
		assembleFooter();
	}

	public void replaceCenterPanel(Component view)
	{
		if(center != null)
		{
			remove(center);				
		}
		
		center = view;
		
		BorderLayoutData data = new BorderLayoutData(LayoutRegion.CENTER);
	    data.setMargins(new Margins(0));    	  
			
	    if(center != null)
	    {
	    	add(center,data);
	    }
		
		layout();
	}	
	
	public void replaceWestPanel(Component view)
	{		
		if(west != null)
		{						
			//make sure we are expanded before we try and remove
			layout.expand(LayoutRegion.WEST);			
			remove(west);			
		}
		
		west = view;
		   
	    if(west == null)
	    {    	
	    	layout.hide(LayoutRegion.WEST);
	    }
	    else
	    {		    
	    	BorderLayoutData data = new BorderLayoutData(LayoutRegion.WEST,200);
	    	data.setSplit(true);
	    	// true, this is false by default - but it might change in the future 
			data.setCollapsible(false);
			data.setMargins(new Margins(0,5,0,0));
						
	    	add(west,data);	    		    	
	    }
	    
	    layout();	
	}

	public void initEventHandlers()
	{
		if(statusBar != null)
		{
			statusBar.initEventHandlers();
		}
	}
}
