package org.iplantc.phyloviewer.viewer.server;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.viewer.client.services.CombinedService.LayoutResponse;

public interface ILayoutData {
	
	public abstract LayoutResponse getLayout(INode node) throws Exception;
}
