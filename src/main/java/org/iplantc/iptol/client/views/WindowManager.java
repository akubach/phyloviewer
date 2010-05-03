package org.iplantc.iptol.client.views;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.iptol.client.factories.WindowFactory;
import org.iplantc.iptol.client.windows.IplantWindow;

import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Window;

public class WindowManager 
{
	//////////////////////////////////////////
	//private variables
	private String idWorkspace;
	private WindowListener listener;
	private IplantWindow activeWindow;
	private List<IplantWindow> windows = new ArrayList<IplantWindow>();
		
	//////////////////////////////////////////
	//constructor
	public WindowManager(String idWorkspace, WindowListener listener)
	{
		this.idWorkspace = idWorkspace;
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
	public IplantWindow add(String tag)
	{
		WindowFactory factory = WindowFactory.getInstance();
		IplantWindow ret = factory.buildWindow(idWorkspace, tag);
		
		add(ret);
		
		return ret;
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
	public IplantWindow getWindow(String tag)
	{
		IplantWindow ret = null;
		
		for(IplantWindow window : windows)
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
