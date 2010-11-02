package org.iplantc.phyloviewer.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.iplantc.phyloviewer.shared.layout.ILayout;
import org.iplantc.phyloviewer.shared.layout.ILayoutCircular;
import org.iplantc.phyloviewer.shared.math.AnnularSector;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.PolarVector2;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;

public class ImportLayout {

	Connection connection;
	PreparedStatement addNodeLayoutStmt = null;
	
	public ImportLayout(Connection conn) throws SQLException {
		this.connection = conn;
		
		// Create our prepared statement.
		addNodeLayoutStmt = conn.prepareStatement("insert into node_layout(node_id,layout_id,point_x,point_y,min_x,min_y,max_x,max_y) values (?,?,?,?,?,?,?,?)");
	}
	
	public void close() {
		ConnectionUtil.close(addNodeLayoutStmt);
	}
	
	public void addLayout(String layoutID, ILayout layout, ITree tree) throws SQLException {
		addNodeLayoutStmt.setString(2, layoutID);
		this.addNode(layout,tree.getRootNode());
	}
	
	private void addNode(ILayout layout, INode node) throws SQLException {
		
		addNodeLayoutStmt.setInt(1, node.getId());
			
		if (layout instanceof ILayoutCircular ) {
			PolarVector2 position = ((ILayoutCircular) layout).getPolarPosition(node);
			AnnularSector box = ((ILayoutCircular) layout).getPolarBoundingBox(node);
			
			addNodeLayoutStmt.setDouble(3, position.getX());
			addNodeLayoutStmt.setDouble(4, position.getY());
			
			addNodeLayoutStmt.setDouble(5, box.getMin().getX());
			addNodeLayoutStmt.setDouble(6, box.getMin().getY());
			
			addNodeLayoutStmt.setDouble(7, box.getMax().getX());
			addNodeLayoutStmt.setDouble(8, box.getMax().getY());
		}
		else {
			Vector2 position = layout.getPosition(node);
			Box2D box = layout.getBoundingBox(node);
			
			addNodeLayoutStmt.setDouble(3, position.getX());
			addNodeLayoutStmt.setDouble(4, position.getY());
			
			addNodeLayoutStmt.setDouble(5, box.getMin().getX());
			addNodeLayoutStmt.setDouble(6, box.getMin().getY());
			
			addNodeLayoutStmt.setDouble(7, box.getMax().getX());
			addNodeLayoutStmt.setDouble(8, box.getMax().getY());
		}
		
		addNodeLayoutStmt.executeUpdate();
		
		for(int i=0; i<node.getNumberOfChildren(); ++i) {
			addNode(layout,node.getChild(i));
		}
	}
}
