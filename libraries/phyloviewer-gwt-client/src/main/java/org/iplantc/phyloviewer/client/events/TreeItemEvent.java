package org.iplantc.phyloviewer.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public abstract class TreeItemEvent<H extends EventHandler> extends GwtEvent<H>
{
	private int nodeId;
	private int clientX;
	private int clientY;
	private String metaDataString;
	
	protected TreeItemEvent(int nodeId, int clientX, int clientY, String metaDataString)
	{
		this.nodeId = nodeId;
		this.clientX = clientX;
		this.clientY = clientY;
		this.metaDataString = metaDataString;
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
	
	public String getMetaDataString()
	{
		return metaDataString;
	}
}
