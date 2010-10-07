package org.iplantc.phyloviewer.client.tree.viewer.model.remote;

import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("remoteNode")
public interface RemoteNodeService extends RemoteService {
	RemoteNode[] getChildren(String parentID);
	Tree getTree(int i); //TODO replace i with a uuid
	
	/**
	 * @param id
	 * @return the tree with the given ID. On the client, the tree will only have a root node and the
	 *         rest must be fetched using RemoteNode.getChildrenAsync()
	 */
	Tree getTree(String id);
}
