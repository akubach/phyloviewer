package org.iplantc.de.client.views;

import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.controllers.EditorController;
import org.iplantc.de.client.dispatchers.ActionDispatcher;
import org.iplantc.de.client.dispatchers.DefaultActionDispatcher;
import org.iplantc.de.client.events.UserEvent;
import org.iplantc.de.client.events.UserEventHandler;
import org.iplantc.de.client.models.Workflow;
import org.iplantc.de.client.models.WorkflowStep;
import org.iplantc.de.client.utils.WindowManager;
import org.iplantc.de.client.views.taskbar.DefaultStartMenuComposer;
import org.iplantc.de.client.views.taskbar.IPlantTaskButton;
import org.iplantc.de.client.views.taskbar.IPlantTaskbar;
import org.iplantc.de.client.views.windows.IPlantWindow;

import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.core.client.GWT;

public class DesktopView extends ContentPanel
{
	// ////////////////////////////////////////
	// private variables
	private static final DEClientConstants CONSTANTS = (DEClientConstants)GWT
			.create(DEClientConstants.class);
	@SuppressWarnings("unused")
	private EditorController controllerEditor;
	private String idWorkspace;
	private WindowManager mgrWindow;
	private WorkflowGuidePanel guide;
	private ActionDispatcher actionDispatcher = new DefaultActionDispatcher();
	private IPlantTaskbar taskBar = new IPlantTaskbar(new DefaultStartMenuComposer("Some Heading"));

	// ////////////////////////////////////////
	// constructor
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

	// ////////////////////////////////////////
	// private methods
	private void showWindow(String tag)
	{
		IPlantWindow window = mgrWindow.getWindow(tag);

		// do we already have this window?
		if(window == null)
		{
			window = mgrWindow.add(tag);
		}

		// show the window and bring it to the front
		if(window != null)
		{
			window.show();
			window.toFront();
		}
	}

	// ////////////////////////////////////////
	private void dispatchUserEvent(String action, String tag)
	{
		if(action.equals(CONSTANTS.windowTag()))
		{
			showWindow(tag);
		}
		else if(action.equals(CONSTANTS.actionTag()))
		{
			actionDispatcher.dispatchAction(tag);
		}
	}

	// ////////////////////////////////////////
	private void initEventHandlers()
	{
		EventBus eventbus = EventBus.getInstance();
		// user action
		eventbus.addHandler(UserEvent.TYPE, new UserEventHandler()
		{
			@Override
			public void onEvent(UserEvent event)
			{
				dispatchUserEvent(event.getAction(), event.getTag());
			}
		});
	}

	// ////////////////////////////////////////
	private Workflow buildTestWorkflow()
	{
		Workflow ret = new Workflow("test");

		ret.addStep(new WorkflowStep("Manage Data", CONSTANTS.windowTag(), CONSTANTS.myDataTag()));
		ret.addStep(new WorkflowStep("Perform Analysis", CONSTANTS.windowTag(), ""));
		ret.addStep(new WorkflowStep("View Results", CONSTANTS.windowTag(), ""));

		return ret;
	}

	// ////////////////////////////////////////
	private void initWorkflowGuide()
	{
		Workflow workflow = buildTestWorkflow();
		guide = new WorkflowGuidePanel(workflow);

		setTopComponent(guide);
	}

	// ////////////////////////////////////////
	private void onHide(IPlantWindow window)
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

		taskBar.removeTaskButton((IPlantTaskButton)window.getData("taskButton"));
	}

	// ////////////////////////////////////////
	private void markActive(IPlantWindow window)
	{
		IPlantWindow activeWindow = mgrWindow.getActiveWindow();

		if(activeWindow != null && activeWindow != window)
		{
			markInactive(activeWindow);
		}

		taskBar.setActiveButton((IPlantTaskButton)window.getData("taskButton"));
		activeWindow = window;

		IPlantTaskButton btn = window.getData("taskButton");
		btn.addStyleName("active-win");
		window.setData("minimize", null);
	}

	// ////////////////////////////////////////
	private void markInactive(IPlantWindow window)
	{
		if(window == mgrWindow.getActiveWindow())
		{
			mgrWindow.setActiveWindow(null);

			IPlantTaskButton btn = window.getData("taskButton");
			btn.removeStyleName("active-win");
		}
	}

	// ////////////////////////////////////////
	private void onShow(IPlantWindow window)
	{
		IPlantTaskButton btn = window.getData("taskButton");
		window.setData("minimize", null);

		if(btn != null && taskBar.getButtons().contains(btn))
		{
			return;
		}

		taskBar.addTaskButton(window);
	}

	// ////////////////////////////////////////
	private void minimizeWindow(IPlantWindow window)
	{
		window.setData("minimize", true);
		window.hide();
	}

	// ////////////////////////////////////////
	private void initWindowManager()
	{
		mgrWindow = new WindowManager(idWorkspace, new WindowListener()
		{
			@Override
			public void windowActivate(WindowEvent we)
			{
				markActive((IPlantWindow)we.getWindow());
			}

			@Override
			public void windowDeactivate(WindowEvent we)
			{
				markInactive((IPlantWindow)we.getWindow());
			}

			@Override
			public void windowHide(WindowEvent we)
			{
				onHide((IPlantWindow)we.getWindow());
			}

			@Override
			public void windowMinimize(WindowEvent we)
			{
				minimizeWindow((IPlantWindow)we.getWindow());
			}

			@Override
			public void windowShow(WindowEvent we)
			{
				onShow((IPlantWindow)we.getWindow());
			}
		});
	}

	// ////////////////////////////////////////
	private void initEditorController()
	{
		controllerEditor = new EditorController(idWorkspace, mgrWindow);
	}
}
