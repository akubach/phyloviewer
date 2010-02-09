package org.iplantc.iptol.server;

import java.util.ArrayList;
import java.util.List;

/**
 * The MatrixData contains the headers and data values within a
 * matrix (trait table).  The headers are column titles, e.g. the
 * names of the traits.
 *
 * This class is a simple POJO so that it can be easily converted
 * to a JSON string to return to a client via a Restful service.
 * @author Donald A. Barre
 */
public class MatrixData {

	private List<MatrixHeader> headers = new ArrayList<MatrixHeader>();
	private List<MatrixRow> data = new ArrayList<MatrixRow>();

	public List<MatrixHeader> getHeaders() {
		return headers;
	}

	public void setHeaders(List<MatrixHeader> headers) {
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
			if (rowValue == null) {
				matrixRow.addValue(null);
			}
			else {
			    matrixRow.addValue(rowValue.toString());
			}
		}
		data.add(matrixRow);
	}
}
