package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;

public class LabelStyleWidget extends AbstractElementStyleWidget
{
	private static final int LABEL_COLUMN = 0;
	private static final int WIDGET_COLUMN = 1;
	
	private static final int COLOR_ROW = 0;
	
	private SingleValueChangeHandler<String> colorUpdater = new SingleValueChangeHandler<String>()
	{
		@Override
		public void onValueChange(ValueChangeEvent<String> event)
		{
			for(INode node : getNodes())
			{
				getStyle(node).getLabelStyle().setColor(event.getValue());
			}
		}
	};
	
	public LabelStyleWidget(IDocument document)
	{
		super(document);
		
		setText(COLOR_ROW, LABEL_COLUMN, "Label color:");
		setColorWidget(new TextBox());
	}
	
	public void setColorWidget(HasValue<String> widget)
	{
		colorUpdater.attachTo(widget);
		super.setWidget(COLOR_ROW, WIDGET_COLUMN, widget);
	}
}
