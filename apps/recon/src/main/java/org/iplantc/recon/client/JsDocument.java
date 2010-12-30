package org.iplantc.recon.client;

import org.iplantc.phyloviewer.client.tree.viewer.model.JsTree;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;

import com.google.gwt.core.client.JavaScriptObject;

public class JsDocument extends JavaScriptObject {
	
	protected JsDocument() {}

	public final ITree getTree() {
		return this.getTreeNative();
	}
	
	private final native JsTree getTreeNative() /*-{return this.tree;}-*/;

	public final IStyleMap getStyleMap() {
		return this.getStyleMapNative();
	}
	
	private final native JsStyleMap getStyleMapNative() /*-{return this.styleMap;}-*/;
}
