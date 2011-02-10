package org.iplantc.phyloviewer.shared.render.style;

import org.iplantc.phyloviewer.shared.model.INode;

public interface IStyleMap
{
	public IStyle get(INode node);
	public void put(INode node, IStyle style);
}
