package org.iplantc.phyloviewer.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class NodeClickEvent extends GwtEvent<NodeClickHandler>
{
	public static final Type<NodeClickHandler> TYPE = new Type<NodeClickHandler>();
	private int nodeId;
	private int clientX;
	private int clientY;

	public NodeClickEvent(int nodeId, int clientX, int clientY)
	{
		this.nodeId = nodeId;
		this.clientX = clientX;
		this.clientY = clientY;
	}

	public int getNodeId()
	{
		return nodeId;
	}
	
	public int getClientX()
	{
		return clientX;
	}
	
	public int getClientY()
	{
		return clientY;
	}

	@Override
	protected void dispatch(NodeClickHandler handler)
	{
		handler.onNodeClick(this);
	}

	@Override
	public Type<NodeClickHandler> getAssociatedType()
	{
		return TYPE;
	}
}
