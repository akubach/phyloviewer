package org.iplantc.iptol.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.iplantc.iptol.client.IptolService;
import org.iplantc.iptol.client.ServiceCallWrapper;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class IptolServiceDispatcher extends RemoteServiceServlet implements
		IptolService 
{
	private static final long serialVersionUID = 5625374046154309665L;

	/**
	 * Retrieve results from a service
	 * @param urlc
	 * @return
	 */
	private String retrieveResult(URLConnection urlc) throws UnsupportedEncodingException, IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream(),"UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line;

		while ((line = br.readLine()) != null) 
		{
			sb.append(line);
			sb.append("\n");
		}

		br.close();
		return sb.toString();
	}
	
	/**
	 * Implements GET
	 * @param address
	 * @return
	 */
	private String get(String address) throws IOException  
	{
		URL url = new URL(address);

		//make post mode connection
		URLConnection urlc = url.openConnection();
		urlc.setDoOutput(true);
			
		return retrieveResult(urlc);	
	}

	/**
	 * Implements POST and PUT
	 * @param address
	 * @param body
	 * @param requestMethod
	 * @return
	 */
	private String update(String address,String body,String requestMethod) throws IOException  
	{
		URL url = new URL(address);

		// make post mode connection
		HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
		urlc.setDoOutput(true);
		urlc.setRequestMethod(requestMethod);

		// send post
		OutputStreamWriter outRemote = new OutputStreamWriter(urlc.getOutputStream());
		outRemote.write(body);
		outRemote.flush();

		return retrieveResult(urlc);		
	}
	
	/**
	 * Implements DELETE
	 * @param address
	 * @return
	 */
	private String delete(String address) throws IOException 
	{
		URL url = new URL(address);

		// make post mode connection
		HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
		urlc.setDoOutput(true);
		urlc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		urlc.setRequestMethod("DELETE");
		urlc.connect();

		return retrieveResult(urlc);
	}

	/**
	 * Determines validity of service call wrapper
	 * @param wrapper
	 * @return
	 */
	private boolean isValidServiceCall(ServiceCallWrapper wrapper)
	{
		boolean ret = false;  //assume failure
		
		if(wrapper != null)
		{
			String test = wrapper.getAddress();
			
			if(test != null && test.length() > 0)
			{
				switch(wrapper.getType())
				{
					case GET:
					case DELETE:
						ret = true;
						break;
					
					case PUT:	
					case POST:
						test = wrapper.getBody();
						if(test != null && test.length() > 0)
						{
							ret = true;
						}
						break;
						
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
