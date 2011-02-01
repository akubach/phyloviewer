package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.model.INode;

public interface ILODSelector
{
	public enum LODLevel
	{
		LOD_LOW,
		LOD_HIGH
	}
	
	public abstract LODLevel getLODLevel(INode node, ILayoutData layout, Matrix33 viewMatrix);
}
