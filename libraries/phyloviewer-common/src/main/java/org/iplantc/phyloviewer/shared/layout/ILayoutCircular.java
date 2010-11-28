package org.iplantc.phyloviewer.shared.layout;

import org.iplantc.phyloviewer.shared.math.AnnularSector;
import org.iplantc.phyloviewer.shared.math.PolarVector2;
import org.iplantc.phyloviewer.shared.model.INode;

public interface ILayoutCircular extends ILayout {
	public abstract AnnularSector getPolarBoundingBox(INode node);
	
	public abstract PolarVector2 getPolarPosition(INode node);
}
