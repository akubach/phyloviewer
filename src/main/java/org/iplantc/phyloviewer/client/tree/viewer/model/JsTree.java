package org.iplantc.phyloviewer.client.tree.viewer.model;

import com.google.gwt.core.client.JavaScriptObject;


public class JsTree extends JavaScriptObject implements ITree {

	protected JsTree() {
	}
	
	public final native void setRootNode ( INode node ) /*-{ this.root = node; }-*/;
	public final native INode getRootNode() /*-{ return this.root; }-*/;
	
	public final native String getName() /*-{ return this.name; }-*/;
}
