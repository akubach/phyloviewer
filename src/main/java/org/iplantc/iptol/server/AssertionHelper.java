package org.iplantc.iptol.server;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;

import org.apache.xml.security.utils.Base64;
import org.iplantc.saml.AssertionBuilder;
import org.iplantc.saml.AssertionEncrypter;
import org.iplantc.saml.AttributeStatementBuilder;
import org.iplantc.saml.Saml2Exception;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.xml.encryption.EncryptionConstants;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.signature.SignatureConstants;

/**
 * Provides utility functionality to aide in the creation and processing of SAML Assertions
 * 
 * @author lenards
 * 
 */
public class AssertionHelper {
    /**
     * Used in SAML Assertions as the NameFormat attribute of the saml2:Attribute element
     */
    public static final String ATTRIBUTE_URL = "http://auth.iplantcollaborative.org/attributes/";
    
    /**
     * Creates a base64 encoded version of the encrypted SAML2 assertion.
     * 
     * @param request http request that contains relevant, known request attributes
     * @param cert X509 certificate to use, with the privKey, when signing the assertion 
     * @param privKey private key to use, with X509 certificate, when signing the assertion
     * @param encryptKey public key to use when encrypting the assertion
     * @return a base64 encoded version of the encrypted SAML Assertion
     * @throws Saml2Exception
     * @throws MarshallingException
     */
    public static String createEncodedAssertion(HttpServletRequest request, X509Certificate cert, 
            PrivateKey privKey, PublicKey encryptKey) throws Saml2Exception, MarshallingException {   
        AssertionBuilder builder = new AssertionBuilder();
        // Subject and Auth Info
        builder.setSubject(extract(request, DESecurityConstants.LOCAL_SHIB_EPPN));
        builder.addAuthnMethod(extract(request, DESecurityConstants.LOCAL_SHIB_AUTHENTICATION_METHOD),
                extract(request, DESecurityConstants.LOCAL_SHIB_AUTHENTICATION_INSTANT));
        // Include relevant known request attributes
        includeAttributes(request, builder);
        // Sign assertion
        builder.signAssertion(cert, privKey,
                SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        Assertion assertion = builder.getAssertion();
        // Encode the encrypted assertion
        return Base64.encode(encryptAssertion(assertion, encryptKey).getBytes(), 0);
    }
    
    /**
     * Creates a base64 encoded version of the encrypted SAML2 assertion.
     * 
     * @param request http request that contains relevant, known request attributes
     * @param cert X509 certificate to use, with the privKey, when signing the assertion 
     * @param privKey private key to use, with X509 certificate, when signing the assertion
     * @param encryptKeyPair key-pair containing the public key to use when encrypting the assertion
     * @return a base64 encoded version of the encrypted SAML Assertion
     * @throws Saml2Exception
     * @throws MarshallingException
     */
    public static String createEncodedAssertion(HttpServletRequest request, X509Certificate cert, 
    PrivateKey privKey, KeyPair encryptKeyPair) throws Saml2Exception, MarshallingException {
        PublicKey encryptKey = encryptKeyPair.getPublic();
        return createEncodedAssertion(request, cert, privKey, encryptKey);
    }
    
    /**
     * Add known attributes from request to the assertion being built with builder
     * 
     * Known attributes are those specified by RequestAttributes. 
     * 
     * @param request http request that contains relevant request attributes
     * @param builder object building the assertion
     * @throws Saml2Exception
     */
    private static void includeAttributes(HttpServletRequest request, AssertionBuilder builder) throws Saml2Exception {
        AttributeStatementBuilder attributeStatementBuilder = builder.addAttributeStatement();
        for (String attrName : DESecurityConstants.getLocalAttributeNames()) {
            Object value = request.getSession().getAttribute(attrName);
            if (value != null && !value.toString().isEmpty()) {
                String attrDisplayName = getDisplayName(attrName);
                attributeStatementBuilder.addStringAttribute(attrDisplayName, ATTRIBUTE_URL, value.toString());
            }
        }
    }

    /**
     * Gets the display name for the attribute.  For our purposes, the display name is the same as the local attribute
     * name, but with the "shibboleth" prefix removed.
     *
     * @param attrName the local session attribute name.
     * @return the attribute name to use in the assertion.
     */
    private static String getDisplayName(String attrName) {
        return attrName.startsWith("shibboleth") ? attrName.substring("shibboleth".length()) : attrName;
    }

    /**
     * Encrypts an assertion given a public key. 
     * 
     * @param assertion SAML2 assertion
     * @param encryptionPublicKey public key to use when encrypting
     * @return an encrypted version of the given assertion
     * @throws MarshallingException
     */
    public static String encryptAssertion(Assertion assertion, PublicKey encryptionPublicKey) throws MarshallingException {
        AssertionEncrypter encrypter = new AssertionEncrypter();
        encrypter.setPublicKey(encryptionPublicKey);
        encrypter.setPublicKeyAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        encrypter.setSecretKeyAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        return encrypter.encryptAssertion(assertion);
    }
    
    /**
     * Pull the attribute from the request's attribute collection. 
     * 
     * Helper method to encapsulate error handling and type-conversion
     * 
     * @param request http request with the attribute
     * @param attribute attribute contained in the http request
     * @return a string representation of the request attribute.
     */
    private static String extract(HttpServletRequest request, String attribute) {
        Object attr = request.getSession().getAttribute(attribute);
        // if attr is null, we should throw an exception to indicate that request 
        // is missing attributes and not just kick out a NullPointerException
        if (attr == null) {
            throw new IllegalArgumentException("HTTP Request missing expect attribute: " + attribute);
        }
        return attr.toString();
    }
}
