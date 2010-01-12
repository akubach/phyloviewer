package org.iplantc.iptol.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.iplantc.treedata.model.File;
import org.iplantc.treedata.model.Folder;
import org.iplantc.treedata.model.Workspace;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractTransformer;

public class ExtractWorkspaceInfoTransformer extends AbstractTransformer {

	@Override
	protected Object doTransform(Object source, String arg1)
			throws TransformerException {
		Workspace workspace;
		if (source instanceof Workspace) {
			workspace = (Workspace)source;
		} else {
			throw new TransformerException(
				MessageFactory.createStaticMessage(
					"Received object that was not a File or list of Files"));
		}		
		
		WorkspaceInfo workspaceInfo = new WorkspaceInfo();
		FolderInfo rootFolder = new FolderInfo();
		workspaceInfo.setId(workspace.getId());
		workspaceInfo.setRootFolder(rootFolder);
		
		Stack<Folder> folderStack = new Stack<Folder>();
		Map<Folder, FolderInfo> folderToFolderInfo = new HashMap<Folder, FolderInfo>();
		folderToFolderInfo.put(workspace.getRootFolder(), rootFolder);
		folderStack.push(workspace.getRootFolder());
		while (!folderStack.empty()) {
			Folder folder = folderStack.pop();
			for (Folder subfolder: folder.getSubfolders()) {
				folderStack.push(subfolder);
				FolderInfo folderInfo = new FolderInfo();
				folderInfo.setId(folder.getId());
				folderInfo.setLabel(folder.getLabel());
				folderToFolderInfo.get(folder).getSubfolders().add(folderInfo);
				folderToFolderInfo.put(subfolder, folderInfo);
			}
			for (File file: folder.getFiles()) {
				FileInfo fileInfo = new FileInfo();
				fileInfo.setId(file.getId());
				fileInfo.setName(file.getName());
				fileInfo.setUploaded(file.getUploaded());
				fileInfo.setType(file.getType().getDescription());				
				folderToFolderInfo.get(folder).getFiles().add(fileInfo);
			}
		}
		
		return workspaceInfo;
	}

}
