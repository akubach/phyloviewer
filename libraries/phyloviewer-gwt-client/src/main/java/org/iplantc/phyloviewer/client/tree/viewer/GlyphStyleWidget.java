package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;

public class GlyphStyleWidget extends AbstractElementStyleWidget
{
	private static final int LABEL_COLUMN = 0;
	private static final int WIDGET_COLUMN = 1;
	
	private static final int FILL_COLOR_ROW = 0;
	private static final int STROKE_COLOR_ROW = 1;
	private static final int WIDTH_ROW = 2;
	
	private SingleValueChangeHandler<String> fillColorUpdater = new SingleValueChangeHandler<String>()
	{
		@Override
		public void onValueChange(ValueChangeEvent<String> event)
		{
			for(INode node : getNodes())
			{
				getStyle(node).getGlyphStyle().setFillColor(event.getValue());
			}
		}
	};
	
	private SingleValueChangeHandler<String> strokeColorUpdater = new SingleValueChangeHandler<String>()
	{
		@Override
		public void onValueChange(ValueChangeEvent<String> event)
		{
			for(INode node : getNodes())
			{
				getStyle(node).getGlyphStyle().setStrokeColor(event.getValue());
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
				getStyle(node).getGlyphStyle().setLineWidth(event.getValue());
			}
		}
	};
	
	public GlyphStyleWidget(IDocument document)
	{
		super(document);
		
		setText(FILL_COLOR_ROW, LABEL_COLUMN, "Glyph fill color:");
		setFillColorWidget(new TextBox());
		
		setText(STROKE_COLOR_ROW, LABEL_COLUMN, "Glyph stroke color:");
		setStrokeColorWidget(new TextBox());
		
		setText(WIDTH_ROW, LABEL_COLUMN, "Glyph outline width:");
		setLineWidthWidget(new DoubleBox());
	}
	
	public void setFillColorWidget(HasValue<String> widget)
	{
		fillColorUpdater.attachTo(widget);
		super.setWidget(FILL_COLOR_ROW, WIDGET_COLUMN, widget);
	}
	
	public void setStrokeColorWidget(HasValue<String> widget)
	{
		strokeColorUpdater.attachTo(widget);
		super.setWidget(STROKE_COLOR_ROW, WIDGET_COLUMN, widget);
	}
	
	public void setLineWidthWidget(HasValue<Double> widget)
	{
		lineWidthUpdater.attachTo(widget);
		super.setWidget(WIDTH_ROW, WIDGET_COLUMN, widget);
	}
}
