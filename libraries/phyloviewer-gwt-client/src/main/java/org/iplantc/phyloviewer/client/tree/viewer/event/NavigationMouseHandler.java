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
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;

public class NavigationMouseHandler extends BaseMouseHandler 
{
	//TODO listen for tree changes on the view and clear nodeHistory and currentNodeShown
	private final DetailView view;
	private double dragThreshold = 10;
	private Stack<INode> nodeHistory = new Stack<INode>(); // tried LinkedList here and the compiler complained that LinkedList.push() and LinkedList.pop() are undefined...
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
		
		super.onMouseUp(event);
		
		double finalDx = event.getX() - downEvent.x;
		double finalDy = event.getY() - downEvent.y;
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
	}

	@Override
	public void onMouseMove(MouseMoveEvent event)
	{
		SavedMouseEvent downEvent = super.getCurrentMouseDownEvent(NativeEvent.BUTTON_LEFT);
		
		Vector2 event1 = super.getLastMousePosition(); //getting prev position before super.onMouseMove(event) updates it
		super.onMouseMove(event);
		Vector2 event0 = super.getLastMousePosition();
		
		if(downEvent != null && event0 != null && event1 != null)
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

	@Override
	public void onMouseWheel(MouseWheelEvent event)
	{
		super.onMouseWheel(event);
		
		double amount = -event.getDeltaY() / 10.0;
		amount = Math.pow(2, amount);
		view.getCamera().zoom(amount);
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event)
	{
		super.onDoubleClick(event);

		INode node = getClickedNode(event);
		nodeHistory.push(currentNodeShown);
		show(node);
	}
	
	private void show(INode node)
	{
		if (node == null)
		{
			node = view.getTree().getRootNode();
		}

		//TODO move this into View and AnimatedView
		if(view instanceof AnimatedView)
		{
			((AnimatedView)view).animateZoomToNode(node);
		}
		else
		{
			view.zoomToFitSubtree(node);
		}
		
		currentNodeShown = node;
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
			// TODO pan right one 'step'. Where to? Keep forward history?  Go to highest child?
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
}
