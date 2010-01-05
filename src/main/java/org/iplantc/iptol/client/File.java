package org.iplantc.iptol.client;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

import java.io.Serializable;

public class File extends BaseTreeModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private static int ID = 0;
	
	private FileInfo info;
	  
	  public File() {
	    set("id", ID++);
	  }

	  public File(String name) {
	    set("id", ID++);
	    set("name", name);
	  }

	  public Integer getId() {
	    return (Integer) get("id");
	  }

	  public String getName() {
	    return (String) get("name");
	  }

	  public String toString() {
	    return getName();
	  }

	public void setInfo(FileInfo info) {
		this.info = info;
	}

	public FileInfo getInfo() {
		return info;
	}
}
