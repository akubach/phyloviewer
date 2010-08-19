package org.iplantc.phyloviewer.client.tree.viewer.model.remote;

import java.util.UUID;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RemoteNodeServiceAsync {

	void getChildren(UUID parentID, AsyncCallback<RemoteNode[]> callback);

}
