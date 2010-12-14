package org.iplantc.phyloviewer.client.tree.viewer;

import java.util.List;

import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class ContextMenu extends StackLayoutPanel implements SelectionHandler<List<INode>>
{
	List<INode> selection; 
	//TODO add a widget at the top that shows some info about the selected nodes
	
	/**
	 * Creates a ContextMenu that listens to SelectionEvents from one source
	 */
	public ContextMenu(HasSelectionHandlers<List<INode>> widget)
	{
		super(Unit.EM);
		widget.addSelectionHandler(this);
	}
	
	/**
	 * Creates a ContextMenu that listens to all SelectionEvents on an EventBus
	 * @param eventBus
	 */
	public ContextMenu(EventBus eventBus)
	{
		super(Unit.EM);
		eventBus.addHandler(SelectionEvent.getType(), this); //listens for all SelectionEvents
	}

	@Override
	public void onSelection(SelectionEvent<List<INode>> event)
	{
		this.selection = event.getSelectedItem();
		
		//notify children
		for (int index = 0; index < this.getWidgetCount(); index++)
		{
			Widget child = getWidget(index);
			if (child instanceof SelectionHandler<?>)
			{
				((SelectionHandler)child).onSelection(event);
			}
		}
	}
	
	
}
