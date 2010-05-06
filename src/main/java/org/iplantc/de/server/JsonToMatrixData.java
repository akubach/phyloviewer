package org.iplantc.de.server;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.iplantc.treedata.info.MatrixData;
import org.iplantc.treedata.info.MatrixHeader;
import org.iplantc.treedata.info.MatrixRow;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

/**
 * Transform the JSON for a Matrix into a MatrixData bean.
 * 
 * @author Donald A. Barre
 */
public class JsonToMatrixData extends AbstractTransformer
{

	@SuppressWarnings( { "unchecked" })
	@Override
	protected Object doTransform(Object payload, String encoding) throws TransformerException
	{

		MatrixData matrixData = new MatrixData();

		String json = (String)payload;
		JSONObject jsonObject = JSONObject.fromObject(json);

		List<MatrixHeader> headers = new ArrayList<MatrixHeader>();
		List<JSONObject> jsonHeaders = jsonObject.getJSONArray("headers");
		for(JSONObject jsonHeader : jsonHeaders)
		{
			MatrixHeader header = (MatrixHeader)JSONObject.toBean(jsonHeader, MatrixHeader.class);
			headers.add(header);
		}
		matrixData.setHeaders(headers);

		List<MatrixRow> rows = new ArrayList<MatrixRow>();
		List<JSONObject> jsonRows = jsonObject.getJSONArray("data");
		for(JSONObject jsonRow : jsonRows)
		{
			MatrixRow row = (MatrixRow)JSONObject.toBean(jsonRow, MatrixRow.class);
			rows.add(row);
		}
		matrixData.setData(rows);

		return matrixData;
	}
}
