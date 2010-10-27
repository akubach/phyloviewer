package org.iplantc.phyloviewer.client.services;

import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("search")
public interface SearchService extends RemoteService
{
	RemoteNode[] find(String query, int tree, Type type);
	
	public enum Type { 
		EXACT 
		{
			public String queryString(String query)
			{
				return query;
			}
		},
		PREFIX
		{
			public String queryString(String query)
			{
				return query + "%";
			}
		}, 
		CONTAINS
		{
			public String queryString(String query)
			{
				return "%" + query + "%";
			}
		};
		
		abstract public String queryString(String query);
	};
}
