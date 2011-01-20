/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.iplantc.phyloviewer.client.events.NavigationMouseHandler;
import org.iplantc.phyloviewer.client.events.NodeClickEvent;
import org.iplantc.phyloviewer.client.events.NodeClickHandler;
import org.iplantc.phyloviewer.client.events.NodeSelectionEvent;
import org.iplantc.phyloviewer.client.events.NodeSelectionHandler;
import org.iplantc.phyloviewer.client.events.SelectionMouseHandler;
import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayout;
import org.iplantc.phyloviewer.client.tree.viewer.render.SearchHighlighter;
import org.iplantc.phyloviewer.client.tree.viewer.render.canvas.Graphics;
import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.math.PolarVector2;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.CameraCladogram;
import org.iplantc.phyloviewer.shared.render.RenderPreferences;
import org.iplantc.phyloviewer.shared.render.RenderTree;
import org.iplantc.phyloviewer.shared.render.RenderTreeCircular;
import org.iplantc.phyloviewer.shared.render.RenderTreeCladogram;

import com.google.gwt.core.client.Duration;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HandlesAllMouseEvents;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class DetailView extends AnimatedView implements Broadcaster
{
	private int renderCount;
	private double[] renderTime = new double[60];
	private boolean debug = true;

	private Graphics graphics = null;
	private RenderTree renderer = new RenderTreeCladogram();
	private SearchHighlighter highlighter = null;

	private boolean panX = false;
	private boolean panY = true;

	private SearchServiceAsyncImpl searchService;

	private Map<EventHandler,List<HandlerRegistration>> handlerRegistrations = new HashMap<EventHandler,List<HandlerRegistration>>();

	private NavigationMouseHandler navigationMouseHandler;
	private SelectionMouseHandler selectionMouseHandler;

	BroadcastCommand broadcastCommand;

	public DetailView(int width, int height, SearchServiceAsyncImpl searchService)
	{
		this.setStylePrimaryName("detailView");

		this.searchService = searchService;

		this.setCamera(new CameraCladogram());

		graphics = new Graphics(width, height);
		this.add(graphics.getWidget());

		this.addMouseDownHandler(new MouseDownHandler()
		{

			@Override
			public void onMouseDown(MouseDownEvent arg0)
			{
				if(arg0.getNativeButton() == 1)
				{
					INode node = getNodeAt(arg0.getX(), arg0.getY());
					if(node != null)
					{
						dispatch(new NodeClickEvent(node.getId()));
					}
				}
			}

		});
	}

	public void setDefaults()
	{
		navigationMouseHandler = new NavigationMouseHandler(this);
		selectionMouseHandler = new SelectionMouseHandler(this);

		selectionMouseHandler.addSelectionHandler(new HighlightSelectionHandler());
		selectionMouseHandler.addSelectionHandler(refireHandler);
		setNavigationMode();

		this.addKeyPressHandler(new KeyPressHandler()
		{
			@Override
			public void onKeyPress(KeyPressEvent event)
			{
				if(event.getCharCode() == 's')
				{
					setSelectionMode();
				}
				else if(event.getCharCode() == 'n')
				{
					setNavigationMode();
				}
			}
		});
	}

	public void render()
	{

		if(this.isReady())
		{
			Duration duration = new Duration();
			renderer.renderTree(graphics, getCamera());

			if(debug)
			{
				renderStats(duration.elapsedMillis());
			}
		}
	}

	public void resize(int width, int height)
	{
		graphics.resize(width, height);
	}

	public void setPannable(boolean x, boolean y)
	{
		this.panX = x;
		this.panY = y;
	}

	public boolean isXPannable()
	{
		return this.panX;
	}

	public boolean isYPannable()
	{
		return this.panY;
	}

	@Override
	public int getHeight()
	{
		return graphics.getHeight();
	}

	@Override
	public int getWidth()
	{
		return graphics.getWidth();
	}

	protected RenderTree getRenderer()
	{
		return renderer;
	}

	public void setRenderer(RenderTree renderer)
	{
		this.renderer = renderer;
	}

	@Override
	public void setDocument(IDocument document)
	{
		super.setDocument(document);
		this.getCamera().reset();

		if(highlighter != null)
		{
			highlighter.dispose();
		}

		ITree tree = this.getTree();
		if(tree != null && renderer != null && this.searchService != null)
		{
			highlighter = new SearchHighlighter(this, this.searchService, tree,
					renderer.getRenderPreferences());
		}

		if(renderer != null)
		{
			renderer.setDocument(document);
		}
	}

	@Override
	public boolean isReady()
	{
		boolean ready = this.getTree() != null && this.getLayout() != null && graphics != null
				&& getCamera() != null;

		if(ready && this.getLayout() instanceof RemoteLayout)
		{
			ready &= ((RemoteLayout)this.getLayout()).containsNode(this.getTree().getRootNode());
		}

		return ready;
	}

	private void renderStats(double time)
	{
		renderCount++;
		int index = renderCount % 60;
		renderTime[index] = time;

		String text = renderCount + " frames, last: " + Math.round(1.0 / time * 1000) + " FPS";

		if(renderCount >= 60)
		{
			double totalTime = 0;

			for(double t : renderTime)
			{
				totalTime += t;
			}
			double fps = (60.0 / totalTime) * 1000;

			text += " average: " + Math.round(fps) + " FPS";
		}

		graphics.getCanvas().setFillStyle("red");
		graphics.getCanvas().fillText(text, 5, graphics.getCanvas().getHeight() - 5);
	}

	public String exportImageURL()
	{
		return graphics.getCanvas().toDataURL();
	}

	public void setRenderPreferences(RenderPreferences preferences)
	{
		renderer.setRenderPreferences(preferences);
	}

	/**
	 * @return the node at the given location, in this View's screen coordinate system
	 */
	public INode getNodeAt(int x, int y)
	{
		Vector2 position = getPositionInLayoutSpace(new Vector2(x, y));

		if(renderer instanceof RenderTreeCircular)
		{
			Vector2 p = position.subtract(new Vector2(0.5, 0.5));
			PolarVector2 polar = new PolarVector2(p);
			position.setX((polar.getRadius() * 2) * 0.8);
			position.setY(polar.getAngle() / (Math.PI * 2.0));
		}

		// Calculate the maximum size of a pixel side
		Vector2 v0 = getPositionInLayoutSpace(new Vector2(0, 0));
		Vector2 v1 = getPositionInLayoutSpace(new Vector2(1, 1));
		Vector2 distanceInObjectSpace = v1.subtract(v0);
		double distance = Math.max(distanceInObjectSpace.getX(), distanceInObjectSpace.getY());

		// Use 3 pixels around a point for the clickable hot-spot.
		IntersectTree intersector = new IntersectTree(getTree(), position, getLayout(), distance * 5);
		intersector.intersect();
		INode hit = intersector.hit();
		return hit;
	}

	public Set<INode> getNodesIn(Box2D screenBox)
	{
		Set<INode> nodes = Collections.emptySet();

		if(getTree() != null)
		{
			Box2D range = getBoxInLayoutSpace(screenBox);
			INode root = getTree().getRootNode();
			ILayoutData layout = getLayout();

			nodes = IntersectTreeBox.intersect(root, layout, range);
		}

		return nodes;
	}

	public Vector2 getPositionInLayoutSpace(Vector2 position)
	{
		Matrix33 IM = getCamera().getMatrix(getWidth(), getHeight()).inverse();
		return IM.transform(position);
	}

	public Box2D getBoxInLayoutSpace(Box2D box)
	{
		Vector2 min = getPositionInLayoutSpace(box.getMin());
		Vector2 max = getPositionInLayoutSpace(box.getMax());
		return new Box2D(min, max);
	}

	public void setSelectionMode()
	{
		unregisterAllHandlers(); // remove any other mouse handlers
		this.addMouseHandler(selectionMouseHandler); // selectionMouseHandler will handle this view's
														// mouse events

		removeStyleName("navigation");
		addStyleName("selection");
	}

	public void setNavigationMode()
	{
		unregisterAllHandlers();
		this.addMouseHandler(navigationMouseHandler);

		removeStyleName("selection");
		addStyleName("navigation");
	}

	private void addMouseHandler(HandlesAllMouseEvents handler)
	{
		List<HandlerRegistration> registrations = handlerRegistrations.get(handler);
		if(registrations == null)
		{
			registrations = new ArrayList<HandlerRegistration>();
			handlerRegistrations.put(handler, registrations);
		}

		// add this handler for all supported events
		registrations.add(this.addMouseDownHandler(handler));
		registrations.add(this.addMouseUpHandler(handler));
		registrations.add(this.addMouseOutHandler(handler));
		registrations.add(this.addMouseOverHandler(handler));
		registrations.add(this.addMouseMoveHandler(handler));
		registrations.add(this.addMouseWheelHandler(handler));

		if(handler instanceof ClickHandler)
		{
			registrations.add(this.addClickHandler((ClickHandler)handler));
		}

		if(handler instanceof DoubleClickHandler)
		{
			registrations.add(this.addDoubleClickHandler((DoubleClickHandler)handler));
		}
	}

	/**
	 * Removes all handler registrations for the given handler
	 */
	private void unregister(EventHandler handler)
	{
		List<HandlerRegistration> registrations = handlerRegistrations.get(handler);
		if(registrations != null)
		{
			for(HandlerRegistration registration : registrations)
			{
				registration.removeHandler();
			}
		}

		registrations.clear();
	}

	private void unregisterAllHandlers()
	{
		for(EventHandler handler : handlerRegistrations.keySet())
		{
			unregister(handler);
		}
	}

	/**
	 * Highlights the selected nodes in this view
	 */
	private class HighlightSelectionHandler implements NodeSelectionHandler
	{
		@Override
		public void onNodeSelection(NodeSelectionEvent event)
		{
			getRenderer().getRenderPreferences().clearHighlights();
			for(INode node : event.getSelectedNodes())
			{
				getRenderer().getRenderPreferences().highlight(node);
			}
			requestRender();
		}
	}

	@Override
	protected void initEventListeners()
	{
		super.initEventListeners();

		EventBus eventBus = getEventBus();
		if(eventBus != null)
		{
			eventBus.addHandler(NodeClickEvent.TYPE, new NodeClickHandler()
			{

				@Override
				public void onNodeClick(NodeClickEvent event)
				{
					if(broadcastCommand != null)
					{
						String json = "{\"event\":\"node_clicked\",\"id\":\""
								+ Integer.toString(event.getNodeId()) + "\"}";
						broadcastCommand.broadcast(json);
					}
				}

			});
		}
	}

	@Override
	public void setBroadcastCommand(BroadcastCommand cmdBroadcast)
	{
		this.broadcastCommand = cmdBroadcast;
	}
	
	public void clearHighlights()
	{
		RenderTree renderer = this.getRenderer();
		if(renderer != null) 
		{
			RenderPreferences prefs = renderer.getRenderPreferences();
			if(prefs != null)
			{
				prefs.clearHighlights();
				this.requestRender();
			}
		}
	}
	
	public void highlight(Integer id) 
	{
		RenderTree renderer = this.getRenderer();
		if(renderer != null) 
		{
			RenderPreferences prefs = renderer.getRenderPreferences();
			if(prefs != null)
			{
				prefs.highlight(id);
				this.requestRender();
			}
		}
	}
}
