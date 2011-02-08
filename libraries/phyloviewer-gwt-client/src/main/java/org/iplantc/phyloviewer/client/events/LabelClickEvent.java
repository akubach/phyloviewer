package org.iplantc.phyloviewer.client.events;

public class LabelClickEvent extends TreeItemEvent<LabelClickHandler>
{
	public static final Type<LabelClickHandler> TYPE = new Type<LabelClickHandler>();

	public LabelClickEvent(int nodeId, int clientX, int clientY)
	{
		super(nodeId, clientX, clientY);
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
