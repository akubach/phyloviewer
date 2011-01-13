package org.iplantc.phyloviewer.server.db;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.sql.DataSource;

import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.Node;
import org.iplantc.phyloparser.model.block.Block;
import org.iplantc.phyloparser.model.block.TreesBlock;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTreeCladogram;
import org.iplantc.phyloviewer.server.IImportTreeData;
import org.iplantc.phyloviewer.server.render.ImageGraphics;
import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.layout.LayoutCladogram;
import org.iplantc.phyloviewer.shared.model.Document;
import org.iplantc.phyloviewer.shared.model.Tree;

public class ImportTreeData implements IImportTreeData {
	
	private DataSource pool;
	private String imageDirectory;

	public ImportTreeData(DataSource pool,String imageDirectory) {
		this.pool = pool;
		this.imageDirectory = imageDirectory;
		
		new File(imageDirectory).mkdir();
	}
	
	public static RemoteNode rootNodeFromNewick(String newick, String name) {
		org.iplantc.phyloparser.parser.NewickParser parser = new org.iplantc.phyloparser.parser.NewickParser();
		FileData data = null;
		try {
			data = parser.parse(newick);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		org.iplantc.phyloparser.model.Tree tree = null;
		
		List<Block> blocks = data.getBlocks();
		for ( Block block : blocks ) {
			if ( block instanceof TreesBlock ) {
				TreesBlock trees = (TreesBlock) block;
				tree = trees.getTrees().get( 0 );
			}
		}
		
		int depth = 0;
		int nextTraversalIndex = 1; //starts with 1 to avoid ambiguity, because JDBC ResultSet.getInt() returns 0 for null values 
		return convertDataModels(tree.getRoot(), depth, nextTraversalIndex);
	}
	
	private static BufferedImage renderTreeImage(Tree tree, ILayoutData layout,
			int width, int height) {

		ImageGraphics graphics = new ImageGraphics(width, height);

		RenderTreeCladogram renderer = new RenderTreeCladogram();
		renderer.getRenderPreferences().setCollapseOverlaps(false);
		renderer.getRenderPreferences().setDrawLabels(false);

		Document document = new Document();
		document.setTree(tree);
		document.setLayout(layout);
		
		renderer.setDocument(document);
		renderer.renderTree(graphics, null, null);

		return graphics.getImage();
	}
	
	public int importTreeData(RemoteNode root, String name) {
		
		Tree tree = new Tree();
		tree.setRootNode(root);

		Connection connection = null;
		ImportLayout layoutImporter = null;
		try
		{
			connection = pool.getConnection();
			
			// The tree and all associated data will be added in a single transaction.
			connection.setAutoCommit(false);
			
			ImportTree importer = new ImportTree(connection);
			importer.addTree(tree, name);
			
			layoutImporter = new ImportLayout(connection);

			LayoutCladogram cladogramLayout = new LayoutCladogram(0.8,1.0);
			cladogramLayout.layout(tree);
			
			{
				String uuid = "LAYOUT_TYPE_CLADOGRAM";
				layoutImporter.addLayout(uuid, cladogramLayout, tree);
				
				BufferedImage image = renderTreeImage(tree,cladogramLayout,256,1024);
				this.putOverviewImage(connection,tree.getId(), uuid, image);
			}
			
			connection.commit();
		}
		catch(SQLException e)
		{
			//rolls back entire tree transaction on exception anywhere in the tree
			ConnectionUtil.rollback(connection);
			e.printStackTrace();
		}
		finally
		{
			ConnectionUtil.close(connection);
			
			if(layoutImporter != null) {
				layoutImporter.close();
			}
		}
		
		return tree.getId();
	}
	
	private void putOverviewImage(Connection connection, int treeId, String layoutId,
			BufferedImage image) throws SQLException {
		
		PreparedStatement statement = null;
		
		try { 
			String filename = UUID.randomUUID().toString();
			String path = imageDirectory+"/"+filename+".png";
		
			File file = new File(path);
			try {
				ImageIO.write(image, "png", file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			statement = connection.prepareStatement("insert into overview_images (tree_id,layout_id,image_width,image_height,image_path) values (?,?,?,?,?)");
			statement.setInt(1, treeId);
			statement.setString(2, layoutId);
			statement.setInt(3, image.getWidth());
			statement.setInt(4, image.getHeight());
			statement.setString(5, path);
			
			statement.executeUpdate();
			ConnectionUtil.close(statement);
		}
		finally
		{
			ConnectionUtil.close(statement);
		}
	}
	
	private static RemoteNode convertDataModels(org.iplantc.phyloparser.model.Node node, int depth, int nextTraversalIndex) {
		
		List<Node> myChildren = node.getChildren();
		
		int len = myChildren.size();
		RemoteNode[] children = new RemoteNode[len];
		int numNodes = 1;
		int maxChildHeight = -1;
		int numLeaves = len == 0 ? 1 : 0;
		int leftIndex = nextTraversalIndex;
		nextTraversalIndex++;
		
		for (int i = 0; i < len; i++) {
			Node myChild = myChildren.get(i);
			
			RemoteNode child = convertDataModels(myChild, depth + 1, nextTraversalIndex);
			children[i] = child;
			
			//note: numNodes, height and numLeaves are fields in RemoteNode, so the tree is not actually traversed again for each of these.
			maxChildHeight = Math.max(maxChildHeight, child.findMaximumDepthToLeaf()); 
			numLeaves += child.getNumberOfLeafNodes();
			numNodes += child.getNumberOfNodes();
			nextTraversalIndex = child.getRightIndex() + 1;
		}
		
		//create a RemoteNode for the current node
		String label = node.getName();
		
		int height = maxChildHeight + 1;
		int id = -1;  //id will be assigned when this node is inserted in the DB
		int numChildren = children.length;
		RemoteNode rNode = new RemoteNode(id, label, numChildren, numNodes, numLeaves, depth, height, leftIndex, nextTraversalIndex);
		rNode.setChildren(children);
		
		return rNode;
	}

	@Override
	public int importFromNewick(String newick, String name)
	{
		RemoteNode root = rootNodeFromNewick(newick, name);
		return this.importTreeData(root, name);
	}
}
