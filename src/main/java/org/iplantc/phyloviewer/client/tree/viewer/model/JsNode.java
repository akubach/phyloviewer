/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.model;

import java.util.Collections;
import java.util.Comparator;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class JsNode extends JavaScriptObject implements INode {

	protected JsNode() {
	}
	
	/**
	 * Gets the id from the JavaScript object.  Will be null if not defined. 
	 * @return
	 */
	
	private final native Integer getNativeId() /*-{ return this.id; }-*/;
	
	/**
	 * Set the new id.
	 * @param id
	 */
	
	private final native void setNativeId ( Integer id ) /*-{ this.id = id; }-*/;
	
	public final int getId()
	{
		Integer id = this.getNativeId();
		if ( null == id )
		{
			id = UniqueIdGenerator.getInstance().getNextId();
			this.setNativeId ( id );
		}
		
		return id;
	}
	
	public final native String getLabel() /*-{ return this.name; }-*/;
	public final native void setLabel(String label) /*-{ this.name = label; }-*/;
	
	private final native <T extends JavaScriptObject> JsArray<T> getChildren() /*-{ return this.children; }-*/;
	private final JsNode getNativeChild(int index) { return (JsNode) this.getChildren().get(index); }
	
	public final int getNumberOfChildren()
	{
		if ( null == this.getChildren() )
			return 0;
		return this.getChildren().length();
	}
	
	public final INode getChild(int index) { return (JsNode) this.getChildren().get(index); }

	public final native String getNodeColor() /*-{ return this.nodeColor; }-*/;
	public final native void setNodeColor(String color) /*-{ this.nodeColor = color; }-*/;

	public final Boolean isLeaf() {
		return 0 == this.getNumberOfChildren();
	}
	
	public final int getNumberOfLeafNodes() {
		int count = 0;
		if (this.isLeaf()) {
			return 1;
		}
		else {
			for ( int i = 0; i < this.getNumberOfChildren(); ++i ) {
				count += this.getChild(i).getNumberOfLeafNodes();
			}
		}
		return count;
	}
	
	private final int _findMaximumDepthToLeafImpl(int currentDepth) {
		int localMaximum = currentDepth;
		if (!this.isLeaf()) {
			for ( int i = 0; i < this.getNumberOfChildren(); ++i ) {
				int depth = this.getNativeChild(i)._findMaximumDepthToLeafImpl ( currentDepth + 1 );

		        if ( depth > localMaximum )
		        {
		          localMaximum = depth;
		        }
			}
		}
		
		return localMaximum;
	}

	public final int findMaximumDepthToLeaf() {
		return this._findMaximumDepthToLeafImpl ( 0 );
	}
	
	public final String findLabelOfFirstLeafNode() {
		if ( this.isLeaf() ) {
			return this.getLabel();
		}
		
		return this.getChild(0).findLabelOfFirstLeafNode();
	}

	@Override
	public final void sortChildrenBy(Comparator<INode> comparator) {
		if (this.getNumberOfChildren() > 0) {
			NodeList list = new NodeList(getChildren());
			Collections.sort(list, comparator);
		}
	}
}
