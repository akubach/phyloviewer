/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloviewer.client.tree.viewer.render.Camera;
import org.iplantc.phyloviewer.shared.layout.ILayout;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.FocusPanel;

public abstract class View extends FocusPanel {

	private Camera camera;
	private ITree tree;
	private ILayout layout;
	private List<NodeClickedHandler> nodeClickedHandlers = new ArrayList<NodeClickedHandler>();
	private boolean renderRequestPending = false;
	
	private static final char KEY_LEFT = 0x25;
	private static final char KEY_UP = 0x26;
	private static final char KEY_RIGHT = 0x27;
	private static final char KEY_DOWN = 0x28;
	
	public View() {
		this.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent arg0) {
				if ( getCamera() == null ) {
					return;
				}
				
				final char charCode = arg0.getCharCode();
				if ( charCode == ' ' ) {
					getCamera().reset();
				}
				else if ( charCode == KEY_UP ) {
					getCamera().pan(0.0, 0.1);
				}
				else if ( charCode == KEY_DOWN ) {
					getCamera().pan(0.0, -0.1);
				}
				else if ( charCode == KEY_LEFT ) {
					getCamera().pan(0.1, 0.0);
				}
				else if ( charCode == KEY_RIGHT ) {
					getCamera().pan(-0.1, 0.0);
				}
			}
		});
	}
	
	public ITree getTree() {
		return tree;
	}

	public void setTree(ITree tree) {
		/* FIXME: After the tree is changed, the client is asking the RemoteLayoutService for layouts of nodes in the old tree, on every render. Figure out why. */
		this.tree = tree;
		
		if (layout != null && tree != null)
		{
			layout.init(tree.getNumberOfNodes());
		}
		//doLayout();
	}
	
	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	public ILayout getLayout() {
		return layout;
	}

	public void setLayout(ILayout layout) {
		this.layout = layout;
		if (layout != null && tree != null)
		{
			layout.init(tree.getNumberOfNodes());
		}
		//doLayout();
	}
	
	public void addNodeClickedHandler(NodeClickedHandler handler) {
		if(handler!=null) {
			nodeClickedHandlers.add(handler);
		}
	}
	
	public void zoomToFit() {
		if ( null != this.getCamera() && null != this.getLayout() && null != this.getTree() ) {
			getCamera().zoomToFitSubtree(getTree().getRootNode(),getLayout());
		}
	}
	
	protected void notifyNodeClicked(INode node) {
		if(node!=null) {
			for(NodeClickedHandler handler : nodeClickedHandlers) {
				handler.onNodeClicked(node);
			}
		}
	}
	
	public abstract void resize(int width, int height);
	public abstract void render(); 

	public abstract int getWidth();
	public abstract int getHeight();
	
	/** 
	 * This gets called by TreeWidget before every render, so it must return quickly 
	 */
	public abstract boolean isReady();

	public void requestRender() {
		
		if(!this.renderRequestPending) {
			this.renderRequestPending = true;
			DeferredCommand.addCommand(new Command() {
		
				@Override
				public void execute() {
					View.this.render();
					View.this.renderRequestPending = false;
				}
			});
		}
	}
}
