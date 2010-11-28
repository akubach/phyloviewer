package org.iplantc.phyloviewer.client.tree.viewer.render.style;

import org.iplantc.phyloviewer.shared.render.style.INodeStyle;

import com.google.gwt.core.client.JavaScriptObject;

public class JsNodeStyle extends JavaScriptObject implements INodeStyle {

	protected JsNodeStyle() {}
	
	@Override
	public final native IElementStyle getElementStyle(Element element) /*-{
		if (!this[element]) {
			this[element] = {};
		}
		return this[element];
	}-*/;
	
	public static class JsElementStyle extends JavaScriptObject implements IElementStyle {
		protected JsElementStyle() {}

		@Override
		public final native String getFillColor() /*-{
			return this.fillColor;
		}-*/;

		@Override
		public final native double getLineWidth() /*-{
			return this.lineWidth;
		}-*/;

		@Override
		public final native String getStrokeColor() /*-{
			return this.strokeColor;
		}-*/;

		@Override
		public final native void setFillColor(String color) /*-{
			this.fillColor = color;
		}-*/;

		@Override
		public final native void setLineWidth(double width) /*-{
			this.lineWidth = width;
		}-*/;

		@Override
		public final native void setStrokeColor(String color) /*-{
			this.strokeColor = color;
		}-*/;
		
		
	}
}
