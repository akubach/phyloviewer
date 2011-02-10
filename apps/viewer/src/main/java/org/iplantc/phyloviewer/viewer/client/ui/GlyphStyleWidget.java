package org.iplantc.phyloviewer.viewer.client.ui;

import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.style.IGlyphStyle;

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
		setWidget(FILL_COLOR_ROW, WIDGET_COLUMN, widget);
	}
	
	public void setStrokeColorWidget(HasValue<String> widget)
	{
		strokeColorUpdater.attachTo(widget);
		setWidget(STROKE_COLOR_ROW, WIDGET_COLUMN, widget);
	}
	
	public void setLineWidthWidget(HasValue<Double> widget)
	{
		lineWidthUpdater.attachTo(widget);
		setWidget(WIDTH_ROW, WIDGET_COLUMN, widget);
	}
	
	@SuppressWarnings("unchecked")
	public HasValue<String> getFillColorWidget()
	{
		return (HasValue<String>)getWidget(FILL_COLOR_ROW, WIDGET_COLUMN);
	}
	
	@SuppressWarnings("unchecked")
	public HasValue<String> getStrokeColorWidget()
	{
		return (HasValue<String>)getWidget(STROKE_COLOR_ROW, WIDGET_COLUMN);
	}
	
	@SuppressWarnings("unchecked")
	public HasValue<Double> getLineWidthWidget()
	{
		return (HasValue<Double>)getWidget(WIDTH_ROW, WIDGET_COLUMN);
	}
	
	@Override
	public void updateValues(INode node)
	{
		IGlyphStyle style = getStyle(node).getGlyphStyle();
		String fillColor = style.getFillColor();
		getFillColorWidget().setValue(fillColor, true);
		
		String strokeColor = style.getStrokeColor();
		getStrokeColorWidget().setValue(strokeColor, true);
		
		double lineWidth = style.getLineWidth();
		getLineWidthWidget().setValue(lineWidth, true);
	}
}
