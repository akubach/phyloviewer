package org.iplantc.iptol.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * An authentication filter that allows or denies access based on the presence or absence of a valid SAML assertion
 * that is known by the local Shibboleth service provider.  Our login page is the only page that is guarded by
 * Shibboleth; when it is accessed it stores assertion URLs in a session property.  If this property is defined and
 * at least one of the URLs stored in this property produces a SAML assertion then the user is authenticated.
 * Otherwise, the user is not authenticated.
 *
 * Because this class is used to secure pages that are normally only called via AJAX, performing a redirection at
 * this point is inappropriate (redirecting an AJAX requests always results in the AJAX connection throwing an
 * exception).  Because of this, we always block the connection and let the client handle the redirection.
 *
 * @author Sriram Srinivasan
 * @author Dennis Roberts
 */
@SuppressWarnings("unused")
public class AuthFilter implements Filter {

    /**
     * The logger for error and informational messages.
     */
    private static Logger LOG = Logger.getLogger(AuthFilter.class);

    /**
     * A certificate trust store that trusts all certificates.  This trust store will be used exclusively for
     * connections that have been forced to point to the loopback interface, so we can be sure that they're secure.
     * Important note: be sure to reinstall the default trust manager after the connection is established.
     */
    private static TrustManager[] trustAllCertificates = new TrustManager[] {
        new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }
        }
    };
    
    /**
     * A host name verifier that accepts all host names, no matter what's in the certificate.  Important note: be sure
     * to reinstall the default host name verifier after the connection is established.
     */
    private static HostnameVerifier trustAllHostNames = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * The servlet filter configuration. 
     */
    private FilterConfig config;

    /**
     * Called when the filter is being destroyed.
     */
    @Override
    public void destroy() {
    }

    /**
     * Filters the request.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException
    {
        if (!isUserAuthenticated(request)) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.sendError(HttpURLConnection.HTTP_FORBIDDEN);
        }
        else {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Determines whether or not the user is authenticated.  The user is authenticated if our Shibboleth service
     * provider knows of at least one session that the user is associated with.
     *
     * @param request the servlet request.
     * @return true if the user is authenticated.
     * @throws ServletException if an unexpected error is encountered.
     */
    private boolean isUserAuthenticated(ServletRequest request) throws ServletException {
        boolean authenticated = false;
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            List<String> assertionUrls = getAssertionUrls(httpRequest);
            authenticated = validAssertionAvailable(assertionUrls);
            if (LOG.isDebugEnabled()) {
                String msg = authenticated ? "user was authenticated" : "user was not authenticated"; 
                LOG.debug(msg);
            }
        }
        catch (Throwable t) {
            String msg = "unexpected exception";
            LOG.debug(msg, t);
            throw new ServletException(msg, t);
        }
        return authenticated;
    }

    /**
     * Initializes the filter.
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
        this.config = config;
    }

    /**
     * Obtains the list of assertion URLs from the request session.
     *
     * @param request the request to fetch the URLs from.
     * @return the list of URLs.
     */
    @SuppressWarnings("unchecked")
    private List<String> getAssertionUrls(HttpServletRequest request) {
        String attributeName = DESecurityConstants.LOCAL_ASSERTION_URL_LIST;
        return (List<String>) request.getSession().getAttribute(attributeName);
    }

    /**
     * Determines whether or not a valid assertion is available for this session.
     *
     * @param assertionUrls the list of URLs to use when attempting to fetch assertions.
     * @return true if at least one URL in the list references a valid assertion.
     */
    private boolean validAssertionAvailable(List<String> assertionUrls) {
        if (assertionUrls != null) {
            for (String urlString : assertionUrls) {
                if (isValidAssertionUrl(urlString)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines whether or not the given URL references a valid SAML assertion.
     *
     * @param urlString the string representation of the URL to use when attempting to fetch the assertion.
     * @return true if the URL references a SAML assertion.
     */
    private boolean isValidAssertionUrl(String urlString) {
        try {
            URL url = buildLocalUrl(urlString);
            LOG.debug("local url: " + url.toString());
            URLConnection connection = getTrustingUrlConnection(url);
            InputStream in = connection.getInputStream();
            return true;
        }
        catch (IOException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("valid session check failed for url " + urlString, e);
            }
        }
        return false;
    }

    /**
     * Obtains a URL connection for which SSL certificates and host names are not verified.  It is safe to do so in
     * this case because we're forcing the URL to point to the loopback interface, meaning that someone would have
     * had to infiltrate the discovery environment server to stage an attack using any of these URLs.  Note that this
     * method changes the default trust store and host name verifier for SSL connections.  It is essential that the
     * original trust store and host name verifier are restored before this method exits.
     *
     * @param url the URL to connect to.
     * @return The URL connection.
     * @throws IOException if the connection can't be established.
     */
    private URLConnection getTrustingUrlConnection(URL url) throws IOException {
        SSLSocketFactory originalSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
        HostnameVerifier originalHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCertificates, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(trustAllHostNames);
            return url.openConnection();
        }
        catch (GeneralSecurityException e) {
            throw new IOException("unable to establish a trusting HTTPS connection", e);
        }
        finally {
            HttpsURLConnection.setDefaultSSLSocketFactory(originalSocketFactory);
            HttpsURLConnection.setDefaultHostnameVerifier(originalHostnameVerifier);
        }
    }

    /**
     * Builds a URL based on the given URL string that points to the loopback interface.  To form this URL, the host
     * name or IP address in the original URL string is replaced with the loopback IP address (127.0.0.1).
     *
     * @param originalUrlString the URL string that was provided to us by Shibboleth.
     * @return a URL that is based on the original URL string but contains an IP address of 127.0.0.1.
     * @throws IOException if the original URL or the newly formed URL is invalid.
     */
    private URL buildLocalUrl(String originalUrlString) throws IOException {
        URL originalUrl = new URL(originalUrlString);
        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append(extractProtocol(originalUrl));
        urlBuffer.append(DESecurityConstants.ASSERTION_QUERY_HOST);
        urlBuffer.append(extractPort(originalUrl));
        urlBuffer.append(extractPath(originalUrl));
        urlBuffer.append(extractQuery(originalUrl));
        return new URL(urlBuffer.toString());
    }

    /**
     * Extracts the query from the given URL.
     *
     * @param originalUrl the URL that was provided to us by Shibboleth.
     * @return the query or the empty string if there was no query in the original URL.
     */
    private Object extractQuery(URL originalUrl) {
        String query = originalUrl.getQuery();
        return query == null ? "" : "?" + query;
    }

    /**
     * Extracts the path from the given URL.
     *
     * @param originalUrl the URL that was provided to us by Shibboleth.
     * @return the path or the empty string if there was no path in the original URL.
     */
    private Object extractPath(URL originalUrl) {
        String path = originalUrl.getPath();
        return path == null ? "" : path;
    }

    /**
     * Extracts the port from the given URL.
     *
     * @param originalUrl the URL that was provided to us by Shibboleth.
     * @return the port or the empty string if there was no port in the original URL.
     */
    private Object extractPort(URL originalUrl) {
        int port = originalUrl.getPort();
        return port < 0 ? "" : ":" + port;
    }

    /**
     * Extracts the protocol from the given URL.
     *
     * @param originalUrl the URL that was provided to us by Shibboleth.
     * @return the protocol string, including the colon and slashes.
     */
    private Object extractProtocol(URL originalUrl) {
        return originalUrl.getProtocol() + "://";
    }
}
