package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;

public class NodeStyleWidget extends AbstractElementStyleWidget
{
	private SingleValueChangeHandler<String> colorUpdater = new SingleValueChangeHandler<String>()
	{
		@Override
		public void onValueChange(ValueChangeEvent<String> event)
		{
			for(INode node : getNodes())
			{
				getStyle(node).getNodeStyle().setColor(event.getValue());
			}
		}
	};
	
	private SingleValueChangeHandler<Double> sizeUpdater = new SingleValueChangeHandler<Double>()
	{
		@Override
		public void onValueChange(ValueChangeEvent<Double> event)
		{
			for(INode node : getNodes())
			{
				getStyle(node).getNodeStyle().setPointSize(event.getValue());
			}
		}
	};
	
	public NodeStyleWidget(IDocument document)
	{
		super(document);
		setText(0, 0, "Node color:");
		setColorWidget(0, 1, new TextBox());
		
		setText(1, 0, "Node size:");
		setSizeWidget(1, 1, new DoubleBox());
	}
	
	public void setColorWidget(int row, int col, HasValue<String> widget)
	{
		colorUpdater.attachTo(widget);
		super.setWidget(row, col, widget);
	}
	
	public void setSizeWidget(int row, int col, HasValue<Double> widget)
	{
		sizeUpdater.attachTo(widget);
		super.setWidget(row, col, widget);
	}
}
