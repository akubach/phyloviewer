package org.iplantc.phyloviewer.client.events;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloviewer.client.tree.viewer.DetailView;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class SelectionMouseHandler extends BaseMouseHandler implements HasNodeSelectionHandlers
{
	//TODO listen for tree changes on the view and clear the selection
	
	public static final int DRAG_THRESHOLD = 3; //drags less than this distance will be treated like a click and select a single node
	public static final int BUTTON = NativeEvent.BUTTON_LEFT;
	
	private final DetailView view;
	private Set<INode> currentSelection = new HashSet<INode>();

	/**
	 * Creates a new SelectionMouseHandler that selects nodes on the given view. SelectionEvents are
	 * broadcast on the view's eventBus (as well as to any handlers added with addSelectionHandler()).
	 * 
	 * @param view
	 */
	public SelectionMouseHandler(DetailView view)
	{
		super(view);
		this.view = view;
		//TODO add an overlay to the view, to draw a selection box on
	}

	@Override
	public void onMouseUp(MouseUpEvent upEvent)
	{
		SavedMouseEvent downEvent = super.getCurrentMouseDownEvent(BUTTON); //getting this before it's nulled by super.onMouseUp(upEvent)
		
		super.onMouseUp(upEvent);

		// downEvent can be null.  (I can't recreate it reliably.)
		if (upEvent.getNativeButton() == BUTTON && downEvent != null)
		{
			Vector2 mouseDownPosition = new Vector2(downEvent.x, downEvent.y);
			Vector2 mouseUpPosition = new Vector2(upEvent.getX(), upEvent.getY());
			Box2D selectionRange = Box2D.createBox(mouseDownPosition, mouseUpPosition);
			
			getEventBus().fireEventFromSource(new SelectionAreaChangeEvent(null), this);
			
			updateSelection(selectionRange);
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event)
	{
		super.onMouseMove(event);
		
		/*
		 * MouseMoveEvent.getNativeButton always returns 1, whether the button is up or down, so we check
		 * for a MouseDownEvent stored by the superclass
		 */
		SavedMouseEvent downEvent = super.getCurrentMouseDownEvent(BUTTON);
		
		if (downEvent != null)
		{
			Vector2 mouseDownPosition = new Vector2(downEvent.x, downEvent.y);
			Vector2 mousePosition = new Vector2(event.getX(), event.getY());
			Box2D selectionRange = Box2D.createBox(mouseDownPosition, mousePosition);
			
			getEventBus().fireEventFromSource(new SelectionAreaChangeEvent(selectionRange), this);
			
			updateSelection(selectionRange);
		}
	}

	@Override
	public HandlerRegistration addSelectionHandler(NodeSelectionHandler handler)
	{
		return getEventBus().addHandlerToSource(NodeSelectionEvent.TYPE, this, handler);
	}
	
	public HandlerRegistration addSelectionAreaHandler(SelectionAreaChangeHandler handler)
	{
		return getEventBus().addHandlerToSource(SelectionAreaChangeEvent.TYPE, this, handler);
	}

	@Override
	public void fireEvent(GwtEvent<?> event)
	{
		getEventBus().fireEventFromSource(event, this);
	}
	
	public EventBus getEventBus()
	{
		return view.getEventBus();
	}
	
	private void updateSelection(Box2D screenBox) 
	{
		double dragLength = screenBox.getMax().subtract(screenBox.getMin()).length();
		
		Set<INode> newSelection = Collections.emptySet();
		if (dragLength < DRAG_THRESHOLD)
		{
			//select a single node
			Vector2 center = screenBox.getCenter();
			INode node = view.getNodeAt((int)center.getX(), (int)center.getY());
			newSelection = new HashSet<INode>();
			
			if (node != null)
			{
				newSelection.add(node);
			}
		}
		else
		{
			//select nodes in an area
			newSelection = view.getNodesIn(screenBox);
		}
		
		if (!newSelection.equals(currentSelection))
		{
			Logger.getLogger("").log(Level.FINEST, "Selected " + currentSelection.size() + " nodes in view area " + screenBox);
			currentSelection = newSelection;
			getEventBus().fireEventFromSource(new NodeSelectionEvent(currentSelection), this);
		}
	}
}
