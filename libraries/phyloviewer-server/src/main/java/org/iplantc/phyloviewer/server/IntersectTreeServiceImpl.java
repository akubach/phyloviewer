package org.iplantc.phyloviewer.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.iplantc.phyloviewer.client.services.TreeIntersectService;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.json.JSONException;
import org.json.JSONObject;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgis.Polygon;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class IntersectTreeServiceImpl extends RemoteServiceServlet implements TreeIntersectService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3415482053977170710L;

	@Override
	public String intersectTree(int treeId, double x, double y) {
		
		DataSource pool = (DataSource) this.getServletContext().getAttribute("db.connectionPool");
		Connection connection = null;
		try {
			 connection = pool.getConnection();
			
			 double distance = 0.015;
			 
			 double xMin = x - distance;
			 double yMin = y - distance;
			 double xMax = x + distance;
			 double yMax = y + distance;
			 
			 String boxGeometry="BOX3D(" + xMin + " " + yMin + "," + xMax + " " + yMax + ")";
			PreparedStatement statement = connection.prepareStatement("select node_id, asText(point), asText(bounding_box), ST_Distance(point,?)  as distance from node_layout where point && ?::box3d and ST_Distance(point,?) < ? and tree_id=? order by distance limit 1");
			String geometry = "SRID=-1;POINT(" + x + " " + y + ")";
			statement.setString(1, geometry);
			statement.setString(2,boxGeometry);
			statement.setString(3, geometry);
			statement.setDouble(4, distance);
			statement.setInt(5,treeId);
			
			ResultSet rs = statement.executeQuery();
			if(rs.next()) {
				
				Point point = (Point) PGgeometry.geomFromString(rs.getString(2));
				Polygon polygon = (Polygon) PGgeometry.geomFromString(rs.getString(3));
				Vector2 min = convertPoint(polygon.getPoint(0));
				Vector2 max = convertPoint(polygon.getPoint(2));
				
				int nodeId = rs.getInt(1);
				Vector2 position = convertPoint(point);
				Box2D boundingBox = new Box2D(min,max);
				
				connection.close();
				
				try {
					JSONObject hit = new JSONObject();
					hit.put("nodeId", nodeId);
					hit.put("position",vectorToJSON(position));
					hit.put("boundingBox", boundingBoxToJSON(boundingBox));
					
					JSONObject hitResult = new JSONObject();
					hitResult.put("hit", hit);
					
					return hitResult.toString();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			connection.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "{\"hit\":null}";
	}
	
	static JSONObject vectorToJSON(Vector2 vector) throws JSONException {
		JSONObject object = new JSONObject();
		object.put("x",vector.getX());
		object.put("y",vector.getY());
		return object;
	}
	
	static JSONObject boundingBoxToJSON(Box2D box) throws JSONException {
		JSONObject object = new JSONObject();
		object.put("min",vectorToJSON(box.getMin()));
		object.put("max",vectorToJSON(box.getMax()));
		return object;
	}
	
	private Vector2 convertPoint(Point point) {
		double positionX = point.getX();
		double positionY = point.getY();
		Vector2 p = new Vector2(positionX,positionY);
		return p;
	}

}
