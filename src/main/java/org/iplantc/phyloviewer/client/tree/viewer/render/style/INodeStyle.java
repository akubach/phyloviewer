package org.iplantc.phyloviewer.client.tree.viewer.render.style;

/**
 * INodeStyle defines the graphical elements and accessors for a node's styling info
 */
public interface INodeStyle {
	public abstract IElementStyle getElementStyle(Element element);
	
	public interface IElementStyle {
		public abstract String getStrokeColor();
		public abstract void setStrokeColor(String color);
		public abstract String getFillColor();
		public abstract void setFillColor(String color);
		public abstract double getLineWidth();
		public abstract void setLineWidth(double width);
	}

	public enum Element 
	{
		/** The graphical element representing the node itself */ 
		NODE, 
		
		/** The graphical element representing the branch from a node to its parent */ 
		BRANCH, 
		
		/** A graphical element representing an entire subtree */
		GLYPH, 
		
		/** A text label for a node */
		LABEL
	}
	
	public enum Feature
	{
		STROKE,
		FILL,
		WIDTH
	}
}
