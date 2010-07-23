package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.math.AnnularSector;
import org.iplantc.phyloviewer.client.tree.viewer.math.PolarVector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;

public interface ILayoutCircular extends ILayout {
	public abstract AnnularSector getPolarBoundingBox(INode node);
	
	public abstract PolarVector2 getPolarPosition(INode node);
}
