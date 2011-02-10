package org.iplantc.phyloviewer.viewer.client.ui;

import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.style.IBranchStyle;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;

public class BranchStyleWidget extends AbstractElementStyleWidget
{
	private static final int LABEL_COLUMN = 0;
	private static final int WIDGET_COLUMN = 1;
	
	private static final int COLOR_ROW = 0;
	private static final int WIDTH_ROW = 1;
	
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
		setText(COLOR_ROW, LABEL_COLUMN, "Branch color:");
		setStrokeColorWidget(new TextBox());
		
		setText(WIDTH_ROW, LABEL_COLUMN, "Branch width:");
		setLineWidthWidget(new DoubleBox());
	}
	
	public void setStrokeColorWidget(HasValue<String> widget)
	{
		colorUpdater.attachTo(widget);
		setWidget(COLOR_ROW, WIDGET_COLUMN, widget);
	}
	
	public void setLineWidthWidget(HasValue<Double> widget)
	{
		lineWidthUpdater.attachTo(widget);
		setWidget(WIDTH_ROW, WIDGET_COLUMN, widget);
	}
	
	@SuppressWarnings("unchecked")
	public HasValue<String> getStrokeColorWidget()
	{
		return (HasValue<String>)getWidget(COLOR_ROW, WIDGET_COLUMN);
	}
	
	@SuppressWarnings("unchecked")
	public HasValue<Double> getLineWidthWidget()
	{
		return (HasValue<Double>)getWidget(WIDTH_ROW, WIDGET_COLUMN);
	}

	@Override
	public void updateValues(INode node)
	{
		IBranchStyle style = getStyle(node).getBranchStyle();
		String color = style.getStrokeColor();
		getStrokeColorWidget().setValue(color, true);
		
		double pointSize = style.getLineWidth();
		getLineWidthWidget().setValue(pointSize, true);
	}
}
