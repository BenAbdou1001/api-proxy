package com.t24.apiproxy.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for SSL/TLS context creation and configuration
 */
public class SSLUtil {
    private static final Logger logger = LoggerFactory.getLogger(SSLUtil.class);
    private static final String TLS_PROTOCOL = "TLS";
    
    /**
     * Creates an SSLContext with the specified configuration
     * 
     * @param trustAll Whether to trust all certificates (WARNING: Use only for testing!)
     * @param keyStorePath Path to the keystore file (optional)
     * @param keyStorePass Password for the keystore (optional)
     * @param trustStorePath Path to the truststore file (optional)
     * @param trustStorePass Password for the truststore (optional)
     * @return Configured SSLContext or null if creation fails
     */
    public static SSLContext createContext(boolean trustAll, String keyStorePath, 
                                          String keyStorePass, String trustStorePath, 
                                          String trustStorePass) {
        try {
            SSLContext sslContext = SSLContext.getInstance(TLS_PROTOCOL);
            
            // Initialize KeyManagers
            KeyManager[] keyManagers = null;
            if (keyStorePath != null && !keyStorePath.isEmpty()) {
                keyManagers = createKeyManagers(keyStorePath, keyStorePass);
            }
            
            // Initialize TrustManagers
            TrustManager[] trustManagers;
            if (trustAll) {
                logger.warn("Trust-all SSL mode is enabled. This is INSECURE and should only be used for testing!");
                trustManagers = createTrustAllManagers();
            } else if (trustStorePath != null && !trustStorePath.isEmpty()) {
                trustManagers = createTrustManagers(trustStorePath, trustStorePass);
            } else {
                trustManagers = null; // Use default trust managers
            }
            
            // Initialize the SSLContext
            sslContext.init(keyManagers, trustManagers, new SecureRandom());
            
            logger.info("SSLContext created successfully");
            return sslContext;
            
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            logger.error("Failed to create SSLContext", e);
            return null;
        }
    }
    
    /**
     * Creates a default SSLContext using system defaults
     * 
     * @return Default SSLContext
     */
    public static SSLContext createDefaultContext() {
        try {
            return SSLContext.getDefault();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to get default SSLContext", e);
            return null;
        }
    }
    
    /**
     * Creates an SSLContext that trusts all certificates (INSECURE!)
     * 
     * @return SSLContext that trusts all certificates
     */
    public static SSLContext createTrustAllContext() {
        return createContext(true, null, null, null, null);
    }
    
    /**
     * Creates KeyManagers from a keystore file
     * 
     * @param keyStorePath Path to the keystore file
     * @param keyStorePass Password for the keystore
     * @return Array of KeyManagers
     */
    private static KeyManager[] createKeyManagers(String keyStorePath, String keyStorePass) {
        try {
            KeyStore keyStore = loadKeyStore(keyStorePath, keyStorePass);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm()
            );
            keyManagerFactory.init(keyStore, keyStorePass != null ? keyStorePass.toCharArray() : null);
            
            logger.debug("KeyManagers created from keystore: {}", keyStorePath);
            return keyManagerFactory.getKeyManagers();
            
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e) {
            logger.error("Failed to create KeyManagers from keystore: {}", keyStorePath, e);
            return null;
        }
    }
    
    /**
     * Creates TrustManagers from a truststore file
     * 
     * @param trustStorePath Path to the truststore file
     * @param trustStorePass Password for the truststore
     * @return Array of TrustManagers
     */
    private static TrustManager[] createTrustManagers(String trustStorePath, String trustStorePass) {
        try {
            KeyStore trustStore = loadKeyStore(trustStorePath, trustStorePass);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm()
            );
            trustManagerFactory.init(trustStore);
            
            logger.debug("TrustManagers created from truststore: {}", trustStorePath);
            return trustManagerFactory.getTrustManagers();
            
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            logger.error("Failed to create TrustManagers from truststore: {}", trustStorePath, e);
            return null;
        }
    }
    
    /**
     * Creates TrustManagers that trust all certificates (INSECURE!)
     * 
     * @return Array of TrustManagers that trust everything
     */
    private static TrustManager[] createTrustAllManagers() {
        return new TrustManager[] {
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
                
                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    // Trust all client certificates
                }
                
                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    // Trust all server certificates
                }
            }
        };
    }
    
    /**
     * Loads a KeyStore from a file
     * 
     * @param keyStorePath Path to the keystore file
     * @param keyStorePass Password for the keystore
     * @return Loaded KeyStore
     */
    private static KeyStore loadKeyStore(String keyStorePath, String keyStorePass) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            
            try (InputStream inputStream = new FileInputStream(keyStorePath)) {
                keyStore.load(inputStream, keyStorePass != null ? keyStorePass.toCharArray() : null);
            }
            
            logger.debug("KeyStore loaded from: {}", keyStorePath);
            return keyStore;
            
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            logger.error("Failed to load KeyStore from: {}", keyStorePath, e);
            throw new RuntimeException("Failed to load KeyStore", e);
        }
    }
    
    /**
     * Validates SSL/TLS protocol version
     * 
     * @param protocol Protocol to validate (e.g., "TLSv1.2", "TLSv1.3")
     * @return true if protocol is supported, false otherwise
     */
    public static boolean isSupportedProtocol(String protocol) {
        try {
            SSLContext.getInstance(protocol);
            return true;
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }
    
    /**
     * Gets the list of supported SSL/TLS protocols
     * 
     * @return Array of supported protocol names
     */
    public static String[] getSupportedProtocols() {
        try {
            SSLContext context = SSLContext.getDefault();
            return context.getSupportedSSLParameters().getProtocols();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to get supported protocols", e);
            return new String[0];
        }
    }
    
    /**
     * Gets the list of supported cipher suites
     * 
     * @return Array of supported cipher suite names
     */
    public static String[] getSupportedCipherSuites() {
        try {
            SSLContext context = SSLContext.getDefault();
            return context.getSupportedSSLParameters().getCipherSuites();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to get supported cipher suites", e);
            return new String[0];
        }
    }
}