/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloviewer.client.math.ConvertMathTypes;
import org.iplantc.phyloviewer.client.services.TreeImage;
import org.iplantc.phyloviewer.client.services.TreeImageAsync;
import org.iplantc.phyloviewer.client.services.TreeIntersectService;
import org.iplantc.phyloviewer.client.services.TreeIntersectServiceAsync;
import org.iplantc.phyloviewer.client.tree.viewer.canvas.Canvas;
import org.iplantc.phyloviewer.client.tree.viewer.canvas.Image;
import org.iplantc.phyloviewer.client.tree.viewer.canvas.ImageListener;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.Camera;
import org.iplantc.phyloviewer.shared.render.Defaults;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OverviewView extends AnimatedView
{
	private static final String MESSAGE_ERROR_LOADING_IMAGE = "Error loading image.";
	private static final String MESSAGE_LOADING_IMAGE = "Loading image...";

	private final class ImageListenerImpl implements ImageListener
	{
		private OverviewView view = null;

		public ImageListenerImpl(OverviewView view)
		{
			this.view = view;
		}

		public void onLoadingComplete(Image image)
		{
			view.image = image;
			view.downloadingImage = null;
			view.imageStatus = ImageStatus.IMAGE_STATUS_IMAGE_LOADED;
			view.requestRender();
		}
	}

	enum ImageStatus
	{
		IMAGE_STATUS_NO_TREE, IMAGE_STATUS_IMAGE_LOADED, IMAGE_STATUS_LOADING_IMAGE, IMAGE_STATUS_ERROR
	}

	private Canvas canvas = null;
	private Image image = null;
	@SuppressWarnings("unused")
	private Image downloadingImage = null;
	private int width;
	private int height;
	private ImageStatus imageStatus = ImageStatus.IMAGE_STATUS_NO_TREE;
	private JsHit hit;
	private TreeImageAsync treeImageService = GWT.create(TreeImage.class);
	private TreeIntersectServiceAsync treeIntersectService = GWT.create(TreeIntersectService.class);

	public OverviewView(int width, int height)
	{
		this.width = width;
		this.height = height;

		canvas = new Canvas(width, height);

		this.add(canvas);

		this.addMouseMoveHandler(new MouseMoveHandler()
		{

			@Override
			public void onMouseMove(MouseMoveEvent arg0)
			{
				int x = arg0.getX();
				int y = arg0.getY();

				// Project the point in screen space to object space.
				Vector2 position = new Vector2((double)x / OverviewView.this.width, (double)y
						/ OverviewView.this.height);

				ITree tree = getTree();
				if(tree != null)
				{
					treeIntersectService.intersectTree(tree.getId(), position.getX(), position.getY(),
							new AsyncCallback<String>()
							{

								@Override
								public void onFailure(Throwable arg0)
								{
									hit = null;
									OverviewView.this.requestRender();
								}

								@Override
								public void onSuccess(String arg0)
								{
									JsHitResult result = (JsHitResult)JsonUtils.safeEval(arg0);
									hit = result.getHit();
									OverviewView.this.requestRender();
								}
							});
				}
			}

		});

		this.addMouseDownHandler(new MouseDownHandler()
		{

			@Override
			public void onMouseDown(MouseDownEvent arg0)
			{
				if(hit != null)
				{
					Box2D bbox = ConvertMathTypes.convertToBox2(hit.boundingBox());
					zoomToBoundingBox(bbox);
				}
			}
		});
	}

	@Override
	public void setDocument(IDocument document)
	{
		super.setDocument(document);

		this.image = null;
		this.downloadingImage = null;

		this.requestRender();
	}

	public void updateImage()
	{
		this.retrieveOverviewImage();
	}

	private void retrieveOverviewImage()
	{
		this.downloadingImage = null;

		if(this.getTree() == null)
		{
			this.image = null;
			return;
		}

		this.imageStatus = ImageStatus.IMAGE_STATUS_LOADING_IMAGE;

		final AsyncCallback<String> callback = new AsyncCallback<String>()
		{
			final OverviewView caller = OverviewView.this;

			@Override
			public void onFailure(Throwable arg0)
			{
				image = null;
				downloadingImage = null;
				caller.imageStatus = ImageStatus.IMAGE_STATUS_ERROR;

				Logger.getLogger("").log(Level.INFO, "Failure retrieving overview image.", arg0);
			}

			@Override
			public void onSuccess(String result)
			{
				downloadingImage = new Image(result, new ImageListenerImpl(caller));
				caller.requestRender();
			}
		};

		if(this.getTree() != null && this.getLayout() != null)
		{
			final ITree tree = this.getTree();

			String layoutID = this.getLayoutType().toString();
			treeImageService.getTreeImageURL(tree.getId(), layoutID, width, height, callback);
		}
		else
		{
			imageStatus = ImageStatus.IMAGE_STATUS_ERROR;
		}

	}

	public void render()
	{
		try
		{
			canvas.clear();

			if(image != null && image.isLoaded())
			{
				canvas.save();

				canvas.scale((double)width / image.getWidth(), (double)height / image.getHeight());
				canvas.drawImage(image, 0, 0);

				canvas.restore();
			}
			else
			{
				switch (imageStatus)
				{
					case IMAGE_STATUS_LOADING_IMAGE:
						showStatusMessage(OverviewView.MESSAGE_LOADING_IMAGE);
						break;
					case IMAGE_STATUS_ERROR:
						showStatusMessage(OverviewView.MESSAGE_ERROR_LOADING_IMAGE);
				}
			}

			Camera camera = this.getCamera();
			if(camera != null)
			{
				Matrix33 V = camera.getViewMatrix();
				Matrix33 IV = V.inverse();
				Vector2 min = new Vector2(0, 0);
				Vector2 max = new Vector2(1, 1);

				min = IV.transform(min);
				max = IV.transform(max);

				Matrix33 S = Matrix33.makeScale(canvas.getWidth(), canvas.getHeight());
				min = S.transform(min);
				max = S.transform(max);

				double x = min.getX();
				double y = min.getY();
				double width = max.getX() - x;
				double height = max.getY() - y;

				canvas.setStrokeStyle(Defaults.OVERVIEW_OUTLINE_COLOR);
				canvas.setFillStyle(Defaults.OVERVIEW_FILL_COLOR);
				canvas.beginPath();
				canvas.rect(x, y, width, height);
				canvas.fill();
				canvas.stroke();
			}

			if(hit != null)
			{
				canvas.setFillStyle("red");
				canvas.beginPath();

				double x = hit.position().getX();
				double y = hit.position().getY();

				canvas.arc(x * this.width, y * this.height, Defaults.POINT_SIZE / 2.0, 0, Math.PI * 2, true);
				canvas.closePath();
				canvas.fill();
			}
		}
		catch(Exception e)
		{
			Logger.getLogger("").log(Level.WARNING,
					"An exception was caught in OverviewView.render: " + e.getMessage());
		}
	}

	private void showStatusMessage(String message)
	{
		canvas.setStrokeStyle(Defaults.TEXT_COLOR);
		canvas.setFillStyle(Defaults.TEXT_COLOR);
		canvas.fillText(message, 0, canvas.getHeight() / 2);
	}

	public void resize(int width, int height)
	{
		this.width = width;
		this.height = height;
		canvas.setWidth(width);
		canvas.setHeight(height);
		retrieveOverviewImage(); // FIXME: limit the rate of these requests. Most browsers are going to
									// have many resize events as the user drags the window border
									// around.
	}

	@Override
	public boolean isReady()
	{
		return true;
	}

	@Override
	public String exportImageURL()
	{
		return image.getElement().getSrc();
	}
}
