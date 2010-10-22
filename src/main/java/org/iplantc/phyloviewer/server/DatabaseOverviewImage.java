package org.iplantc.phyloviewer.server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.sql.DataSource;

import org.iplantc.phyloviewer.server.db.ConnectionAdapter;

public class DatabaseOverviewImage implements IOverviewImageData {

	private DataSource pool;

	public DatabaseOverviewImage(DataSource pool) {
		this.pool = pool;
	}
	
	@Override
	public BufferedImage getOverviewImage(int treeId, String layoutId) {
		
		BufferedImage image = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try {
			connection = pool.getConnection();
		
			String sql = "Select * from overview_images where tree_id=?";
			statement = connection.prepareCall(sql);
			statement.setInt(1, treeId);
			
			rs = statement.executeQuery();
			
			if(rs.next()) {
				String path = rs.getString("image_path");
				image = ImageIO.read(new File(path));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			ConnectionAdapter.close(connection);
			ConnectionAdapter.close(statement);
			ConnectionAdapter.close(rs);
		}
		
		return image;
	}
}
