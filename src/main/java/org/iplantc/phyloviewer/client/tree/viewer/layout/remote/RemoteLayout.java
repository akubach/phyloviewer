package org.iplantc.phyloviewer.client.tree.viewer.layout.remote;

import java.util.HashMap;
import java.util.Map;
import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RemoteLayout implements ILayout {
	public static final RemoteLayoutServiceAsync service = (RemoteLayoutServiceAsync) GWT.create(RemoteLayoutService.class);
	
	private String layoutID;
	private final ILayout algorithm;
	
	private Map<String, Vector2> positions = new HashMap<String, Vector2>();
	private Map<String, Box2D> bounds = new HashMap<String, Box2D>();

	public RemoteLayout(ILayout algorithm) {
		this.algorithm = algorithm;
	}
	
	public ILayout getAlgorithm() {
		return algorithm;
	}
	
	@Override
	public Box2D getBoundingBox(INode node) {
		return bounds.get(node.getUUID());
	}

	@Override
	public Vector2 getPosition(INode node) {
		return positions.get(node.getUUID());
	}
	
	public void setBoundingBox(String nodeID, Box2D box) {
		bounds.put(nodeID, box);
	}

	public void setPosition(String nodeID, Vector2 position) {
		positions.put(nodeID, position);
	}

	public String getLayoutID() {
		return layoutID;
	}
	
	public void setLayoutID(String layoutID) {
		this.layoutID = layoutID;
	}

	@Override
	public void layout(ITree tree) {
		if (tree instanceof Tree) {
			clear();
			 //TODO do an RPC here for the root layout and hope it returns before we want to render?  Or just make sure the local portion of the tree is small and do algorithm.layout
//			algorithm.layout(tree);
			service.layout(((Tree) tree).getId(), this.getAlgorithm(), new AsyncCallback<String>() {
				
				@Override
				public void onSuccess(String layoutID) {
					setLayoutID(layoutID);
					//TODO get layout position/bounds for root?  All nodes currently in tree?
				}
				
				@Override
				public void onFailure(Throwable arg0) {
					// TODO Auto-generated method stub
					
				}
			});
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	public void clear() {
		positions.clear();
		bounds.clear();
	}

}
