package org.iplantc.recon.client;

import org.iplantc.phyloviewer.client.tree.viewer.model.JsTree;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.Defaults;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;

import com.google.gwt.core.client.JavaScriptObject;

public class JsDocument extends JavaScriptObject implements IDocument {
	
	protected JsDocument() {}

	@Override
	public final ITree getTree() {
		return this.getTreeNative();
	}
	
	private final native JsTree getTreeNative() /*-{return this.tree;}-*/;

	@Override
	public final IStyleMap getStyleMap() {
		return this.getStyleMapNative();
	}
	
	private final native JsStyleMap getStyleMapNative() /*-{return this.styleMap;}-*/;

	@Override
	public final void setStyleMap(IStyleMap styleMap) {
		// TODO Auto-generated method stub		
	}

	@Override
	public final IStyle getStyle(INode node) {
		if(node!=null) {
			IStyleMap map=this.getStyleMap();
			if(map!=null) {
				IStyle style=map.get(node);
				if(style!=null) {
					return style;
				}
			}
			IStyle style=node.getStyle();
			if(style!=null) {
				return style;
			}
		}

		return Defaults.DEFAULT_STYLE;
	}

}
