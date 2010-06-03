package org.iplantc.phyloviewer.client.tree.viewer;


import org.iplantc.phyloviewer.client.tree.viewer.canvas.Canvas;
import org.iplantc.phyloviewer.client.tree.viewer.math.Matrix33;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;
import org.iplantc.phyloviewer.client.tree.viewer.render.Camera;
import org.iplantc.phyloviewer.client.tree.viewer.render.IGraphics;
import org.iplantc.phyloviewer.client.tree.viewer.render.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTree;
import org.iplantc.phyloviewer.client.tree.viewer.render.canvas.Graphics;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.user.client.ui.FocusPanel;

public class DetailView extends FocusPanel {

	private Canvas canvas = null;
	private IGraphics graphics = null;
	private ITree tree = null;
	private Camera camera = new Camera();
	private ILayout layout = null;
	
	private Vector2 _clickedPosition = null;
	private Vector2 _e0 = null;
	private Vector2 _e1 = null;
	
	public DetailView(int width,int height) {
		canvas = new Canvas(width,height);
		graphics = new Graphics(canvas);
		camera.resize(width,height);
		
		this.add(canvas);
		
		this.addMouseWheelHandler(new MouseWheelHandler() {
			public void onMouseWheel(MouseWheelEvent event) {
				getCamera().zoomInYDirection(event.getDeltaY()/10.0);
			}
		});
		
		this.addMouseDownHandler(new MouseDownHandler(){
			public void onMouseDown(MouseDownEvent event) {
				_clickedPosition = new Vector2(event.getX(), event.getY());
			}
		});
		
		this.addMouseMoveHandler(new MouseMoveHandler() {
			public void onMouseMove(MouseMoveEvent event) {
				
				_e1 = _e0;
				_e0 = new Vector2 ( event.getX(),event.getY() );
				
				 if ( NativeEvent.BUTTON_LEFT == event.getNativeButton() ) {
					 if ( _e0 != null && _e1 != null && _clickedPosition != null ) {
						 
						 Matrix33 M = camera.getMatrix();
						 Matrix33 IM = M.inverse();
						 
						 Vector2 p0 = IM.transform(_e0);
						 Vector2 p1 = IM.transform(_e1);
						 
						getCamera().panY(p0.getY()-p1.getY());
					 }
				 }
			}
		});
		
		this.addMouseUpHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) {
				_clickedPosition = null;
				_e0 = null;
				_e1 = null;
			}
		});
	}

	public ITree getTree() {
		return tree;
	}

	public void setTree(ITree tree) {
		this.tree = tree;
	}

	public Camera getCamera() {
		return camera;
	}

	public void render() {
		RenderTree.renderTree(tree, layout, graphics, camera);
	}

	public void resize(int width, int height) {
		canvas.setWidth(width);
		canvas.setHeight(height);
		camera.resize(width, height);
	}

	public ILayout getLayout() {
		return layout;
	}

	public void setLayout(ILayout layout) {
		this.layout = layout;
	}
}
