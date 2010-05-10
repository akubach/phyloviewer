package org.iplantc.de.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.iplantc.phyloviewer.shared.DEService;
import org.iplantc.phyloviewer.shared.HTTPPart;
import org.iplantc.phyloviewer.shared.MultiPartServiceWrapper;
import org.iplantc.phyloviewer.shared.ServiceCallWrapper;
import org.iplantc.saml.Saml2Exception;
import org.iplantc.security.SecurityConstants;
import org.opensaml.xml.io.MarshallingException;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class DEServiceDispatcher extends RemoteServiceServlet implements DEService
{
	private static final long serialVersionUID = 5625374046154309665L;

	private static final Logger logger = Logger.getLogger(DEServiceDispatcher.class);

	// TODO: make these configurable.
	private static final String KEYSTORE_PATH = "WEB-INF/classes/keystore.jceks";
	private static final String KEYSTORE_TYPE = "JCEKS";
	private static final String KEYSTORE_PASSWORD = "changeit";
	private static final String SIGNING_KEY_ALIAS = "signing";
	private static final String SIGNING_KEY_PASSWORD = "changeit";
	private static final String ENCRYPTING_KEY_ALIAS = "encrypting";

	/**
	 * The certificate used to sign SAML assertions.
	 */
	private X509Certificate signingCertificate = null;

	/**
	 * The key used to sign SAML assertions.
	 */
	private PrivateKey signingKey = null;

	/**
	 * The key used to encrypt SAML assertions.
	 */
	private PublicKey encryptingKey = null;

	/**
	 * The servlet context to use when looking up the keystore path.
	 */
	private ServletContext context = null;

	/**
	 * The servlet request to use when building the SAML assertion.
	 */
	private HttpServletRequest request = null;

	private URLConnection myUrlc;

	public URLConnection getURLConnection()
	{
		return myUrlc;
	}

	/**
	 * Sets the servlet context to use when looking up the keystore path.
	 * 
	 * @param context the context.
	 */
	public void setContext(ServletContext context)
	{
		this.context = context;
	}

	/**
	 * Gets the servlet context to use when looking up the keystore path.
	 */
	public ServletContext getContext()
	{
		return context == null ? getServletContext() : context;
	}

	/**
	 * Sets the servlet request to use when building the SAML assertion.
	 * 
	 * @param request the request to use.
	 */
	public void setRequest(HttpServletRequest request)
	{
		this.request = request;
	}

	/**
	 * Gets the servlet request to use when building the SAML assertion.
	 * 
	 * @return the request to use.
	 */
	public HttpServletRequest getRequest()
	{
		return request == null ? getThreadLocalRequest() : request;
	}

	/**
	 * Loads the signing and encrypting keys and certificates.
	 * 
	 * @throws IOException if the keystore can't be loaded.
	 * @throws GeneralSecurityException if the keys and certificates can't be loaded.
	 */
	private void loadKeys() throws IOException, GeneralSecurityException
	{
		logger.debug("inside loadKeys");
		if(signingCertificate == null)
		{
			logger.debug("laoding the keystore");
			File fullKeystorePath = new File(getContext().getRealPath(KEYSTORE_PATH));
			KeyLoader keyLoader = new KeyLoader(fullKeystorePath, KEYSTORE_TYPE, KEYSTORE_PASSWORD);
			signingCertificate = keyLoader.getCertificate(SIGNING_KEY_ALIAS);
			signingKey = keyLoader.getPrivateKey(SIGNING_KEY_ALIAS, SIGNING_KEY_PASSWORD);
			encryptingKey = keyLoader.getCertificate(ENCRYPTING_KEY_ALIAS).getPublicKey();
		}
	}

	private String retrieveResult(URLConnection urlc) throws UnsupportedEncodingException, IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream(), "UTF-8"));
		StringBuffer buffer = new StringBuffer();

		while(true)
		{
			int ch = br.read();

			if(ch < 0)
			{
				break;
			}

			buffer.append((char)ch);
		}

		br.close();

		return buffer.toString();
	}

	/**
	 * Obtains an authenticated or unauthenticated URL connection, depending on whether or
	 * not the receiving service requires us to authenticate.
	 * 
	 * TODO: make this more portable.
	 * 
	 * @param address the address to connect to.
	 * @return the URL connection.
	 * @throws IOException if the connection can't be established.
	 */
	private HttpURLConnection getUrlConnection(String address) throws IOException
	{
		if(!isSecurityEnabled())
		{
			return getUnauthenticatedUrlConnection(address);
		}
		else
		{
			return address.contains("genji") ? getUnauthenticatedUrlConnection(address)
					: getAuthenticatedUrlConnection(address);
		}
	}

	/**
	 * Obtains an unauthenticated URL connection.
	 * 
	 * @param address the address to connect to.
	 * @return the URL connection.
	 * @throws IOException if the connection can't be established.
	 */
	private HttpURLConnection getUnauthenticatedUrlConnection(String address) throws IOException
	{
		return (HttpURLConnection)new URL(address).openConnection();
	}

	/**
	 * Creates a URL connection with a SAML assertion in the custom header defined by
	 * SecurityConstants.ASSERTION_HEADER.
	 * 
	 * @param address the address to connect to.
	 * @return the new URL connection.
	 * @throws IOException if the connection can't be established or the assertion can't
	 *             be built.
	 */
	private HttpURLConnection getAuthenticatedUrlConnection(String address) throws IOException
	{
		try
		{
			URL url = new URL(address);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestProperty(SecurityConstants.ASSERTION_HEADER, buildSamlAssertion());
			return connection;
		}
		catch(Saml2Exception e)
		{
			String msg = "unable to build the SAML assertion";
			logger.debug(msg, e);
			throw new IOException(msg, e);
		}
		catch(MarshallingException e)
		{
			String msg = "unable to marshall the SAML assertion";
			logger.debug(msg, e);
			throw new IOException(msg, e);
		}
		catch(IOException e)
		{
			String msg = "unable to build assertion or set request property";
			logger.debug(msg, e);
			throw new IOException(msg, e);
		}
		catch(Throwable e)
		{
			String msg = "severe error";
			logger.warn(msg, e);
			throw new IOException(msg, e);
		}
	}

	/**
	 * Builds, signs, encrypts and encodes a SAML assertion.
	 * 
	 * @return the SAML assertion.
	 * @throws Saml2Exception if the assertion can't be built, signed or encrypted.
	 * @throws MarshallingException if the assertion can't be converted to XML.
	 * @throws IOException if any of the cryptography keys can't be loaded.
	 */
	private String buildSamlAssertion() throws Saml2Exception, MarshallingException, IOException
	{
		logger.debug("building the SAML assertion");
		try
		{
			loadKeys();
			HttpServletRequest request = getRequest();
			return AssertionHelper.createEncodedAssertion(request, signingCertificate, signingKey,
					encryptingKey);
		}
		catch(GeneralSecurityException e)
		{
			String msg = "unable to load the encryption keys";
			logger.debug(msg, e);
			throw new IOException(msg, e);
		}
	}

	private String get(String address) throws IOException
	{
		if(logger.isDebugEnabled())
		{
			logger.debug("sending a GET request to " + address);
		}

		// make post mode connection
		URLConnection urlc = getUrlConnection(address);
		urlc.setDoOutput(true);
		this.myUrlc = urlc;

		logger.debug("GET request sent");

		String result = retrieveResult(urlc);

		logger.debug("response to GET request received");

		return result;
	}

	private String update(String address, String body, String requestMethod) throws IOException
	{
		if(logger.isDebugEnabled())
		{
			logger.debug("sending an UPDATE request to " + address);
		}

		// make post mode connection
		HttpURLConnection urlc = getUrlConnection(address);
		urlc.setRequestMethod(requestMethod);
		urlc.setDoOutput(true);

		// send post
		OutputStreamWriter outRemote = new OutputStreamWriter(urlc.getOutputStream());
		outRemote.write(body);
		outRemote.flush();
		outRemote.close();

		logger.debug("UPDATE request sent");

		String res = retrieveResult(urlc);
		urlc.disconnect();

		logger.debug("response to UPDATE request received");

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

	private String updateMultipart(String address, List<HTTPPart> parts, String requestMethod)
			throws IOException
	{
		if(logger.isDebugEnabled())
		{
			logger.debug("sending a multipart UPDATE request to " + address);
		}

		String boundary = buildBoundary();

		// make post mode connection
		HttpURLConnection urlc = getUrlConnection(address);
		urlc.setRequestProperty("content-type", getContentType(boundary));
		urlc.setRequestMethod(requestMethod);
		urlc.setDoOutput(true);

		// send post
		DataOutputStream outRemote = new DataOutputStream(urlc.getOutputStream());

		for(HTTPPart part : parts)
		{
			outRemote.writeBytes("--" + boundary);
			outRemote.writeBytes("\n");
			outRemote.writeBytes("Content-Disposition: form-data; " + part.getDisposition());
			outRemote.writeBytes("\r\n\r\n");
			outRemote.writeBytes(part.getBody());
			outRemote.writeBytes("\r\n--" + boundary + "--\r\n");
		}

		outRemote.flush();
		outRemote.close();

		logger.debug("multipart UPDATE request sent");

		String res = retrieveResult(urlc);
		urlc.disconnect();

		logger.debug("response to multipart UPDATE request received");

		return res;
	}

	private String delete(String address) throws IOException
	{
		// make post mode connection
		HttpURLConnection urlc = getUrlConnection(address);

		urlc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
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
		boolean ret = false; // assume failure

		if(wrapper != null)
		{
			if(isValidString(wrapper.getAddress()))
			{
				switch (wrapper.getType())
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

				default:
					break;
				}
			}
		}

		return ret;
	}

	private boolean isValidServiceCall(MultiPartServiceWrapper wrapper)
	{
		boolean ret = false; // assume failure

		if(wrapper != null)
		{
			if(isValidString(wrapper.getAddress()))
			{
				switch (wrapper.getType())
				{
				case PUT:
				case POST:
					if(wrapper.getNumParts() > 0)
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
	 * 
	 * @param wrapper
	 * @return
	 */
	@Override
	public String getServiceData(ServiceCallWrapper wrapper) throws SerializationException
	{
		String json = null;

		if(isValidServiceCall(wrapper))
		{
			String address = wrapper.getAddress();
			String body = wrapper.getBody();

			try
			{
				switch (wrapper.getType())
				{
				case GET:
					json = get(address);
					break;

				case PUT:
					json = update(address, body, "PUT");
					break;

				case POST:
					json = update(address, body, "POST");
					break;

				case DELETE:
					json = delete(address);
					break;

				default:
					break;
				}
			}
			catch(Exception ex)
			{
				// because the GWT compiler will issue a warning if we simply
				// throw exception, we'll
				// use SerializationException()
				throw new SerializationException(ex);
			}
		}

		logger.debug("json==>" + json);
		System.out.println("json==>" + json);
		return json;
	}

	@Override
	public String getServiceData(MultiPartServiceWrapper wrapper) throws SerializationException
	{
		String json = null;

		if(isValidServiceCall(wrapper))
		{
			String address = wrapper.getAddress();
			List<HTTPPart> parts = wrapper.getParts();

			try
			{
				switch (wrapper.getType())
				{
				case PUT:
					json = updateMultipart(address, parts, "PUT");
					break;

				case POST:
					json = updateMultipart(address, parts, "POST");
					break;

				default:
					break;
				}
			}
			catch(Exception ex)
			{
				// because the GWT compiler will issue a warning if we simply
				// throw exception, we'll
				// use SerializationException()
				throw new SerializationException(ex);
			}
		}

		System.out.println("json==>" + json);
		return json;
	}

	/**
	 * Is security enabled?
	 * 
	 * @return
	 */
	private boolean isSecurityEnabled()
	{
		return DiscoveryEnvironmentProperties.isWebSecurityEnabled();
	}
}
