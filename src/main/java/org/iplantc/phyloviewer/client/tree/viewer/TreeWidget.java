package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.client.tree.viewer.math.Matrix33;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.JSONParser;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.render.Camera;
import org.iplantc.phyloviewer.client.tree.viewer.render.CameraChangedHandler;
import org.iplantc.phyloviewer.client.tree.viewer.render.LayoutCladogram;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TreeWidget extends Composite {

	private VerticalPanel _mainPanel = new VerticalPanel();
	private HorizontalPanel _horizontalPanel = new HorizontalPanel();
	private Button _zoomIn = new Button();
	private Button _zoomOut = new Button();
	private Button _panUp = new Button();
	private Button _panDown = new Button();
	private OverviewView _overviewView;
	private DetailView _detailView;
	private Timer _renderTimer;
	
	private Vector2 _clickedPosition = null;
	private Vector2 _e0 = null;
	private Vector2 _e1 = null;
	
	public TreeWidget() {
		HorizontalPanel viewContainer = new HorizontalPanel();
		
		_overviewView = new OverviewView(200,600);
		_detailView = new DetailView(800,600);
		
		viewContainer.add(_overviewView);
		viewContainer.add(_detailView);
		
		_panUp.setText("Up");
		_panDown.setText("Down");
		_zoomIn.setText("+");
		_zoomOut.setText("-");
		
		_horizontalPanel.add(_panUp);
		_horizontalPanel.add(_panDown);
		_horizontalPanel.add(_zoomIn);
		_horizontalPanel.add(_zoomOut);
		
		_mainPanel.add(viewContainer);
		_mainPanel.add(_horizontalPanel);
		
		Camera camera = _detailView.getCamera();
		camera.addCameraChangedHandler(new CameraChangedHandler() {
			@Override
			public void onCameraChanged() {
				TreeWidget.this.requestRender();
			}
		});
		
		_overviewView.setCamera(camera);
		
		this.initWidget(_mainPanel);
		
		// Create a timer to render the tree when needed.
		_renderTimer = new Timer() {
			public void run() {
				_overviewView.render();
				_detailView.render();
			}
		};
		
		_zoomIn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				_detailView.getCamera().zoomInYDirection(0.5);
			}
		});
		
		_zoomOut.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				_detailView.getCamera().zoomInYDirection(-0.5);
			}
		});
		
		_panUp.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				_detailView.getCamera().panY(0.05);
			}
		});
		
		_panDown.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				_detailView.getCamera().panY(-0.05);
			}
		});
		
		_detailView.addMouseWheelHandler(new MouseWheelHandler() {
			public void onMouseWheel(MouseWheelEvent event) {
				_detailView.getCamera().zoomInYDirection(event.getDeltaY()/10.0);
			}
		});
		
		_detailView.addMouseDownHandler(new MouseDownHandler(){
			public void onMouseDown(MouseDownEvent event) {
				_clickedPosition = new Vector2(event.getX(), event.getY());
			}
		});
		
		_detailView.addMouseMoveHandler(new MouseMoveHandler() {
			public void onMouseMove(MouseMoveEvent event) {
				
				_e1 = _e0;
				_e0 = new Vector2 ( event.getX(),event.getY() );
				
				 if ( NativeEvent.BUTTON_LEFT == event.getNativeButton() ) {
					 if ( _e0 != null && _e1 != null && _clickedPosition != null ) {
						 
						 Matrix33 M = _detailView.getCamera().getMatrix();
						 Matrix33 IM = M.inverse();
						 
						 Vector2 p0 = IM.transform(_e0);
						 Vector2 p1 = IM.transform(_e1);
						 
						_detailView.getCamera().panY(p0.getY()-p1.getY());
					 }
				 }
			}
		});
		
		_detailView.addMouseUpHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) {
				_clickedPosition = null;
				_e0 = null;
				_e1 = null;
			}
		});
	}
	
	public void loadFromJSON(String json) {
		Tree tree = JSONParser.parseJSON(json);
		if ( tree != null ) {
			LayoutCladogram layout = new LayoutCladogram(0.8,1.0);
			layout.layout(tree);
			
			// TODO: Pass the tree to the over view to allow intersections (once the intersection code is created).
			_overviewView.loadFromJSON(json);
			_overviewView.setTree(tree);
			_detailView.setTree(tree);
		}
	}

	public void requestRender() {
		_renderTimer.schedule(1);
	}
	
	public void resize(int width, int height) {
		int overviewWidth=(int) (width*0.20);
		int detailWidth = width-overviewWidth;
		
		_overviewView.resize(overviewWidth,height);
		_detailView.resize(detailWidth,height);
	}
}
