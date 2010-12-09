package org.iplantc.recon.client;

import org.iplantc.phyloviewer.shared.render.style.IGlyphStyle;

import com.google.gwt.core.client.JavaScriptObject;

public class JsGlyphStyle extends JavaScriptObject implements IGlyphStyle {

	protected JsGlyphStyle() {}
	
	@Override
	public final native String getStrokeColor() /*-{return this.strokeColor;}-*/;

	@Override
	public final native void setStrokeColor(String color) /*-{this.strokeColor=color;}-*/;

	@Override
	public final native String getFillColor() /*-{return this.fillColor;}-*/;

	@Override
	public final native void setFillColor(String color) /*-{this.fillColor=color;}-*/;

	@Override
	public final native double getLineWidth() /*-{return this.lineWidth;}-*/;

	@Override
	public final native void setLineWidth(double width) /*-{this.lineWidth=width;}-*/;

}
