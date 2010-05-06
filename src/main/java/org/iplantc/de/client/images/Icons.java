package org.iplantc.de.client.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Provides access to bundled image resources.
 */
public interface Icons extends ClientBundle
{
	@Source("Green.png")
	ImageResource green();

	@Source("Orange.png")
	ImageResource orange();

	@Source("Red.png")
	ImageResource red();

	@Source("list-items.gif")
	ImageResource listItems();

	@Source("User.png")
	ImageResource user();

	@Source("add.gif")
	ImageResource add();

	@Source("Upload.png")
	ImageResource upload();

	@Source("Edit.png")
	ImageResource edit();

	@Source("action_delete.gif")
	ImageResource cancel();

	@Source("arrow_back.gif")
	ImageResource back();

	@Source("arrow_next.gif")
	ImageResource next();

	@Source("save.gif")
	ImageResource save();

	@Source("action_check.gif")
	ImageResource apply();

	@Source("Refresh.png")
	ImageResource refresh();

	@Source("download.gif")
	ImageResource download();

	@Source("Play.png")
	ImageResource play();
}
