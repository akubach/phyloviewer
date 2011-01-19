package org.iplantc.phyloviewer.client.events;

import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.event.shared.GwtEvent;

public class NodeClickEvent extends GwtEvent<NodeClickHandler> {

	public static final Type<NodeClickHandler> TYPE = new Type<NodeClickHandler>();
	private INode node;
	
	public NodeClickEvent(INode node) {
		this.node = node;
	}
	
	public INode getNode() {
		return node;
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
