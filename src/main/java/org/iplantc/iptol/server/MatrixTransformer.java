package org.iplantc.iptol.server;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.exporttree.exception.TransformException;
import org.iplantc.treedata.model.Matrix;
import org.iplantc.treedata.model.Thing;

/**
 * Transform a Matrix into a MatrixData that can then converted into a JSON string
 * to be used in Restful services.
 * @author Donald A. Barre
 */
public class MatrixTransformer {

	/**
	 * Transform the Matrix into the MatrixData.
	 * @param matrix
	 * @return the MatrixData
	 * @throws TransformException
	 */
	public MatrixData transform(Matrix matrix) throws TransformException {

		MatrixData matrixData = new MatrixData();
		matrixData.setHeaders(getHeaders(matrix));

		int rowIndex = 0;
		List<Thing> taxons = matrix.getTaxons();
		for (Thing taxon : taxons) {
			List<Object> values = matrix.getValues(rowIndex++);
			matrixData.addRowData(taxon.getId(), taxon.getName(), values);
		}

		return matrixData;
	}

	/**
	 * Get the Headers of the Trait Table.
	 * @param matrix
	 * @return the headers
	 */
	private List<String> getHeaders(Matrix matrix) {
		List<String> headers = new ArrayList<String>();
		headers.add("Species");
		for (Thing character : matrix.getCharacters()) {
			headers.add(character.getName());
		}
		return headers;
	}
}
