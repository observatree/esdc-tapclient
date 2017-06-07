/*******************************************************************************
 * Copyright (C) 2017 European Space Agency
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
    

