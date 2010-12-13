package org.iplantc.phyloviewer.client.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloviewer.client.services.CombinedService.CombinedResponse;
import org.iplantc.phyloviewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Client side proxy for the CombinedService.  
 * 
 * Gets nodes and layouts in one RPC call and caches the layout response until the client needs it.
 */
public class CombinedServiceAsyncImpl implements CombinedServiceAsync
{
	private CombinedServiceAsync service;
	private HashMap<Integer, LayoutResponse> layouts;
	private boolean defer = true;
	private BatchRequestCommand nextRequestCommand;
	
	public CombinedServiceAsyncImpl() {
		service = GWT.create(CombinedService.class);
		layouts = new HashMap<Integer, LayoutResponse>();
	}

	@Override
	public void getChildren(int parentID, final AsyncCallback<RemoteNode[]> callback)
	{
		/* Assumes that the client will also need the layout for these nodes, using the last layoutID param that was given */
		getChildrenAndLayout(parentID, new AsyncCallback<CombinedResponse>()
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
					layouts.put(layout.nodeID, layout);
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
	public void getLayout(INode node, AsyncCallback<LayoutResponse> callback)
	{
		//Returns the cached layout if possible
		if (layouts.containsKey(node.getId()))
		{
			callback.onSuccess(layouts.remove(node.getId()));
		}
		else 
		{
			service.getLayout(node, callback);
			Logger.getLogger("").log(Level.INFO, "Making a layout-only request (layout hasn't been cached) for node " + node.getId());
		}

	}

	@Override
	public void getLayout(INode[] nodes, AsyncCallback<LayoutResponse[]> callback)
	{	
		LayoutResponse[] responses = new LayoutResponse[nodes.length];
		
		//Returns the cached layouts if possible
		boolean allLayoutsFound = true;
		for (int i = 0; i < responses.length; i++) 
		{
			responses[i] = layouts.remove(nodes[i].getId());
			allLayoutsFound &= responses[i] != null;
		}
		
		if (allLayoutsFound) 
		{
			callback.onSuccess(responses);
		}
		else
		{
			service.getLayout(nodes, callback);
			Logger.getLogger("").log(Level.INFO, "Making a layout-only request (layout hasn't been cached)");
		}
	}

	@Override
	public void getChildrenAndLayout(int parentID, 
			AsyncCallback<CombinedResponse> callback)
	{
		if (defer)
		{
			addDeferredRequest(parentID, callback);
		}
		else
		{
			service.getChildrenAndLayout(parentID, callback);
		}
	}

	@Override
	public void getChildrenAndLayout(int[] parentIDs,
			AsyncCallback<CombinedResponse[]> callback)
	{
		service.getChildrenAndLayout(parentIDs, callback);
	}
	
	private void addDeferredRequest(int parentID,
			AsyncCallback<CombinedResponse> callback)
	{
		if (nextRequestCommand == null) {
			nextRequestCommand = new BatchRequestCommand();
			Scheduler.get().scheduleDeferred(nextRequestCommand);
		}
		
		nextRequestCommand.addRequest(parentID, callback);
	}
	
	private class BatchRequestCommand implements ScheduledCommand {
		ArrayList<Integer> parentList = new ArrayList<Integer>();
		HashMap<Integer, AsyncCallback<CombinedResponse>> callbacks = new HashMap<Integer, AsyncCallback<CombinedResponse>>();
		
		public void addRequest(int parentID, AsyncCallback<CombinedResponse> callback) {
			parentList.add(parentID);
			callbacks.put(parentID, callback);
		}

		@Override
		public void execute()
		{
			CombinedServiceAsyncImpl.this.nextRequestCommand = null;
			
			int[] parentIDs = new int[parentList.size()];
			for(int i = 0; i < parentList.size(); ++i) {
				parentIDs[i] = parentList.get(i);
			}
			
			Logger.getLogger("").log(Level.INFO, "Making a combined request for the children and layouts of " + parentIDs.length + " parent nodes");
			service.getChildrenAndLayout(parentIDs, new AsyncCallback<CombinedResponse[]>()
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
