package org.iplantc.phyloviewer.client.services;

import org.iplantc.phyloviewer.client.services.SearchService.Type;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SearchServiceAsync
{

	void find(String query, int tree, Type type, AsyncCallback<RemoteNode[]> callback);

}
