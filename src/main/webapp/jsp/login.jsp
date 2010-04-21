<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.LinkedList"%>
<%@page import="org.iplantc.iptol.server.DESecurityConstants"%>

<%
    // Determine how many assertions we have available.
    String assertionCountValue = (String) request.getAttribute(DESecurityConstants.REMOTE_SHIB_ASSERTION_COUNT);
    int assertionCount = 0;
    try {
        assertionCount = Integer.parseInt(assertionCountValue);
    }
    catch (Exception ingore) {
    }
    if (assertionCount == 0) {
        response.setContentType("text/plain");
        response.getWriter().println("internal configuration error: no assertion count provided");
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return;
    }

    // Build the list of assertion URLs.
    LinkedList<String> assertionUrls = new LinkedList<String>();
    DecimalFormat assertionNumberFormat = new DecimalFormat("00");
    for (int i = 1; i <= assertionCount; i++) {
        String headerName = DESecurityConstants.REMOTE_ASSERTION_URL_PREFIX + assertionNumberFormat.format(i);
        assertionUrls.add(request.getHeader(headerName));
    }
    session.setAttribute(DESecurityConstants.LOCAL_ASSERTION_URL_LIST, assertionUrls);

    // Copy the attributes provided by Shibboleth to our local attributes.
    for (String remoteName : DESecurityConstants.ATTRIBUTE_MAP_REMOTE_TO_LOCAL.keySet()) {
        String localName = DESecurityConstants.ATTRIBUTE_MAP_REMOTE_TO_LOCAL.get(remoteName);
        String value = (String) request.getAttribute(remoteName);
        if (value != null) {
    session.setAttribute(localName, value);
        }
    }

    // Redirect the user to the main discovery environment page.
    StringBuffer urlBuffer = request.getRequestURL();
    int lastSlashPos = urlBuffer.lastIndexOf("/");
    int nextToLastSlashPos = urlBuffer.lastIndexOf("/", lastSlashPos - 1);
    urlBuffer.delete(nextToLastSlashPos, urlBuffer.length());
    response.sendRedirect(urlBuffer.toString());
%>
