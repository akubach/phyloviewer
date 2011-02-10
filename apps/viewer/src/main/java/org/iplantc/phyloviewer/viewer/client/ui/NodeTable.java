package org.iplantc.phyloviewer.viewer.client.ui;

import org.iplantc.phyloviewer.client.events.NodeSelectionEvent;
import org.iplantc.phyloviewer.client.events.NodeSelectionHandler;
import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

public class NodeTable extends FlexTable implements NodeSelectionHandler
{
	public NodeTable()
	{
		super();
		setStylePrimaryName("nodeTable");
	}

	@Override
	public void onNodeSelection(NodeSelectionEvent event)
	{
		removeAllRows();
		
		if (event.getSelectedNodes().size() == 1)
		{
			INode node = event.getSelectedNodes().iterator().next();
			
			setLabel(0, 0, "id");
			setText(0, 1, String.valueOf(node.getId()));
			
			setLabel(1, 0, "label");
			setText(1, 1, node.getLabel());
			
			setLabel(2, 0, "style id");
			setText(2, 1, node.getStyleId());
			
			setLabel(3, 0, "# of children");
			setText(3, 1, String.valueOf(node.getNumberOfChildren()));
			
			setLabel(4, 0, "# of leaves");
			setText(4, 1, String.valueOf(node.getNumberOfLeafNodes()));
			
			setLabel(5, 0, "subtree size");
			setText(5, 1, String.valueOf(node.getNumberOfNodes()));
			
			setLabel(6, 0, "height");
			setText(6, 1, String.valueOf(node.findMaximumDepthToLeaf()));
		}
	}
	
	private void setLabel(int row, int col, String text)
	{
		setWidget(row, col, new Label(text));
	}
}
