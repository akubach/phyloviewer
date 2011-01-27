package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.model.INode;

public interface IBranchBuilder
{
	public Drawable[] buildBranch(INode parent, INode child, ILayoutData layout);
}
