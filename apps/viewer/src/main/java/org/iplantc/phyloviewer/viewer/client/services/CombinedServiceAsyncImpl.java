package org.iplantc.phyloviewer.viewer.client.services;

import java.util.ArrayList;
import java.util.HashMap;

import org.iplantc.phyloviewer.viewer.client.services.CombinedService.CombinedResponse;
import org.iplantc.phyloviewer.viewer.client.services.CombinedService.NodeResponse;

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
	private boolean defer = true;
	private BatchRequestCommand nextRequestCommand;
	
	public CombinedServiceAsyncImpl() {
		service = GWT.create(CombinedService.class);
	}

	@Override
	public void getRootNode(int treeId, AsyncCallback<NodeResponse> callback)
	{
		service.getRootNode(treeId, callback);
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
			
			// Is this logging needed?
			//Logger.getLogger("").log(Level.INFO, "Making a combined request for the children and layouts of " + parentIDs.length + " parent nodes");
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
