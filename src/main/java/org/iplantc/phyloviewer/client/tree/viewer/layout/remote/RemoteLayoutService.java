package org.iplantc.phyloviewer.client.tree.viewer.layout.remote;

import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.math.AnnularSector;
import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.PolarVector2;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("remoteLayout")
public interface RemoteLayoutService extends RemoteService {

	public String layout(String treeID, ILayout layout);
	
	public LayoutResponse getLayout(INode node, String layoutID) throws Exception;
	
	public LayoutResponse[] getLayout(INode[] nodes, String layoutID) throws Exception;
	
	public class LayoutResponse implements IsSerializable {
		public String nodeID;
		public String layoutID;
		public Box2D boundingBox;
		public Vector2 position;
		public AnnularSector polarBounds;
		public PolarVector2 polarPosition;
	}

}
