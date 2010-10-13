package org.iplantc.phyloviewer.client.services;

import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayoutService;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNodeService;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("nodeLayout")
public interface CombinedService extends RemoteNodeService, RemoteLayoutService
{
	CombinedResponse getChildrenAndLayout(String parentID, String layoutID) throws Exception;
	CombinedResponse[] getChildrenAndLayout(String[] parentIDs, String[] layoutIDs) throws Exception;
	
	public class CombinedResponse implements IsSerializable
	{
		public String parentID;
		public String layoutID;
		public LayoutResponse[] layouts;
		public RemoteNode[] nodes;
	}
}
