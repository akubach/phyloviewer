package org.iplantc.phyloviewer.client.services;

import org.iplantc.phyloviewer.client.tree.viewer.math.AnnularSector;
import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.PolarVector2;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("nodeLayout")
public interface CombinedService extends RemoteService
{
	CombinedResponse getChildrenAndLayout(int parentID, String layoutID) throws Exception;
	CombinedResponse[] getChildrenAndLayout(int[] parentIDs, String[] layoutIDs) throws Exception;
	
	public class CombinedResponse implements IsSerializable
	{
		public int parentID;
		public String layoutID;
		public LayoutResponse[] layouts;
		public RemoteNode[] nodes;
	}
	
	RemoteNode[] getChildren(int parentID);
	
	/**
	 * @param id
	 * @return the tree with the given ID. On the client, the tree will only have a root node and the
	 *         rest must be fetched using RemoteNode.getChildrenAsync()
	 */
	Tree getTree(int id);
	
	public LayoutResponse getLayout(INode node, String layoutID) throws Exception;
	
	public LayoutResponse[] getLayout(INode[] nodes, String layoutID) throws Exception;
	
	public class LayoutResponse implements IsSerializable {
		public int nodeID;
		public String layoutID;
		public Box2D boundingBox;
		public Vector2 position;
		public AnnularSector polarBounds;
		public PolarVector2 polarPosition;
	}
}
