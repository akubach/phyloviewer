package org.iplantc.iptol.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

public class ServiceHelper {

	public static String buildWebQuery(Map<String, String> params)
			throws Exception {

		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			String key = URLEncoder.encode(entry.getKey(), "UTF-8");
			String value = URLEncoder.encode(entry.getValue(), "UTF-8");
			sb.append(key).append("=").append(value).append("&");
		}

		return sb.toString().substring(0, sb.length() - 1);

	}

	public static String call(String address, Map<String, String> params)
			throws Exception {

		String ret = "";
		String query = null;

		if (params != null) {
			query = buildWebQuery(params);
		}

		URL url = new URL(address);

		// make post mode connection
		URLConnection urlc = url.openConnection();

		urlc.setDoOutput(true);

		// send query
		PrintStream ps = new PrintStream(urlc.getOutputStream());
		ps.print(query);
		ps.close();

		// retrieve result
		BufferedReader br = new BufferedReader(new InputStreamReader(urlc
				.getInputStream(), "UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line;

		while ((line = br.readLine()) != null) {
			sb.append(line);
			sb.append("\n");
		}

		br.close();
		ret = sb.toString();

		return ret;
	}

}
