package org.iplantc.iptol.server;

import java.util.ArrayList;
import java.util.List;

/**
 * The MatrixData is simple pojo that can be easily converted
 * to a JSON string to return to a client via a Restful service.
 * @author Donald A. Barre
 */
public class MatrixData {

	private List<String> headers = new ArrayList<String>();

	private List<MatrixRow> data = new ArrayList<MatrixRow>();

	public List<String> getHeaders() {
		return headers;
	}

	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}

	public List<MatrixRow> getData() {
		return data;
	}

	public void addRowData(Long taxonId, String taxonName, List<Object> rowValues) {
		MatrixRow matrixRow = new MatrixRow();
		matrixRow.setId(taxonId);
		matrixRow.addValue(taxonName);
		for (Object rowValue : rowValues) {
			matrixRow.addValue(rowValue.toString());
		}
		data.add(matrixRow);
	}
}
