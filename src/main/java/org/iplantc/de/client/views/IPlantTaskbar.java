package org.iplantc.de.client.views;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.WindowManager;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class IPlantTaskbar extends LayoutContainer 
{
	protected StartBox startBox;  //west
	protected TasksButtonsPanel tbPanel; // center

	public IPlantTaskbar() 
	{
		setId("ux-taskbar");
		setLayout(new RowLayout(Orientation.HORIZONTAL));
		tbPanel = new TasksButtonsPanel();
		add(tbPanel,new RowData(1,1));
	}

	/**
	 * Adds a button.
	 * 
	 * @param win the window
	 * @return the new task button
	 */
	public TaskButton addTaskButton(Window win) 
	{
		return tbPanel.addButton(win);
	}

	/**
	 * Returns the bar's buttons.
	 * 
	 * @return the buttons
	 */
	public List<TaskButton> getButtons() 
	{
		return tbPanel.getItems();
	}

	/**
	 * Removes a button.
	 * 
	 * @param btn the button to remove
	 */
	public void removeTaskButton(TaskButton btn) 
	{
		tbPanel.removeButton(btn);
	}

	/**
	 * Sets the active button.
	 * 
	 * @param btn the button
	 */
	public void setActiveButton(TaskButton btn) 
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

class TaskButton extends Button 
{
	private Window win;

	TaskButton(Window win, Element parent) 
	{
		this.win = win;
	    
		setText(win.getHeading());
		setIcon(win.getIcon());
		template = new Template(getButtonTemplate());

		render(parent);
	}

	@Override
	protected void autoWidth() 
	{
		  
	}
	  
	@Override
	public void setIcon(AbstractImagePrototype icon) 
	{
		if(rendered) 
		{
			El oldIcon = buttonEl.selectNode(".x-taskbutton-icon");
				
			if(oldIcon != null) 
			{
				oldIcon.remove();
				buttonEl.setPadding(new Padding(7, 0, 7, 0));
			}
				
			if(icon != null) 
			{
				buttonEl.setPadding(new Padding(7, 0, 7, 20));
					
				Element e = (Element) icon.createElement().cast();
				e.setClassName("x-taskbutton-icon");
				buttonEl.insertFirst(e);
				El.fly(e).makePositionable(true);

				String align = "b-b";  //assume bottom alignment
				if(getIconAlign() == IconAlign.TOP) 
				{
					align = "t-t";
				} 
				else if(getIconAlign() == IconAlign.LEFT) 
				{
					align = "l-l";
				}
				else if(getIconAlign() == IconAlign.RIGHT) 
				{
					align = "r-r";
				}
					
				El.fly(e).alignTo(buttonEl.dom, align, null);
			}
		}
	
		this.icon = icon;
	}

	@Override
	protected void onClick(ComponentEvent ce) 
	{
		super.onClick(ce);
			
		if(win.getData("minimized") != null || !win.isVisible()) 
		{
			win.show();
		}
		else if(win == WindowManager.get().getActive()) 
		{
			win.minimize();
		} 
		else 
		{
			win.toFront();
		}
	}

	@Override
	protected void onResize(int width, int height) 
	{
		super.onResize(width,height);
		buttonEl.setSize(width - 8,height,true);
	}

	private native String getButtonTemplate() /*-{
		return [
	    '<table border="0" cellpadding="0" cellspacing="0" class="x-btn-wrap"><tbody><tr>',
	    '<td class="ux-taskbutton-left"><i>&#160;</i></td><td class="ux-taskbutton-center"><em unselectable="on"><button class="x-btn-text" type="{1}" style="height:28px;">{0}</button></em></td><td class="ux-taskbutton-right"><i>&#160;</i></td>',
	    '</tr></tbody></table>'
	    ].join("");
		}-*/;
}

class TasksButtonsPanel extends BoxComponent 
{
	private int buttonMargin = 2;
	private int buttonWidth = 168;
	private boolean buttonWidthSet = false;
	private boolean enableScroll = true;	 
	private List<TaskButton> items;
	private int lastButtonWidth;
	private int minButtonWidth = 118;
	private boolean resizeButtons = true;
	private int scrollIncrement = -1;
	private El stripWrap, strip, edge;

	TasksButtonsPanel() 
	{
		setId("ux-taskbuttons-panel");
		items = new ArrayList<TaskButton>();
	}

	public TaskButton addButton(Window win) 
	{
	    Element li = strip.createChild("<li></li>", edge.dom).dom;
	    TaskButton btn = new TaskButton(win, li);
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

	public List<TaskButton> getItems() 
	{
		return items;
	}

	public void removeButton(TaskButton btn) 
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

	public void setActiveButton(TaskButton btn) 
	{
		delegateUpdates();
	}

	@Override
	protected void doAttachChildren() 
	{
		super.doAttachChildren();
	    
		for(TaskButton btn : items) 
		{
			ComponentHelper.doAttach(btn);
	    }
	}

	@Override
	protected void doDetachChildren() 
	{
		super.doDetachChildren();
	    
	    for(TaskButton btn : items) 
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
		  
		for(TaskButton btn : items) 
		{
			btn.disable();
		}
	}

	@Override
	protected void onEnable() 
	{
		super.onEnable();

		for(TaskButton btn : items) 
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


