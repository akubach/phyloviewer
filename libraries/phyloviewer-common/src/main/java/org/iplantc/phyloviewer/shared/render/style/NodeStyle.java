package org.iplantc.phyloviewer.shared.render.style;

import com.google.gwt.user.client.rpc.IsSerializable;

public class NodeStyle extends ElementStyle implements INodeStyle, IsSerializable {

	public NodeStyle(String fillColor,String strokeColor,double strokeWidth){
		super(fillColor, strokeColor, strokeWidth);
	}
}
