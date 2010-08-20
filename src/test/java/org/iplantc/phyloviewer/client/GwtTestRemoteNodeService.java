package org.iplantc.phyloviewer.client;

import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNodeService;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNodeServiceAsync;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Tests RemoteNodeService as GWT RPC service
 */
public class GwtTestRemoteNodeService extends GWTTestCase {
	RemoteNodeServiceAsync service;

	@Override
	public String getModuleName() {
		return "org.iplantc.phyloviewer.Phyloviewer";
	}
	
	@Before
	public void gwtSetUp() {
		service = GWT.create(RemoteNodeService.class);
	}
	
	@Test
	public void testFetchTree() {
		delayTestFinish(1000);
		
		service.fetchTree(Constants.SMALL_TREE, new AsyncCallback<Tree>() {

			@Override
			public void onFailure(Throwable cause) {
				throw new RuntimeException("fetchTree failed", cause);
			}

			@Override
			public void onSuccess(Tree arg0) {
				finishTest();
			}
			
		});
	}
	
	@Test
	public void testGetChildren() {
		delayTestFinish(1000);
		
		service.fetchTree(Constants.SMALL_TREE, new AsyncCallback<Tree>() {

			@Override
			public void onFailure(Throwable cause) {
				throw new RuntimeException("fetchTree failed", cause);
			}

			@Override
			public void onSuccess(Tree tree) {
				RemoteNode root = (RemoteNode) tree.getRootNode();
				
				service.getChildren(root.getUUID(), new AsyncCallback<RemoteNode[]>() {
					
					@Override
					public void onSuccess(RemoteNode[] children) {
						assertNotNull(children);
						assertEquals(2, children.length);
						assertEquals("Asa._alaskanum", children[0].getLabel());
						assertEquals("interventor", children[1].getLabel());
						finishTest();
					}
					
					@Override
					public void onFailure(Throwable cause) {
						throw new RuntimeException("getChildren failed", cause);
					}
				});
			}
		});

	}

}
