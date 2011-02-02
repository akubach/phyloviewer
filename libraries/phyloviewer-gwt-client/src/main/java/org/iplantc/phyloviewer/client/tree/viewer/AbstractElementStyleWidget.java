package org.iplantc.phyloviewer.client.tree.viewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.iplantc.phyloviewer.client.events.DocumentChangeEvent;
import org.iplantc.phyloviewer.client.events.DocumentChangeHandler;
import org.iplantc.phyloviewer.client.events.NodeSelectionEvent;
import org.iplantc.phyloviewer.client.events.NodeSelectionHandler;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.Defaults;
import org.iplantc.phyloviewer.shared.render.style.CompositeStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyle;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractElementStyleWidget extends FlexTable implements NodeSelectionHandler, DocumentChangeHandler
{
	private IDocument document;
	private Set<INode> nodes = Collections.emptySet();
	private ArrayList<HasValue<?>> widgets = new ArrayList<HasValue<?>>();
	
	public AbstractElementStyleWidget(IDocument document)
	{	
		this.document = document; 
	}
	
	public void setDocument(IDocument document)
	{
		this.document = document;
	}
	
	public IStyle getStyle(INode node)
	{
		IStyle style = document.getStyleMap().get(node);
		
		if (style == null)
		{	
			style = new CompositeStyle(String.valueOf(node.getId()), Defaults.DEFAULT_STYLE);
			document.getStyleMap().put(node, style);
		}
		
		return style;
	}
	
	public Set<INode> getNodes()
	{
		return nodes;
	}
	
	protected final void setWidget(int row, int col, HasValue<?> widget)
	{
		if (widget instanceof Widget)
		{
			widgets.add(widget);
			setWidget(row, col, (Widget)widget);
		}
	}
	
	@Override
	public void onNodeSelection(NodeSelectionEvent event)
	{
		AbstractElementStyleWidget.this.nodes = event.getSelectedNodes();
		updateWidgets(nodes);
	}

	@Override
	public void onDocumentChange(DocumentChangeEvent event)
	{
		this.document = event.getDocument();
	}
	
	public abstract void updateValues(INode node);
	
	private void updateWidgets(Set<INode> selectedNodes)
	{
		setEnabled(widgets, true);
		clearWidgets(widgets);
		
		if(selectedNodes.size() == 1)
		{
			INode node = selectedNodes.iterator().next();
			updateValues(node);
		} 
		else if(selectedNodes.size() == 0)
		{
			setEnabled(widgets, false);
		}
	}

	private void clearWidgets(List<HasValue<?>> widgets)
	{
		for (HasValue<?> widget : widgets)
		{
			widget.setValue(null, false);
		}
	}

	private void setEnabled(List<HasValue<?>> widgets, boolean enabled)
	{
		for (HasValue<?> widget : widgets)
		{
			if (widget instanceof HasEnabled)
			{
				((HasEnabled)widget).setEnabled(enabled);
			}
		}
	}
}
