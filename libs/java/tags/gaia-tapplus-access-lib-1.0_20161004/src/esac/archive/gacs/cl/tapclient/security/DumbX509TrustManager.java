/*******************************************************************************
 * Copyright (c) 2016 European Space Agency.
 ******************************************************************************/
package esac.archive.gacs.cl.tapclient.security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

/**
 * Dummy class to remove complexities of certificates handling for
 * https secure connections to the Gacs subsystem
 * 
 * A future implementation could be more complex
 *
 */

public class DumbX509TrustManager implements X509TrustManager {

    
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    	// do nothing
	}

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    	// do nothing    	
    }

    public X509Certificate[] getAcceptedIssuers() { 
        return new X509Certificate[] {};
    }
}
    

