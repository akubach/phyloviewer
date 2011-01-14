package org.iplantc.phyloviewer.client.services;

import org.iplantc.phyloviewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("search")
public interface SearchService extends RemoteService
{
	public class SearchResult implements IsSerializable
	{
		public RemoteNode node;
		public LayoutResponse layout;
	}
	
	/**
	 * Finds nodes matching the given query (case-insensitive) in the given tree
	 */
	SearchResult[] find(String query, int tree, SearchType type);
	
	public enum SearchType implements IsSerializable { 
		EXACT 
		{
			public String queryString(String query)
			{
				return query;
			}
			
			public boolean match(String query, String string)
			{
				return string.equalsIgnoreCase(query);
			}
		},
		PREFIX
		{
			public String queryString(String query)
			{
				return query + "%";
			}
			
			public boolean match(String query, String string)
			{
				return string.toLowerCase().startsWith(query.toLowerCase());
			}
		}, 
		CONTAINS
		{
			public String queryString(String query)
			{
				return "%" + query + "%";
			}
			
			public boolean match(String query, String string)
			{
				return string.toLowerCase().contains(query.toLowerCase());
			}
		};
		
		/** Adds SQL wildcards for a database query */
		abstract public String queryString(String query);
		
		/** @return true if the query matches the string for this query type */
		abstract boolean match(String query, String string);
	};
}
