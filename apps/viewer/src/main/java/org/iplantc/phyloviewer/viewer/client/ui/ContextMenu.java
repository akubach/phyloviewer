package org.iplantc.phyloviewer.viewer.client.ui;

import org.iplantc.phyloviewer.client.events.DocumentChangeEvent;
import org.iplantc.phyloviewer.client.events.DocumentChangeHandler;
import org.iplantc.phyloviewer.client.events.HasDocument;
import org.iplantc.phyloviewer.client.events.HasNodeSelectionHandlers;
import org.iplantc.phyloviewer.client.events.NodeSelectionEvent;
import org.iplantc.phyloviewer.client.events.NodeSelectionHandler;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Children of ContextMenu will automatically be registered as handlers of {@link DocumentChangeEvent}s and {@link NodeSelectionEvent}s
 * from its targetWidget.
 *
 */
public class ContextMenu extends StackLayoutPanel
{
	private HasNodeSelectionHandlers targetWidget;
	
	public ContextMenu(HasNodeSelectionHandlers targetWidget)
	{
		super(Unit.EM);
		this.targetWidget = targetWidget;
	}
	
	public HasNodeSelectionHandlers getTargetWidget()
	{
		return targetWidget;
	}

	/*
	 * At least for now, all of the add(...) and insert(...) methods in StackLayoutPanel end up going
	 * through this insert (before going into a private insert method), so I can make the new child
	 * handle events here. If StackLayoutPanel changes, more drastic measures may be required.
	 */
	public void insert(Widget child, Widget header, double headerSize, int beforeIndex)
	{
		handleDocumentChanges(child);
		handleSelectionChanges(child);
		super.insert(child, header, headerSize, beforeIndex);
	}
	
	private void handleDocumentChanges(Widget child)
	{
		if (targetWidget instanceof HasDocument && child instanceof DocumentChangeHandler)
		{
			((HasDocument)targetWidget).addDocumentChangeHandler((DocumentChangeHandler)child);
		}
	}
	
	private void handleSelectionChanges(Widget child)
	{
		if (child instanceof NodeSelectionHandler)
		{
			targetWidget.addSelectionHandler((NodeSelectionHandler) child);
		}
	}
}
