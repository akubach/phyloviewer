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
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.iplantc.iptol.client.IptolService;
import org.iplantc.iptol.client.services.HTTPPart;
import org.iplantc.iptol.client.services.MultiPartServiceWrapper;
import org.iplantc.iptol.client.services.ServiceCallWrapper;
import org.iplantc.saml.Saml2Exception;
import org.iplantc.saml.util.AssertionHelper;
import org.iplantc.security.SecurityConstants;
import org.opensaml.xml.io.MarshallingException;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class IptolServiceDispatcher extends RemoteServiceServlet implements
        IptolService
{
    private static final long serialVersionUID = 5625374046154309665L;

    // TODO: make these configurable.
    private static final String KEYSTORE_PATH = "WEB-INF/keystore.jceks";
    private static final String KEYSTORE_TYPE = "JCEKS";
    private static final String KEYSTORE_PASSWORD = "changeit";
    private static final String SIGNING_KEY_ALIAS = "signing";
    private static final String SIGNING_KEY_PASSWORD = "changeit";
    private static final String ENCRYPTING_KEY_ALIAS = "encrypting";

    private String retrieveResult(URLConnection urlc) throws UnsupportedEncodingException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream(), "UTF-8"));
        StringBuffer buffer = new StringBuffer();

        while (true) {
            int ch = br.read();

            if (ch < 0) {
                break;
            }

            buffer.append((char) ch);
        }

        br.close();

        return buffer.toString();
    }

    /**
     * Creates a URL connection with a SAML assertion in the custom header defined by
     * SecurityConstants.ASSERTION_HEADER.
     * 
     * @param address the address to connect to.
     * @return the new URL connection.
     * @throws IOException if the connection can't be established or the assertion can't be built.
     */
    private HttpURLConnection getAuthenticatedUrlConnection(String address) throws IOException {
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty(SecurityConstants.ASSERTION_HEADER, buildSamlAssertion());
            return connection;
        }
        catch (Saml2Exception e) {
            String msg = "unable to build the SAML assertion";
            throw new IOException(msg, e);
        }
        catch (MarshallingException e) {
            String msg = "unable to marshall the SAML assertion";
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
    private String buildSamlAssertion() throws Saml2Exception, MarshallingException, IOException {
        try {
            KeyLoader keyLoader = new KeyLoader(KEYSTORE_PATH, KEYSTORE_TYPE, KEYSTORE_PASSWORD);
            X509Certificate cert = keyLoader.getCertificate(SIGNING_KEY_ALIAS);
            PrivateKey privateKey = keyLoader.getPrivateKey(SIGNING_KEY_ALIAS, SIGNING_KEY_PASSWORD);
            PublicKey publicKey = keyLoader.getCertificate(ENCRYPTING_KEY_ALIAS).getPublicKey();
            HttpServletRequest request = getThreadLocalRequest();
            return AssertionHelper.createEncodedAssertion(request, cert, privateKey, publicKey);
        }
        catch (GeneralSecurityException e) {
            throw new IOException("unable to load the certificate", e);
        }
    }

    private String get(String address) throws IOException {
        // make post mode connection
        URLConnection urlc = getAuthenticatedUrlConnection(address);
        urlc.setDoOutput(true);

        return retrieveResult(urlc);
    }

    private String update(String address, String body, String requestMethod) throws IOException {
        // make post mode connection
        HttpURLConnection urlc = getAuthenticatedUrlConnection(address);
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

    private String getContentType(String boundary) {
        return "multipart/form-data; boundary=" + boundary;
    }

    private String buildBoundary() {
        return "--------------------" + Long.toString(System.currentTimeMillis(), 16);
    }

    private String updateMultipart(String address, List<HTTPPart> parts, String requestMethod) throws IOException {
        String boundary = buildBoundary();

        // make post mode connection
        HttpURLConnection urlc = getAuthenticatedUrlConnection(address);
        urlc.setRequestProperty("content-type", getContentType(buildBoundary()));
        urlc.setRequestMethod(requestMethod);
        urlc.setDoOutput(true);

        // send post
        DataOutputStream outRemote = new DataOutputStream(urlc.getOutputStream());

        for (HTTPPart part : parts) {
            outRemote.writeBytes("--" + boundary);
            outRemote.writeBytes("\n");
            outRemote.writeBytes("Content-Disposition: form-data; " + part.getDisposition());
            outRemote.writeBytes("\r\n\r\n");
            outRemote.writeBytes(part.getBody());
            outRemote.writeBytes("\r\n--" + boundary + "--\r\n");
        }

        outRemote.flush();
        outRemote.close();

        String res = retrieveResult(urlc);
        urlc.disconnect();

        return res;
    }

    private String delete(String address) throws IOException {
        URL url = new URL(address);

        // make post mode connection
        HttpURLConnection urlc = (HttpURLConnection) url.openConnection();

        urlc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlc.setRequestMethod("DELETE");
        urlc.setDoOutput(true);
        urlc.connect();

        String res = retrieveResult(urlc);
        urlc.disconnect();

        return res;
    }

    private boolean isValidString(String in) {
        return (in != null && in.length() > 0);
    }

    private boolean isValidServiceCall(ServiceCallWrapper wrapper) {
        boolean ret = false; // assume failure

        if (wrapper != null) {
            if (isValidString(wrapper.getAddress())) {
                switch (wrapper.getType()) {
                case GET:
                case DELETE:
                    ret = true;
                    break;

                case PUT:
                case POST:
                    if (isValidString(wrapper.getBody())) {
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

    private boolean isValidServiceCall(MultiPartServiceWrapper wrapper) {
        boolean ret = false; // assume failure

        if (wrapper != null) {
            if (isValidString(wrapper.getAddress())) {
                switch (wrapper.getType()) {
                case PUT:
                case POST:
                    if (wrapper.getNumParts() > 0) {
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
    public String getServiceData(ServiceCallWrapper wrapper) throws SerializationException {
        String json = null;

        if (isValidServiceCall(wrapper)) {
            String address = wrapper.getAddress();
            String body = wrapper.getBody();

            try {
                switch (wrapper.getType()) {
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
            catch (Exception ex) {
                // because the GWT compiler will issue a warning if we simply throw exception, we'll
                // use SerializationException()
                throw new SerializationException(ex);
            }
        }

        System.out.println("json==>" + json);
        return json;
    }

    @Override
    public String getServiceData(MultiPartServiceWrapper wrapper)
            throws SerializationException
    {
        String json = null;

        if (isValidServiceCall(wrapper)) {
            String address = wrapper.getAddress();
            List<HTTPPart> parts = wrapper.getParts();

            try {
                switch (wrapper.getType()) {
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
            catch (Exception ex) {
                // because the GWT compiler will issue a warning if we simply throw exception, we'll
                // use SerializationException()
                throw new SerializationException(ex);
            }
        }

        System.out.println("json==>" + json);
        return json;
    }
}
