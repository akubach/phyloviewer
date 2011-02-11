package org.iplantc.phyloviewer.client.tree.viewer.render.style;

import org.iplantc.phyloviewer.shared.render.style.INodeStyle;

import com.google.gwt.core.client.JavaScriptObject;

public class JsNodeStyle extends JavaScriptObject implements INodeStyle
{
	protected JsNodeStyle()
	{
	}

	@Override
	public final native String getColor() /*-{return this.color;}-*/;

	@Override
	public final native void setColor(String color) /*-{this.color=color;}-*/;

	@Override
	public final native double getPointSize() /*-{return this.pointSize;}-*/;

	@Override
	public final native void setPointSize(double size) /*-{this.pointSize=size;}-*/;
	
	private final native String getShapeNative() /*-{return this.nodeShape;}-*/;

	@Override
	public final Shape getShape()
	{
		String shape = this.getShapeNative();
		if(shape.equalsIgnoreCase("square"))
		{
			return Shape.SHAPE_SQUARE;
		}

		return Shape.SHAPE_CIRCLE;
	}
}
