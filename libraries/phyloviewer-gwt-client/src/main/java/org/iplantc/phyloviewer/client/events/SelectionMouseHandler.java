package org.iplantc.phyloviewer.client.events;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloviewer.client.tree.viewer.DetailView;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.scene.Rectangle;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
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
	
	Rectangle selectionBox = new Rectangle();

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
	}

	@Override
	public void onMouseDown(MouseDownEvent event)
	{
		super.onMouseDown(event);
		
		SavedMouseEvent downEvent = super.getCurrentMouseDownEvent(BUTTON);
		
		if (downEvent != null)
		{	
			if (!downEvent.isControlKeyDown && !downEvent.isMetaKeyDown) 
			{
				clearSelection();
			}
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent upEvent)
	{
		SavedMouseEvent downEvent = super.getCurrentMouseDownEvent(BUTTON); //getting this before it's nulled by super.onMouseUp(upEvent)
		boolean isDragging = isDragging(BUTTON);
		
		super.onMouseUp(upEvent);

		// downEvent can be null.  (I can't recreate it reliably.)
		if (upEvent.getNativeButton() == BUTTON && downEvent != null)
		{
			if (isDragging)
			{
				Vector2 up = new Vector2(upEvent.getX(), upEvent.getY());
				Box2D box = Box2D.createBox(downEvent.getLocation(), up);
				addToSelection(view.getNodesIn(box));
			}
			else
			{
				//handle an actual (non-drag) click.  (onClick() gets called whether dragging or not)
				INode node = view.getNodeAt(upEvent.getX(), upEvent.getY());
				if (node != null)
				{
					if (downEvent.isShiftKeyDown)
					{
						addSubtreeToSelection(node);
					}
					else 
					{
						addToSelection(node, true);
					}
				}
			}
		}
		
		//clear selection box
		updateSelectionArea(null);
	}

	@Override
	public void onMouseMove(MouseMoveEvent event)
	{
		super.onMouseMove(event);
		
		if (isDragging(BUTTON))
		{
			Vector2 start = super.getCurrentMouseDownEvent(BUTTON).getLocation();
			Vector2 end = new Vector2(event.getX(), event.getY());
			Box2D box = Box2D.createBox(start, end);
			updateSelectionArea(box);
		}
	}

	@Override
	public void onMouseOut(MouseOutEvent event)
	{
		super.onMouseOut(event);
		
		updateSelectionArea(null); //assume the button is released when leaving the widget.  (see BaseMouseHandler.onMouseOver())
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
	
	private void updateSelectionArea(Box2D box)
	{
		if (box != null)
		{
			selectionBox.setMin(box.getMin());
			selectionBox.setMax(box.getMax());
			view.addOverlay(selectionBox);
		}
		else
		{
			view.removeOverlay(selectionBox);
		}
		
		view.requestRender();
		
		getEventBus().fireEventFromSource(new SelectionAreaChangeEvent(box), this);
	}

	private void addToSelection(INode node, boolean fireEvent)
	{
		boolean change = currentSelection.add(node);
		
		if (change && fireEvent)
		{
			Logger.getLogger("").log(Level.FINEST, "Added node to selection. " + currentSelection.size() + " total nodes are selected.");
			getEventBus().fireEventFromSource(new NodeSelectionEvent(currentSelection), this);
		}
	}
	
	private void addToSelection(Set<INode> nodes)
	{
		boolean change = currentSelection.addAll(nodes);
		
		if (change)
		{
			Logger.getLogger("").log(Level.FINEST, "Added nodes to selection. " + currentSelection.size() + " total nodes are selected.");
			getEventBus().fireEventFromSource(new NodeSelectionEvent(currentSelection), this);
		}
	}
	
	private void clearSelection()
	{
		Logger.getLogger("").log(Level.FINEST, "Cleared selection");
		currentSelection = new HashSet<INode>();
		getEventBus().fireEventFromSource(new NodeSelectionEvent(currentSelection), this);
	}
	
	private void addSubtreeToSelection(INode node)
	{
		addToSelection(node, false);
		
		INode[] children = node.getChildren();
		if (children != null)
		{
			for (INode child : children)
			{
				if (child != null)
				{
					addSubtreeToSelection(child);
				}
			}
		}
		
		getEventBus().fireEventFromSource(new NodeSelectionEvent(currentSelection), this);
	}
}
