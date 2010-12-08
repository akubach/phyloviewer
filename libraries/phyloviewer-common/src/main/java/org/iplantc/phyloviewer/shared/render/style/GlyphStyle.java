package org.iplantc.phyloviewer.shared.render.style;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GlyphStyle extends ElementStyle implements IGlyphStyle, IsSerializable {

	public GlyphStyle(String fillColor,String strokeColor,double strokeWidth){
		super(fillColor, strokeColor, strokeWidth);
	}
}
