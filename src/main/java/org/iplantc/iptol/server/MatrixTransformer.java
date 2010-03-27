package org.iplantc.iptol.server;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.exporttree.exception.TransformException;
import org.iplantc.treedata.model.Matrix;
import org.iplantc.treedata.model.Thing;
import org.iplantc.treedata.info.MatrixData;
import org.iplantc.treedata.info.MatrixHeader;

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

		if (matrix == null) {
			throw new TransformException("Matrix is null");
		}

		MatrixData matrixData = new MatrixData();
		matrixData.setHeaders(getHeaders(matrix));

		List<Thing> taxons = matrix.getTaxons();
		for (Thing taxon : taxons) {
			List<Object> values = matrix.getValues(taxon);
			matrixData.addRowData(taxon.getId(), taxon.getName(), values);
		}

		return matrixData;
	}

	/**
	 * Get the Headers of the Trait Table.
	 * @param matrix
	 * @return the headers
	 */
	private List<MatrixHeader> getHeaders(Matrix matrix) {
		List<MatrixHeader> headers = new ArrayList<MatrixHeader>();
		if (!matrix.getTaxons().isEmpty()) {
			headers.add(new MatrixHeader(0L, "Species"));
			for (Thing character : matrix.getCharacters()) {
				headers.add(new MatrixHeader(character.getId(), character.getName()));
			}
		}
		return headers;
	}

	/**
	* This transform is only to handle exceptions.  The issue pertains to the calling
	 * of the tree transform service when there is an exception in the payload encoded as
	 * a string.  When that happens, we don't have a tree to transform.  By including this
	 * simple method, we are simply returning the exception string to the client.  If we
	 * don't have this method, Mule will throw a new exception because there won't be a
	 * matching method to call based upon the payload type.
	 * @param exception the encoded exception
	 * @return the same exception
	 */
	public String transform(String exception) {
		return exception;
	}
}
