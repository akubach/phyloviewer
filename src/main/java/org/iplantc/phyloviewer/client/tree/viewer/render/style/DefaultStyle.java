package org.iplantc.phyloviewer.client.tree.viewer.render.style;

import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.render.Defaults;

/**
 * An IStyleMap that just returns the styling defined in Defaults
 * @see org.iplantc.phyloviewer.client.tree.viewer.render.Defaults
 */
public class DefaultStyle extends IStyleMap {

	@Override
	public String getFillStyle(INodeStyle.Element element, INode node) {
		switch(element) {
		case NODE: return Defaults.POINT_COLOR;
		case BRANCH: return Defaults.LINE_COLOR;
		case GLYPH: return Defaults.TRIANGLE_FILL_COLOR;
		case LABEL: return Defaults.TEXT_COLOR;
		default: return "black";
		}
	}

	@Override
	public String getStrokeStyle(INodeStyle.Element element, INode node) {
		switch(element) {
		case NODE: return Defaults.POINT_COLOR;
		case BRANCH: return Defaults.LINE_COLOR;
		case GLYPH: return Defaults.TRIANGLE_OUTLINE_COLOR;
		case LABEL: return Defaults.TEXT_COLOR;
		default: return "black";
		}
	}

	@Override
	public double getLineWidth(INodeStyle.Element element, INode node) {
		return 1.0;
	}

}
