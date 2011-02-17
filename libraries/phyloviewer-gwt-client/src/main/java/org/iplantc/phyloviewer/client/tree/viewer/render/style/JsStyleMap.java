package org.iplantc.phyloviewer.client.tree.viewer.render.style;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;

import com.google.gwt.core.client.JavaScriptObject;

public class JsStyleMap extends JavaScriptObject implements IStyleMap
{
	protected JsStyleMap()
	{
	}

	@Override
	public final IStyle get(INode node)
	{
		if(node != null)
		{
			return getStyleById(node.getId());
		}
		return null;
	}

	private final IStyle getStyleById(int nodeId)
	{
		String styleId = this.getStyleIdForNode(nodeId);
		if(styleId != null)
		{
			return this.getStyleNative(styleId);
		}

		return null;
	}

	private final native String getStyleIdForNode(int nodeId) /*-{
		if(this.nodeStyleMappings != null) {
			return this.nodeStyleMappings[nodeId];
		}
		
		return null;
	}-*/;

	private final native JsStyle getStyleNative(String styleId) /*-{return this.styles[styleId];}-*/;

	@Override
	public final void put(INode node, IStyle style)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public final native boolean hasBranchDecoration(int nodeId) /*-{
		if(this.branchDecorations != null) {
			return this.branchDecorations[nodeId] == "triangle";
		}
	
		return false;
	}-*/;
}
