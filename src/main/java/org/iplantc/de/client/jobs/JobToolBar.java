package org.iplantc.de.client.jobs;

import org.iplantc.de.client.DEDisplayStrings;
import org.iplantc.de.client.images.Resources;

import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Toolbar for job configuration panel. Provides buttons for saving,finishing and cancel
 * 
 * @author sriram
 * 
 */
public class JobToolBar extends ToolBar
{
	private static final DEDisplayStrings displayStrings = (DEDisplayStrings)GWT.create(DEDisplayStrings.class);
	
	private Button cancel;

	private Button save;

	private Status info;

	private Button finish;

	/**
	 * Create a new JobToolBar
	 */
	public JobToolBar()
	{
		super();
		info = new Status();

		cancel = new Button(displayStrings.cancel());
		save = new Button(displayStrings.save());
		finish = new Button(displayStrings.finish());

		cancel.setIcon(AbstractImagePrototype.create(Resources.ICONS.cancel()));
		save.setIcon(AbstractImagePrototype.create(Resources.ICONS.save()));
		finish.setIcon(AbstractImagePrototype.create(Resources.ICONS.apply()));

		this.add(info);
		this.add(new FillToolItem());
		this.add(save);
		this.add(cancel);
		// this.add(finish);
		this.add(new FillToolItem());

	}

	public ToolBar assembleView()
	{
		this.setWidth(300);
		return this;
	}

	public Button getFinish()
	{
		return finish;
	}	
	
	public Button getCancel()
	{
		return cancel;
	}

	public Button getSave()
	{
		return save;
	}

	public void setInfo(Status info)
	{
		this.info = info;
	}

	public Status getInfo()
	{
		return info;
	}

}
