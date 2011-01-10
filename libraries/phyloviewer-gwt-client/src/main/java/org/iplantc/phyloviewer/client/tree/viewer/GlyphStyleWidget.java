package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;

public class GlyphStyleWidget extends AbstractElementStyleWidget
{
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
		
		setText(0, 0, "Glyph fill color:");
		setFillColorWidget(0, 1, new TextBox());
		
		setText(1, 0, "Glyph stroke color:");
		setStrokeColorWidget(1, 1, new TextBox());
		
		setText(2, 0, "Glyph outline width:");
		setLineWidthWidget(2, 1, new DoubleBox());
	}
	
	public void setFillColorWidget(int row, int col, HasValue<String> widget)
	{
		fillColorUpdater.attachTo(widget);
		super.setWidget(row, col, widget);
	}
	
	public void setStrokeColorWidget(int row, int col, HasValue<String> widget)
	{
		strokeColorUpdater.attachTo(widget);
		super.setWidget(row, col, widget);
	}
	
	public void setLineWidthWidget(int row, int col, HasValue<Double> widget)
	{
		lineWidthUpdater.attachTo(widget);
		super.setWidget(row, col, widget);
	}
}
