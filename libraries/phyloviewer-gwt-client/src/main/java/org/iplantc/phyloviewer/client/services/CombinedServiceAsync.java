package org.iplantc.phyloviewer.client.services;

import org.iplantc.phyloviewer.client.services.CombinedService.CombinedResponse;
import org.iplantc.phyloviewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CombinedServiceAsync
{

	void getChildren(int i, AsyncCallback<RemoteNode[]> callback);

	void getTree(int id, AsyncCallback<Tree> callback);
	
	void getChildrenAndLayout(int parentID,
			AsyncCallback<CombinedResponse> callback);

	void getChildrenAndLayout(int[] parentIDs,
			AsyncCallback<CombinedResponse[]> callback);	
	
	void getLayout(INode node,
			AsyncCallback<LayoutResponse> callback);

	void getLayout(INode[] nodes,
			AsyncCallback<LayoutResponse[]> callback);
}
