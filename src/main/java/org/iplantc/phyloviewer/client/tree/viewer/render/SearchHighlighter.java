package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl;
import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl.SearchResultListener;
import org.iplantc.phyloviewer.client.tree.viewer.View;
import org.iplantc.phyloviewer.client.tree.viewer.model.Node;
import org.iplantc.phyloviewer.client.tree.viewer.model.Node.NodeListener;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.shared.model.ITree;

/**
 * Listens to a SearchServiceAsyncImpl for search results and highlights the ancestors of the result nodes in the tree.
 * Also listens to the nodes in a view's tree for new children and highlights them if they are ancestors of the search result nodes.
 */
public class SearchHighlighter implements SearchResultListener, NodeListener
{
	View view;
	SearchServiceAsyncImpl searchService;
	
	public SearchHighlighter(View view, SearchServiceAsyncImpl searchService, ITree tree)
	{
		this.view = view;
		this.searchService = searchService;
		searchService.addSearchResultListener(this);
		highlightSubtree((RemoteNode)tree.getRootNode());
	}

	@Override
	public void handleSearchResult(RemoteNode[] result, String query, int treeID)
	{
		view.clearHighlights();
		highlightSubtree((RemoteNode)view.getTree().getRootNode());
		view.requestRender();
	}

	@Override
	public void handleChildren(Node[] children)
	{
		if (children instanceof RemoteNode[])
		{
			for (RemoteNode child : (RemoteNode[]) children)
			{
				highlightSubtree(child);
			}
		}
		view.requestRender();
	}

	private void highlightSubtree(RemoteNode node)
	{
		node.addNodeListener(this);
		
		for (RemoteNode resultNode : searchService.getLastResult())
		{
			if (node.subtreeContains(resultNode))
			{
				view.highlight(node);
			}
		}
		
		if (node.getChildren() != null)
		{
			for (RemoteNode child : node.getChildren())
			{
				highlightSubtree(child);
			}
		}
	}
	
	/**
	 * Removes this listener from the search service and from all of the nodes of the tree
	 */
	public void dispose()
	{
		searchService.removeSearchResultListener(this);
		Node root = (Node)view.getTree().getRootNode();
		root.removeNodeListenerFromSubtree(this);
	}
}
