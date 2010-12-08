package org.iplantc.phyloviewer.shared.render.style;

import com.google.gwt.user.client.rpc.IsSerializable;

public class LabelStyle extends ElementStyle implements ILabelStyle, IsSerializable {

	public LabelStyle(String fillColor,String strokeColor,double strokeWidth){
		super(fillColor, strokeColor, strokeWidth);
	}
}
