package org.iplantc.phyloviewer.client.tree.viewer.render;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloviewer.client.services.SearchService.SearchResult;
import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl;
import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl.SearchResultListener;
import org.iplantc.phyloviewer.client.tree.viewer.View;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.model.Node;
import org.iplantc.phyloviewer.shared.model.Node.NodeListener;
import org.iplantc.phyloviewer.shared.render.RenderPreferences;

/**
 * Listens to a SearchServiceAsyncImpl for search results and highlights the ancestors of the result
 * nodes in the tree. Also listens to the nodes in a view's tree for new children and highlights them if
 * they are ancestors of the search result nodes.
 */
public class SearchHighlighter implements SearchResultListener, NodeListener
{
	private View view;
	private ITree tree;
	private final SearchServiceAsyncImpl searchService;
	private RenderPreferences renderPreferences;

	public SearchHighlighter(SearchServiceAsyncImpl searchService)
	{
		this.searchService = searchService;
		searchService.addSearchResultListener(this);
	}

	public void dispose()
	{
		searchService.removeSearchResultListener(this);
		this.clear();
	}

	public void setView(View view)
	{
		this.view = view;
	}

	public void setRenderPreferences(RenderPreferences renderPreferences)
	{
		this.renderPreferences = renderPreferences;
	}

	public void setTree(ITree tree)
	{
		clearNodeListener();

		this.tree = tree;
		if(tree != null)
		{
			((Node)tree.getRootNode()).addNodeListener(this);
		}
	}

	@Override
	public void handleSearchResult(SearchResult[] result, String query, int treeID)
	{
		if(renderPreferences != null)
		{
			renderPreferences.clearAllHighlights();
		}

		if(tree != null)
		{
			highlightSubtree((RemoteNode)tree.getRootNode());
		}

		Logger.getLogger("").log(Level.INFO, "Rendering: new set of search results were highlighted");

		if(view != null)
		{
			view.requestRender();
		}
	}

	@Override
	public void handleChildren(Node[] children)
	{
		if(children instanceof RemoteNode[] && searchService.getLastResult() != null
				&& searchService.getLastResult().length > 0)
		{
			for(RemoteNode child : (RemoteNode[])children)
			{
				highlightSubtree(child);
			}

			Logger.getLogger("").log(Level.INFO,
					"Rendering: new nodes were fetched and highlighting was updated");
			view.requestRender();
		}
	}

	private void highlightSubtree(RemoteNode node)
	{
		node.addNodeListener(this);

		for(SearchResult result : searchService.getLastResult())
		{
			if(node.subtreeContains(result.node))
			{
				renderPreferences.highlightNode(node);
				renderPreferences.highlightBranch(node);
			}
		}

		if(node.getChildren() != null)
		{
			for(RemoteNode child : node.getChildren())
			{
				highlightSubtree(child);
			}
		}
	}

	/**
	 * Removes this listener from all of the nodes of the tree
	 */
	public void clear()
	{
		if(renderPreferences != null)
		{
			renderPreferences.clearAllHighlights();
		}

		clearNodeListener();
	}

	private void clearNodeListener()
	{
		if(tree != null)
		{
			Node root = (Node)tree.getRootNode();

			if(root != null)
			{
				root.removeNodeListenerFromSubtree(this);
			}
		}
	}
}
