package org.iplantc.phyloviewer.shared.render.style;

import org.iplantc.phyloviewer.shared.model.INode;

public interface IStyleMap
{
	abstract IStyle get(INode node);
	abstract void put(INode node, IStyle style);
	abstract void clear();
}
