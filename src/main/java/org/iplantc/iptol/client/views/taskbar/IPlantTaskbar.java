package org.iplantc.iptol.client.views.taskbar;

import java.util.ArrayList;
import java.util.List;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public class IPlantTaskbar extends LayoutContainer 
{
	protected StartBox startBox;  //west
	protected TasksButtonsPanel tbPanel; // center

	public IPlantTaskbar(StartMenuComposer composer) 
	{
		setId("ux-taskbar");
		setLayout(new RowLayout(Orientation.HORIZONTAL));
		startBox = new StartBox(composer);
		tbPanel = new TasksButtonsPanel();
		add(startBox, new RowData(90, 1));
		add(tbPanel, new RowData(1, 1));
	}

	/**
	 * Adds a button.
	 * 
	 * @param win the window
	 * @return the new task button
	 */
	public IPlantTaskButton addTaskButton(Window win) 
	{
		return tbPanel.addButton(win);
	}

	/**
	 * Returns the bar's buttons.
	 * 
	 * @return the buttons
	 */
	public List<IPlantTaskButton> getButtons() 
	{
		return tbPanel.getItems();
	}
	  
	/**
	 * Returns the bar's start menu.
	 * 
	 * @return the start menu
	 */
	public StartMenu getStartMenu() 
	{
		return (StartMenu)startBox.startBtn.getMenu();
	}
	
	/**
	 * Removes a button.
	 * 
	 * @param btn the button to remove
	 */
	public void removeTaskButton(IPlantTaskButton btn) 
	{
		tbPanel.removeButton(btn);
	}

	/**
	 * Sets the active button.
	 * 
	 * @param btn the button
	 */
	public void setActiveButton(IPlantTaskButton btn) 
	{
		tbPanel.setActiveButton(btn);
	}

	@Override
	protected void onRender(Element parent, int index) 
	{
		super.onRender(parent, index);
		setStyleAttribute("zIndex","10");
	}
}	 



class TasksButtonsPanel extends BoxComponent 
{
	private int buttonMargin = 2;
	private int buttonWidth = 168;
	private boolean buttonWidthSet = false;
	private boolean enableScroll = true;	 
	private List<IPlantTaskButton> items;
	private int lastButtonWidth;
	private int minButtonWidth = 118;
	private boolean resizeButtons = true;
	private int scrollIncrement = -1;
	private El stripWrap, strip, edge;

	TasksButtonsPanel() 
	{
		setId("ux-taskbuttons-panel");
		items = new ArrayList<IPlantTaskButton>();
	}

	public IPlantTaskButton addButton(Window win) 
	{
	    Element li = strip.createChild("<li></li>", edge.dom).dom;
	    IPlantTaskButton btn = new IPlantTaskButton(win, li);
	    items.add(btn);
	    
	    if(!buttonWidthSet) 
	    {
	    	lastButtonWidth = li.getOffsetWidth();
	    }
	    
	    setActiveButton(btn);
	    win.setData("taskButton", btn);
	    
	    if (isAttached()) 
	    {
	    	ComponentHelper.doAttach(btn);
	    }
	    
	    if(!isEnabled()) 
	    {
	    	btn.disable();
	    }
	    return btn;
	}

	public List<IPlantTaskButton> getItems() 
	{
		return items;
	}

	public void removeButton(IPlantTaskButton btn) 
	{
		Element li = (Element) btn.getElement().getParentElement();
		
	    if(li != null && li.getParentElement() != null) 
	    {
	    	li.getParentElement().removeChild(li);
	    }

	    items.remove(btn);

	    delegateUpdates();
	    ComponentHelper.doDetach(btn);
	}

	public void setActiveButton(IPlantTaskButton btn) 
	{
		delegateUpdates();
	}

	@Override
	protected void doAttachChildren() 
	{
		super.doAttachChildren();
	    
		for(IPlantTaskButton btn : items) 
		{
			ComponentHelper.doAttach(btn);
	    }
	}

	@Override
	protected void doDetachChildren() 
	{
		super.doDetachChildren();
	    
	    for(IPlantTaskButton btn : items) 
	    {
	    	ComponentHelper.doDetach(btn);
	    }
	}

	protected int getScrollIncrement() 
	{
		return scrollIncrement != -1 ? scrollIncrement : lastButtonWidth + 2;
	}

	@Override
	protected void onDisable() 
	{
		super.onDisable();
		  
		for(IPlantTaskButton btn : items) 
		{
			btn.disable();
		}
	}

	@Override
	protected void onEnable() 
	{
		super.onEnable();

		for(IPlantTaskButton btn : items) 
		{
			btn.enable();
		}
	}

	@Override
	protected void onRender(Element target,int index) 
	{
		super.onRender(target, index);
	    
		setElement(DOM.createDiv(), target, index);
		setStyleName("ux-taskbuttons-panel");

		stripWrap = el().createChild("<div class='ux-taskbuttons-strip-wrap'><ul class='ux-taskbuttons-strip'></ul></div>");
		el().createChild("<div class='ux-taskbuttons-strip-spacer'></div>");
		strip = stripWrap.firstChild();
		edge = strip.createChild("<li class='ux-taskbuttons-edge'></li>");
		strip.createChild("<div class='x-clear'></div>");
	}

	@Override
	protected void onResize(int width,int height) 
	{
		super.onResize(width, height);
		delegateUpdates();
	}

	private void autoScroll() 
	{
		//auto scroll not functional
	}

	private void autoSize() 
	{
		int count = items.size();
		int aw = el().getStyleWidth();

		if(!resizeButtons || count < 1) 
		{
			return;
		}

		int each = (int) Math.max(Math.min(Math.floor((aw - 4) / count) - buttonMargin, buttonWidth), minButtonWidth);
		NodeList<com.google.gwt.dom.client.Element> btns = stripWrap.dom.getElementsByTagName("button");

		El b = items.get(0).el();
		lastButtonWidth = b.findParent("li", 5).getWidth();

		for(int i = 0,len = btns.getLength();i < len;i++) 
		{
			Element btn = btns.getItem(i).cast();

			int tw = items.get(i).el().getParent().dom.getOffsetWidth();
			int iw = btn.getOffsetWidth();
			  
			btn.getStyle().setPropertyPx("width", (each - (tw - iw)));
		}
	}

	private void delegateUpdates() 
	{
		if(resizeButtons && rendered) 
		{
			autoSize();
		}
		  
		if(enableScroll && rendered) 
		{
			autoScroll();
		}
	}	
}


