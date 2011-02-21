package org.iplantc.phyloviewer.client.events;

import org.iplantc.phyloviewer.shared.math.Box2D;

import com.google.gwt.event.shared.GwtEvent;

public class SelectionAreaChangeEvent extends GwtEvent<SelectionAreaChangeHandler>
{
	public static final Type<SelectionAreaChangeHandler> TYPE = new Type<SelectionAreaChangeHandler>();
	
	private Box2D selectionArea;
	
	public SelectionAreaChangeEvent(Box2D selectionArea)
	{
		this.selectionArea = selectionArea;
	}

	public Box2D getSelectionArea()
	{
		return selectionArea;
	}

	@Override
	protected void dispatch(SelectionAreaChangeHandler handler)
	{
		handler.onSelectionAreaChange(this);
	}

	@Override
	public Type<SelectionAreaChangeHandler> getAssociatedType()
	{
		return TYPE;
	}

}
