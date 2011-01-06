package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.client.math.JsBox2;
import org.iplantc.phyloviewer.client.math.JsVector2;

import com.google.gwt.core.client.JavaScriptObject;

public class JsHit extends JavaScriptObject {

	protected JsHit() {}

	public final native int nodeId() /*-{return this.nodeId;}-*/;
	public final native JsVector2 position() /*-{return this.position;}-*/;
	public final native JsBox2 boundingBox() /*-{return this.boundingBox;}-*/;
}
