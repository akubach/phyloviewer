package org.iplantc.recon.client;

import org.iplantc.phyloviewer.shared.layout.ILayout;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;

import com.google.gwt.core.client.JavaScriptObject;

public class JsLayoutCladogram extends JavaScriptObject implements ILayout {
	
	protected JsLayoutCladogram() {}

	@Override
	public final Vector2 getPosition(INode node) 
	{ 
		JsVector2 v = this.getPositionNative(node);
		return new Vector2 (v.getX(),v.getY());
	}

	@Override
	public final Box2D getBoundingBox(INode node)
	{
		JsBox2 box = this.getBoundingBoxNative(node);
		JsVector2 min = box.getMin();
		JsVector2 max = box.getMax();
		return new Box2D(new Vector2(min.getX(),min.getY()),new Vector2(max.getX(),max.getY()));
	}
	
	private final native JsVector2 getPositionNative(INode node) /*-{ return this.positions[node.id]; }-*/;
	private final native JsBox2 getBoundingBoxNative(INode node) /*-{ return this.bounds[node.id]; }-*/;

	@Override
	public final void layout(ITree tree) {
		// No-op
	}

	@Override
	public final void init(int numberOfNodes) {
		// No-op
		
	}

	@Override
	public final boolean containsNode(INode node) {
		return true;
	}

}
