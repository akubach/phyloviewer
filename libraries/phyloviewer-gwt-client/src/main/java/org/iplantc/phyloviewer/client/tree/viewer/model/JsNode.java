/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONObject;

public class JsNode extends JavaScriptObject implements INode
{
	protected JsNode()
	{
	}

	@Override
	public final native int getId() /*-{ return this.id; }-*/;

	@Override
	public final native void setId(int id) /*-{ this.id = id; }-*/;

	public final native String getLabel() /*-{ return this.name; }-*/;

	public final native void setLabel(String label) /*-{ this.name = label; }-*/;

	private final native <T extends JavaScriptObject> JsArray<T> getNativeChildren() /*-{ return this.children; }-*/;

	public final int getNumberOfChildren()
	{
		if(null == this.getNativeChildren())
			return 0;
		return this.getNativeChildren().length();
	}

	public final JsNode getChild(int index)
	{
		return (JsNode)this.getNativeChildren().get(index);
	}

	public final INode[] getChildren()
	{
		JsNode[] children = new JsNode[getNumberOfChildren()];
		for(int i = 0;i < getNumberOfChildren();i++)
		{
			children[i] = getChild(i);
		}

		return children;
	}

	public final Boolean isLeaf()
	{
		return 0 == this.getNumberOfChildren();
	}

	public final int getNumberOfLeafNodes()
	{
		int count = 0;
		if(this.isLeaf())
		{
			count = 1;
		}
		else
		{
			for(int i = 0;i < this.getNumberOfChildren();++i)
			{
				count += this.getChild(i).getNumberOfLeafNodes();
			}
		}

		return count;
	}

	private final int _findMaximumDepthToLeafImpl(int currentDepth)
	{
		int localMaximum = currentDepth;
		if(!this.isLeaf())
		{
			for(int i = 0;i < this.getNumberOfChildren();++i)
			{
				int depth = this.getChild(i)._findMaximumDepthToLeafImpl(currentDepth + 1);

				if(depth > localMaximum)
				{
					localMaximum = depth;
				}
			}
		}

		return localMaximum;
	}

	public final int findMaximumDepthToLeaf()
	{
		return this._findMaximumDepthToLeafImpl(0);
	}

	public final String findLabelOfFirstLeafNode()
	{
		if(this.isLeaf())
		{
			return this.getLabel();
		}

		return this.getChild(0).findLabelOfFirstLeafNode();
	}

	@Override
	public final void sortChildrenBy(Comparator<INode> comparator)
	{
		if(this.getNumberOfChildren() > 0)
		{
			NodeList list = new NodeList(getNativeChildren());
			Collections.sort(list, comparator);
		}
	}

	@Override
	public final int getNumberOfNodes()
	{
		int count = 1;

		for(int i = 0;i < getNumberOfChildren();i++)
		{
			INode child = getChild(i);
			count += child.getNumberOfNodes();
		}

		return count;
	}

	@Override
	public final native Double getBranchLength() /*-{ this.branchLength; }-*/;

	@Override
	public final native void setBranchLength(Double branchLength) /*-{ this.branchLength = branchLength; }-*/;

	@Override
	public final double findMaximumDistanceToLeaf()
	{
		return this.findMaximumDistanceToLeaf(0.0);
	}

	private double findMaximumDistanceToLeaf(double currentDistance)
	{
		double localMaximum = currentDistance;

		int numChildren = this.getNumberOfChildren();
		if(0 < numChildren)
		{
			for(int i = 0;i < numChildren;++i)
			{
				JsNode child = this.getChild(i);
				double distance = child.findMaximumDistanceToLeaf(currentDistance
						+ this.getBranchLength());

				if(distance > localMaximum)
				{
					localMaximum = distance;
				}
			}
		}

		return localMaximum;
	}
	
	@Override
	public final String getMetaDataString()
	{
		JSONObject object = new JSONObject ( this.getMetaDataStringNative() );
		return object.toString();
	}
	
	private final native JavaScriptObject getMetaDataStringNative() /*-{	return this.metadata; }-*/;

	@Override
	public final INode mrca(Set<INode> nodes)
	{
		/*
		 * FIXME return the most recent common ancestor of the given set of nodes within this INode's
		 * subtree. Return null if this subtree does not contain all of the nodes. Probably need a parent
		 * reference to do this with any kind of efficiency
		 */
		return null;
	}
}
