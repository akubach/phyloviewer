package org.iplantc.phyloviewer.client.tree.viewer.render;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl;
import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl.SearchResultListener;
import org.iplantc.phyloviewer.client.tree.viewer.View;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.model.Node;
import org.iplantc.phyloviewer.shared.model.Node.NodeListener;
import org.iplantc.phyloviewer.shared.render.RenderPreferences;

/**
 * Listens to a SearchServiceAsyncImpl for search results and highlights the ancestors of the result nodes in the tree.
 * Also listens to the nodes in a view's tree for new children and highlights them if they are ancestors of the search result nodes.
 */
public class SearchHighlighter implements SearchResultListener, NodeListener
{
	private final View view;
	private final SearchServiceAsyncImpl searchService;
	private final RenderPreferences renderPreferences;
	
	public SearchHighlighter(View view, SearchServiceAsyncImpl searchService, ITree tree, RenderPreferences pref)
	{
		this.view = view;
		this.searchService = searchService;
		this.renderPreferences = pref;
		searchService.addSearchResultListener(this);
		((Node)tree.getRootNode()).addNodeListener(this);
	}

	@Override
	public void handleSearchResult(RemoteNode[] result, String query, int treeID)
	{
		renderPreferences.clearHighlights();
		highlightSubtree((RemoteNode)view.getTree().getRootNode());
		
		Logger.getLogger("").log(Level.INFO, "Rendering: new set of search results were highlighted");
		view.requestRender();
	}

	@Override
	public void handleChildren(Node[] children)
	{
		if (children instanceof RemoteNode[] && searchService.getLastResult() != null && searchService.getLastResult().length > 0)
		{
			for (RemoteNode child : (RemoteNode[]) children)
			{
				highlightSubtree(child);
			}
			
			Logger.getLogger("").log(Level.INFO, "Rendering: new nodes were fetched and highlighting was updated");
			view.requestRender();
		}
	}

	private void highlightSubtree(RemoteNode node)
	{
		node.addNodeListener(this);
		
		for (RemoteNode resultNode : searchService.getLastResult())
		{
			if (node.subtreeContains(resultNode))
			{
				renderPreferences.highlight(node);
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
