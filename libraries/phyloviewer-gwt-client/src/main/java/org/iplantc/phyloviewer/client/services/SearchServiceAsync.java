package org.iplantc.phyloviewer.client.services;

import org.iplantc.phyloviewer.client.services.SearchService.SearchResult;
import org.iplantc.phyloviewer.client.services.SearchService.SearchType;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SearchServiceAsync
{

	void find(String query, int tree, SearchType type, AsyncCallback<SearchResult[]> callback);

}
