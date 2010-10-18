package org.iplantc.phyloviewer.client.services;

import java.util.ArrayList;
import java.util.HashMap;

import org.iplantc.phyloviewer.client.services.CombinedService.CombinedResponse;
import org.iplantc.phyloviewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Client side proxy for the CombinedService.  
 * 
 * Gets nodes and layouts in one RPC call and caches the layout response until the client needs it.
 */
public class CombinedServiceAsyncImpl implements CombinedServiceAsync
{
	private CombinedServiceAsync service;
	private HashMap<String, LayoutResponse> layouts; //key is concatenation of layoutID + nodeID
	private String lastLayoutID;
	private boolean defer = true;
	private BatchRequestCommand nextRequestCommand;
	
	public CombinedServiceAsyncImpl() {
		service = GWT.create(CombinedService.class);
		layouts = new HashMap<String, LayoutResponse>();
	}

	@Override
	public void getChildren(int parentID, final AsyncCallback<RemoteNode[]> callback)
	{
		/* Assumes that the client will also need the layout for these nodes, using the last layoutID param that was given */
		getChildrenAndLayout(parentID, lastLayoutID, new AsyncCallback<CombinedResponse>()
		{

			@Override
			public void onFailure(Throwable arg0)
			{
				callback.onFailure(arg0);
			}

			@Override
			public void onSuccess(CombinedResponse responses)
			{
				for (LayoutResponse layout: responses.layouts)
				{
					layouts.put(layout.nodeID + layout.layoutID, layout);
				}
					
				
				callback.onSuccess(responses.nodes);
				
			}
		});
	}

	@Override
	public void getTree(int id, AsyncCallback<Tree> callback)
	{
		service.getTree(id, callback);
	}

	@Override
	public void getLayout(INode node, String layoutID, AsyncCallback<LayoutResponse> callback)
	{
		lastLayoutID = layoutID;
		
		//Returns the cached layout if possible
		if (layouts.containsKey(node.getId()))
		{
			String key = node.getId() + layoutID;
			callback.onSuccess(layouts.remove(key));
		}
		else 
		{
			service.getLayout(node, layoutID, callback);
			GWT.log("Making a layout-only request (layout hasn't been cached) for node " + node.getId() + " in layout " + layoutID);
		}

	}

	@Override
	public void getLayout(INode[] nodes, String layoutID, AsyncCallback<LayoutResponse[]> callback)
	{
		lastLayoutID = layoutID;
		
		LayoutResponse[] responses = new LayoutResponse[nodes.length];
		
		//Returns the cached layouts if possible
		boolean allLayoutsFound = true;
		for (int i = 0; i < responses.length; i++) 
		{
			String key = nodes[i].getId() + layoutID;
			responses[i] = layouts.remove(key);
			allLayoutsFound &= responses[i] != null;
		}
		
		if (allLayoutsFound) 
		{
			callback.onSuccess(responses);
		}
		else
		{
			service.getLayout(nodes, layoutID, callback);
			GWT.log("Making a layout-only request (layout hasn't been cached) for nodes in layout " + layoutID);
		}
	}

	@Override
	public void getChildrenAndLayout(int parentID, String layoutID,
			AsyncCallback<CombinedResponse> callback)
	{
		lastLayoutID = layoutID;
		
		if (defer)
		{
			addDeferredRequest(parentID, layoutID, callback);
		}
		else
		{
			service.getChildrenAndLayout(parentID, layoutID, callback);
		}
	}

	@Override
	public void getChildrenAndLayout(int[] parentIDs, String[] layoutIDs,
			AsyncCallback<CombinedResponse[]> callback)
	{
		lastLayoutID = layoutIDs[layoutIDs.length];
		service.getChildrenAndLayout(parentIDs, layoutIDs, callback);
	}
	
	private void addDeferredRequest(int parentID, String layoutID,
			AsyncCallback<CombinedResponse> callback)
	{
		if (nextRequestCommand == null) {
			nextRequestCommand = new BatchRequestCommand();
			DeferredCommand.addCommand(nextRequestCommand);
		}
		
		nextRequestCommand.addRequest(parentID, layoutID, callback);
	}
	
	private class BatchRequestCommand implements Command {
		ArrayList<Integer> parentList = new ArrayList<Integer>();
		ArrayList<String> layoutList = new ArrayList<String>();
		HashMap<Integer, AsyncCallback<CombinedResponse>> callbacks = new HashMap<Integer, AsyncCallback<CombinedResponse>>();
		
		public void addRequest(int parentID, String layoutID, AsyncCallback<CombinedResponse> callback) {
			parentList.add(parentID);
			layoutList.add(layoutID);
			callbacks.put(parentID, callback);
		}

		@Override
		public void execute()
		{
//			CombinedServiceAsyncImpl.this.nextRequestCommand = new BatchRequestCommand();
//			DeferredCommand.addCommand(nextRequestCommand);
			CombinedServiceAsyncImpl.this.nextRequestCommand = null;
			
			int[] parentIDs = new int[parentList.size()];
			for(int i = 0; i < parentList.size(); ++i) {
				parentIDs[i] = parentList.get(i);
			}
			String[] layoutIDs = layoutList.toArray(new String[layoutList.size()]);
			
			GWT.log("Making a combined request for the children and layouts of " + parentIDs.length + " parent nodes");
			service.getChildrenAndLayout(parentIDs, layoutIDs, new AsyncCallback<CombinedResponse[]>()
			{

				@Override
				public void onFailure(Throwable arg0)
				{
					for (AsyncCallback<CombinedResponse> callback : callbacks.values())
					{
						callback.onFailure(arg0);
					}
				}

				@Override
				public void onSuccess(CombinedResponse[] responses)
				{
					for (CombinedResponse response : responses) 
					{
						AsyncCallback<CombinedResponse> callback = callbacks.get(response.parentID);
						callback.onSuccess(response);
					}
				}

			});
		}
	}
}
