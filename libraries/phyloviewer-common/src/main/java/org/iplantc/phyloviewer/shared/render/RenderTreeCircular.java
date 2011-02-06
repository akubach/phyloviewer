package org.iplantc.phyloviewer.shared.render;

import org.iplantc.phyloviewer.shared.layout.CircularCoordinates;
import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.scene.DrawableBuilderCircular;
import org.iplantc.phyloviewer.shared.scene.LODSelectorCircular;

public class RenderTreeCircular extends RenderTree
{
	public RenderTreeCircular()
	{
		this.setDrawableBuilder(new DrawableBuilderCircular());
		this.setLODSelector(new LODSelectorCircular());
	}

	@Override
	public Box2D getBoundingBox(INode node, ILayoutData layout)
	{
		return CircularCoordinates.getCartesianBoundingBox(node, layout);
	}
}
