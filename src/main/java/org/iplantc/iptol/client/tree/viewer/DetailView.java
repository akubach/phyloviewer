package org.iplantc.iptol.client.tree.viewer;


import org.iplantc.iptol.client.tree.viewer.canvas.Canvas;
import org.iplantc.iptol.client.tree.viewer.model.Tree;
import org.iplantc.iptol.client.tree.viewer.render.Camera;
import org.iplantc.iptol.client.tree.viewer.render.Graphics;
import org.iplantc.iptol.client.tree.viewer.render.RenderTree;
import com.google.gwt.user.client.ui.FocusPanel;

public class DetailView extends FocusPanel {

	private Canvas _canvas = null;
	private Graphics _graphics = null;
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
}
