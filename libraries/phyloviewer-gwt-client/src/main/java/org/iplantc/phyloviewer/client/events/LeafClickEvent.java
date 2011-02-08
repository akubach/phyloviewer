package org.iplantc.phyloviewer.client.events;

public class LeafClickEvent extends TreeItemEvent<LeafClickHandler>
{
	public static final Type<LeafClickHandler> TYPE = new Type<LeafClickHandler>();

	public LeafClickEvent(int nodeId, int clientX, int clientY)
	{
		super(nodeId, clientX, clientY);
	}

	@Override
	protected void dispatch(LeafClickHandler handler)
	{
		handler.onLeafClick(this);
	}

	@Override
	public Type<LeafClickHandler> getAssociatedType()
	{
		return TYPE;
	}
}
