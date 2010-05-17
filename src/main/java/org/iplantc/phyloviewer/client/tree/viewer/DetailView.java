package org.iplantc.phyloviewer.client.tree.viewer;


import org.iplantc.phyloviewer.client.tree.viewer.canvas.Canvas;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.render.Camera;
import org.iplantc.phyloviewer.client.tree.viewer.render.IGraphics;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTree;
import org.iplantc.phyloviewer.client.tree.viewer.render.canvas.Graphics;

import com.google.gwt.user.client.ui.FocusPanel;

public class DetailView extends FocusPanel {

	private Canvas canvas = null;
	private IGraphics graphics = null;
	private Tree tree = null;
	private Camera camera = new Camera();
	
	public DetailView(int width,int height) {
		canvas = new Canvas(width,height);
		graphics = new Graphics(canvas);
		camera.resize(width,height);
		
		this.add(canvas);
	}

	public Tree getTree() {
		return tree;
	}

	public void setTree(Tree tree) {
		this.tree = tree;
	}

	public Camera getCamera() {
		return camera;
	}

	public void render() {
		RenderTree.renderTree(tree, graphics,camera);
	}

	public void resize(int width, int height) {
		canvas.setWidth(width);
		canvas.setHeight(height);
		camera.resize(width, height);
	}
}
