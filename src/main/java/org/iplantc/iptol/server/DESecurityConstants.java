package org.iplantc.iptol.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides several constants that we use to secure our services.  The REMOTE constants are the names of the attributes
 * that are provided by Shibboleth.  The LOCAL constants are the names of the attributes that we use internally.  These
 * names are different so as to avoid potential conflicts.  There's also an attribute map that is used to map the
 * attribute names used by Shibboleth to those used by the discovery environment.
 * 
 * @author Dennis Roberts
 */
@SuppressWarnings("serial")
public class DESecurityConstants {

    // The names of the attributes containing the Shibboleth session ID.
    public static final String REMOTE_SHIB_SESSION_ID = "HTTP_SHIB_SESSION_ID";
    public static final String LOCAL_SHIB_SESSION_ID = "shibbolethSessionId";

    // The names of the attributes containing the unique identifier of the Shibboleth identity provider.
    public static final String LOCAL_SHIB_IDENTITY_PROVIDER = "shibbolethIdentityProvider";
    public static final String REMOTE_SHIB_IDENTITY_PROVIDER = "HTTP_SHIB_IDENTITY_PROVIDER";

    // The names of the attributes containing the URI for the authentication method.
    public static final String LOCAL_SHIB_AUTHENTICATION_METHOD = "shibbolethAuthenticationMethod";
    public static final String REMOTE_SHIB_AUTHENTICATION_METHOD = "HTTP_SHIB_AUTHENTICATION_METHOD";

    // The names of the attributes containing the date and time of authentication.
    public static final String LOCAL_SHIB_AUTHENTICATION_INSTANT = "shibbolethAuthenticationInstant";
    public static final String REMOTE_SHIB_AUTHENTICATION_INSTANT = "HTTP_SHIB_AUTHENTICATION_INSTANT";

    // The names of the attributes containing the authentication context class.
    public static final String LOCAL_SHIB_AUTHNCONTEXT_CLASS = "shibbolethAuthnContextClass";
    public static final String REMOTE_SHIB_AUTHNCONTEXT_CLASS = "HTTP_SHIB_AUTHNCONTEXT_CLASS";

    // The names of the attributes containing the authentication context declaration.
    public static final String LOCAL_SHIB_AUTHNCONTEXT_DECL = "shibbolethAuthnContextDecl";
    public static final String REMOTE_SHIB_AUTHNCONTEXT_DECL = "HTTP_SHIB_AUTHNCONTEXT_DECL";

    // The names of the attributes containing the number of assertions known to our service provider.
    public static final String LOCAL_SHIB_ASSERTION_COUNT = "shibbolethAssertionCount";
    public static final String REMOTE_SHIB_ASSERTION_COUNT = "HTTP_SHIB_ASSERTION_COUNT";

    // The names of the attributes containing the user's EduPersonPrincipalName.
    public static final String LOCAL_SHIB_EPPN = "shibbolethEppn";
    public static final String REMOTE_SHIB_EPPN = "HTTP_EPPN";

    // The names of the attributes containing the user's organization affiliation.
    public static final String LOCAL_SHIB_AFFILIATION = "shibbolethAffiliation";
    public static final String REMOTE_SHIB_AFFILIATION = "HTTP_AFFILIATION";

    // The names of the attributes containing the user's unscoped organization affiliation.
    public static final String LOCAL_SHIB_UNSCOPED_AFFILIATION = "shibboletehUnscopedAffiliation";
    public static final String REMOTE_SHIB_UNSCOPED_AFFILIATION = "HTTP_UNSCOPED_AFFILIATION";

    // The names of the attributes containing the user's entitlement information.
    public static final String LOCAL_SHIB_ENTITLEMENT = "shibbolethEntitlement";
    public static final String REMOTE_SHIB_ENTITLEMENT = "HTTP_ENTITLEMENT";

    // The names of the attributes containing the user's assurance information.
    public static final String LOCAL_SHIB_ASSURANCE = "shibbolethAssurance";
    public static final String REMOTE_SHIB_ASSURANCE = "HTTP_ASSURANCE";

    // The names of the attributes containing the user's targeted identifier.
    public static final String LOCAL_SHIB_TARGETED_ID = "shibbolethTargetedId";
    public static final String REMOTE_SHIB_TARGETED_ID = "HTTP_TARGETED_ID";

    // The names of the attributes containing the user's persistent identifier.
    public static final String LOCAL_SHIB_PERSISTENT_ID = "shibbolethPersistentId";
    public static final String REMOTE_SHIB_PERSISTENT_ID = "HTTP_PERSISTENT_ID";

    // The names of the attributes containing the identifier of our service provider.
    public static final String LOCAL_SHIB_APPLICATION_ID = "shibbolethApplicationId";
    public static final String REMOTE_SHIB_APPLICATION_ID = "HTTP_SHIB_APPLICATION_ID";

    // The names of the attributes containing the remote user string.
    public static final String LOCAL_SHIB_REMOTE_USER = "shibbolethRemoteUser";
    public static final String REMOTE_SHIB_REMOTE_USER = "HTTP_REMOTE_USER";

    // The HTTP header prefix used to identify Shibboleth assertion URLs.
    public static final String REMOTE_ASSERTION_URL_PREFIX = "Shib-Assertion-";

    // The name of the attribute that we're using to store the list of assertion URLs.
    public static final String LOCAL_ASSERTION_URL_LIST = "shibbolethAssertions";

    // The host name or IP address to use for all SAML assertion queries.
    public static final String ASSERTION_QUERY_HOST = "127.0.0.1";

    // Maps the names of the attributes used by Shibboleth to the names that we use.
    public static final Map<String, String> ATTRIBUTE_MAP_REMOTE_TO_LOCAL = new HashMap<String, String>() {{
        put(REMOTE_SHIB_SESSION_ID,             LOCAL_SHIB_SESSION_ID);
        put(REMOTE_SHIB_IDENTITY_PROVIDER,      LOCAL_SHIB_IDENTITY_PROVIDER);
        put(REMOTE_SHIB_AUTHENTICATION_METHOD,  LOCAL_SHIB_AUTHENTICATION_METHOD);
        put(REMOTE_SHIB_AUTHENTICATION_INSTANT, LOCAL_SHIB_AUTHENTICATION_INSTANT);
        put(REMOTE_SHIB_AUTHNCONTEXT_CLASS,     LOCAL_SHIB_AUTHNCONTEXT_CLASS);
        put(REMOTE_SHIB_AUTHNCONTEXT_DECL,      LOCAL_SHIB_AUTHNCONTEXT_DECL);
        put(REMOTE_SHIB_ASSERTION_COUNT,        LOCAL_SHIB_ASSERTION_COUNT);
        put(REMOTE_SHIB_EPPN,                   LOCAL_SHIB_EPPN);
        put(REMOTE_SHIB_AFFILIATION,            LOCAL_SHIB_AFFILIATION);
        put(REMOTE_SHIB_UNSCOPED_AFFILIATION,   LOCAL_SHIB_UNSCOPED_AFFILIATION);
        put(REMOTE_SHIB_ENTITLEMENT,            LOCAL_SHIB_ENTITLEMENT);
        put(REMOTE_SHIB_ASSURANCE,              LOCAL_SHIB_ASSURANCE);
        put(REMOTE_SHIB_TARGETED_ID,            LOCAL_SHIB_TARGETED_ID);
        put(REMOTE_SHIB_PERSISTENT_ID,          LOCAL_SHIB_PERSISTENT_ID);
        put(REMOTE_SHIB_APPLICATION_ID,         LOCAL_SHIB_APPLICATION_ID);
        put(REMOTE_SHIB_REMOTE_USER,            LOCAL_SHIB_REMOTE_USER);
    }};

    /**
     * Returns a collection of local attribute names that can be used for building new SAML assertions.
     *
     * @return the collection of attribute names.
     */
    public static Collection<String> getLocalAttributeNames() {
        return ATTRIBUTE_MAP_REMOTE_TO_LOCAL.values();
    }
}
