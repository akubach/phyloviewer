package org.iplantc.phyloviewer.shared.render.style;

import com.google.gwt.user.client.rpc.IsSerializable;

public class BranchStyle extends ElementStyle implements IBranchStyle, IsSerializable {
	
	public BranchStyle(String fillColor,String strokeColor,double strokeWidth){
		super(fillColor, strokeColor, strokeWidth);
	}
}
