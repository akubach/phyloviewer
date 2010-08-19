package org.iplantc.phyloviewer.server;

import java.util.HashMap;
import java.util.UUID;

import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNodeService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RemoteNodeServiceImpl extends RemoteServiceServlet implements RemoteNodeService {
	//TODO parse a tree
	private HashMap<UUID, RemoteNode[]> childrenMap = new HashMap<UUID, RemoteNode[]>();

	@Override
	public RemoteNode[] getChildren(UUID parentID) {
		return childrenMap.get(parentID);
	}
}
