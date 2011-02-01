package org.iplantc.phyloviewer.shared.render;

import org.iplantc.phyloviewer.shared.scene.DrawableBuilderCladogram;
import org.iplantc.phyloviewer.shared.scene.LODSelectorCladogram;

public class RenderTreeCladogram extends RenderTree
{
	public RenderTreeCladogram()
	{
		this.setDrawableBuilder(new DrawableBuilderCladogram());
		this.setLODSelector(new LODSelectorCladogram());
	}
}
