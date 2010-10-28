package org.iplantc.phyloviewer.client.services;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloviewer.client.services.SearchService.Type;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.shared.model.ITree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestOracle;

/**
 * A proxy for SearchServiceAsync that stores the last query and last result 
 */
public class SearchServiceAsyncImpl extends SuggestOracle implements SearchServiceAsync 
{
	private SearchServiceAsync searchService = GWT.create(SearchService.class);
	private String lastQuery;
	private RemoteNode[] lastResult = new RemoteNode[0];
	private ITree tree;
	
	private ArrayList<SearchResultListener> listeners = new ArrayList<SearchResultListener>();
	
	@Override
	public void find(final String query, final int treeID, Type type, final AsyncCallback<RemoteNode[]> callback)
	{
		//TODO if the query just adds characters to the last query, we can just filter the last results instead of going back to the server.  Or we could keep a stack of results to quickly handle added and deleted characters.
		
		if (query == null || query.isEmpty())
		{
			lastQuery = query;
			lastResult = new RemoteNode[0];
			callback.onSuccess(lastResult);
			notifyListeners(lastResult, query, treeID);
			return;
		}
		
		searchService.find(query, treeID, type, new AsyncCallback<RemoteNode[]>(){

			@Override
			public void onFailure(Throwable thrown)
			{
				callback.onFailure(thrown);
			}

			@Override
			public void onSuccess(RemoteNode[] result)
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
			find(request.getQuery(), tree.getId(), Type.PREFIX, new AsyncCallback<RemoteNode[]>()
			{
				@Override
				public void onFailure(Throwable arg0)
				{
					//TODO
				}
	
				@Override
				public void onSuccess(RemoteNode[] nodes)
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

	public RemoteNode[] getLastResult()
	{
		return lastResult;
	}
	
	public void setTree(ITree tree)
	{
		this.tree = tree;
		lastQuery = null;
		lastResult = new RemoteNode[0];
	}
	
	public void addSearchResultListener(SearchResultListener listener)
	{
		listeners.add(listener);
	}
	
	public void removeSearchResultListener(SearchResultListener listener)
	{
		listeners.remove(listener);
	}
	
	private SuggestOracle.Response createResponse(RemoteNode[] nodes, int limit)
	{
		List<RemoteNodeSuggestion> suggestions = new ArrayList<RemoteNodeSuggestion>();
		
		for (RemoteNode node : nodes)
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
	
	private void notifyListeners(RemoteNode[] result, String query, int treeID)
	{
		for (SearchResultListener listener : listeners)
		{
			listener.handleSearchResult(result, query, treeID);
		}
	}
	
	public class RemoteNodeSuggestion implements SuggestOracle.Suggestion
	{
		private RemoteNode node;
		
		public RemoteNodeSuggestion(RemoteNode node)
		{
			this.node = node;
		}
		
		@Override
		public String getDisplayString()
		{
			return node.getLabel();
		}

		@Override
		public String getReplacementString()
		{
			return node.getLabel();
		}
		
		public RemoteNode getNode()
		{
			return node;
		}
	}
	
	public interface SearchResultListener
	{
		/** Called when there is a new search result */
		void handleSearchResult(RemoteNode[] result, String query, int treeID);
	}
}
