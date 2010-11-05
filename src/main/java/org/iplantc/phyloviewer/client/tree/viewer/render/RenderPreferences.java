package org.iplantc.phyloviewer.client.tree.viewer.render;

import java.util.HashSet;

import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.IStyleMap;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.NodeStyle;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.StyleById;
import org.iplantc.phyloviewer.shared.model.INode;

public class RenderPreferences
{
	private boolean collapseOverlaps = true;
	
	private INodeStyle defaultStyle = new NodeStyle(Defaults.POINT_COLOR, Defaults.POINT_COLOR, 2 * Defaults.POINT_RADIUS,
			Defaults.LINE_COLOR, Defaults.LINE_COLOR, 1.0, 
			Defaults.TRIANGLE_OUTLINE_COLOR, Defaults.TRIANGLE_FILL_COLOR, 1.0, 
			Defaults.TEXT_COLOR, Defaults.TEXT_COLOR, 0.0);

	private boolean drawLabels = true;

	private HashSet<Integer> highlights = new HashSet<Integer>();

	private INodeStyle highlightStyle = new NodeStyle("#FFFF00", null, Defaults.POINT_RADIUS + 1, 
			"#FFFF00", null, 2.0,
			"#FFFF00", null, Double.NaN, 
			null, null, Double.NaN);

	private IStyleMap userStyle = new StyleById();

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

	public INodeStyle getDefaultStyle()
	{
		return defaultStyle;
	}

	public INodeStyle getHighlightStyle()
	{
		return highlightStyle;
	}

	public IStyleMap getUserStyle()
	{
		return userStyle;
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

	public void setDefaultStyle(INodeStyle style)
	{
		defaultStyle = style;
	}

	public void setDrawLabels(boolean drawLabels)
	{
		this.drawLabels = drawLabels;
	}

	public void setHighlightStyle(INodeStyle style)
	{
		highlightStyle = style;
	}

	public void setUserStyle(IStyleMap userStyle)
	{
		this.userStyle = userStyle;
	}
}
