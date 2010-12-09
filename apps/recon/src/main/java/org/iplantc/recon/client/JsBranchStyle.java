package org.iplantc.recon.client;

import org.iplantc.phyloviewer.shared.render.style.IBranchStyle;

import com.google.gwt.core.client.JavaScriptObject;

public class JsBranchStyle extends JavaScriptObject implements IBranchStyle {

	protected JsBranchStyle() {}
	
	@Override
	public final native String getStrokeColor() /*-{return this.strokeColor;}-*/;

	@Override
	public final native void setStrokeColor(String color) /*-{this.strokeColor=color;}-*/;

	@Override
	public final native double getLineWidth() /*-{return this.lineWidth;}-*/;

	@Override
	public final native void setLineWidth(double width) /*-{this.lineWidth=width;}-*/;

}
