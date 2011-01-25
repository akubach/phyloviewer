package org.iplantc.phyloviewer.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class BranchClickEvent extends GwtEvent<BranchClickHandler>
{
	public static final Type<BranchClickHandler> TYPE = new Type<BranchClickHandler>();
	private int nodeId;
	private int clientX;
	private int clientY;

	public BranchClickEvent(int nodeId, int clientX, int clientY)
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
	protected void dispatch(BranchClickHandler handler)
	{
		handler.onBranchClick(this);
	}

	@Override
	public Type<BranchClickHandler> getAssociatedType()
	{
		return TYPE;
	}
}
