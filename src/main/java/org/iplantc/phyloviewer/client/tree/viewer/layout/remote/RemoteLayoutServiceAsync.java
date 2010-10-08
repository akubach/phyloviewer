package org.iplantc.phyloviewer.client.tree.viewer.layout.remote;

import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayoutService.LayoutResponse;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RemoteLayoutServiceAsync {

	void getLayout(INode node, String layoutID,
			AsyncCallback<LayoutResponse> callback);

	void getLayout(INode[] nodes, String layoutID,
			AsyncCallback<LayoutResponse[]> callback);
	
	void layout(String treeID, ILayout layout, AsyncCallback<String> callback);
}
