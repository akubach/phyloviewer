package org.iplantc.phyloviewer.client.tree.viewer.model.remote;

import java.util.UUID;

import com.google.gwt.user.client.rpc.RemoteService;

public interface RemoteNodeService extends RemoteService {
	RemoteNode[] getChildren(UUID parentID);
}
