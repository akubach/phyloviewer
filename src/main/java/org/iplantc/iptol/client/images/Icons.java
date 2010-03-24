package org.iplantc.iptol.client.images;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

public interface Icons extends ImageBundle{
	
	@Resource("Green.png")
	AbstractImagePrototype green();
	
	@Resource("Orange.png")
	AbstractImagePrototype orange();
	
	@Resource("Red.png")
	AbstractImagePrototype red();
	
	@Resource("list-items.gif")
	AbstractImagePrototype listItems();
	
	@Resource("User.png")
	AbstractImagePrototype user();
	
	@Resource("add.gif")
	AbstractImagePrototype add();
	
	@Resource("Upload.png")
	AbstractImagePrototype upload();
	
	@Resource("Edit.png")
	AbstractImagePrototype edit();
	
	@Resource("action_delete.gif")
	AbstractImagePrototype cancel();
	
	@Resource("arrow_back.gif")
	AbstractImagePrototype back();
	
	@Resource("arrow_next.gif")
	AbstractImagePrototype next();
	
	@Resource("save.gif")
	AbstractImagePrototype save();
	
	@Resource("action_check.gif")
	AbstractImagePrototype apply();
	
}
