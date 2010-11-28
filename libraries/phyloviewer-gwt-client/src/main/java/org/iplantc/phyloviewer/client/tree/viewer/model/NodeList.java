/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.model;

import java.util.AbstractList;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/** 
 * Wraps a JsArray in a List implementation, for Collections goodness
 */
public class NodeList extends AbstractList<JsNode> {

	private final JsArray<JavaScriptObject> jsArray;
	
	public NodeList(JsArray<JavaScriptObject> jsArray) {
		this.jsArray = jsArray;
	}

	@Override
	public JsNode get(int index) {
		return (JsNode) this.jsArray.get(index);
	}
	
	@Override
	public JsNode set(int index, JsNode element) {
		JsNode prev = this.get(index);
		jsArray.set(index, element);
		
		return prev;
	}

	@Override
	public int size() {
		return jsArray.length();
	}
}
