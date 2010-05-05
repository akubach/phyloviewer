package org.iplantc.de.client.jobs;

import org.iplantc.de.client.DEDisplayStrings;
import org.iplantc.de.client.images.Resources;

import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;

/**
 * Toolbar for job configuration panel. Provides buttons for saving,finishing
 * and cancel
 * 
 * @author sriram
 * 
 */
public class JobToolBar extends ToolBar {

	private Button cancel;

	private Button save;

	private Status info;

	private Button finish;

	private DEDisplayStrings displayStrings = (DEDisplayStrings) GWT
			.create(DEDisplayStrings.class);

	public Button getFinish() {
		return finish;
	}

	/**
	 * Create a new JobToolBar
	 */
	public JobToolBar() {
		super();
		info = new Status();

		cancel = new Button(displayStrings.cancel());
		save = new Button(displayStrings.save());
		finish = new Button(displayStrings.finish());

		cancel.setIcon(Resources.ICONS.cancel());
		save.setIcon(Resources.ICONS.save());
		finish.setIcon(Resources.ICONS.apply());

		this.add(info);
		this.add(new FillToolItem());
		this.add(save);
		this.add(cancel);
	//	this.add(finish);
		this.add(new FillToolItem());

	}

	public ToolBar assembleView() {
		this.setWidth(300);
		return this;
	}

	public Button getCancel() {
		return cancel;
	}

	public Button getSave() {
		return save;
	}

	public void setInfo(Status info) {
		this.info = info;
	}

	public Status getInfo() {
		return info;
	}

}
