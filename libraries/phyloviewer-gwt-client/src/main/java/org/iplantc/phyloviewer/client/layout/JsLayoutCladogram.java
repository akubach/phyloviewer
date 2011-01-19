package org.iplantc.phyloviewer.client.layout;

import org.iplantc.phyloviewer.client.math.JsBox2;
import org.iplantc.phyloviewer.client.math.JsVector2;
import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.core.client.JavaScriptObject;

public class JsLayoutCladogram extends JavaScriptObject implements ILayoutData {
	
	protected JsLayoutCladogram() {}

	@Override
	public final Vector2 getPosition(INode node) 
	{ 
		JsVector2 v = this.getPositionNative(node.getId());
		return new Vector2 (v.getX(),v.getY());
	}
	
	@Override
	public final Box2D getBoundingBox(int nodeId) {
		JsBox2 box = this.getBoundingBoxNative(nodeId);
		JsVector2 min = box.getMin();
		JsVector2 max = box.getMax();
		return new Box2D(new Vector2(min.getX(),min.getY()),new Vector2(max.getX(),max.getY()));
	}

	@Override
	public final Box2D getBoundingBox(INode node)
	{
		return this.getBoundingBox(node.getId());
	}
	
	private final native JsVector2 getPositionNative(int nodeId) /*-{ return this.positions[nodeId]; }-*/;
	private final native JsBox2 getBoundingBoxNative(int nodeId) /*-{ return this.bounds[nodeId]; }-*/;

	@Override
	public final boolean containsNode(INode node) {
		return true;
	}

}
