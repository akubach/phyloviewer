package org.iplantc.phyloviewer.client.tree.viewer;

import com.google.gwt.core.client.JavaScriptObject;

public class JsHitResult extends JavaScriptObject {

	protected JsHitResult() {}
	
	public final native JsHit getHit() /*-{return this.hit;}-*/;
}
