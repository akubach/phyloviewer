package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.model.INode;

public class LODSelectorCladogram implements ILODSelector
{
	@Override
	public LODLevel getLODLevel(INode node, ILayoutData layout, Matrix33 viewMatrix)
	{
		if(canDrawChildLabels(node, layout, viewMatrix))
		{
			return LODLevel.LOD_HIGH;
		}
		return LODLevel.LOD_LOW;
	}

	protected double estimateNumberOfPixelsNeeded(INode node)
	{
		int numberOfLeafNodes = node.getNumberOfChildren();
		int pixelsPerTaxon = 15;
		return numberOfLeafNodes * pixelsPerTaxon;
	}

	protected boolean canDrawChildLabels(INode node, ILayoutData layout, Matrix33 viewMatrix)
	{
		Box2D boundingBox = layout.getBoundingBox(node);
		double projectedBoxHeight = viewMatrix.transform(boundingBox).getHeight();
		return estimateNumberOfPixelsNeeded(node) < projectedBoxHeight;
	}
}
