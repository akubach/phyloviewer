package org.iplantc.phyloviewer.client.tree.viewer.model;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;
import org.iplantc.phyloviewer.client.layout.JsLayoutCladogram;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.JsStyleMap;

import com.google.gwt.core.client.JavaScriptObject;

public class JsDocument extends JavaScriptObject {
	
	protected JsDocument() {}

	public final ITree getTree()
	{
		return this.getTreeNative();
	}
	
	private final native JsTree getTreeNative() /*-{return this.tree;}-*/;

	public final IStyleMap getStyleMap()
	{
		return this.getStyleMapNative();
	}
	
	private final native JsStyleMap getStyleMapNative() /*-{return this.styleMap;}-*/;
	
	public final ILayoutData getLayout()
	{
		return this.getLayoutDataNative();
	}
	
	private final native JsLayoutCladogram getLayoutDataNative() /*-{return this.layout;}-*/;
}
