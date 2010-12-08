package org.iplantc.phyloviewer.shared.model;

import org.iplantc.phyloviewer.shared.render.style.IStyleMap;

public class Document {

	ITree tree;
	IStyleMap styleMap;
	
	public Document() {
	}

	public ITree getTree() {
		return tree;
	}

	public void setTree(ITree tree) {
		this.tree = tree;
	}

	public IStyleMap getStyleMap() {
		return styleMap;
	}

	public void setStyleMap(IStyleMap styleMap) {
		this.styleMap = styleMap;
	}
}
