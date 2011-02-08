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

	private HashSet<Integer> highlightNodes = new HashSet<Integer>();
	private HashSet<Integer> highlightBranches = new HashSet<Integer>();

	private CompositeStyle highlightStyle;

	private HashSet<Integer> forceCollapsed = new HashSet<Integer>();
	
	public RenderPreferences()
	{
		String highlightColor = "#aaaa00";
		highlightStyle = new CompositeStyle("highlight", Defaults.DEFAULT_STYLE);
		highlightStyle.setNodeStyle(new NodeStyle(highlightColor, Double.NaN));
		highlightStyle.setLabelStyle(new LabelStyle(highlightColor));
		highlightStyle.setGlyphStyle(new GlyphStyle(null, highlightColor, Double.NaN));
		highlightStyle.setBranchStyle(new BranchStyle(highlightColor, Double.NaN));
	}

	public void clearAllHighlights()
	{
		highlightNodes.clear();
		highlightBranches.clear();
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

	public void highlightNode(INode node)
	{
		this.highlightNode(node.getId());
	}
	
	public void highlightNode(Integer id) 
	{
		highlightNodes.add(id);
	}

	public boolean isNodeHighlighted(INode node)
	{
		return highlightNodes.contains(node.getId());
	}
	
	public void highlightBranch(INode node)
	{
		this.highlightBranch(node.getId());
	}
	
	public void highlightBranch(Integer id) 
	{
		highlightBranches.add(id);
	}

	public boolean isBranchHighlighted(INode node)
	{
		return highlightBranches.contains(node.getId());
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
