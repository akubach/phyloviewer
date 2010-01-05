package org.iplantc.iptol.client;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

import java.io.Serializable;

public class Folder extends File implements Serializable {
	 /**
	 * Folder model
	 */
	private static final long serialVersionUID = 1L;
	private static int ID = 0;
	  
	  public Folder() {
	    set("id", ID++);
	  }

	  public Folder(String name) {
	    set("id", ID++);
	    set("name", name);
	  }

	  public Folder(String name, BaseTreeModel[] children) {
	    this(name);
	    for (int i = 0; i < children.length; i++) {
	      add(children[i]);
	    }
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
}
