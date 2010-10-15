package org.iplantc.phyloviewer.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.iplantc.phyloviewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.math.AnnularSector;
import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.PolarVector2;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;
import org.iplantc.phyloviewer.server.db.ImportLayout;

public class DatabaseLayoutData implements ILayoutData {

	private DataSource pool;

	public DatabaseLayoutData(DataSource pool) {
		this.pool = pool;
		
	}

	public void putLayout(String layoutID, ILayout layout, ITree tree) {
		try {
			ImportLayout importer = new ImportLayout(pool.getConnection());		
			importer.addLayout(layoutID, layout, tree);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public LayoutResponse getLayout(INode node, String layoutID) throws Exception {
		LayoutResponse response = new LayoutResponse();
		
		response.layoutID = layoutID;
		response.nodeID = node.getId();
		
		try {
			Connection connection = pool.getConnection();
			
			PreparedStatement statement = connection.prepareStatement("Select * from node_layout where node_id=? and layout_id=?");
			statement.setInt(1, node.getId());
			statement.setString(2, layoutID);
			
			ResultSet rs = statement.executeQuery();
			
			if(rs.next()) {
				
				double positionX = rs.getDouble("point_x");
				double positionY = rs.getDouble("point_y");
				double minX = rs.getDouble("min_x");
				double minY = rs.getDouble("min_y");
				double maxX = rs.getDouble("max_x");
				double maxY = rs.getDouble("max_y");
				
				if (layoutID.equals("org.iplantc.phyloviewer.client.tree.viewer.layout.LayoutCircular")) {
					PolarVector2 p = new PolarVector2(positionX,positionY);
					PolarVector2 min = new PolarVector2(minX,minY);
					PolarVector2 max = new PolarVector2(maxX,maxY);
					
					response.polarBounds = new AnnularSector(min, max);
					response.polarPosition = p;
				}
				
				Vector2 p = new Vector2(positionX,positionY);
				Vector2 min = new Vector2(minX,minY);
				Vector2 max = new Vector2(maxX,maxY);
				
				response.position = p;
				response.boundingBox = new Box2D(min,max);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}
}
