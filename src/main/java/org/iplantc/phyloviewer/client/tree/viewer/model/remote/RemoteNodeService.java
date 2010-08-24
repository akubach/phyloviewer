package org.iplantc.phyloviewer.client.tree.viewer.model.remote;

import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("remoteNode")
public interface RemoteNodeService extends RemoteService {
	RemoteNode[] getChildren(String parentID);
	//TODO get bigger chunks of subtrees.  may require custom serialization.
//	void loadTree(int i);
	Tree fetchTree(int i); //TODO replace i with a uuid
	Tree fetchTree(String id);
}
