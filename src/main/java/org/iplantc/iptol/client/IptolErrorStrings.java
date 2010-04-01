package org.iplantc.iptol.client;

import com.google.gwt.i18n.client.Constants;

public interface IptolErrorStrings extends Constants 
{
	String error();
	String loginFailed();
	String unableToBuildWorkspace();
	String renameFolderFailed();
	String renameFileFailed();
	String deleteFolderFailed();
	String deleteFileFailed();
	String createFolderFailed();
	String retrieveFiletreeFailed();
	String retrieveUserInfoFailed();
	String rawDataSaveFailed();
	String fileUploadFailed();
	String importFailed();
	String searchFailed();
	String mustSelectBeforeImport();
	String treeServiceRetrievalFailed();
	String saveJobError();
	String getJobsError();
	String deleteJobError();
	String runJobError();
}
