package org.iplantc.phyloviewer.client.tree.viewer;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloviewer.client.tree.viewer.event.DocumentChangeEvent;
import org.iplantc.phyloviewer.client.tree.viewer.event.DocumentChangeHandler;
import org.iplantc.phyloviewer.client.tree.viewer.event.NodeSelectionEvent;
import org.iplantc.phyloviewer.client.tree.viewer.event.NodeSelectionHandler;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.Defaults;
import org.iplantc.phyloviewer.shared.render.style.CompositeStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyle;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractElementStyleWidget extends FlexTable implements NodeSelectionHandler, DocumentChangeHandler
{
	private IDocument document;
	private List<INode> nodes;
	private ArrayList<HasValue<?>> widgetsToClearOnSelectionChange = new ArrayList<HasValue<?>>();
	
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
	
	public List<INode> getNodes()
	{
		return nodes;
	}
	
	public void clearOnSelectionChange(HasValue<?> widget)
	{
		widgetsToClearOnSelectionChange.add(widget);
	}
	
	public final void setWidget(int row, int col, HasValue<?> widget)
	{
		if (widget instanceof Widget)
		{
			clearOnSelectionChange(widget);
			setWidget(row, col, (Widget)widget);
		}
	}
	
	@Override
	public void onNodeSelection(NodeSelectionEvent event)
	{
		AbstractElementStyleWidget.this.nodes = event.getSelectedNodes();
		
		for (HasValue<?> widget : widgetsToClearOnSelectionChange)
		{
			widget.setValue(null, false);
		}
	}

	@Override
	public void onDocumentChange(DocumentChangeEvent event)
	{
		this.document = event.getDocument();
	}
	
	
}
