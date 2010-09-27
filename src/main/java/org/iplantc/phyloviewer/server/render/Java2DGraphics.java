package org.iplantc.phyloviewer.server.render;

import java.awt.Graphics2D;

import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Matrix33;
import org.iplantc.phyloviewer.client.tree.viewer.math.PolarVector2;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.render.IGraphics;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle.IElementStyle;

public class Java2DGraphics implements IGraphics {
	public final Graphics2D g2d;
	
	public Java2DGraphics(Graphics2D graphics) {
		this.g2d = graphics;
	}
	
	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawArc(Vector2 center, double radius, double startAngle,
			double endAngle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawLine(Vector2 start, Vector2 end) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawPoint(Vector2 position) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawRightAngle(Vector2 start, Vector2 end) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawText(Vector2 position, String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawTextRadial(PolarVector2 position, String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawTriangle(Vector2 v0, double x, double y0, double y1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawWedge(Vector2 peak, PolarVector2 base0, PolarVector2 base1) {
		// TODO Auto-generated method stub

	}

	@Override
	public Boolean isCulled(Box2D iBox2D) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStyle(IElementStyle style) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setViewMatrix(Matrix33 matrix) {
		// TODO Auto-generated method stub

	}

}
