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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.core.broadcaster.shared.BroadcastCommand;
import org.iplantc.core.broadcaster.shared.Broadcaster;
import org.iplantc.phyloviewer.client.events.BranchClickEvent;
import org.iplantc.phyloviewer.client.events.BranchClickHandler;
import org.iplantc.phyloviewer.client.events.LabelClickEvent;
import org.iplantc.phyloviewer.client.events.LabelClickHandler;
import org.iplantc.phyloviewer.client.events.NavigationMouseHandler;
import org.iplantc.phyloviewer.client.events.NodeClickEvent;
import org.iplantc.phyloviewer.client.events.NodeClickHandler;
import org.iplantc.phyloviewer.client.events.NodeSelectionEvent;
import org.iplantc.phyloviewer.client.events.NodeSelectionHandler;
import org.iplantc.phyloviewer.client.events.SelectionMouseHandler;
import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl;
import org.iplantc.phyloviewer.client.tree.viewer.IntersectTree.Hit;
import org.iplantc.phyloviewer.client.tree.viewer.render.SearchHighlighter;
import org.iplantc.phyloviewer.client.tree.viewer.render.canvas.Graphics;
import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.CameraCladogram;
import org.iplantc.phyloviewer.shared.render.RenderPreferences;
import org.iplantc.phyloviewer.shared.render.RenderTree;
import org.iplantc.phyloviewer.shared.render.RenderTreeCladogram;
import org.iplantc.phyloviewer.shared.scene.Drawable;
import org.iplantc.phyloviewer.shared.scene.DrawableContainer;

import com.google.gwt.core.client.Duration;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HandlesAllMouseEvents;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class DetailView extends AnimatedView implements Broadcaster
{
	private int renderCount;
	private double[] renderTime = new double[60];
	private boolean drawRenderStats = false;

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
	Hit lastHit;

	public DetailView(int width, int height, SearchServiceAsyncImpl searchService)
	{
		this.setStylePrimaryName("detailView");

		this.searchService = searchService;

		this.setCamera(new CameraCladogram());

		graphics = new Graphics(width, height);
		this.add(graphics.getWidget());

		this.addMouseMoveHandler(new MouseMoveHandler()
		{

			@Override
			public void onMouseMove(MouseMoveEvent arg0)
			{
				IntersectTree intersector = createIntersector(arg0.getX(), arg0.getY());
				intersector.intersect();

				Hit hit = intersector.getClosestHit();

				int x = arg0.getClientX();
				int y = arg0.getClientY();

				if(lastHit != null && hit != null)
				{
					// Check the drawables and make sure they are the same.
					if(lastHit.getDrawable() != hit.getDrawable())
					{
						handleMouseOut(lastHit, x, y);
						handleMouseOver(hit, x, y);
					}
				}
				else if(lastHit == null && hit != null)
				{
					handleMouseOver(hit, x, y);
				}
				else if(lastHit != null && hit == null)
				{
					handleMouseOut(lastHit, x, y);
				}

				lastHit = hit;
			}
		});

		this.addMouseDownHandler(new MouseDownHandler()
		{

			@Override
			public void onMouseDown(MouseDownEvent arg0)
			{
				if(arg0.getNativeButton() == 1)
				{
					Hit hit = lastHit;
					int x = arg0.getClientX();
					int y = arg0.getClientY();

					handleMouseClick(hit, x, y);
				}
			}

		});
	}

	public void render()
	{
		try
		{
			if(this.isReady())
			{
				Duration duration = new Duration();
				renderer.renderTree(graphics, getCamera());

				if(drawRenderStats)
				{
					renderStats(duration.elapsedMillis());
				}
			}
		}
		catch(Exception e)
		{
			Logger.getLogger("").log(Level.WARNING,
					"An exception was caught in DetailView.render: " + e.getMessage());
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
		boolean documentReady = this.getDocument() != null && this.getDocument().isReady();
		boolean ready = documentReady && graphics != null && getCamera() != null;

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

	public void setRenderPreferences(RenderPreferences rp)
	{
		super.setRenderPreferences(rp);
		renderer.setRenderPreferences(rp);
	}

	/**
	 * @return the node at the given location, in this View's screen coordinate system
	 */
	public INode getNodeAt(int x, int y)
	{
		IntersectTree intersector = createIntersector(x, y);
		intersector.intersect();

		Hit hit = intersector.getClosestHit();
		return hit != null ? hit.getNode() : null;
	}

	/**
	 * Create an intersector
	 * 
	 * @param x position in screen coordinates
	 * @param y position in screen coordinates
	 * @return An object to perform intersections.
	 */
	private IntersectTree createIntersector(int x, int y)
	{
		Vector2 position = getPositionInLayoutSpace(new Vector2(x, y));

		// Calculate the maximum size of a pixel side
		Vector2 v0 = getPositionInLayoutSpace(new Vector2(0, 0));
		Vector2 v1 = getPositionInLayoutSpace(new Vector2(1, 1));
		Vector2 distanceInObjectSpace = v1.subtract(v0);
		double distance = Math.max(distanceInObjectSpace.getX(), distanceInObjectSpace.getY());

		DrawableContainer container = renderer != null ? renderer.getDrawableContainer() : null;

		IntersectTree intersector = new IntersectTree(getDocument(), container, position, distance);
		return intersector;
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

	public void setDefaults()
	{
		navigationMouseHandler = new NavigationMouseHandler(this);
		selectionMouseHandler = new SelectionMouseHandler(this);

		selectionMouseHandler.addSelectionHandler(new HighlightSelectionHandler());
		selectionMouseHandler.addSelectionHandler(refireHandler);
		setSelectionMode();

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

		this.setDrawRenderStats(true);
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
			getRenderPreferences().clearAllHighlights();
			for(INode node : event.getSelectedNodes())
			{
				getRenderPreferences().highlightNode(node);
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
					broadcastEvent("node_clicked", event.getNodeId(), event.getClientX(),
							event.getClientY());
				}

			});

			eventBus.addHandler(BranchClickEvent.TYPE, new BranchClickHandler()
			{

				@Override
				public void onBranchClick(BranchClickEvent event)
				{
					broadcastEvent("branch_clicked", event.getNodeId(), event.getClientX(),
							event.getClientY());
				}

			});

			eventBus.addHandler(LabelClickEvent.TYPE, new LabelClickHandler()
			{

				@Override
				public void onLabelClick(LabelClickEvent event)
				{
					broadcastEvent("label_clicked", event.getNodeId(), event.getClientX(),
							event.getClientY());
				}

			});
		}
	}

	private void broadcastEvent(String type, int id, int clientX, int clientY)
	{
		if(broadcastCommand != null)
		{
			String json = "{\"event\":\"" + type + "\",\"id\":\"" + id + "\",\"mouse\":{\"x\":" + clientX
					+ ",\"y\":" + clientY + "}}";
			broadcastCommand.broadcast(json);
		}
	}

	@Override
	public void setBroadcastCommand(BroadcastCommand cmdBroadcast)
	{
		this.broadcastCommand = cmdBroadcast;
	}

	/**
	 * Clear all highlighted nodes.
	 */
	public void clearHighlights()
	{
		RenderTree renderer = this.getRenderer();
		if(renderer != null)
		{
			RenderPreferences prefs = renderer.getRenderPreferences();
			if(prefs != null)
			{
				prefs.clearAllHighlights();
				this.requestRender();
			}
		}
	}

	/**
	 * Highlight given node
	 * @param node id
	 */
	public void highlightNode(Integer id)
	{
		RenderTree renderer = this.getRenderer();
		if(renderer != null)
		{
			RenderPreferences prefs = renderer.getRenderPreferences();
			if(prefs != null)
			{
				prefs.highlightNode(id);
				this.requestRender();
			}
		}
	}
	
	/**
	 * Highlight node and subtree for given id.
	 * @param node id
	 */
	public void highlightSubtree(Integer id)
	{
		RenderTree renderer = this.getRenderer();
		if(renderer != null)
		{
			RenderPreferences prefs = renderer.getRenderPreferences();
			if(prefs != null)
			{
				prefs.highlightSubtree(id);
				this.requestRender();
			}
		}
	}
	
	/**
	 * Highlight branch to given node id.
	 * @param node id
	 */
	public void highlightBranch(Integer id)
	{
		RenderTree renderer = this.getRenderer();
		if(renderer != null)
		{
			RenderPreferences prefs = renderer.getRenderPreferences();
			if(prefs != null)
			{
				prefs.highlightBranch(id);
				this.requestRender();
			}
		}
	}

	public boolean isDrawRenderStats()
	{
		return drawRenderStats;
	}

	public void setDrawRenderStats(boolean drawRenderStats)
	{
		this.drawRenderStats = drawRenderStats;
	}

	private void handleMouseClick(Hit hit, int x, int y)
	{
		if(hit != null && hit.getDrawable() != null)
		{
			Drawable.Context context = hit.getDrawable().getContext();
			if(Drawable.Context.CONTEXT_NODE == context)
			{
				dispatch(new NodeClickEvent(hit.getNodeId(), x, y));
			}

			else if(Drawable.Context.CONTEXT_BRANCH == context)
			{
				dispatch(new BranchClickEvent(hit.getNodeId(), x, y));
			}

			else if(Drawable.Context.CONTEXT_LABEL == context)
			{
				dispatch(new LabelClickEvent(hit.getNodeId(), x, y));
			}
		}
	}

	private void handleMouseOver(Hit hit, int x, int y)
	{
		if(hit != null && hit.getDrawable() != null)
		{
			Drawable.Context context = hit.getDrawable().getContext();
			if(Drawable.Context.CONTEXT_NODE == context)
			{
				broadcastEvent("node_mouse_over", hit.getNodeId(), x, y);
			}

			else if(Drawable.Context.CONTEXT_BRANCH == context)
			{
				broadcastEvent("branch_mouse_over", hit.getNodeId(), x, y);
			}

			else if(Drawable.Context.CONTEXT_LABEL == context)
			{
				broadcastEvent("label_mouse_over", hit.getNodeId(), x, y);
			}
		}
	}

	private void handleMouseOut(Hit hit, int x, int y)
	{
		if(hit != null && hit.getDrawable() != null)
		{
			Drawable.Context context = hit.getDrawable().getContext();
			if(Drawable.Context.CONTEXT_NODE == context)
			{
				broadcastEvent("node_mouse_out", hit.getNodeId(), x, y);
			}

			else if(Drawable.Context.CONTEXT_BRANCH == context)
			{
				broadcastEvent("branch_mouse_out", hit.getNodeId(), x, y);
			}

			else if(Drawable.Context.CONTEXT_LABEL == context)
			{
				broadcastEvent("label_mouse_out", hit.getNodeId(), x, y);
			}
		}
	}
}
