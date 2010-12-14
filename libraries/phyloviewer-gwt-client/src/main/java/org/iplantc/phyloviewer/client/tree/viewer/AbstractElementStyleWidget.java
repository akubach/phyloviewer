package org.iplantc.phyloviewer.client.tree.viewer;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.style.IStyle;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractElementStyleWidget extends FlexTable implements SelectionHandler<List<INode>>
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
		return document.getStyleMap().get(node);
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
	public void onSelection(SelectionEvent<List<INode>> event)
	{
		AbstractElementStyleWidget.this.nodes = event.getSelectedItem();
		
		for (HasValue<?> widget : widgetsToClearOnSelectionChange)
		{
			widget.setValue(null, false);
		}
	}
}
