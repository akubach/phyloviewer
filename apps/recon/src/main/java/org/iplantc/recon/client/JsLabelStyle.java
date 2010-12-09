package org.iplantc.recon.client;

import org.iplantc.phyloviewer.shared.render.style.ILabelStyle;

import com.google.gwt.core.client.JavaScriptObject;

public class JsLabelStyle extends JavaScriptObject implements ILabelStyle {

	protected JsLabelStyle() {}

	@Override
	public final native String getColor() /*-{return this.color;}-*/;

	@Override
	public final native void setColor(String color) /*-{this.color=color;}-*/;

}
