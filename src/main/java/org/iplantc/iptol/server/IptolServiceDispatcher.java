package org.iplantc.iptol.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.iplantc.iptol.client.IptolService;
import org.iplantc.iptol.client.services.ServiceCallWrapper;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class IptolServiceDispatcher extends RemoteServiceServlet implements
		IptolService 
{
	private static final long serialVersionUID = 5625374046154309665L;

	private String retrieveResult(URLConnection urlc) throws UnsupportedEncodingException, IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream(),"UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line;

		while ((line = br.readLine()) != null) 
		{
			sb.append(line);
		}

		br.close();
		
		return sb.toString();
	}
	
	private String get(String address) throws IOException  
	{
		URL url = new URL(address);

		//make post mode connection
		URLConnection urlc = url.openConnection();
		urlc.setDoOutput(true);
			
		return retrieveResult(urlc);	
	}

	private String update(String address,String body,String requestMethod) throws IOException  
	{
		URL url = new URL(address);

		// make post mode connection
		HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
		urlc.setRequestMethod(requestMethod);
		urlc.setDoOutput(true);
		
		// send post
		OutputStreamWriter outRemote = new OutputStreamWriter(urlc.getOutputStream());
		outRemote.write(body);
		outRemote.flush();
		outRemote.close();

		String res = retrieveResult(urlc);
		urlc.disconnect();
		
		return res;		
	}
	
	private String getContentType(String boundary) 
	{
		return "multipart/form-data; boundary=" + boundary;
	}
	
	private String buildBoundary()
	{
		return "--------------------" + Long.toString(System.currentTimeMillis(), 16);
	}
	
	private String updateMultipart(String address,String body,String disposition) throws IOException  
	{
		URL url = new URL(address);

		String boundary = buildBoundary();
		
		// make post mode connection
		HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
		urlc.setRequestProperty("Content-Type",getContentType(boundary));
		urlc.setRequestMethod("POST");
		urlc.setDoOutput(true);
		
		//we now need to add the prefix to the boundary prior to writing it out
		boundary = "--" + boundary;
		
		// send post
		DataOutputStream outRemote = new DataOutputStream(urlc.getOutputStream());
		outRemote.writeBytes(boundary);
		outRemote.writeBytes("\n");
		outRemote.writeBytes("Content-Disposition: form-data; " + disposition);
		outRemote.writeBytes("\r\n\r\n");
		outRemote.writeBytes(body);
		outRemote.writeBytes(boundary  + "--\r\n");
		outRemote.flush();
		outRemote.close();

		String res = retrieveResult(urlc);
		urlc.disconnect();
		
		return res;		
	}
	
	private String delete(String address) throws IOException 
	{
		URL url = new URL(address);

		// make post mode connection
		HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
		
		urlc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		urlc.setRequestMethod("DELETE");
		urlc.setDoOutput(true);
		urlc.connect();

		String res = retrieveResult(urlc);
		urlc.disconnect();
		
		return res;
	}

	private boolean isValidString(String in)
	{
		return (in != null && in.length() > 0);
	}
	
	private boolean isValidServiceCall(ServiceCallWrapper wrapper)
	{
		boolean ret = false;  //assume failure
		
		if(wrapper != null)
		{			
			if(isValidString(wrapper.getAddress()))
			{
				switch(wrapper.getType())
				{
					case GET:
					case DELETE:
						ret = true;
						break;
						
					case PUT:	
					case POST:
						if(isValidString(wrapper.getBody()))
						{
							ret = true;
						}
						break;
					
					case POST_MULTIPART:
						if(isValidString(wrapper.getBody()) && isValidString(wrapper.getDisposition()))
						{
							ret = true;
						}
					
					default:
						break;
				}				
			}
		}
		
		return ret;
	}
	
	/**
	 * Implements entry point for service dispatcher
	 * @param wrapper
	 * @return
	 */
	@Override
	public String getServiceData(ServiceCallWrapper wrapper) 
	{
		String json = null;
		
		if(isValidServiceCall(wrapper))
		{
			String address = wrapper.getAddress();
			String body = wrapper.getBody();		
			String disposition = wrapper.getDisposition();
			
			try 
			{
				switch(wrapper.getType())
				{
					case GET:
						json = get(address);
						break;
					
					case PUT:
						json = update(address,body,"PUT");
						break;
						
					case POST:
						json = update(address,body,"POST");
						break;
						
					case POST_MULTIPART:
						json = updateMultipart(address,body,disposition);
						break;
						
					case DELETE:
						json = delete(address);
						break;
						
					default:
						break;
				}				
			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("json==>" + json);
		return json;
	}
}
