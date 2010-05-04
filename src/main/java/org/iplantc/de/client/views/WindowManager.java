package org.iplantc.de.client.views;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.de.client.factories.WindowFactory;
import org.iplantc.de.client.windows.IPlantWindow;

import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Window;

public class WindowManager 
{
	//////////////////////////////////////////
	//private variables
	private String idWorkspace;
	private WindowListener listener;
	private IPlantWindow activeWindow;
	private List<IPlantWindow> windows = new ArrayList<IPlantWindow>();
		
	//////////////////////////////////////////
	//constructor
	public WindowManager(String idWorkspace, WindowListener listener)
	{
		this.idWorkspace = idWorkspace;
		this.listener = listener;
	}
	
	//////////////////////////////////////////
	//public methods
	public void setActiveWindow(IPlantWindow window)
	{
		activeWindow = window;
	}
	
	//////////////////////////////////////////
	public IPlantWindow getActiveWindow()
	{
		return activeWindow;
	}
	
	//////////////////////////////////////////
	public IPlantWindow add(String tag)
	{
		WindowFactory factory = WindowFactory.getInstance();
		IPlantWindow ret = factory.buildWindow(idWorkspace, tag);
		
		add(ret);
		
		return ret;
	}
	
	//////////////////////////////////////////
	public void add(IPlantWindow window)
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
	public IPlantWindow getWindow(String tag)
	{
		IPlantWindow ret = null;
		
		for(IPlantWindow window : windows)
		{
			if(window.getTag().equals(tag))
			{
				ret = window;
				break;
			}
		}		
		
		return ret;
	}
	
	//////////////////////////////////////////	
	public List<IPlantWindow> getWindows()
	{
		return windows;
	}
	
	//////////////////////////////////////////
	public void remove(IPlantWindow window)
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
