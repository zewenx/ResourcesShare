package server;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;

public class SSLLoader {

	public static void load(String keyStorePath, String trustKeyStorePath) {
		InputStream keystoreInput = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(keyStorePath);
		InputStream truststoreInput = Thread.currentThread().getContextClassLoader().getResourceAsStream(trustKeyStorePath);
		try {
			setSSLFactories(keystoreInput, "111111", truststoreInput);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (keystoreInput != null) {
					
					keystoreInput.close();
				}
				if (truststoreInput != null) {
					
					truststoreInput.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private static void setSSLFactories(InputStream keyStream, String keyStorePassword, InputStream trustStream)
			throws Exception {
		// Get keyStore
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

		// if your store is password protected then declare it (it can be null
		// however)
		char[] keyPassword = keyStorePassword.toCharArray();

		// load the stream to your store
		keyStore.load(keyStream, keyPassword);

		// initialize a trust manager factory with the trusted store
		KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyFactory.init(keyStore, keyPassword);

		// get the trust managers from the factory
		KeyManager[] keyManagers = keyFactory.getKeyManagers();

		// Now get trustStore
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

		// if your store is password protected then declare it (it can be null
		// however)
		// char[] trustPassword = password.toCharArray();

		// load the stream to your store
		trustStore.load(trustStream, keyPassword);

		// initialize a trust manager factory with the trusted store
		TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustFactory.init(trustStore);

		// get the trust managers from the factory
		TrustManager[] trustManagers = trustFactory.getTrustManagers();

		// initialize an ssl context to use these managers and set as default
		SSLContext sslContext = SSLContext.getInstance("SSL");
		sslContext.init(keyManagers, trustManagers, null);
		SSLContext.setDefault(sslContext);
	}

}
