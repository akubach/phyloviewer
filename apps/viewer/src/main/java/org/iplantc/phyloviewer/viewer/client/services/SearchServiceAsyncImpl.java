package org.iplantc.phyloviewer.viewer.client.services;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.viewer.client.services.SearchService.SearchResult;
import org.iplantc.phyloviewer.viewer.client.services.SearchService.SearchType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestOracle;

/**
 * A proxy for SearchServiceAsync that stores the last query and last result 
 */
public class SearchServiceAsyncImpl extends SuggestOracle implements SearchServiceAsync 
{
	public static final int MIN_QUERY_LENGTH = 3;
	private SearchServiceAsync searchService = GWT.create(SearchService.class);
	private String lastQuery;
	private SearchResult[] lastResult = new SearchResult[0];
	private ITree tree;
	
	private ArrayList<SearchResultListener> listeners = new ArrayList<SearchResultListener>();
	
	@Override
	public void find(final String query, final int treeID, SearchType type, final AsyncCallback<SearchResult[]> callback)
	{
		if (query == null || query.length() < MIN_QUERY_LENGTH)
		{
			lastQuery = query;
			lastResult = new SearchResult[0];
			callback.onSuccess(lastResult);
			notifyListeners(lastResult, query, treeID);
			return;
		} 
		else if (lastQuery != null && !lastQuery.isEmpty() && query.startsWith(lastQuery) && lastResult.length > 0)
		{
			//if the query just adds characters to the last query, we can just filter the last results instead of going back to the server.
			//(Alternatively, we could keep a stack of results to quickly handle added and deleted characters.)
			lastQuery = query;
			lastResult = filter(query, type, lastResult);
			callback.onSuccess(lastResult);
			notifyListeners(lastResult, query, treeID);
			return;
		}
		
		searchService.find(query, treeID, type, new AsyncCallback<SearchResult[]>(){

			@Override
			public void onFailure(Throwable thrown)
			{
				callback.onFailure(thrown);
			}

			@Override
			public void onSuccess(SearchResult[] result)
			{
				lastQuery = query;
				lastResult = result;
				callback.onSuccess(result);
				notifyListeners(lastResult, query, treeID);
			}
		});
	}

	@Override
	public void requestSuggestions(final Request request, final Callback callback)
	{
		if (tree != null)
		{
			find(request.getQuery(), tree.getId(), SearchType.PREFIX, new AsyncCallback<SearchResult[]>()
			{
				@Override
				public void onFailure(Throwable arg0)
				{
					//TODO
				}
	
				@Override
				public void onSuccess(SearchResult[] nodes)
				{
					callback.onSuggestionsReady(request, createResponse(nodes, request.getLimit()));
				}
				
			});
		}
	}

	public String getLastQuery()
	{
		return lastQuery;
	}

	public SearchResult[] getLastResult()
	{
		return lastResult;
	}
	
	public void setTree(ITree tree)
	{
		this.tree = tree;
		lastQuery = null;
		lastResult = new SearchResult[0];
	}
	
	public void addSearchResultListener(SearchResultListener listener)
	{
		listeners.add(listener);
	}
	
	public void removeSearchResultListener(SearchResultListener listener)
	{
		listeners.remove(listener);
	}
	
	private SuggestOracle.Response createResponse(SearchResult[] nodes, int limit)
	{
		List<RemoteNodeSuggestion> suggestions = new ArrayList<RemoteNodeSuggestion>();
		
		for (SearchResult node : nodes)
		{
			suggestions.add(new RemoteNodeSuggestion(node));
		}
		
		//TODO add size limit param to SearchService.find()?
		if (suggestions.size() > limit)
		{
			suggestions = suggestions.subList(0, limit);
		}
		
		return new Response(suggestions);
	}
	
	private void notifyListeners(SearchResult[] result, String query, int treeID)
	{
		for (SearchResultListener listener : listeners)
		{
			listener.handleSearchResult(result, query, treeID);
		}
	}
	
	private SearchResult[] filter(String query, SearchType type, SearchResult[] nodes)
	{
		ArrayList<SearchResult> filteredList = new ArrayList<SearchResult>(nodes.length);
		
		for (SearchResult result : nodes)
		{
			if (type.match(query, result.node.getLabel()))
			{
				filteredList.add(result);
			}
		}
		
		return filteredList.toArray(new SearchResult[filteredList.size()]);
	}
	
	public class RemoteNodeSuggestion implements SuggestOracle.Suggestion
	{
		private SearchResult result;
		
		public RemoteNodeSuggestion(SearchResult node)
		{
			this.result = node;
		}
		
		@Override
		public String getDisplayString()
		{
			return result.node.getLabel();
		}

		@Override
		public String getReplacementString()
		{
			return result.node.getLabel();
		}
		
		public SearchResult getResult()
		{
			return result;
		}
	}
	
	public interface SearchResultListener
	{
		/** Called when there is a new search result */
		void handleSearchResult(SearchResult[] result, String query, int treeID);
	}
}
