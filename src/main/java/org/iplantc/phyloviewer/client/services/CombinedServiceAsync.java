package org.iplantc.phyloviewer.client.services;

import org.iplantc.phyloviewer.client.services.CombinedService.CombinedResponse;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayoutServiceAsync;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNodeServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CombinedServiceAsync extends RemoteNodeServiceAsync, RemoteLayoutServiceAsync
{

	void getChildrenAndLayout(String parentID, String layoutID,
			AsyncCallback<CombinedResponse> callback);

	void getChildrenAndLayout(String[] parentIDs, String[] layoutIDs,
			AsyncCallback<CombinedResponse[]> callback);

}
