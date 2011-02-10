package org.iplantc.phyloviewer.viewer.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.viewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.viewer.server.db.ConnectionUtil;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgis.Polygon;

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
			
			statement = connection.prepareStatement("Select ST_AsText(point), ST_AsText(bounding_box) from node_layout where node_id=?");
			statement.setInt(1, node.getId());
			
			rs = statement.executeQuery();
			
			if(rs.next()) {

				Point point = (Point) PGgeometry.geomFromString(rs.getString(1));
				Vector2 p = convertPoint(point);

				Polygon polygon = (Polygon) PGgeometry.geomFromString(rs.getString(2));
				Vector2 min = convertPoint(polygon.getPoint(0));
				Vector2 max = convertPoint(polygon.getPoint(2));
				
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

	private Vector2 convertPoint(Point point) {
		double positionX = point.getX();
		double positionY = point.getY();
		Vector2 p = new Vector2(positionX,positionY);
		return p;
	}
}
