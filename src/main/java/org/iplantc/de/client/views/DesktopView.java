package org.iplantc.de.client.views;

import org.iplantc.de.client.ActionDispatcher;
import org.iplantc.de.client.DefaultActionDispatcher;
import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.controllers.EditorController;
import org.iplantc.de.client.events.UserEvent;
import org.iplantc.de.client.events.UserEventHandler;
import org.iplantc.de.client.models.Workflow;
import org.iplantc.de.client.models.WorkflowStep;
import org.iplantc.de.client.views.taskbar.DefaultStartMenuComposer;
import org.iplantc.de.client.views.taskbar.IPlantTaskButton;
import org.iplantc.de.client.views.taskbar.IPlantTaskbar;
import org.iplantc.de.client.windows.IplantWindow;

import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.core.client.GWT;

public class DesktopView extends ContentPanel 
{
	//////////////////////////////////////////
	//private variables
	@SuppressWarnings("unused")
	private EditorController controllerEditor;
	private String idWorkspace;	
	private WindowManager mgrWindow;
	private WorkflowGuide guide;
	private ActionDispatcher actionDispatcher = new DefaultActionDispatcher();
	private IPlantTaskbar taskBar = new IPlantTaskbar(new DefaultStartMenuComposer("Some Heading"));	
	private static final DEClientConstants constants = (DEClientConstants)GWT.create(DEClientConstants.class);
	
	//////////////////////////////////////////
	//constructor
	public DesktopView(String idWorkspace)
	{
		this.idWorkspace = idWorkspace;
			
		setHeaderVisible(false);
		setFooter(false);
		initEventHandlers();
		initWindowManager();	
		initEditorController();
		initWorkflowGuide();
		
		setBottomComponent(taskBar);
	}
	
	//////////////////////////////////////////
	//private methods
	private void showWindow(String tag)
	{
		IplantWindow window = mgrWindow.getWindow(tag);
		
		//do we already have this window?
		if(window == null)
		{
			window = mgrWindow.add(tag);						
		}
		
		//show the window and bring it to the front
		if(window != null)
		{
			window.show();
			window.toFront();
		}		
	}
	
	//////////////////////////////////////////
	private void dispatchUserEvent(String action,String tag)
	{
		if(action.equals(constants.windowTag()))
		{
			showWindow(tag);
		}
		else if(action.equals(constants.actionTag()))
		{
			actionDispatcher.dispatchAction(tag);
		}
	}
	
	//////////////////////////////////////////
	private void initEventHandlers()
	{
		EventBus eventbus = EventBus.getInstance();	
		//user action
		eventbus.addHandler(UserEvent.TYPE,new UserEventHandler()
		{
			@Override
			public void onEvent(UserEvent event) 
			{
				dispatchUserEvent(event.getAction(),event.getTag());
			}
		});
	}

	//////////////////////////////////////////
	private Workflow buildTestWorkflow()
	{
		Workflow ret = new Workflow("test");
		
		ret.addStep(new WorkflowStep("Manage Data",constants.windowTag(),constants.myDataTag()));
		ret.addStep(new WorkflowStep("Perform Analysis",constants.windowTag(),""));
		ret.addStep(new WorkflowStep("View Results",constants.windowTag(),""));
		
		return ret;
	}
	
	//////////////////////////////////////////
	private void initWorkflowGuide()
	{
		Workflow workflow = buildTestWorkflow();
		guide = new WorkflowGuide(workflow);
		
		setTopComponent(guide);
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
		
		taskBar.removeTaskButton((IPlantTaskButton) window.getData("taskButton"));
	}

	//////////////////////////////////////////
	private void markActive(IplantWindow window) 
	{
		IplantWindow activeWindow = mgrWindow.getActiveWindow();
		
		if(activeWindow != null && activeWindow != window) 
		{
			markInactive(activeWindow);
		}
	
		taskBar.setActiveButton((IPlantTaskButton) window.getData("taskButton"));
		activeWindow = window;
		
		IPlantTaskButton btn = window.getData("taskButton");
		btn.addStyleName("active-win");
		window.setData("minimize", null);
	}

	//////////////////////////////////////////
	private void markInactive(IplantWindow window) 
	{
		if(window == mgrWindow.getActiveWindow()) 
		{
			mgrWindow.setActiveWindow(null);
			
			IPlantTaskButton btn = window.getData("taskButton");
			btn.removeStyleName("active-win");
		}
	}

	//////////////////////////////////////////
	private void onShow(IplantWindow window) 
	{
		IPlantTaskButton btn = window.getData("taskButton");
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
		mgrWindow = new WindowManager(idWorkspace,new WindowListener() 
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
}
