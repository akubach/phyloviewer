package org.iplantc.phyloviewer.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class NodeClickEvent extends GwtEvent<NodeClickHandler> {

	public static final Type<NodeClickHandler> TYPE = new Type<NodeClickHandler>();
	private int nodeId;
	
	public NodeClickEvent(int nodeId) {
		this.nodeId = nodeId;
	}
	
	public int getNodeId() {
		return nodeId;
	}
	
	@Override
	protected void dispatch(NodeClickHandler handler) {
		handler.onNodeClick(this);
	}

	@Override
	public Type<NodeClickHandler> getAssociatedType() {
		return TYPE;
	}
}
