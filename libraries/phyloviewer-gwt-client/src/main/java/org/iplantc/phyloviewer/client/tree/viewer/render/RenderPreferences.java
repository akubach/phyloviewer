package org.iplantc.phyloviewer.client.tree.viewer.render;

import java.util.HashSet;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.Defaults;
import org.iplantc.phyloviewer.shared.render.style.BranchStyle;
import org.iplantc.phyloviewer.shared.render.style.GlyphStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.LabelStyle;
import org.iplantc.phyloviewer.shared.render.style.NodeStyle;
import org.iplantc.phyloviewer.shared.render.style.Style;

public class RenderPreferences
{
	private boolean collapseOverlaps = true;
	
	private boolean drawLabels = true;

	private HashSet<Integer> highlights = new HashSet<Integer>();

	private IStyle highlightStyle = new Style("highlight", new NodeStyle(null, "#FFFF00", Defaults.POINT_RADIUS + 1), 
			new LabelStyle(null, null, Double.NaN),
			new GlyphStyle(null, "#FFFF00", Double.NaN), 
			new BranchStyle(null, "#FFFF00", 2.0));

	public void clearHighlights()
	{
		highlights.clear();
	}

	public boolean collapseOverlaps()
	{
		return collapseOverlaps;
	}

	public boolean drawLabels()
	{
		return drawLabels;
	}

	public IStyle getHighlightStyle()
	{
		return highlightStyle;
	}

	public void highlight(INode node)
	{
		highlights.add(node.getId());
	}

	public boolean isHighlighted(INode node)
	{
		return highlights.contains(node.getId());
	}

	public void setCollapseOverlaps(boolean collapseOverlaps)
	{
		this.collapseOverlaps = collapseOverlaps;
	}

	public void setDrawLabels(boolean drawLabels)
	{
		this.drawLabels = drawLabels;
	}

	public void setHighlightStyle(IStyle style)
	{
		highlightStyle = style;
	}

}
