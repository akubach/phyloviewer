package org.iplantc.phyloviewer.client.tree.viewer.model.remote;

import org.iplantc.phyloviewer.client.DemoTree;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RemoteNodeServiceAsync {

	void getChildren(String parentID, AsyncCallback<RemoteNode[]> callback);

	void getTree(String id, AsyncCallback<Tree> callback);

	void getTree(DemoTree tree, AsyncCallback<Tree> callback);

}
