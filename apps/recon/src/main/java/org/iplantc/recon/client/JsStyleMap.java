package org.iplantc.recon.client;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;

import com.google.gwt.core.client.JavaScriptObject;

public class JsStyleMap extends JavaScriptObject implements IStyleMap {

	protected JsStyleMap() {}
	
	@Override
	public final IStyle get(INode node) {
		if(node!=null) {
			return this.getStyleNative(node.getStyleId());
		}
		return null;
	}
	
	private final native JsStyle getStyleNative(String styleId) /*-{return this[styleId];}-*/;

	@Override
	public final void put(INode node, IStyle style) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public final void clear() {
		// TODO Auto-generated method stub
		
	}

}
