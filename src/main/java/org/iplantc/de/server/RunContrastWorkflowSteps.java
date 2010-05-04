package org.iplantc.de.server;

import java.util.List;
import java.util.Map;

import org.iplantc.phyloparser.model.FileData;
import org.iplantc.runcontrast.ContrastJobConfiguration;

public interface RunContrastWorkflowSteps {
	void updateContrastJobStatus(Long jobId, String status);
	ContrastJobConfiguration getContrastJobConfig(Long jobId);
	List<org.iplantc.treedata.model.Tree> retrieveTreeList(ContrastJobConfiguration cjc);
	org.iplantc.treedata.model.Matrix retrieveMatrix(ContrastJobConfiguration cjc);
	void reconcileData(List<org.iplantc.treedata.model.Tree> treeList, org.iplantc.treedata.model.Matrix matrix, Map<Long, Long> reconciliationMatrix);
	List<org.iplantc.phyloparser.model.Tree> transformTrees(List<org.iplantc.treedata.model.Tree> trees);
	org.iplantc.phyloparser.model.Matrix transformMatrix(org.iplantc.treedata.model.Matrix matrix);
	FileData buildTreeFileData(List<org.iplantc.phyloparser.model.Tree> trees);
	FileData buildMatrixFileData(org.iplantc.phyloparser.model.Matrix matrix);
	String generateNewick(FileData fd);
	String generatePhylipTrait(FileData fd);
	void runContrast(String newick, String phylipTrait, ContrastJobConfiguration cjc);
	void uploadFile(String fileContent, String fileName, Long workspaceId, Long folderId);
}
