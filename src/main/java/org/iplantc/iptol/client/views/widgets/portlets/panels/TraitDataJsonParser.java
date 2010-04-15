package org.iplantc.iptol.client.views.widgets.portlets.panels;

import java.util.Iterator;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;

/**
 * A class to parse trait data json value returned by the service. Clients
 * should call parseRoot() to get tokenize the json.
 * 
 * @author sriram
 * 
 */
public class TraitDataJsonParser {

	private String json;
	private JSONObject root;
	private JSONValue header;
	private JSONObject data;

	public TraitDataJsonParser(String json) {
		this.json = json;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
	
	/**
	 * Parse json into header and data parts 
	 */
	public void parseRoot() {
		if(json == null || json == "") {
			return;
		}
		
		JSONArray data_rows = null;
		root = (JSONObject) JSONParser.parse(json);
		header = root.get("headers");
		//header = transformHeader(headerRoot);
		//Window.alert("header->" + header);

		JSONValue dataRoot = root.get("data");
		//Window.alert("dataRoot->" + dataRoot.toString());

		if (dataRoot != null && header != null) {
			data_rows = dataRoot.isArray();
		//	Window.alert("data_rows->" + data_rows.toString());
			tranformData(data_rows,header);
		}
	}

	/**
	 * Transform data and header into a format that is consumable by the grid
	 * @param data_rows
	 *           [{"id":"1340","values":["foo","4.3","0"]},
				  {"id":"1339","values":["bar","1.3","1"]}
				  ]
	 * @param header
	 *            [{"id":"0","label":"species"},{"id":"1234","label":"flowerSize"},{"id":"2345","label":"color"}]
	 * @return {"data":[{"id":"101", "species":"foo", "flowerSize":"4.3",
	 *         "color":"0"},{"id":"102", "species":"bar", "flowerSize":"1.3",
	 *         "color":"1"}]}
	 */
	public JSONObject tranformData(JSONArray data_rows, JSONValue header) {
		
		Iterator<String> iter = null;
		String key = null;
		
		// string thats holds header portion intially. data portion added subsequently
		StringBuilder temp = new StringBuilder();
		
		//string that holds data (one row at a time)
		StringBuilder tempRow = new StringBuilder();
		
		JSONArray tempArray = null;
		
		StringBuilder finalJson = new StringBuilder();
		
		JSONArray headerArray =  header.isArray();
		
		JSONArray headerNames = new JSONArray();
		
		for (int j=0;j<headerArray.size();j++) {
			JSONValue val = headerArray.get(j);
			JSONObject obj = val.isObject();
			headerNames.set(j, obj.get("label"));
		}

		if (data_rows != null && header != null) {
			for (int i = 0; i < data_rows.size(); i++) {
				JSONObject row = (JSONObject) data_rows.get(i);
				iter = row.keySet().iterator();

				while (iter.hasNext()) {
					//clean up
					if (temp.length() > 0) {
						temp.delete(0, temp.length());
					}

					if (tempRow.length() > 0) {
						tempRow.delete(0, tempRow.length());
					}
					
					//headers
					key = iter.next();
					temp.append("{" + "\"id\":" + row.get(key) + ",");
					//Window.alert(temp.toString());
					key = iter.next();
					tempArray = row.get(key).isArray();

					//data
					for (int k = 0; k < tempArray.size(); k++) {
						String str = trim(headerNames.get(k).toString());
						if (str.equals("")) {
							str = makeUpEmptyHeader(k);
						}
						
						if (k == 0) {
							tempRow.append(str + ":"
									+ tempArray.get(k).toString());

						} else {
							tempRow.append("," + str + ":"
									+ tempArray.get(k).toString());
						}
					}

					//Window.alert(tempRow.toString());
					temp.append(tempRow.toString() + "}");
					//Window.alert(temp.toString());
				}

				finalJson.append(temp.toString()).append(",");
				//Window.alert(finalJson.toString());
			}
			///formatting
			// remove the last ","
			if( finalJson.length() > 1 && finalJson.charAt(finalJson.length() - 1) == ',') {
				finalJson.deleteCharAt(finalJson.length() - 1);
			}
			finalJson.append("]}");
			String jsonValue = "{\"data\": [" + finalJson.toString();
			JSONValue value = JSONParser.parse(jsonValue);
			//Window.alert("json vlaue -> " + value.toString());
			data = value.isObject();
			return data;
		} else {
			return null;
		}
	}

	/**
	 * util method to remove quotes around a json string
	 * @param value
	 * @return
	 */
	public static String trim(String value) {
		StringBuilder temp = new StringBuilder(value);
		if(value.startsWith("\"")) {
			temp.deleteCharAt(0);
		}
		
		if(value.endsWith("\"")) {
			temp.deleteCharAt(temp.length()-1);
		}
		
		return temp.toString();
	}
	
	/**
	 * util method to fix empty column header problem 
	 * @param spaceCount
	 * @return
	 */
	public static String makeUpEmptyHeader(int k) {
		return "unk" + k;
	}
		
	public JSONObject getRoot() {
		return root;
	}

	public void setRoot(JSONObject root) {
		this.root = root;
	}

	public JSONValue getHeader() {
		return header;
	}

	public void setHeader(JSONValue header) {
		this.header = header;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	public JSONObject getData() {
		return data;
	}

	
}
