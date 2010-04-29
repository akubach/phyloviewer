package org.iplantc.iptol.client;

import java.util.List;

import org.iplantc.iptol.client.views.widgets.portlets.panels.TraitDataJsonGen;
import org.iplantc.iptol.client.views.widgets.portlets.panels.TraitDataJsonParser;

import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.JsonLoadResultReader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.MemoryProxy;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;

public class GwtTestTraitDataJsonGen extends GWTTestCase {

	private TraitDataJsonGen gen;
	private String data = "{\"data\":[{\"id\":\"1067\",\"values\":[\"Ursus_mari\",\"2.423245872\",\"2.062957834\"]},{\"id\":\"1085\",\"values\":[\"Ursus_arct\",\"2.400192489\",\"1.918030337\"]}],\"headers\":[{\"id\":\"0\",\"label\":\"Species\"},{\"id\":\"1088\",\"label\":\"1\"},{\"id\":\"1148\",\"label\":\"2\"}]}";
	String header = "[{\"id\":\"0\", \"label\":\"Species\"},{\"id\":\"1088\", \"label\":\"1\"},{\"id\":\"1148\", \"label\":\"2\"}]";
	@Override
	public String getModuleName() {
		return "org.iplantc.iptol.discoveryenvironment";
	}

	public void testNullData() {
		JSONValue val = JSONParser.parse(header);
		gen = new TraitDataJsonGen(null, val.isArray());
		assertNull(gen.generateJson());
	}

	public void testNullHeader() {
		gen = new TraitDataJsonGen(mockData(),null);
		assertNull(gen.generateJson());
	}
	
	public void testNull() {
		gen = new TraitDataJsonGen(null,null);
		assertNull(gen.generateJson());
	}
	
	public void testgenerateJson() {
		JSONValue val = JSONParser.parse(header);
		gen = new TraitDataJsonGen(mockData(), val.isArray());
		assertNotNull(gen.generateJson());
	}
	
	private List<ModelData> mockData() {
		TraitDataJsonParser parser = new TraitDataJsonParser(data);
		parser.parseRoot();
	
		JSONArray headers = parser.getHeader().isArray();
		// create modeltype
		ModelType type = new ModelType();
		type.setRoot("data");
		type.addField("id", "id");

		TextField<String> text = new TextField<String>();
		text.setAllowBlank(false);
		CellEditor editor = new CellEditor(text);

		for (int i = 0; i < headers.size(); i++) {
			JSONValue val = headers.get(i);
			JSONObject obj = val.isObject();
			if(TraitDataJsonParser.trim(obj.get("label").toString()).equals("")) {
				type.addField(TraitDataJsonParser.trim(TraitDataJsonParser.trim(obj
						.get("id").toString())),TraitDataJsonParser.makeUpEmptyHeader(i));
						
			} else {
				type.addField(TraitDataJsonParser.trim(TraitDataJsonParser.trim(obj
						.get("id").toString())),TraitDataJsonParser.trim(obj.get("label").toString()));
			}
		}

		JsonLoadResultReader<ListLoadResult<ModelData>> reader = new JsonLoadResultReader<ListLoadResult<ModelData>>(type);  
		MemoryProxy<ModelData> proxy = new MemoryProxy<ModelData>(reader.read(null, parser.getData().toString()));
		final BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy);
		ListStore<ModelData> store = new ListStore<ModelData>(loader);
		loader.load();
		return store.getModels();
	}
}
