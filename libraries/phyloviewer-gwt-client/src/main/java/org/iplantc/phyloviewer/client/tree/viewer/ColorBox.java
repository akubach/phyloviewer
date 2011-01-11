package org.iplantc.phyloviewer.client.tree.viewer;

import com.google.gwt.user.client.ui.TextBox;

/**
 * Just a TextBox with the css class "color" added, for attaching a color picker widget to
 */
public class ColorBox extends TextBox
{
	public ColorBox()
	{
		this.addStyleName("color");
	}
}
