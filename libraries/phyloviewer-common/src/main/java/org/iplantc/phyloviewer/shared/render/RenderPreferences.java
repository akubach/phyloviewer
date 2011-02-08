package org.iplantc.phyloviewer.shared.render;

import java.util.HashMap;
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
	private boolean drawPoints = true;

	private HashMap<Integer,Boolean> highlightNodes = new HashMap<Integer,Boolean>();
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

	/**
	 * Should labels be drawn?
	 * @return
	 */
	public boolean drawLabels()
	{
		return drawLabels;
	}

	/**
	 * Renderers should call highlightStyle.setBaseStyle(someOtherStyle) for whatever style they are
	 * highlighting before using the returned style.
	 */
	public CompositeStyle getHighlightStyle()
	{
		return highlightStyle;
	}

	/**
	 * Turn on highlighting for given node.
	 * @param node
	 */
	public void highlightNode(INode node)
	{
		if(node != null)
		{
			this.highlightNode(node.getId());
		}
	}

	/**
	 * Turn on highlighting for given node id.
	 * @param id
	 */
	public void highlightNode(Integer id)
	{
		highlightNodes.put(id, false);
	}

	/**
	 * Turn on highlighting for node id and entire sub-tree.
	 * @param id
	 */
	public void highlightSubtree(Integer id)
	{
		highlightNodes.put(id, true);
	}

	/**
	 * Is the node highlighted?
	 * @param node
	 * @return
	 */
	public boolean isNodeHighlighted(INode node)
	{
		return highlightNodes.containsKey(node.getId());
	}

	public boolean isSubTreeHighlighted(INode node)
	{
		if(isNodeHighlighted(node))
		{
			return highlightNodes.get(node.getId());
		}

		return false;
	}

	public void highlightBranch(INode node)
	{
		if(node != null)
		{
			this.highlightBranch(node.getId());
		}
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
		if(isCollapsed)
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

	public boolean isDrawPoints()
	{
		return drawPoints;
	}

	public void setDrawPoints(boolean drawPoints)
	{
		this.drawPoints = drawPoints;
	}
}
