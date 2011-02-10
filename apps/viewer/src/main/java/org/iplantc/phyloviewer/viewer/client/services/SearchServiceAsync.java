package org.iplantc.phyloviewer.viewer.client.services;

import org.iplantc.phyloviewer.viewer.client.services.SearchService.SearchResult;
import org.iplantc.phyloviewer.viewer.client.services.SearchService.SearchType;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SearchServiceAsync
{

	void find(String query, int tree, SearchType type, AsyncCallback<SearchResult[]> callback);

}
