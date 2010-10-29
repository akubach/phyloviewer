package org.iplantc.phyloviewer.client.services;

import org.iplantc.phyloviewer.client.services.SearchService.SearchType;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SearchServiceAsync
{

	void find(String query, int tree, SearchType type, AsyncCallback<RemoteNode[]> callback);

}
