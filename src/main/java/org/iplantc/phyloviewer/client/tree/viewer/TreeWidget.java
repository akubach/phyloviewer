/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;
import org.iplantc.phyloviewer.client.tree.viewer.model.JSONParser;
//import org.iplantc.phyloviewer.client.tree.viewer.model.Ladderizer;
//import org.iplantc.phyloviewer.client.tree.viewer.model.Ladderizer.Direction;
import org.iplantc.phyloviewer.client.tree.viewer.render.Camera;
import org.iplantc.phyloviewer.client.tree.viewer.render.CameraChangedHandler;
import org.iplantc.phyloviewer.client.tree.viewer.render.LayoutCladogram;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TreeWidget extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();
	private HorizontalPanel horizontalPanel = new HorizontalPanel();
	private Button zoomIn = new Button();
	private Button zoomOut = new Button();
	private Button panUp = new Button();
	private Button panDown = new Button();
	private OverviewView overviewView;
	private DetailView detailView;
	private Timer renderTimer;
	private AnimateCamera animator;
	
	public TreeWidget() {
		HorizontalPanel viewContainer = new HorizontalPanel();
		
		overviewView = new OverviewView(200,600);
		detailView = new DetailView(800,600);
		
		viewContainer.add(overviewView);
		viewContainer.add(detailView);
		
		panUp.setText("Up");
		panDown.setText("Down");
		zoomIn.setText("+");
		zoomOut.setText("-");
		
		horizontalPanel.add(panUp);
		horizontalPanel.add(panDown);
		horizontalPanel.add(zoomIn);
		horizontalPanel.add(zoomOut);
		
		mainPanel.add(viewContainer);
		//_mainPanel.add(_horizontalPanel);
		
		Camera camera = detailView.getCamera();
		camera.addCameraChangedHandler(new CameraChangedHandler() {
			@Override
			public void onCameraChanged() {
				requestRender();
			}
		});
		
		overviewView.setCamera(camera);
		
		this.initWidget(mainPanel);
		
		// Create a timer to render the tree when needed.
		renderTimer = new Timer() {
			public void run() {
				render();
			}
		};
		
		zoomIn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				detailView.getCamera().zoomInYDirection(0.5);
			}
		});
		
		zoomOut.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				detailView.getCamera().zoomInYDirection(-0.5);
			}
		});
		
		panUp.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				detailView.getCamera().panY(0.05);
			}
		});
		
		panDown.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				detailView.getCamera().panY(-0.05);
			}
		});
		
		NodeClickedHandler nodeClickedHandler = new NodeClickedHandler() {
			public void onNodeClicked(INode node) {
				Camera finalCamera = new Camera();
				finalCamera.zoomToBoundingBox(detailView.getLayout().getBoundingBox(node));
				
				startAnimation(finalCamera);
			}
		};
		
		detailView.addNodeClickedHandler(nodeClickedHandler);
		overviewView.addNodeClickedHandler(nodeClickedHandler);
	}
	
	public void loadFromJSON(String json) {
		ITree tree = JSONParser.parseJSON(json);
		if ( tree != null ) {

			//FIXME note that the overview ignores the client layout, so it will not change
			//Ladderizer ladderizer = new Ladderizer(Direction.UP); 
			//ladderizer.ladderize(tree.getRootNode());
			
			LayoutCladogram layout = new LayoutCladogram(0.8,1.0);
			layout.layout(tree);
			
			overviewView.loadFromJSON(json);
			overviewView.setTree(tree);
			overviewView.setLayout(layout);
			detailView.setTree(tree);
			detailView.setLayout(layout);
		}
	}

	public void requestRender() {
		renderTimer.schedule(1);
	}
	
	public void resize(int width, int height) {
		int overviewWidth=(int) (width*0.20);
		int detailWidth = width-overviewWidth;
		
		overviewView.resize(overviewWidth,height);
		detailView.resize(detailWidth,height);
	}
	
	protected void startAnimation(Camera finalCamera) {
		animator = new AnimateCamera(detailView.getCamera().getViewMatrix(),finalCamera.getViewMatrix(),25);
		
		renderTimer.scheduleRepeating(30);
	}

	private void render() {
		if(animator!=null) {
			overviewView.getCamera().setMatrix(animator.getNextMatrix());
			
			if(animator.isDone()) {
				animator = null;
				
				renderTimer.cancel();
			}
		}
		
		overviewView.render();
		detailView.render();
	}
}
