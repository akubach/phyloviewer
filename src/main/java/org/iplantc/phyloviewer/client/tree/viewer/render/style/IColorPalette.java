package org.iplantc.phyloviewer.client.tree.viewer.render.style;

public interface IColorPalette {
	
	/**
	 * Gets a color for a given value
	 * @param value the value, e.g. some node metadata
	 * @return the color for that value, in the same format as {@link org.iplantc.phyloviewer.client.tree.viewer.canvas.Canvas#setFillStyle(String) Canvas.setFillStyle(String)}
	 */
	public abstract String getColor(Object value);

}