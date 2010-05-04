package org.iplantc.de.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

/**
 * Provides simple ways to load encryption keys from a keystore.
 * 
 * @author Dennis Roberts
 */
public class KeyLoader {

    /**
     * The keystore.
     */
    private KeyStore keystore;

    /**
     * The default constructor.
     * 
     * @param path the path to the keystore.
     * @param type the type of keystore.
     * @param password the password used to access the keystore.
     * @throws IOException if an I/O error occurs.
     * @throws GeneralSecurityException if the keystore can't be loaded.
     */
    public KeyLoader(File path, String type, String password) throws IOException, GeneralSecurityException {
        FileInputStream in = null;
        try {
            in = new FileInputStream(path);
            keystore = KeyStore.getInstance(type);
            keystore.load(in, password.toCharArray());
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * The default constructor.
     * 
     * @param path the path to the keystore.
     * @param type the type of keystore.
     * @param password the password used to access the keystore.
     * @throws IOException if an I/O error occurs.
     * @throws GeneralSecurityException if the keystore can't be loaded.
     */
    public KeyLoader(String path, String type, String password) throws IOException, GeneralSecurityException {
    	this(new File(path), type, password);
    }

    /**
     * Retrieves a certificate from the keystore.
     * 
     * @param alias the alias used to reference the certificate.
     * @return the certificate.
     * @throws GeneralSecurityException if the certificate can't be loaded.
     */
    public X509Certificate getCertificate(String alias) throws GeneralSecurityException {
        return (X509Certificate) keystore.getCertificate(alias);
    }

    /**
     * Retrieves a private key from the keystore.
     * 
     * @param alias the alias used to reference the private key. 
     * @param password the password used to access the key.
     * @return the key.
     * @throws GeneralSecurityException if the key can't be loaded.
     */
    public PrivateKey getPrivateKey(String alias, String password) throws GeneralSecurityException {
        return (PrivateKey) keystore.getKey(alias, password.toCharArray());
    }

    /**
     * Retrieves a key pair from the keystore.
     * 
     * @param alias the alias used to reference the key pair.
     * @param password the password used to access the key.
     * @return the key pair.
     * @throws GeneralSecurityException if the key pair can't be loaded.
     */
    public KeyPair getKeyPair(String alias, String password) throws GeneralSecurityException {
        PrivateKey privateKey = getPrivateKey(alias, password);
        PublicKey publicKey = getCertificate(alias).getPublicKey();
        return new KeyPair(publicKey, privateKey);
    }
}
