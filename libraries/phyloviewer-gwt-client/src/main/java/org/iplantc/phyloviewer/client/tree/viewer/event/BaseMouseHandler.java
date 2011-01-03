package org.iplantc.phyloviewer.client.tree.viewer.event;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloviewer.shared.math.Vector2;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HandlesAllMouseEvents;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;

public class BaseMouseHandler extends HandlesAllMouseEvents implements ClickHandler, DoubleClickHandler
{
	/** Current mousedown events, indexed by mouse button.  Null if button is up. */
	private Vector<SavedMouseEvent> mouseDownEvents = new Vector<SavedMouseEvent>();
	
	private boolean isMouseOver = false;
	
	private SavedMouseEvent lastMouseMove = null;

	@Override
	public void onClick(ClickEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "Click button: " + event.getNativeButton());
		event.preventDefault();
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "DoubleClick button: " + event.getNativeButton());
		event.preventDefault();
	}

	@Override
	public void onMouseDown(MouseDownEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "MouseDown button: " + event.getNativeButton());
		event.preventDefault();
		
		int button = event.getNativeButton();
		
		if (mouseDownEvents.size() < button + 1) {
			mouseDownEvents.setSize(button + 1);
		}
		
		mouseDownEvents.set(button, new SavedMouseEvent(event));
	}

	@Override
	public void onMouseUp(MouseUpEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "MouseUp button: " + event.getNativeButton());
		event.preventDefault();
		
		int button = event.getNativeButton();
		mouseDownEvents.set(button, null);
	}

	@Override
	public void onMouseMove(MouseMoveEvent event)
	{
		isMouseOver = true; //can I assume the handler doesn't receive events when the mouse is not over the widget?
		lastMouseMove = new SavedMouseEvent(event);
	}

	@Override
	public void onMouseOut(MouseOutEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "MouseOut button: " + event.getNativeButton());
		isMouseOver = false;
	}

	@Override
	public void onMouseOver(MouseOverEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "MouseOver button: " + event.getNativeButton());
		isMouseOver = true;
		
		/*
		 * MouseOverEvent.getNativeButton() currently returns 1 even if no button is down (for many
		 * browsers), so there's no good way to test if the user released the button while the mouse was
		 * outside the view. So, to err on the side of avoiding spurious actions, I'm going to assume
		 * all buttons were released.
		 */
		clearMouseDown();
	}

	@Override
	public void onMouseWheel(MouseWheelEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "MouseWheel delta: " + event.getDeltaY());
	}
	
	public SavedMouseEvent getCurrentMouseDownEvent(int button) 
	{
		SavedMouseEvent event = null;
		
		if(mouseDownEvents.size() > button)
		{
			event = mouseDownEvents.get(button);
		}
		
		return event;
	}
	
	public boolean isMouseOver()
	{
		return isMouseOver;
	}
	
	public SavedMouseEvent getLastMouseMove()
	{
		return lastMouseMove;
	}
	
	public Vector2 getLastMousePosition()
	{
		if (lastMouseMove == null) 
		{
			return null;
		}
		
		return new Vector2(lastMouseMove.x, lastMouseMove.y);
	}
	
	private void clearMouseDown()
	{
		mouseDownEvents = new Vector<SavedMouseEvent>();
	}

	/** MouseDownEvents are killed by GWT after they are handled.  This class stores some details of a MouseDownEvent for later use. */
	public class SavedMouseEvent
	{
		public final int nativeButton;
		public final int x;
		public final int y;
		public final boolean isAltKeyDown;
		public final boolean isControlKeyDown;
		public final boolean isMetaKeyDown;
		public final boolean isShiftKeyDown;
		
		@SuppressWarnings("unchecked")
		public SavedMouseEvent(MouseEvent event)
		{
			nativeButton = event.getNativeButton();
			x = event.getX();
			y = event.getY();
			isAltKeyDown = event.isAltKeyDown();
			isControlKeyDown = event.isControlKeyDown();
			isMetaKeyDown = event.isMetaKeyDown();
			isShiftKeyDown = event.isShiftKeyDown();
		}
	}
}
