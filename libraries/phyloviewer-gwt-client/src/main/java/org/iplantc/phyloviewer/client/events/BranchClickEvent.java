package org.iplantc.phyloviewer.client.events;

public class BranchClickEvent extends TreeItemEvent<BranchClickHandler>
{
	public static final Type<BranchClickHandler> TYPE = new Type<BranchClickHandler>();

	public BranchClickEvent(int nodeId, int clientX, int clientY, String metaDataString)
	{
		super(nodeId, clientX, clientY, metaDataString);
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
