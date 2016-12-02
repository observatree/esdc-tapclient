/*******************************************************************************
 * Copyright (c) 2016 European Space Agency.
 ******************************************************************************/
package esac.archive.gacs.cl.tapclient.security;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


public class DumbHostnameVerifier implements HostnameVerifier {
	
	/**
	 * Dummy implementation for the hostname verifier to Gacs connections
	 * 
	 * A future implementation could be more complex
	 *
	 */	
		
	public boolean verify(String arg0, SSLSession arg1) {
    
    		// Only allow esa.int URLs (and for testing, also allow hosts
    		// from esa.int
    		if (arg0 == null) 	return false;
    		else 			return arg0.endsWith("esa.int");

	}
}


