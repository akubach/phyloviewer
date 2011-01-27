package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;

public class BranchBuilderCladogram implements IBranchBuilder
{
	@Override
	public Drawable[] buildBranch(INode parent, INode child, ILayoutData layout)
	{
		Vector2 start = layout.getPosition(parent);
		Vector2 end = layout.getPosition(child);

		Vector2 vertices[] = new Vector2[3];
		vertices[0] = start;
		vertices[1] = new Vector2(start.getX(), end.getY());
		vertices[2] = end;

		Box2D box = new Box2D();
		box.expandBy(start);
		box.expandBy(end);
		Drawable[] drawables = new Drawable[] { new Line(vertices, box) };
		return drawables;
	}
}
