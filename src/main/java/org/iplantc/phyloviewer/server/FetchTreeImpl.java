/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.iplantc.phyloviewer.client.DemoTree;

public class FetchTreeImpl {

	public static String fetchTree(DemoTree tree, ServletContext context) {
		if(tree.data != null)
		{
			return tree.data;
		}
		else if (tree.localPath != null)
		{
			return readFile(new File(context.getRealPath(tree.localPath)));
		}
		else
		{
			try
			{
				return readURL(new URL(tree.url));
			}
			catch(MalformedURLException e)
			{
				e.printStackTrace();
			}
		}

		return "{}";
	}

	private static String readURL(URL url) {
		
		try {
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			
			connection.setDoOutput(true);

			InputStream in = connection.getInputStream();
			String res = readStream(in);
			connection.disconnect();
			
			return res;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	public static String readFile(File file)
	{
		try
		{
			InputStream in = new FileInputStream(file);
			String res = readStream(in);
			in.close();
			return res;
		}
		catch(FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "{}";
	}
	
	private static String readStream(InputStream in) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		StringBuffer buffer = new StringBuffer();

		String line;
		while((line = br.readLine()) != null)
		{
			buffer.append(line);
		}

		return buffer.toString();
	}
}
