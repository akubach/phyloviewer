package org.iplantc.phyloviewer.shared.model;

import org.iplantc.phyloviewer.shared.render.Defaults;
import org.iplantc.phyloviewer.shared.render.style.BranchStyle;
import org.iplantc.phyloviewer.shared.render.style.GlyphStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;
import org.iplantc.phyloviewer.shared.render.style.LabelStyle;
import org.iplantc.phyloviewer.shared.render.style.NodeStyle;
import org.iplantc.phyloviewer.shared.render.style.Style;
import org.iplantc.phyloviewer.shared.render.style.StyleById;

public class Document implements IDocument {

	ITree tree;
	IStyleMap styleMap = new StyleById();
	
	//TODO: Add a lookup table for internal nodes, instead of setting node object's name
	
	public Document() {
	}

	@Override
	public ITree getTree() {
		return tree;
	}

	public void setTree(ITree tree) {
		this.tree = tree;
	}

	@Override
	public IStyleMap getStyleMap() {
		return styleMap;
	}

	public void setStyleMap(IStyleMap styleMap) {
		this.styleMap = styleMap;
	}

	@Override
	public IStyle getStyle(INode node) {
		
		if(node!=null) {
			if(this.styleMap!=null){
				IStyle style = this.styleMap.get(node);
				if(style!=null) {
					return style;
				}
			}
			IStyle style = node.getStyle();
			if(style!=null) {
				return style;
			}
		}
		
		// If we get here, return the default style.
		return new Style("default", new NodeStyle(Defaults.POINT_COLOR, Defaults.POINT_COLOR, 2 * Defaults.POINT_RADIUS),
				new LabelStyle(Defaults.TEXT_COLOR, Defaults.TEXT_COLOR, 0.0), 
				new GlyphStyle(Defaults.TRIANGLE_FILL_COLOR, Defaults.TRIANGLE_OUTLINE_COLOR, 1.0),
				new BranchStyle(Defaults.LINE_COLOR, Defaults.LINE_COLOR, 1.0));
	}
}
