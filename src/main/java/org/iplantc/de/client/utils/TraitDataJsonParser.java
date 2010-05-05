package org.iplantc.de.client.utils;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

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
	    data = null;
		if (data_rows != null && header != null) {
	        JSONArray headerNames = getHeaderNames(header);
	        String jsonValue = buildJsonValue(data_rows, headerNames);
			JSONValue value = JSONParser.parse(jsonValue);
			//Window.alert("json vlaue -> " + value.toString());
			data = value.isObject();
		}
		return data;
	}

	/**
	 * Builds the JSON string from the given arrays of data rows and header names.
	 *
	 * @param dataRows the array of data rows.
	 * @param headerNames the array of column headers.
	 * @return the JSON string.
	 */
	private String buildJsonValue(JSONArray dataRows, JSONArray headerNames) {
	    StringBuilder builder = new StringBuilder();
	    builder.append("{\"data\": [");
        for (int i = 0; i < dataRows.size(); i++) {
	        if (i > 0) {
	            builder.append(",");
	        }
	        appendJsonRow(builder, (JSONObject) dataRows.get(i), headerNames);
	    }
        builder.append("]}");
        return builder.toString();
    }

	/**
	 * Appends a data row to the JSON string that is being built.
	 *
	 * @param builder the string builder that we're using to build the JSON string.
	 * @param row the row to append.
	 * @param headerNames the array of column headers.
	 */
    private void appendJsonRow(StringBuilder builder, JSONObject row, JSONArray headerNames) {
        builder.append("{\"id\":" + row.get("id"));
        JSONArray values = row.get("values").isArray();
        for (int i = 0; i < values.size(); i++) {
            builder.append(",\"" + getColumnName(headerNames, i) + "\":" + values.get(i).toString());
        }
        builder.append("}");
    }

    /**
     * Gets the column name for the given numbered column.  If a column with the give numbrer doesn't exist then a
     * generic name is created.
     *
     * @param headerNames the array of column header names.
     * @param columnNumber the column number.
     * @return the column name.
     */
    private String getColumnName(JSONArray headerNames, int columnNumber) {
        String columnName = trim(headerNames.get(columnNumber).toString());
        return columnName.equals("") ? makeUpEmptyHeader(columnNumber) : columnName;
    }

    /**
	 * Extract the trait table header names.
	 *
	 * @param header the trait table header.
	 * @return the array of header names.
	 */
    private JSONArray getHeaderNames(JSONValue header) {
        JSONArray headerArray =  header.isArray();
		JSONArray headerNames = new JSONArray();
		for (int j=0;j<headerArray.size();j++) {
			JSONValue val = headerArray.get(j);
			JSONObject obj = val.isObject();
			headerNames.set(j, obj.get("label"));
		}
        return headerNames;
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
