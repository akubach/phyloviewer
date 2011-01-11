package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;

public class NodeStyleWidget extends AbstractElementStyleWidget
{
	private static final int LABEL_COLUMN = 0;
	private static final int WIDGET_COLUMN = 1;
	
	private static final int COLOR_ROW = 0;
	private static final int SIZE_ROW = 1;

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
		setText(COLOR_ROW, LABEL_COLUMN, "Node color:");
		setColorWidget(new TextBox());
		
		setText(SIZE_ROW, LABEL_COLUMN, "Node size:");
		setSizeWidget(new DoubleBox());
	}
	
	public void setColorWidget(HasValue<String> widget)
	{
		colorUpdater.attachTo(widget);
		super.setWidget(COLOR_ROW, WIDGET_COLUMN, widget);
	}
	
	public void setSizeWidget(HasValue<Double> widget)
	{
		sizeUpdater.attachTo(widget);
		super.setWidget(SIZE_ROW, WIDGET_COLUMN, widget);
	}
}
