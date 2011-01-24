package org.iplantc.phyloviewer.model;

import java.util.Iterator;
import java.util.Set;

import org.iplantc.phyloviewer.shared.layout.LayoutCladogram;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.Node;
import org.iplantc.phyloviewer.shared.model.Tree;
import org.iplantc.phyloviewer.shared.render.style.IBranchStyle;
import org.iplantc.phyloviewer.shared.render.style.IGlyphStyle;
import org.iplantc.phyloviewer.shared.render.style.ILabelStyle;
import org.iplantc.phyloviewer.shared.render.style.INodeStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.StyleById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConvertToJSON
{
	static JSONObject buildJSON(INode node) throws JSONException
	{
		JSONObject object = new JSONObject();
		object.put("id", node.getId());

		object.put("name", node.getLabel());
		object.put("branchLength", node.getBranchLength());

		INode[] myChildren = node.getChildren();
		if(myChildren != null)
		{
			JSONArray children = new JSONArray();
			for(INode child : myChildren)
			{
				children.put(buildJSON(child));
			}
			object.put("children", children);
		}

		return object;
	}

	static JSONObject buildJSON(IBranchStyle style) throws JSONException
	{
		JSONObject object = new JSONObject();
		object.put("strokeColor", style.getStrokeColor());
		object.put("lineWidth", style.getLineWidth());
		return object;
	}

	static JSONObject buildJSON(IGlyphStyle style) throws JSONException
	{
		JSONObject object = new JSONObject();
		object.put("strokeColor", style.getStrokeColor());
		object.put("fillColor", style.getFillColor());
		object.put("lineWidth", style.getLineWidth());
		return object;
	}

	static JSONObject buildJSON(ILabelStyle style) throws JSONException
	{
		JSONObject object = new JSONObject();
		object.put("color", style.getColor());
		return object;
	}

	static JSONObject buildJSON(INodeStyle style) throws JSONException
	{
		JSONObject object = new JSONObject();
		object.put("color", style.getColor());
		object.put("pointSize", style.getPointSize());
		return object;
	}

	static JSONObject buildJSON(IStyle style) throws JSONException
	{
		JSONObject object = new JSONObject();
		object.put("branchStyle", buildJSON(style.getBranchStyle()));
		object.put("glyphStyle", buildJSON(style.getGlyphStyle()));
		object.put("labelStyle", buildJSON(style.getLabelStyle()));
		object.put("nodeStyle", buildJSON(style.getNodeStyle()));
		return object;
	}

	static JSONObject buildJSON(StyleById styleMap) throws JSONException
	{
		JSONObject object = new JSONObject();

		Set<String> keys = styleMap.getKeys();
		Iterator<String> iterator = keys.iterator();
		while(iterator.hasNext())
		{
			String key = iterator.next();
			object.put(key, buildJSON(styleMap.get(key)));
		}

		return object;
	}

	public static JSONObject buildJSON(Tree tree) throws JSONException
	{
		JSONObject object = new JSONObject();
		object.put("root", buildJSON(tree.getRootNode()));
		object.put("id", tree.getId());
		return object;
	}

	/**
	 * Convert node to JSON
	 * 
	 * @param Node to convert
	 * @return JSON string
	 */
	public static String getJSON(Node node)
	{
		try
		{
			return buildJSON(node).toString();
		}
		catch(JSONException e)
		{
		}

		return "{}";
	}

	public static String getJSON(Tree tree)
	{
		try
		{
			JSONObject object = buildJSON(tree);
			return object.toString();
		}
		catch(JSONException e)
		{
		}

		return "{}";
	}

	public static String getJSON(StyleById styleMap)
	{
		try
		{
			return buildJSON(styleMap).toString();
		}
		catch(JSONException e)
		{
		}

		return "{}";
	}

	public static JSONObject buildJSON(Vector2 vector) throws JSONException
	{
		JSONObject object = new JSONObject();
		object.put("x", vector.getX());
		object.put("y", vector.getY());
		return object;
	}

	public static JSONObject buildJSON(Box2D box) throws JSONException
	{
		JSONObject object = new JSONObject();
		object.put("min", buildJSON(box.getMin()));
		object.put("max", buildJSON(box.getMax()));
		return object;
	}

	public static JSONObject buildJSON(LayoutCladogram layout) throws JSONException
	{
		JSONObject object = new JSONObject();

		JSONObject bounds = new JSONObject();
		JSONObject positions = new JSONObject();

		Set<Integer> keys = layout.keySet();
		Iterator<Integer> iter = keys.iterator();
		while(iter.hasNext())
		{
			Integer key = iter.next();
			Vector2 position = layout.getPosition(key);
			Box2D box = layout.getBoundingBox(key);
			String stringKey = key.toString();

			positions.put(stringKey, buildJSON(position));
			bounds.put(stringKey, buildJSON(box));
		}

		object.put("bounds", bounds);
		object.put("positions", positions);

		return object;
	}

	public static String getJSON(LayoutCladogram layout)
	{
		try
		{
			return buildJSON(layout).toString();
		}
		catch(JSONException e)
		{
		}

		return "{}";
	}
}
