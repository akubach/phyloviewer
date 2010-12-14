package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;

public class LabelStyleWidget extends AbstractElementStyleWidget
{
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
		
		setText(0, 0, "Label color:");
		setColorWidget(0, 1, new TextBox());
	}
	
	public void setColorWidget(int row, int col, HasValue<String> widget)
	{
		colorUpdater.attachTo(widget);
		super.setWidget(row, col, widget);
	}
}
