package org.iplantc.iptol.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractMessageAwareTransformer;

/**
 * When a file is uploaded, the HTTP body, i.e. payload, contains a string with the following
 * syntax:
 *
 *      filename={filename}&p;folderId={folderId}&f;{file contents}
 *
 * The folderId key/value pair is optional.  Essentially, the body contains a set of key/value
 * pairs separated by "&p;"  The "&f;" separates the key/value pairs from the file contents.
 * @author Donald A. Barre
 */
public class ParseUploadPayload extends AbstractMessageAwareTransformer {

	Logger LOG = Logger.getLogger(ParseUploadPayload.class);

	private static final String WORKSPACE_ID = "workspaceId";
	private static final String INVALID_ID = "-1";

	private static final String FILENAME_KEY = "filename";
	private static final String FOLDER_ID_KEY = "folderId";

	private static final String FILE_DELIMITER = "&f;";
	private static final String PARAM_DELIMITER = "&p;";

	/* (non-Javadoc)
	 * @see org.mule.transformer.AbstractMessageAwareTransformer#transform(org.mule.api.MuleMessage, java.lang.String)
	 */
	@Override
	public Object transform(MuleMessage message, String encoding)
			throws TransformerException {

		if (!(message.getPayload() instanceof String)) {
			throw new TransformerException(MessageFactory.createStaticMessage("Upload Payload is not a string"));
		}

		/*
		 * Split the payload into the parameters (key/value pairs) and the file contents.
		 */
		String payload = (String) message.getPayload();
		String split[] = payload.split(FILE_DELIMITER);
		if (split.length != 2) {
			throw new TransformerException(MessageFactory.createStaticMessage("Could not find " + FILE_DELIMITER + " delimiter"));
		}

		/*
		 * Split the parameters (key/value pairs) apart and store them in a map.
		 */
		Map<String, String> paramMap = getParamMap(split[0]);

		/*
		 * Get the filename from the parameter map.  This is a required field.
		 */
		String filename = paramMap.get(FILENAME_KEY);
		if (filename == null) {
			throw new TransformerException(MessageFactory.createStaticMessage("Filename is required"));
		}

		/*
		 * Get the folderId from the paramter map.  This is an optional field and so it might not be there.
		 */
		Long folderId = null;
		String strFolderId = paramMap.get(FOLDER_ID_KEY);
		if (strFolderId != null) {
			folderId = Long.valueOf(strFolderId);
		}

		/*
		 * Construct the new payload (file contents, filename, workspaceId, optional folderId).
		 */
		Object newPayload[] = new Object[folderId == null ? 3 : 4];
		newPayload[0] = split[1].trim();
		newPayload[1] = filename;
		newPayload[2] = Long.valueOf(message.getStringProperty(WORKSPACE_ID, INVALID_ID));
		if (folderId != null) {
			newPayload[3] = folderId;
		}

		return newPayload;
	}

	/**
	 * Create a parameter map by splitting apart the information in the given string.
	 * The set of parameters are key/value pairs separated by "&p;" delimiter.  If
	 * two delimiters are adjacent, we just ignore it.
	 * @param s
	 * @return
	 * @throws TransformerException
	 */
	private Map<String, String> getParamMap(String s) throws TransformerException {
		Map<String, String> paramMap = new HashMap<String, String>();
		String params[] = s.split(PARAM_DELIMITER);
		for (String param : params) {
			param = param.trim();
			if (param.length() > 0) {
				String split[] = param.split("=");
				if (split.length != 2) {
					throw new TransformerException(MessageFactory.createStaticMessage("Invalid param syntax: no equals"));
				}
				String key = split[0].trim();
				if (key.length() == 0) {
					throw new TransformerException(MessageFactory.createStaticMessage("Param key is null"));
				}
				if (!isValidKey(key)) {
					throw new TransformerException(MessageFactory.createStaticMessage("Param key is unknown"));
				}
				String value = split[1].trim();
				if (value.length() == 0) {
					throw new TransformerException(MessageFactory.createStaticMessage("Param value is null"));
				}
				paramMap.put(key, value);
			}
		}
		return paramMap;
	}

	/**
	 * Is the given key a valid key for a paramter?
	 * @param key
	 * @return true if valid, otherwise false
	 */
	private boolean isValidKey(String key) {
		return FILENAME_KEY.equals(key) || FOLDER_ID_KEY.equals(key);
	}
}
