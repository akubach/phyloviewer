package org.iplantc.phyloviewer.client.events;

import java.util.Set;

import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Using this instead of SelectionEvent because it doesn't have a public constructor and the fire method
 * tends to get it fired on widgets' HandlerManager instead of their EventBus.
 */
public class NodeSelectionEvent extends GwtEvent<NodeSelectionHandler>
{
	public static final Type<NodeSelectionHandler> TYPE = new Type<NodeSelectionHandler>();
	private Set<INode> selectedNodes;
	
	public NodeSelectionEvent(Set<INode> selectedNodes)
	{
		this.selectedNodes = selectedNodes;
	}
	
	public Set<INode> getSelectedNodes()
	{
		return selectedNodes;
	}

	@Override
	protected void dispatch(NodeSelectionHandler handler)
	{
		handler.onNodeSelection(this);
	}

	@Override
	public Type<NodeSelectionHandler> getAssociatedType()
	{
		return TYPE;
	}
}
