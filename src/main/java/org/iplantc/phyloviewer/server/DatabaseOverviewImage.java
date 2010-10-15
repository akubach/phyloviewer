package org.iplantc.phyloviewer.server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.sql.DataSource;

import org.iplantc.phyloviewer.server.db.ConnectionAdapter;

public class DatabaseOverviewImage implements IOverviewImageData {

	private DataSource pool;
	private String imageDirectory;

	public DatabaseOverviewImage(DataSource pool,String imageDirectory) {
		this.pool = pool;
		this.imageDirectory = imageDirectory;
		
		new File(imageDirectory).mkdir();
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

	@Override
	public void setOverviewImage(int treeId, String layoutId,
			BufferedImage image) {
		Connection connection;
		try {
			String filename = UUID.randomUUID().toString();
			String path = imageDirectory+"/"+filename+".png";

			File file = new File(path);
			ImageIO.write(image, "png", file);
			
			connection = pool.getConnection();
			PreparedStatement statement = connection.prepareStatement("insert into overview_images (tree_id,layout_id,image_width,image_height,image_path) values (?,?,?,?,?)");
			statement.setInt(1, treeId);
			statement.setString(2, layoutId);
			statement.setInt(3, image.getWidth());
			statement.setInt(4, image.getHeight());
			statement.setString(5, path);
			
			statement.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
