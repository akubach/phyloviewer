package org.iplantc.phyloviewer.client.tree.viewer;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloviewer.client.events.EventFactory;
import org.iplantc.phyloviewer.client.services.CombinedServiceAsync;
import org.iplantc.phyloviewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayout;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.model.Document;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.Tree;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PagedDocument extends Document {
	
	private static Logger rootLogger = Logger.getLogger("");
	EventBus eventBus;

	public PagedDocument(CombinedServiceAsync combinedService, EventBus eventBus, Tree tree) {
		super();
		
		this.eventBus = eventBus;
		
		this.setTree(tree);
		this.setLayout(new RemoteLayout());
	}
	
	@Override
	public boolean checkForData(final INode node)
	{
		final ILayoutData layout = this.getLayout();
		if (node instanceof RemoteNode && layout instanceof RemoteLayout) 
		{
			final RemoteLayout rLayout = (RemoteLayout) layout;
			final RemoteNode rNode = (RemoteNode) node;
		
			if (rNode.getChildren() == null) {
				
				rNode.getChildrenAsync(new AsyncCallback<RemoteNode[]>()
				{
					@Override
					public void onSuccess(RemoteNode[] arg0)
					{
						//do another check to make sure layouts have been fetched before requesting render
						checkForData(rNode);
					}
					
					@Override
					public void onFailure(Throwable arg0)
					{
						// TODO Auto-generated method stub
					}
				});
	
				return false;
				
			} else if (!rLayout.containsNodes(rNode.getChildren())) {
				rootLogger.log(Level.INFO, "RenderTree.checkForData(): Fetching layouts for children of node \"" + rNode + "\".");
				rLayout.getLayoutAsync(rNode.getChildren(), rLayout.new GotLayouts() {
					@Override
					protected void gotLayouts(LayoutResponse[] responses) {
						if (eventBus != null) {
							rootLogger.log(Level.INFO, "Rendering: got layouts for children of node \"" + rNode + "\".");
							eventBus.fireEvent(EventFactory.createRenderEvent());
						}
					}
				});
				
				return false;
			}
		}
		
		return true;
	}
}
