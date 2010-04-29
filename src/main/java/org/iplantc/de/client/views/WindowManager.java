package org.iplantc.de.client.views;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Window;

public class WindowManager 
{
	//////////////////////////////////////////
	//private variables
	private WindowListener listener;
	private IplantWindow activeWindow;
	private List<IplantWindow> windows = new ArrayList<IplantWindow>();
		
	//////////////////////////////////////////
	//constructor
	public WindowManager(WindowListener listener)
	{
		this.listener = listener;
	}
	
	//////////////////////////////////////////
	//public methods
	public void setActiveWindow(IplantWindow window)
	{
		activeWindow = window;
	}
	
	//////////////////////////////////////////
	public IplantWindow getActiveWindow()
	{
		return activeWindow;
	}
	
	//////////////////////////////////////////
	public void add(IplantWindow window)
	{
		if(window != null)
		{
			if(windows.add(window)) 
			{
				window.addWindowListener(listener);
			}
		}
	}
	
	//////////////////////////////////////////	
	public List<IplantWindow> getWindows()
	{
		return windows;
	}
	
	//////////////////////////////////////////
	public void remove(IplantWindow window)
	{
		if(window != null)
		{
			for(Window item : windows)
			{
				if(item == window)
				{
					windows.remove(item);
					break;
				}
			}
		}		
	}
	
	//////////////////////////////////////////
	public void clear()
	{
		windows.clear();
	}
}
