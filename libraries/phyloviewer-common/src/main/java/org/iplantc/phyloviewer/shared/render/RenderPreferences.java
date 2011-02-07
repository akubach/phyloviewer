package org.iplantc.phyloviewer.shared.render;

import java.util.HashSet;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.style.BranchStyle;
import org.iplantc.phyloviewer.shared.render.style.CompositeStyle;
import org.iplantc.phyloviewer.shared.render.style.GlyphStyle;
import org.iplantc.phyloviewer.shared.render.style.LabelStyle;
import org.iplantc.phyloviewer.shared.render.style.NodeStyle;

public class RenderPreferences
{
	private boolean collapseOverlaps = true;
	
	private boolean drawLabels = true;

	private HashSet<Integer> highlights = new HashSet<Integer>();

	private CompositeStyle highlightStyle;

	private HashSet<Integer> forceCollapsed = new HashSet<Integer>();
	
	public RenderPreferences()
	{
		String highlightColor = "#C2C2F5";
		highlightStyle = new CompositeStyle("highlight", Defaults.DEFAULT_STYLE);
		highlightStyle.setNodeStyle(new NodeStyle(highlightColor, Double.NaN));
		highlightStyle.setLabelStyle(new LabelStyle(highlightColor));
		highlightStyle.setGlyphStyle(new GlyphStyle(null, highlightColor, Double.NaN));
		highlightStyle.setBranchStyle(new BranchStyle(highlightColor, Double.NaN));
	}

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

	/**
	 * Renderers should call highlightStyle.setBaseStyle(someOtherStyle) for whatever style they are highlighting before using the returned style.
	 */
	public CompositeStyle getHighlightStyle()
	{
		return highlightStyle;
	}

	public void highlight(INode node)
	{
		this.highlight(node.getId());
	}
	
	public void highlight(Integer id) 
	{
		highlights.add(id);
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

	/**
	 * When rendering, the baseStyle of highlightStyle will be replaced with the style of the node being
	 * highlighted.
	 */
	public void setHighlightStyle(CompositeStyle style)
	{
		highlightStyle = style;
	}

	
	public void setCollapsed(INode node, boolean isCollapsed) 
	{
		if (isCollapsed)
		{
			forceCollapsed.add(node.getId());
		}
		else
		{
			forceCollapsed.remove(node.getId());
		}
	}
	
	public boolean isCollapsed(INode node)
	{
		return forceCollapsed.contains(node.getId());
	}
}
