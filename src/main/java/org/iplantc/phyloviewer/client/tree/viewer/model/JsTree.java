/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.model;

import com.google.gwt.core.client.JavaScriptObject;


public class JsTree extends JavaScriptObject implements ITree {

	protected JsTree() {
	}
	
	public final native void setRootNode ( INode node ) /*-{ this.root = node; }-*/;
	public final native INode getRootNode() /*-{ return this.root; }-*/;
	
	public final native String getName() /*-{ return this.name; }-*/;
	
	public final int getNumberOfNodes()
	{
		INode root = this.getRootNode();
		if ( null == root ) {
			return 0;
		}
		
		return this.countNumberOfNodes(root);
	}
	
	private final int countNumberOfNodes ( INode node ) {
		int count = 1;
		if (!node.isLeaf()) {
			for ( int i = 0; i < node.getNumberOfChildren(); ++i ) {
				count += this.countNumberOfNodes(node.getChild(i));
			}
		}
		return count;
	}
}
