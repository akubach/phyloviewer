package org.iplantc.phyloviewer.shared.model;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.render.Defaults;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;
import org.iplantc.phyloviewer.shared.render.style.StyleById;

public class Document implements IDocument {

	ITree tree;
	IStyleMap styleMap = new StyleById();
	ILayoutData layout;
	
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
		}
		
		// If we get here, return the default style.
		return Defaults.DEFAULT_STYLE;
	}
	
	@Override
	public String getLabel(INode node)
	{
		//TODO give user options on how to label internal nodes without modifying the INode itself
		return node.getLabel();
	}

	public ILayoutData getLayout() {
		return layout;
	}

	public void setLayout(ILayoutData layout) {
		this.layout = layout;
	}
	
	@Override
	public boolean checkForData(final INode node)
	{
		return true;
	}

	@Override
	public boolean isReady()
	{
		return this.getTree() != null && this.getLayout() != null;
	}
}
