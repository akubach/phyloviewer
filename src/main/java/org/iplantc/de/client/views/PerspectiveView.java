package org.iplantc.de.client.views;

import org.iplantc.de.client.windows.DataBrowserWindow;

import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;

public class PerspectiveView extends ContentPanel 
{
	//////////////////////////////////////////
	//private variables
	@SuppressWarnings("unused")
	private EditorController controllerEditor;
	private String idWorkspace;	
	private IPlantTaskbar taskBar = new IPlantTaskbar();	
	private WindowManager mgrWindow;
	
	//////////////////////////////////////////
	//constructor
	public PerspectiveView(String idWorkspace)
	{
		this.idWorkspace = idWorkspace;
			
		setHeaderVisible(false);
		setFooter(false);
		initEventHandlers();
		initWindowManager();	
		initEditorController();
		
		setBottomComponent(taskBar);
	}
	
	//////////////////////////////////////////
	private void initEventHandlers()
	{
		//TODO: add event handlers
		//EventBus eventbus = EventBus.getInstance();	
	}

	//////////////////////////////////////////
	private void onHide(IplantWindow window) 
	{
		if(window.getData("minimize") != null) 
		{
			markInactive(window);
			return;
		}
		    
		if(mgrWindow.getActiveWindow() == window) 
		{
			mgrWindow.setActiveWindow(null);
		}
		
		window.cleanup();
		mgrWindow.remove(window);
		
		taskBar.removeTaskButton((TaskButton) window.getData("taskButton"));
	}

	//////////////////////////////////////////
	private void markActive(IplantWindow window) 
	{
		IplantWindow activeWindow = mgrWindow.getActiveWindow();
		
		if(activeWindow != null && activeWindow != window) 
		{
			markInactive(activeWindow);
		}
	
		taskBar.setActiveButton((TaskButton) window.getData("taskButton"));
		activeWindow = window;
		
		TaskButton btn = window.getData("taskButton");
		btn.addStyleName("active-win");
		window.setData("minimize", null);
	}

	//////////////////////////////////////////
	private void markInactive(IplantWindow window) 
	{
		if(window == mgrWindow.getActiveWindow()) 
		{
			mgrWindow.setActiveWindow(null);
			
			TaskButton btn = window.getData("taskButton");
			btn.removeStyleName("active-win");
		}
	}

	//////////////////////////////////////////
	private void onShow(IplantWindow window) 
	{
		TaskButton btn = window.getData("taskButton");
		window.setData("minimize", null);
		
		if (btn != null && taskBar.getButtons().contains(btn)) 
		{
			return;
		}
		
		taskBar.addTaskButton(window);
	}
	
	//////////////////////////////////////////
	private void minimizeWindow(IplantWindow window) 
	{
	    window.setData("minimize", true);
	    window.hide();
	}
	
	//////////////////////////////////////////
	private void initWindowManager() 
	{
		mgrWindow = new WindowManager(new WindowListener() 
	    {
			@Override
			public void windowActivate(WindowEvent we) 
			{
				markActive((IplantWindow)we.getWindow());
			}

			@Override
			public void windowDeactivate(WindowEvent we) 
			{
				markInactive((IplantWindow)we.getWindow());
			}

			@Override
			public void windowHide(WindowEvent we) 
			{
				onHide((IplantWindow)we.getWindow());
			}

			@Override
			public void windowMinimize(WindowEvent we) 
			{
				minimizeWindow((IplantWindow)we.getWindow());
			}

			@Override
			public void windowShow(WindowEvent we) 
			{
				onShow((IplantWindow)we.getWindow());
			}
	    });
	}
	
	//////////////////////////////////////////
	private void initEditorController()
	{
		controllerEditor = new EditorController(idWorkspace,mgrWindow);		
	}
	
	//////////////////////////////////////////
	private void buildDataBrowserWindow()
	{
		IplantWindow w = new DataBrowserWindow(idWorkspace);
		
		mgrWindow.add(w);
		w.show();
		w.minimize();   
	}
	
	//////////////////////////////////////////
	private void initDefaultWindows()
	{		
		buildDataBrowserWindow();		
	}
	
	///////////////////////////////////////
	@Override
	protected void afterRender() 
	{
		super.afterRender();
		initDefaultWindows();
	}
}
