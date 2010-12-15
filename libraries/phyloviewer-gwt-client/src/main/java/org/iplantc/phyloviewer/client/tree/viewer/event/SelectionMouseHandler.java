package org.iplantc.phyloviewer.client.tree.viewer.event;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloviewer.client.tree.viewer.DetailView;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HandlesAllMouseEvents;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class SelectionMouseHandler extends HandlesAllMouseEvents implements ClickHandler, DoubleClickHandler, HasNodeSelectionHandlers
{
	//TODO listen for tree changes on the view and clear the selection
	
	public static final int selectionMargin = 10; //max selection distance in pixels
	
	private final DetailView view;
	private Vector2 mouseDownPosition = null;
	private int buttonDown = -1;
	private List<INode> currentSelection = new ArrayList<INode>();
	private final EventBus eventBus;

	/**
	 * Creates a new SelectionMouseHandler that selects nodes on the given view. SelectionEvents are
	 * broadcast on the view's eventBus (as well as to any handlers added with addSelectionHandler()).
	 * 
	 * @param view
	 */
	public SelectionMouseHandler(DetailView view)
	{
		this.view = view;
		this.eventBus = view.getEventBus();
		//TODO add an overlay to the view, to draw a selection box on?
	}

	@Override
	public void onMouseDown(MouseDownEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "MouseDown");
		event.preventDefault();

		currentSelection.clear(); //TODO if control key down, don't clear, for multi-select
		mouseDownPosition = new Vector2(event.getX(), event.getY()); //TODO if shift key down don't update mouseDownPosition
	}

	@Override
	public void onMouseUp(MouseUpEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "MouseUp");
		event.preventDefault();

		buttonDown = -1;
		Vector2 mousePosition = new Vector2(event.getX(), event.getY());
		Box2D selectionRange = Box2D.createBox(mouseDownPosition, mousePosition);
		selectionRange.expandBy(selectionMargin);
		updateSelection(selectionRange);
	}

	@Override
	public void onMouseMove(MouseMoveEvent event)
	{
		if (buttonDown == NativeEvent.BUTTON_LEFT)
		{
			Vector2 mousePosition = new Vector2(event.getX(), event.getY());
			Box2D selectionRange = Box2D.createBox(mouseDownPosition, mousePosition);
			selectionRange.expandBy(selectionMargin);
			updateSelection(selectionRange);
		}
	}

	@Override
	public void onMouseOut(MouseOutEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "MouseOut");
		// TODO
	}

	@Override
	public void onMouseOver(MouseOverEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "MouseOver");
		// TODO
	}

	@Override
	public void onMouseWheel(MouseWheelEvent event)
	{
		//TODO
	}

	@Override
	public void onClick(ClickEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "Click");
		event.preventDefault();
		//onMouseUp already handles a single click
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "DoubleClick");
		event.preventDefault();
		//TODO select subtree?
	}

	@Override
	public HandlerRegistration addSelectionHandler(NodeSelectionHandler handler)
	{
		return getEventBus().addHandlerToSource(NodeSelectionEvent.TYPE, this, handler);
	}

	@Override
	public void fireEvent(GwtEvent<?> event)
	{
		getEventBus().fireEventFromSource(event, this);
	}
	
	public EventBus getEventBus()
	{
		return this.eventBus;
	}
	
	private void updateSelection(Box2D screenBox) 
	{
		currentSelection.addAll(view.getNodesIn(screenBox));
		Logger.getLogger("").log(Level.FINEST, "Selected " + currentSelection.size() + " nodes in view area " + screenBox);
		eventBus.fireEventFromSource(new NodeSelectionEvent(currentSelection), this);
	}
}
