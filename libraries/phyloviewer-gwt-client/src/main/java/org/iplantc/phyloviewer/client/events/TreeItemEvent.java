package org.iplantc.phyloviewer.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public abstract class TreeItemEvent<H extends EventHandler> extends GwtEvent<H>
{
	private int nodeId;
	private int clientX;
	private int clientY;
	
	protected TreeItemEvent(int nodeId, int clientX, int clientY)
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
}
