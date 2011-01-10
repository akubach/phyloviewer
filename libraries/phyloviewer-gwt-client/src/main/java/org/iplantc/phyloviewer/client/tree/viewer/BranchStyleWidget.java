package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;

public class BranchStyleWidget extends AbstractElementStyleWidget
{
	private SingleValueChangeHandler<String> colorUpdater = new SingleValueChangeHandler<String>()
	{
		@Override
		public void onValueChange(ValueChangeEvent<String> event)
		{
			for(INode node : getNodes())
			{
				getStyle(node).getBranchStyle().setStrokeColor(event.getValue());
			}
		}
	};
	
	private SingleValueChangeHandler<Double> lineWidthUpdater = new SingleValueChangeHandler<Double>()
	{
		@Override
		public void onValueChange(ValueChangeEvent<Double> event)
		{
			for(INode node : getNodes())
			{
				getStyle(node).getBranchStyle().setLineWidth(event.getValue());
			}
		}
	};
	
	public BranchStyleWidget(IDocument document)
	{
		super(document);
		setText(0, 0, "Branch color:");
		setStrokeColorWidget(0, 1, new TextBox());
		
		setText(1, 0, "Branch width:");
		setLineWidthWidget(1, 1, new DoubleBox());
	}
	
	public void setStrokeColorWidget(int row, int col, HasValue<String> widget)
	{
		colorUpdater.attachTo(widget);
		super.setWidget(row, col, widget);
	}
	
	public void setLineWidthWidget(int row, int col, HasValue<Double> widget)
	{
		lineWidthUpdater.attachTo(widget);
		super.setWidget(row, col, widget);
	}
	
	
}
