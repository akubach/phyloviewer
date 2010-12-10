/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.model;

import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.model.Tree;

public class JSONParser {

	private final static native JsTree getTree(String json) /*-{ return eval(json); }-*/;
	
	public static ITree parseJSON(String json) {
		JsTree tree = getTree( "(" + json + ")" );
		return new Tree(tree.getId(),tree.getRootNode());
	}
}
