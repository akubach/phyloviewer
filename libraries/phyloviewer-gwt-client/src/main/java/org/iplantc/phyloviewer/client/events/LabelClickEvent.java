package org.iplantc.phyloviewer.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class LabelClickEvent extends GwtEvent<LabelClickHandler>
{
	public static final Type<LabelClickHandler> TYPE = new Type<LabelClickHandler>();
	private int nodeId;
	private int clientX;
	private int clientY;

	public LabelClickEvent(int nodeId, int clientX, int clientY)
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
	protected void dispatch(LabelClickHandler handler)
	{
		handler.onLabelClick(this);
		
	}

	@Override
	public Type<LabelClickHandler> getAssociatedType()
	{
		return TYPE;
	}

}
