package org.iplantc.phyloviewer.server;

import org.iplantc.phyloviewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;

public interface ILayoutData {

	public abstract void putLayout(String layoutID, ILayout layout, ITree tree);
	
	public abstract LayoutResponse getLayout(INode node, String layoutID) throws Exception;
}
