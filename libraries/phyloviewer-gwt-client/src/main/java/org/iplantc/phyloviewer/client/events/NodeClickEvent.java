package org.iplantc.phyloviewer.client.events;

public class NodeClickEvent extends TreeItemEvent<NodeClickHandler>
{
	public static final Type<NodeClickHandler> TYPE = new Type<NodeClickHandler>();

	public NodeClickEvent(int nodeId, int clientX, int clientY, String metaDataString)
	{
		super(nodeId, clientX, clientY, metaDataString);
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
