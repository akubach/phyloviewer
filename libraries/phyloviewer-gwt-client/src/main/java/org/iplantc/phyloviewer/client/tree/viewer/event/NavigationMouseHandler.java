package org.iplantc.phyloviewer.client.tree.viewer.event;

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloviewer.client.tree.viewer.AnimatedView;
import org.iplantc.phyloviewer.client.tree.viewer.DetailView;
import org.iplantc.phyloviewer.shared.math.Matrix33;
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

public class NavigationMouseHandler extends HandlesAllMouseEvents implements ClickHandler,
		DoubleClickHandler
{
	private final DetailView view; // TODO could generalize this to View, if View implements isXPannable(), isYPannable() and getNodeAt
	private Vector2 clickedPosition = null;
	private int buttonDown = -1;
	private Vector2 event0 = null;
	private Vector2 event1 = null;
	private double dragThreshold = 10;
	private Stack<INode> nodeHistory = new Stack<INode>(); // tried LinkedList here and the compiler complained that LinkedList.push() and LinkedList.pop() are undefined...

	public NavigationMouseHandler(DetailView view)
	{
		this.view = view;
	}

	@Override
	public void onMouseDown(MouseDownEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "MouseDown");
		event.preventDefault();

		buttonDown = event.getNativeButton();
		clickedPosition = new Vector2(event.getX(), event.getY());
	}

	@Override
	public void onMouseUp(MouseUpEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "MouseUp");
		event.preventDefault();

		buttonDown = -1;
		double finalDx = event.getX() - clickedPosition.getX();
		double finalDy = event.getY() - clickedPosition.getY();
		double absDx = Math.abs(finalDx);
		double absDy = Math.abs(finalDy);

		if(!view.isXPannable() && absDx > absDy && absDx > dragThreshold)
		{
			gestureX(finalDx);
		}
		else if(!view.isYPannable() && absDy > absDx && absDy > dragThreshold)
		{
			gestureY(finalDy);
		}

		clickedPosition = null;
		event0 = null;
		event1 = null;
	}

	@Override
	public void onMouseMove(MouseMoveEvent event)
	{
		event1 = event0;
		event0 = new Vector2(event.getX(), event.getY());

		if(NativeEvent.BUTTON_LEFT == buttonDown) //event.getNativeButton always has value 1 for MouseMoveEvent.  See http://code.google.com/p/google-web-toolkit/issues/detail?id=3983
		{
			if(event0 != null && event1 != null && clickedPosition != null)
			{

				Matrix33 M = view.getCamera().getMatrix(view.getWidth(), view.getHeight());
				Matrix33 IM = M.inverse();

				Vector2 p0 = IM.transform(event0);
				Vector2 p1 = IM.transform(event1);

				double x = view.isXPannable() ? p0.getX() - p1.getX() : 0.0;
				double y = view.isYPannable() ? p0.getY() - p1.getY() : 0.0;
				view.getCamera().pan(x, y);
			}
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
		double amount = -event.getDeltaY() / 10.0;
		amount = Math.pow(2, amount);
		view.getCamera().zoom(amount);
	}

	@Override
	public void onClick(ClickEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "Click");
		event.preventDefault();
		//TODO
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "DoubleClick");
		event.preventDefault();

		INode node = getClickedNode(event);
		nodeHistory.push(node);
		show(node);
	}

	private void show(INode node)
	{
		if(node != null)
		{
			if(view instanceof AnimatedView)
			{
				((AnimatedView)view).animateZoomToNode(node);
			}
			else
			{
				view.zoomToFitSubtree(node);
			}
		}
	}

	private INode getClickedNode(DoubleClickEvent event)
	{
		int x = event.getRelativeX(view.getElement());
		int y = event.getRelativeY(view.getElement());

		return view.getNodeAt(x, y);
	}

	private void gestureX(double dx)
	{
		Logger.getLogger("").log(Level.FINEST, "gestureX() " + dx);
		if(dx > 0)
		{
			// TODO pan right one 'step'. Where to?
		}
		else
		{
			if(!nodeHistory.isEmpty())
			{
				// TODO if user is still looking at the last node (hasn't panned
				// away) they won't really see a change on the first gesture.
				// Check camera location first? Or just go immediately to the
				// previously viewed node?
				show(nodeHistory.pop());
			}
			else
			{
				show(view.getTree().getRootNode());
			}
		}
	}

	private void gestureY(double dy)
	{
		Logger.getLogger("").log(Level.FINEST, "gestureY() " + dy);
		// TODO
	}
}
