package org.iplantc.phyloviewer.shared.model;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;

public interface IDocument {

	public abstract ITree getTree();

	public abstract IStyleMap getStyleMap();
	public abstract void setStyleMap(IStyleMap styleMap);

	public abstract IStyle getStyle(INode node);
	
	public abstract String getLabel(INode node);
	
	public abstract ILayoutData getLayout();
}