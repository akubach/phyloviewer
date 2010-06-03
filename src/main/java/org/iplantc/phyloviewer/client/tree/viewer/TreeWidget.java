package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;
import org.iplantc.phyloviewer.client.tree.viewer.model.JSONParser;
import org.iplantc.phyloviewer.client.tree.viewer.model.Ladderizer;
import org.iplantc.phyloviewer.client.tree.viewer.model.Ladderizer.Direction;
import org.iplantc.phyloviewer.client.tree.viewer.render.Camera;
import org.iplantc.phyloviewer.client.tree.viewer.render.CameraChangedHandler;
import org.iplantc.phyloviewer.client.tree.viewer.render.LayoutCladogram;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
		//_mainPanel.add(_horizontalPanel);
		
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
	}
	
	public void loadFromJSON(String json) {
		ITree tree = JSONParser.parseJSON(json);
		if ( tree != null ) {

			Ladderizer ladderizer = new Ladderizer(Direction.UP); //FIXME note that the overview ignores the client layout, so it will not change
			ladderizer.ladderize(tree.getRootNode());
			
			LayoutCladogram layout = new LayoutCladogram(0.8,1.0);
			layout.layout(tree);
			
			_overviewView.loadFromJSON(json);
			_overviewView.setTree(tree);
			_overviewView.setLayout(layout);
			_detailView.setTree(tree);
			_detailView.setLayout(layout);
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
