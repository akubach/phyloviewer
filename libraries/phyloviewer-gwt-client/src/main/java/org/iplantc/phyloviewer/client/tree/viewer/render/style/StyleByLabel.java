package org.iplantc.phyloviewer.client.tree.viewer.render.style;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloviewer.shared.parsers.CSVParser;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.Defaults;
import org.iplantc.phyloviewer.shared.render.style.CompositeStyle;
import org.iplantc.phyloviewer.shared.render.style.IBranchStyle;
import org.iplantc.phyloviewer.shared.render.style.IGlyphStyle;
import org.iplantc.phyloviewer.shared.render.style.ILabelStyle;
import org.iplantc.phyloviewer.shared.render.style.INodeStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.Style;

/**
 * Stores node styles indexed by label
 */
public class StyleByLabel implements IStyleMap
{
	HashMap<String, IStyle> map = new HashMap<String,IStyle>();

	@Override
	public IStyle get(INode node)
	{
		return map.get(node.getLabel());
	}

	@Override
	public void put(INode node, IStyle style)
	{
		map.put(node.getLabel(), style);
	}

	@Override
	public void clear()
	{
		map.clear();
	}
	
	/**
	 * Adds style mappings based on a string of comma-separated values.
	 * <pre>
	 * 1st column: node label
	 * 2nd column: element (one of NODE, BRANCH, GLYPH, LABEL)
	 * 3rd column: feature (one of STROKE, FILL, WIDTH)
	 * 4th column: value
	 * 		FILL and STROKE values should be a color as defined in http://dev.w3.org/html5/canvas-api/canvas-2d-api.html#serialization-of-a-color
	 * 		WIDTH values should be a positive number
	 * 		any value that contains a comma should be quoted
	 * </pre>
	 * @see Element
	 * @see Feature
	 */
	public void put(String csv)
	{
		if (csv == null || csv.isEmpty())
		{
			clear();
			return;
		}
		
		List<String[]> rows = new ArrayList<String[]>();
		CSVParser parser = new CSVParser();
		String[] lines = csv.split("\n");
		
		try
		{
			for (String line : lines)
			{
				rows.add(parser.parseLine(line));
			}
		}
		catch(IOException e1)
		{
			throw new IllegalArgumentException("Unable to parse CSV string.", e1);
		}
		
		for (int i = 0; i < rows.size(); i++)
		{
			
			String[] row = rows.get(i);
			IStyle currentNodeStyle = lazyGet(row[0]);
			
			try
			{
				this.setElementStyle(currentNodeStyle, row[1], row[2], row[3]);
			}
			catch(NumberFormatException e)
			{
				Logger.getLogger("").log(Level.WARNING, "NumberFormatException in line " + i, e);
			}
			catch(IllegalArgumentException e)
			{
				Logger.getLogger("").log(Level.WARNING, "Bad element or feature name in line " + i, e);
			}

		}
	}
	
	private void setElementStyle(IStyle style,String type,String feature,String value) {
		if(type.equalsIgnoreCase("node")) 
		{
			this.setNodeStyleValue(style.getNodeStyle(),feature,value);
		}
		else if(type.equalsIgnoreCase("branch")) 
		{
			this.setBranchStyleValue(style.getBranchStyle(),feature,value);
		}
		else if(type.equalsIgnoreCase("glyph")) 
		{
			this.setGlyphStyleValue(style.getGlyphStyle(),feature,value);
		}
		else if(type.equalsIgnoreCase("label")) 
		{
			this.setLabelStyleValue(style.getLabelStyle(),feature,value);
		}
	}
	
	private void setLabelStyleValue(ILabelStyle labelStyle, String feature, String value) {
		if(feature.equalsIgnoreCase("fill") || feature.equalsIgnoreCase("stroke") || feature.equalsIgnoreCase("color")) {
			//TODO check if value is a valid color string
			labelStyle.setColor(value);
		}
	}

	private void setGlyphStyleValue(IGlyphStyle glyphStyle, String feature, String value) {
		if(feature.equalsIgnoreCase("stroke")) {
			//TODO check if value is a valid color string
			glyphStyle.setStrokeColor(value);
		}
		else if(feature.equalsIgnoreCase("fill")) {
			//TODO check if value is a valid color string
			glyphStyle.setFillColor(value);
		}
		else if(feature.equalsIgnoreCase("width")) {
			double width = Double.parseDouble(value);
			glyphStyle.setLineWidth(width);
		}
	}

	private void setBranchStyleValue(IBranchStyle branchStyle, String feature, String value) {
		if(feature.equalsIgnoreCase("stroke")) {
			//TODO check if value is a valid color string
			branchStyle.setStrokeColor(value);
		}
		else if(feature.equalsIgnoreCase("width")) {
			double width = Double.parseDouble(value);
			branchStyle.setLineWidth(width);
		}
	}

	private void setNodeStyleValue(INodeStyle nodeStyle,String feature,String value) {
		if(feature.equalsIgnoreCase("fill") || feature.equalsIgnoreCase("stroke") || feature.equalsIgnoreCase("color")) {
			//TODO check if value is a valid color string
			nodeStyle.setColor(value);
		}
		else if(feature.equalsIgnoreCase("width")) {
			double size = Double.parseDouble(value);
			nodeStyle.setPointSize(size);
		}
	}
	
	private IStyle lazyGet(String label)
	{
		IStyle style = map.get(label);
		if (style == null)
		{
			style = new CompositeStyle(label, Defaults.DEFAULT_STYLE);
			map.put(label, style);
		}
		
		return style;
	}
}
