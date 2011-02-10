package org.iplantc.phyloviewer.viewer.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.postgis.LinearRing;
import org.postgis.Point;
import org.postgis.Polygon;

public class ImportLayout {

	Connection connection;
	PreparedStatement addNodeLayoutStmt = null;
	
	public ImportLayout(Connection conn) throws SQLException {
		this.connection = conn;
		
		// Create our prepared statement.
		// How to handle prepared statements with postgis (http://postgis.refractions.net/pipermail/postgis-users/2006-October/013484.html).
		addNodeLayoutStmt = conn.prepareStatement("insert into node_layout(node_id,tree_id,layout_id,point,bounding_box) values (?,?,?,?::geometry,?::geometry)");
	}
	
	public void close() {
		ConnectionUtil.close(addNodeLayoutStmt);
	}
	
	public void addLayout(String layoutID, ILayoutData layout, ITree tree) throws SQLException {
		addNodeLayoutStmt.setString(3, layoutID);
		this.addNode(layout,tree.getId(),tree.getRootNode());
	}
	
	private void addNode(ILayoutData layout, int treeId, INode node) throws SQLException {
		
		addNodeLayoutStmt.setInt(1, node.getId());
		addNodeLayoutStmt.setInt(2, treeId);

		Vector2 position = layout.getPosition(node);
		Box2D box = layout.getBoundingBox(node);
		
		Point p = new Point(position.getX(), position.getY());
		
		addNodeLayoutStmt.setString(4,"SRID=-1;" + p.toString());
		
		Point points[] = new Point[5];
		points[0] = new Point(box.getMin().getX(),box.getMin().getY());
		points[1] = new Point(box.getMax().getX(),box.getMin().getY());
		points[2] = new Point(box.getMax().getX(),box.getMax().getY());
		points[3] = new Point(box.getMin().getX(),box.getMax().getY());
		points[4] = points[0];
		
		LinearRing boundary = new LinearRing(points);
		LinearRing rings[] = new LinearRing[1];
		rings[0] = boundary;
		
		Polygon polygon = new Polygon(rings);
		
		addNodeLayoutStmt.setString(5,"SRID=-1;" + polygon.toString());
		
		addNodeLayoutStmt.executeUpdate();
		
		for(int i=0; i<node.getNumberOfChildren(); ++i) {
			addNode(layout,treeId,node.getChild(i));
		}
	}
}
