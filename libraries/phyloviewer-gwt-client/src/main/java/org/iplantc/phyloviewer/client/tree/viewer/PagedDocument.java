package org.iplantc.phyloviewer.client.tree.viewer;

import java.util.HashSet;
import java.util.Set;

import org.iplantc.phyloviewer.client.events.RenderEvent;
import org.iplantc.phyloviewer.client.services.CombinedService.CombinedResponse;
import org.iplantc.phyloviewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.client.services.CombinedService.NodeResponse;
import org.iplantc.phyloviewer.client.services.CombinedServiceAsync;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.shared.layout.LayoutStorage;
import org.iplantc.phyloviewer.shared.model.Document;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.Tree;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PagedDocument extends Document
{
	LayoutStorage remoteLayout = new LayoutStorage();
	CombinedServiceAsync combinedService;
	EventBus eventBus;
	Set<Integer> pendingRequests = new HashSet<Integer>();

	public PagedDocument(CombinedServiceAsync combinedService, EventBus eventBus, int treeId,
			NodeResponse nodeData)
	{
		super();

		this.combinedService = combinedService;
		this.eventBus = eventBus;

		Tree tree = new Tree();
		tree.setId(treeId);
		tree.setRootNode(nodeData.node);

		this.setTree(tree);

		int numberOfNodes = nodeData.node.getNumberOfNodes();

		remoteLayout.init(numberOfNodes);
		remoteLayout.setPositionAndBounds(nodeData.layout.nodeID, nodeData.layout.position,
				nodeData.layout.boundingBox);
		this.setLayout(remoteLayout);
	}

	@Override
	public boolean checkForData(final INode node)
	{
		// Return now if we are waiting for a response from the server.
		if(pendingRequests.contains(node.getId()))
		{
			return false;
		}

		final LayoutStorage rLayout = remoteLayout;
		if(node instanceof RemoteNode)
		{
			final RemoteNode rNode = (RemoteNode)node;

			if(rNode.getChildren() == null)
			{
				pendingRequests.add(rNode.getId());

				combinedService.getChildrenAndLayout(rNode.getId(),
						new AsyncCallback<CombinedResponse>()
						{

							@Override
							public void onFailure(Throwable arg0)
							{
								pendingRequests.remove(rNode.getId());
							}

							@Override
							public void onSuccess(CombinedResponse response)
							{
								pendingRequests.remove(rNode.getId());

								rNode.setChildren(response.nodes);

								for(LayoutResponse layoutResponse : response.layouts)
								{
									rLayout.setPositionAndBounds(layoutResponse.nodeID,
											layoutResponse.position, layoutResponse.boundingBox);
								}

								if(eventBus != null)
								{
									eventBus.fireEvent(new RenderEvent());
								}
							}

						});

				return false;

			}
		}

		return true;
	}
}
