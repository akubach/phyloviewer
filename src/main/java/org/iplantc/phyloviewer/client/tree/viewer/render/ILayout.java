package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;

public interface ILayout {
	
	public abstract Vector2 getPosition(INode node);
	
	public abstract Box2D getBoundingBox(INode node);
	
	public abstract void layout(ITree tree);
}
