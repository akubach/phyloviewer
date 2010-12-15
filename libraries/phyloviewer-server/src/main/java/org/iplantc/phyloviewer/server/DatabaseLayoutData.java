package org.iplantc.phyloviewer.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.iplantc.phyloviewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.server.db.ConnectionUtil;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;

public class DatabaseLayoutData implements ILayoutData {

	private DataSource pool;

	public DatabaseLayoutData(DataSource pool) {
		this.pool = pool;
	}
	
	public LayoutResponse getLayout(INode node) throws Exception {
		LayoutResponse response = new LayoutResponse();
		
		response.nodeID = node.getId();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try {
			connection = pool.getConnection();
			
			statement = connection.prepareStatement("Select * from node_layout where node_id=?");
			statement.setInt(1, node.getId());
			
			rs = statement.executeQuery();
			
			if(rs.next()) {
				
				double positionX = rs.getDouble("point_x");
				double positionY = rs.getDouble("point_y");
				double minX = rs.getDouble("min_x");
				double minY = rs.getDouble("min_y");
				double maxX = rs.getDouble("max_x");
				double maxY = rs.getDouble("max_y");
				
				Vector2 p = new Vector2(positionX,positionY);
				Vector2 min = new Vector2(minX,minY);
				Vector2 max = new Vector2(maxX,maxY);
				
				response.position = p;
				response.boundingBox = new Box2D(min,max);
			}
			
			ConnectionUtil.close(connection);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			ConnectionUtil.close(rs);
			ConnectionUtil.close(statement);
			ConnectionUtil.close(connection);
		}
		
		return response;
	}
}
