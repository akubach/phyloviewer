package org.iplantc.phyloviewer.client.events;

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloviewer.client.tree.viewer.DetailView;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.RenderPreferences;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;

public class NavigationMouseHandler extends BaseMouseHandler
{
	// TODO listen for tree changes on the view and clear nodeHistory and currentNodeShown
	private final DetailView view;

	private Stack<INode> nodeHistory = new Stack<INode>(); // tried LinkedList here and the compiler
															// complained that LinkedList.push() and
															// LinkedList.pop() are undefined...
	private INode currentNodeShown = null;

	public NavigationMouseHandler(DetailView view)
	{
		super(view);
		this.view = view;
	}

	@Override
	public void onMouseUp(MouseUpEvent event)
	{
		SavedMouseEvent downEvent = super.getCurrentMouseDownEvent(NativeEvent.BUTTON_LEFT); //getting mouse down data before super.onMouseUp(event) clears it
		boolean wasDragging = isDragging(NativeEvent.BUTTON_LEFT);
		
		super.onMouseUp(event);

		if (wasDragging) 
		{
			handleDragFinished(downEvent, new SavedMouseEvent(event));
		}
		else 
		{
			handleClick(downEvent, new SavedMouseEvent(event));
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event)
	{
		Vector2 event1 = super.getLastMousePosition(); // getting prev position before
														// super.onMouseMove(event) updates it
		super.onMouseMove(event);
		Vector2 event0 = super.getLastMousePosition();

		if(isDragging(NativeEvent.BUTTON_LEFT) && event0 != null && event1 != null)
		{
			Vector2 p0 = view.getPositionInLayoutSpace(event0);
			Vector2 p1 = view.getPositionInLayoutSpace(event1);

			double x = p0.getX() - p1.getX();
			double y = p0.getY() - p1.getY();
			view.pan(x, y);
		}
	}

	@Override
	public void onMouseWheel(MouseWheelEvent event)
	{
		super.onMouseWheel(event);

		double amount = -event.getDeltaY() / 10.0;
		amount = Math.pow(2, amount);
		view.zoom(amount);
	}
	
	@Override
	public void onDoubleClick(DoubleClickEvent event)
	{
		super.onDoubleClick(event);
		
		// On Mac, control + left click shows the context menu.  Also check for the 'meta' (apple) key.
		if (event.isControlKeyDown() || event.isMetaKeyDown())
		{	
			//toggle collapsed
			INode node = view.getNodeAt(event.getX(), event.getY());
			RenderPreferences renderPreferences = view.getRenderPreferences();
			boolean isCollapsed = renderPreferences.isCollapsed(node);
			renderPreferences.setCollapsed(node, !isCollapsed);
			view.requestRender();
		}
		else
		{
			//show the clicked node
			INode node = view.getNodeAt(event.getX(), event.getY());
			nodeHistory.push(currentNodeShown);
			
			// Only show if the node is not null.
			// Otherwise, if the user double clicks on the background, the viewer zooms to the root.
			// (I don't think that behavior is expected.)
			if(node != null)
			{
				show(node);
			}
		}
	}

	private void show(INode node)
	{
		if(node == null)
		{
			node = view.getTree().getRootNode();
		}

		view.zoomToFitSubtree(node);
		currentNodeShown = node;
	}

	private void gestureX(double dx)
	{
		Logger.getLogger("").log(Level.FINEST, "gestureX() " + dx);
		if(dx > 0)
		{
			// TODO pan right one 'step'. Where to? Keep forward history? Go to highest child?
		}
		else
		{
			INode nodeToShow = null;
			if(!nodeHistory.isEmpty())
			{
				nodeToShow = nodeHistory.pop();
			}

			show(nodeToShow);
		}
	}

	private void gestureY(double dy)
	{
		Logger.getLogger("").log(Level.FINEST, "gestureY() " + dy);
		// TODO?
	}

	/**
	 * This gets called from onMouseUp when it decides the mouseup event is not a drag.
	 * 
	 * @param downEvent
	 * @param savedMouseEvent
	 */
	private void handleClick(SavedMouseEvent downEvent, SavedMouseEvent upEvent)
	{
		/*
		 * currently a single click does nothing. Note that this method would get called twice on a
		 * double click (in addition to the onDoubleClick())
		 */
	}
	
	private void handleDragFinished(SavedMouseEvent downEvent, SavedMouseEvent upEvent)
	{
		Vector2 v = upEvent.getLocation().subtract(downEvent.getLocation());
		if(Math.abs(v.getX()) > Math.abs(v.getY()))
		{
			gestureX(v.getX());
		}
		else 
		{
			gestureY(v.getY());
		}
	}
}
