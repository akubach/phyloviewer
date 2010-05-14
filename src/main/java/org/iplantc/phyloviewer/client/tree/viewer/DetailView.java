package org.iplantc.phyloviewer.client.tree.viewer;


import org.iplantc.phyloviewer.client.tree.viewer.canvas.Canvas;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.render.Camera;
import org.iplantc.phyloviewer.client.tree.viewer.render.IGraphics;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTree;
import org.iplantc.phyloviewer.client.tree.viewer.render.canvas.Graphics;

import com.google.gwt.user.client.ui.FocusPanel;

public class DetailView extends FocusPanel {

	private Canvas _canvas = null;
	private IGraphics _graphics = null;
	private Tree _tree = null;
	private Camera _camera = new Camera();
	
	public DetailView(int width,int height) {
		_canvas = new Canvas(width,height);
		_graphics = new Graphics(_canvas);
		_camera.resize(width,height);
		
		this.add(_canvas);
	}

	public Tree getTree() {
		return _tree;
	}

	public void setTree(Tree tree) {
		_tree = tree;
	}

	public Camera getCamera() {
		return _camera;
	}

	public void render() {
		RenderTree.renderTree(_tree, _graphics,_camera);
	}

	public void resize(int width, int height) {
		_canvas.setWidth(width);
		_canvas.setHeight(height);
		_camera.resize(width, height);
	}
}
