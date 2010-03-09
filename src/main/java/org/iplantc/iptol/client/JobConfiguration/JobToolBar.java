package org.iplantc.iptol.client.JobConfiguration;

import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
/**
 * Toolbar for job configuration panel.
 * Provides buttons for navigating thru job configuration steps
 * @author sriram
 *
 */
public class JobToolBar extends  ToolBar{
	
	private Button next;
	
	private Button prev;
	
	private Button cancel;
	
	private Button save;
	
	
	public JobToolBar() {
		super();
		next = new Button("Next");
		prev = new Button("Previous");
		cancel = new Button("Cancel");
		save = new Button("Save");
		
		prev.setIcon(IconHelper.createPath("./images/arrow_back.gif"));
		next.setIcon(IconHelper.createPath("./images/arrow_next.gif"));
		cancel.setIcon(IconHelper.createPath("./images/action_delete.gif"));
		save.setIcon(IconHelper.createPath("./images/save.gif"));
		this.add(prev);
		this.add(next);
		this.add(new FillToolItem());
		this.add(save);
		this.add(cancel);		
	}
	
	public ToolBar assembleView() {
		this.setWidth(300);
		return this;
	}
	
	public Button getNext() {
		return next;
	}

	public Button getPrev() {
		return prev;
	}

	public Button getCancel() {
		return cancel;
	}

	public Button getSave() {
		return save;
	}
	
}
