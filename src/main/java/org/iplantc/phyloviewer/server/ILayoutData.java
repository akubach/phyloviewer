package org.iplantc.phyloviewer.server;

import org.iplantc.phyloviewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;

public interface ILayoutData {
	
	public abstract LayoutResponse getLayout(INode node, String layoutID) throws Exception;
}
