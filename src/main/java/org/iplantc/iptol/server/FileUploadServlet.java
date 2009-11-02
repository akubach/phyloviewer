package org.iplantc.iptol.server;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import org.apache.commons.fileupload.FileItem;

import net.sf.json.JSONObject;
/**
 * A class to accept files from the client. 
 * This class extends the UploadAction class provided by the 
 * GWT Upload library 
 * @author sriram
 *
 */
public class FileUploadServlet extends UploadAction {
	
	public final long MAX_FILE_SIZE =  3145728;
	
	public final int UPLOAD_DELAY = 0;
	

	/**
	 * This method is automatically called for file upload request
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public String executeAction(HttpServletRequest request, List <FileItem> fileItems)
			throws UploadActionException {
		
		this.maxSize = MAX_FILE_SIZE;
		this.uploadDelay = UPLOAD_DELAY;
		
		Map map = new HashMap();
		JSONObject json = null;
		
		for(FileItem item: fileItems){
			if(!item.isFormField()) {
				try {
					String contents = item.getString();
					map.put("file_name",item.getName());
					map.put("date", (new Date()).toString());
					map.put("label",item.getName());
					json = JSONObject.fromObject(map);
					System.out.println("filename ==>" + item.getName());
				} catch (Exception e) {
			          throw new UploadActionException(e.getMessage());
		        }
			}
		}
		removeSessionFileItems(request,false);
		if(json!= null)
			return json.toString();
		else
			return null;
				
	}
}
