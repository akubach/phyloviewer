package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.Camera;
import org.iplantc.phyloviewer.shared.render.CameraCladogram;
import org.iplantc.phyloviewer.shared.render.RenderPreferences;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * The ViewCladogram class is a composite of the overview and detail views.
 * 
 * @author adamk
 * 
 */

public class ViewCladogram extends AnimatedView
{
	private DetailView detailView;
	private OverviewView overviewView;

	public ViewCladogram(int width, int height)
	{
		super();
		detailView = new DetailView(1, 1);
		overviewView = new OverviewView(1, 1);

		HorizontalPanel panel = new HorizontalPanel();
		panel.add(overviewView);
		panel.add(detailView);
		this.add(panel);

		Camera camera = new CameraCladogram();

		this.setCamera(camera);

		overviewView.setCamera(camera);
		detailView.setCamera(camera);

		this.setLayoutType(View.LayoutType.LAYOUT_TYPE_CLADOGRAM);
		overviewView.setLayoutType(View.LayoutType.LAYOUT_TYPE_CLADOGRAM);
		detailView.setLayoutType(View.LayoutType.LAYOUT_TYPE_CLADOGRAM);

		this.resize(width, height);

		this.addKeyPressHandler(new KeyPressHandler()
		{

			@Override
			public void onKeyPress(KeyPressEvent arg0)
			{
				final char charCode = arg0.getCharCode();
				if(charCode == 'h')
				{
					overviewView.setVisible(!overviewView.isVisible());
				}
			}
		});
	}

	public void resize(int width, int height)
	{
		int overviewWidth = (int)(width * 0.20);
		int detailWidth = width - overviewWidth;

		overviewView.resize(overviewWidth, height);
		detailView.resize(detailWidth, height);
	}

	public final void render()
	{
		overviewView.render();
		detailView.render();
	}

	/**
	 * Set the tree. Make sure both views get the tree.
	 */
	public final void setDocument(IDocument document)
	{
		super.setDocument(document);
		detailView.setDocument(document);
		overviewView.setDocument(document);

		overviewView.updateImage();
	}

	@Override
	public ITree getTree()
	{
		return detailView.getTree();
	}

	public final int getWidth()
	{
		return overviewView.getWidth() + detailView.getWidth();
	}

	public final int getHeight()
	{
		// Both the overview and detail views should have the same height.
		return detailView.getHeight();
	}

	@Override
	public boolean isReady()
	{
		return overviewView.isReady() && detailView.isReady();
	}

	@Override
	public ILayoutData getLayout()
	{
		return detailView.getLayout();
	}

	@Override
	public String exportImageURL()
	{
		return detailView.exportImageURL();
	}

	@Override
	public void setRenderPreferences(RenderPreferences rp)
	{
		super.setRenderPreferences(rp);
		detailView.setRenderPreferences(rp);
		overviewView.setRenderPreferences(rp);
	}

	@Override
	public void setEventBus(EventBus eventBus)
	{
		super.setEventBus(eventBus);
		detailView.setEventBus(eventBus);
		overviewView.setEventBus(eventBus);

		// refire events from the sub-views, with this view as source
		detailView.addSelectionHandler(refireHandler);
		overviewView.addSelectionHandler(refireHandler);
	}

	public DetailView getDetailView()
	{
		return this.detailView;
	}
}
