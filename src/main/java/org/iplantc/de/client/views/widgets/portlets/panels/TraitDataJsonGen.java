package org.iplantc.de.client.views.widgets.portlets.panels;

import java.util.Collection;
import java.util.List;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * 
 * @author sriram A class that generates json string from given ModelData.
 * The Model data represents traits.
 * 
 */
public class TraitDataJsonGen {

	private List<ModelData> data;
	private JSONArray headers;

	public TraitDataJsonGen(List<ModelData> data, JSONArray headers) {
		this.data = data;
		this.headers = headers;
	}

	public String generateJson() {
		String json = null;
		StringBuilder sb = new StringBuilder();
		Collection<String> properties = null;
		
		if (data == null || headers == null) {
			return null; 
		}
		
		//data
		sb.append("{\"data\":[");
		for (ModelData record : data) {
			sb.append("{\"id\":");
			sb.append("\"" + record.get("id") + "\",");
			sb.append("\"" + "values" + "\":[");
			if (properties == null) {
				properties = record.getPropertyNames();
			}
			for (String property : properties) {
				if (!"id".equals(property)) {
					sb.append("\"" + record.get(property) + "\",");
				}
			}
			// remove last comma
			sb.deleteCharAt(sb.length() - 1);
			sb.append("]},");
		}
		// remove last comma
		if(sb.charAt(sb.length() - 1) == ',') {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("],\"headers\":[");
		
		//headers in same order as data
		for (String property : properties) {
			for (int i = 0; i < headers.size(); i++) {
				JSONValue val = headers.get(i);
				JSONObject obj1 = val.isObject();
				if (property.equals(TraitDataJsonParser.trim(obj1.get("id")
						.toString()))) {
//					Window.alert(TraitDataJsonParser.trim(obj1.get("id")
//							.toString())
//							+ "-->"
//							+ TraitDataJsonParser.trim(obj1.get("label")
//									.toString()));
					
					sb.append("{\"id\":" + obj1.get("id")
							.toString() + ",\"label\":" +obj1.get("label")
									.toString() + "},");
				}
			}
		}
		
		// remove last comma
		if(sb.charAt(sb.length() - 1) == ',') {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("]}");
	//	System.out.println("sb - >" + sb.toString());
		JSONObject obj = JSONParser.parse(sb.toString()).isObject();
	//	System.out.println("json - >" + obj.toString());
		return sb.toString();

	}
}
