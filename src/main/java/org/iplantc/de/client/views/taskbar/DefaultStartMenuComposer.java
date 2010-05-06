package org.iplantc.de.client.views.taskbar;

import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;

public class DefaultStartMenuComposer extends StartMenuComposer
{
	// ////////////////////////////////////////
	// constructor
	public DefaultStartMenuComposer(String header)
	{
		super(header);
	}

	// ////////////////////////////////////////
	// private methods
	private MenuItem buildMenuItem(String caption, String action, String tag, String style)
	{
		MenuItem ret = new MenuItem(caption);

		ret.setData(constants.action(), action);
		ret.setData(constants.tag(), tag);
		ret.setIcon(IconHelper.createStyle(style));
		ret.addSelectionListener(menuListener);

		return ret;
	}

	// ////////////////////////////////////////
	private void addMyDataMenuItem(StartMenu menu)
	{
		MenuItem item = buildMenuItem(displayStrings.myData(), constants.windowTag(), constants
				.myDataTag(), "icon-grid");
		menu.add(item);
	}

	// ////////////////////////////////////////
	private void addJobsMenuItem(StartMenu menu)
	{
		MenuItem item = buildMenuItem(displayStrings.myJobs(), constants.windowTag(), constants
				.myJobsTag(), "icon-grid");
		menu.add(item);
	}

	// ////////////////////////////////////////
	private void addLogoutMenuItem(StartMenu menu)
	{
		MenuItem item = buildMenuItem(displayStrings.logout(), constants.actionTag(), constants
				.logoutTag(), "logout");
		menu.addTool(item);
	}

	// ////////////////////////////////////////
	@Override
	protected void addMenuItems(StartMenu menu)
	{
		addMyDataMenuItem(menu);
		addJobsMenuItem(menu);
		addLogoutMenuItem(menu);
	}
}
