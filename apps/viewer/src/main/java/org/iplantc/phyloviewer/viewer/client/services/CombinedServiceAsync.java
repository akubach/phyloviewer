package org.iplantc.phyloviewer.viewer.client.services;

import org.iplantc.phyloviewer.viewer.client.services.CombinedService.CombinedResponse;
import org.iplantc.phyloviewer.viewer.client.services.CombinedService.NodeResponse;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CombinedServiceAsync
{
	void getRootNode(int treeId, AsyncCallback<NodeResponse> callback);
	
	void getChildrenAndLayout(int parentID,
			AsyncCallback<CombinedResponse> callback);

	void getChildrenAndLayout(int[] parentIDs,
			AsyncCallback<CombinedResponse[]> callback);
}
