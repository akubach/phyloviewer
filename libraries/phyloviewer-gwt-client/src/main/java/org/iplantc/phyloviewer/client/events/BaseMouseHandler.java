package org.iplantc.phyloviewer.client.events;

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
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;

public class BaseMouseHandler extends HandlesAllMouseEvents implements ClickHandler, DoubleClickHandler
{
	/** The largest button code returned by event.getNativeButton() */
	public static final int MAX_BUTTON = 4;
	
	private Widget targetWidget;
	
	/** Current mousedown events, indexed by mouse button.  Null if button is up. */
	private SavedMouseEvent[] mouseDownEvents = new SavedMouseEvent[MAX_BUTTON + 1];
	
	private SavedMouseEvent lastMouseMove = null;
	
	private double dragThreshold = 10;
	
	private boolean[] isDragging = new boolean[MAX_BUTTON + 1];
	
	public BaseMouseHandler(Widget targetWidget)
	{
		this.targetWidget = targetWidget;
	}

	@Override
	public void onClick(ClickEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "Click button: " + event.getNativeButton());
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "DoubleClick button: " + event.getNativeButton());
	}

	@Override
	public void onMouseDown(MouseDownEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "MouseDown button: " + event.getNativeButton());
		event.preventDefault();
		if (targetWidget instanceof Focusable)
		{
			/*
			 * the preventDefault above is necessary to prevent the mouse cursor changing to a text
			 * selection cursor when dragging, but it also prevents the widget from getting focus, so I
			 * set that here.
			 */
			((Focusable)targetWidget).setFocus(true);
		}
		
		int button = event.getNativeButton();
		mouseDownEvents[button] = new SavedMouseEvent(event);
	}

	@Override
	public void onMouseUp(MouseUpEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "MouseUp button: " + event.getNativeButton());
		
		int button = event.getNativeButton();
		mouseDownEvents[button] = null;
		isDragging[button] = false;
	}

	@Override
	public void onMouseMove(MouseMoveEvent event)
	{
		lastMouseMove = new SavedMouseEvent(event);
		
		//update dragging status, if button is down and hasn't already been marked as dragging
		for (int button = 0; button < mouseDownEvents.length; button++) 
		{
			if (!isDragging[button] && mouseDownEvents[button] != null)
			{
				if (isPastDragThreshold(button))
				{
					isDragging[button] = true;
				}
			}
		}
	}

	@Override
	public void onMouseOut(MouseOutEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "MouseOut button: " + event.getNativeButton());
	}

	@Override
	public void onMouseOver(MouseOverEvent event)
	{
		Logger.getLogger("").log(Level.FINEST, "MouseOver button: " + event.getNativeButton());
		
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
		if (button > MAX_BUTTON || button < 0)
		{
			return null;
		}
		else
		{
			return mouseDownEvents[button];
		}
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
	
	public boolean isDragging(int button)
	{
		return isDragging[button];
	}
	
	private boolean isPastDragThreshold(int button)
	{
		boolean isPast = false;
		
		SavedMouseEvent downEvent = getCurrentMouseDownEvent(button);
		if (downEvent != null)
		{
			int dx = lastMouseMove.x - downEvent.x;
			int dy = lastMouseMove.y - downEvent.y;
			double dragDistSq = dx * dx + dy * dy;
			if (dragDistSq > dragThreshold * dragThreshold)
			{
				isPast = true;
			}
		}
		
		return isPast;
	}
	
	private void clearMouseDown()
	{
		mouseDownEvents = new SavedMouseEvent[MAX_BUTTON + 1];
		isDragging = new boolean[MAX_BUTTON + 1];
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
