package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.layout.CircularCoordinates;
import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.AnnularSector;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.model.INode;

public class LODSelectorCircular implements ILODSelector
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

	protected boolean canDrawChildLabels(INode node, ILayoutData layout, Matrix33 viewMatrix)
	{
		int pixelsPerLabel = 30;
		double pixelsNeeded = node.getNumberOfChildren() * pixelsPerLabel;
		double pixelsAvailable = pixelsAvailableForLabels(node, layout, viewMatrix);
		return pixelsAvailable >= pixelsNeeded;
	}

	private static double pixelsAvailableForLabels(INode node, ILayoutData layout, Matrix33 viewMatrix)
	{
		AnnularSector polarBounds = CircularCoordinates.getPolarBoundingBox(node, layout);
		double arcLength = (polarBounds.getMax().getAngle() - polarBounds.getMin().getAngle())
				* polarBounds.getMax().getRadius();
		arcLength *= viewMatrix.getScaleY(); // assuming xzoom == yzoom
		return arcLength;
	}
}
