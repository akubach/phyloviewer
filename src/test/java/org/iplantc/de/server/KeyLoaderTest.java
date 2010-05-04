package org.iplantc.de.server;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for org.iplantc.de.server.KeyLoader
 * 
 * @author Dennis Roberts
 */
public class KeyLoaderTest {

    private static final String KEYSTORE_PATH = "src/main/resources/keystore.jceks";
    private static final String KEYSTORE_TYPE = "JCEKS";
    private static final String KEYSTORE_PASSWORD = "changeit";

    private KeyLoader instance;
    
    /**
     * Initializes each test.
     * 
     * @throws Exception if an error occurs.
     */
    @Before
    public void initialize() throws Exception {
        instance = new KeyLoader(KEYSTORE_PATH, KEYSTORE_TYPE, KEYSTORE_PASSWORD);
    }

    /**
     * Verifies that we can load a certificate.
     * 
     * @throws Exception if an error occurs.
     */
    @Test
    public void shouldLoadCertificate() throws Exception {
        assertNotNull(instance.getCertificate("encrypting"));
    }

    /**
     * Verifies that we can load a private key.
     *
     * @throws Exception if an error occurs.
     */
    @Test
    public void shouldLoadPrivateKey() throws Exception {
        assertNotNull(instance.getPrivateKey("encrypting", "changeit"));
    }

    /**
     * Verifies that we can load a key pair.
     * 
     * @throws Exception if an error occurs.
     */
    @Test
    public void shouldLoadKeyPair() throws Exception {
        assertNotNull(instance.getKeyPair("encrypting", "changeit"));
    }
    
    /**
     * Verifies that we get an I/O exception for a non-existent file.
     */
    @Test(expected=IOException.class)
    public void shouldGetExceptionForMissingFile() throws Exception {
        instance = new KeyLoader("/some/file/that/does/not/exist.jceks", "JCEKS", "blarg");
    }
}
