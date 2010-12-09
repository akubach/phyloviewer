package org.iplantc.recon.client;

import org.iplantc.phyloviewer.shared.render.style.IBranchStyle;
import org.iplantc.phyloviewer.shared.render.style.IGlyphStyle;
import org.iplantc.phyloviewer.shared.render.style.ILabelStyle;
import org.iplantc.phyloviewer.shared.render.style.INodeStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyle;

import com.google.gwt.core.client.JavaScriptObject;

public class JsStyle extends JavaScriptObject implements IStyle {

	protected JsStyle() {}
	
	@Override
	public final native String getId() /*-{return this.id;}-*/;

	@Override
	public final INodeStyle getNodeStyle() {
		return this.getNodeStyleNative();
	}
	
	private final native JsNodeStyle getNodeStyleNative() /*-{return this.nodeStyle;}-*/;

	@Override
	public final ILabelStyle getLabelStyle() {
		return this.getLabelStyleNative();
	}
	
	private final native JsLabelStyle getLabelStyleNative() /*-{return this.labelStyle;}-*/;

	@Override
	public final IGlyphStyle getGlyphStyle() {
		return this.getGlyphStyleNative();
	}
	
	private final native JsGlyphStyle getGlyphStyleNative() /*-{return this.glyphStyle;}-*/;

	@Override
	public final IBranchStyle getBranchStyle() {
		return this.getBranchStyleNative();
	}
	
	private final native JsBranchStyle getBranchStyleNative() /*-{return this.branchStyle;}-*/;

}
