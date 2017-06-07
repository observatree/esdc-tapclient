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
package esac.archive.gacs.cl.tapclient.connection;
import java.io.BufferedReader; 
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import esac.archive.gacs.cl.tapclient.exception.TapPlusException;
import esac.archive.gacs.cl.tapclient.security.DumbHostnameVerifier;
import esac.archive.gacs.cl.tapclient.security.DumbX509TrustManager;

	 
	/**
	 * Utility class to enable easy use of POST https connections
	 *
	 */
	
	public class ConnectionWrapper {
		
	    private final 			String 				boundary;
	    private static final 	String 				LINE_FEED = "\r\n";
	    private 				HttpURLConnection 	connection;
	    private 				String 				charset;
	    private					BufferedReader		reader;
	    private					InputStream	 		inputStream;
	    private 				OutputStream 		outputStream;
	    private 				PrintWriter 		writer;
	    private					String				type;
	    private					boolean				isMultipart=false;
	 
	    /**
	     * This constructor initializes a new HTTP POST request 
	     * @param requestURL
	     * @param charset
	     * @throws IOException
	     * @throws NoSuchAlgorithmException 
	     * @throws KeyManagementException 
	     */
	    public ConnectionWrapper(String cookie, String requestURL, String charset) throws IOException, KeyManagementException, NoSuchAlgorithmException {
	        
	    	this.charset = charset;
	         
	        // creates a unique boundary based on time stamp
	        boundary = "===" + System.currentTimeMillis() + "===";
	         
			//Initialize the connection 
	        URL url 					= new URL(requestURL);
    		boolean isSecure 			= url.getProtocol().equals("https");
    		
    		if(isSecure) {
    			connection 					= (HttpsURLConnection) url.openConnection();
    		} else {
    			connection 					= (HttpURLConnection) url.openConnection();
    		}
    
    		if (isSecure) {
        		// Set a socket factory that has the manager trusting all certificates
        		HttpsURLConnection sConn 	= (HttpsURLConnection) connection;
        		SSLSocketFactory socketFactory 	= getSocketFactory();
        		sConn.setSSLSocketFactory(socketFactory);
        
				// And overrule the standard hostname verifier (due to absent
        		// hostname in certificates generated with keytool).
        		sConn.setHostnameVerifier(new DumbHostnameVerifier());
   	 		}
	        
    		connection.setUseCaches(false);
	        connection.setDoInput(true);
	        connection.setRequestProperty("User-Agent", "CodeJava Agent");
	        
	        if(cookie != null) {
	        	connection.setRequestProperty("Cookie", cookie);
	        }	        
	    }
	    
	    /** Create connection with session Cookie
	     * 
	     * @param cookie
	     * @param requestURL
	     * @param charset
	     * @throws IOException
	     * @throws KeyManagementException
	     * @throws NoSuchAlgorithmException
	     */
	     

	    public ConnectionWrapper(String requestURL, String charset) throws IOException, KeyManagementException, NoSuchAlgorithmException {
	    	this(null, requestURL, charset);	    	
	    }
	    
	    /**
	     * Set content-type for the connection
	     * @param type
	     * @throws IOException
	     */
	    public void setType(String type, String contentType) throws IOException {
	    	
	    	this.type = type;
	    	
	    	if(contentType!=null && contentType.toLowerCase().contains("multipart")){
	    		isMultipart=true;
	    	}
	    	
	    	if(type.equals("POST")) 	{
	    		connection.setRequestMethod("POST");
	    		connection.setDoOutput(true);
	    		connection.setRequestProperty("Content-Type", contentType + "; boundary=" + boundary);
	    		outputStream = connection.getOutputStream();
	 	        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);	     
	    	}else if(type.equals("DELETE")){
	    		connection.setRequestMethod("DELETE");
	    	}
	    }
	    
	   /**
	    * Return header Field from underlying connection 
	    * @param fieldName
	    * @return
	    */
	    public String getHeaderField(String fieldName) {
	    	return connection.getHeaderField(fieldName);
	    }
	    	
	    /**
	     * SSL Socket factory implementation
	     */	    
    	private static SSLSocketFactory getSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
      		SSLSocketFactory socketFactory = null;
      		SSLContext context = SSLContext.getInstance("SSLv3");
      		context.init(null, new X509TrustManager[]{ new DumbX509TrustManager() }, null);
      		socketFactory = context.getSocketFactory();
      		return socketFactory;
    	}

    	
    	/**
	     * Gets Cookie from the connection
	     */
	    public String getCookie() {
	    	String cookie = connection.getHeaderField("Set-Cookie");
		    if (cookie != null) cookie = cookie.substring(0, cookie.indexOf(';'));
			return cookie;
	    }
	    
	 
	    /**
	     * Adds a form field to the request
	     * @param name field name
	     * @param value field value
	     */
	    public void addFormField(String name, String value) {
	        writer.append("--" + boundary).append(LINE_FEED);
	        writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);
	        writer.append("Content-Type: text/plain; charset=" + charset).append(LINE_FEED);
	        writer.append(LINE_FEED);
	        writer.append(value).append(LINE_FEED);
	        writer.flush();
	    }
	 
	    /**
	     * Adds post parameters to the request
	     * 
	     * @param parameters parameters to be sent encoded
	     * @throws IOException 
	     */
	    public void addPostParameters(String parameters) throws IOException {
		     		     
	    	outputStream.write(parameters.getBytes(charset));
	    	outputStream.flush();
	    }	    
	    
	    /**
	     * Adds a upload file section to the request
	     * @param fieldName name attribute in <input type="file" name="..." />
	     * @param uploadFile a File to be uploaded
	     * @throws IOException
	     */
	    public void addFilePart(String fieldName, File uploadFile)
	            throws IOException {
	    	
	    	
	    	
	        String fileName = uploadFile.getName();
	        writer.append("--" + boundary).append(LINE_FEED);
	        writer.append("Content-Disposition: form-data; name=\"" + fieldName
	                     + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
	        writer.append("Content-Type: "
	                     + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
	        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
	        writer.append(LINE_FEED);
	        writer.flush();
	 
	        FileInputStream inputStream = new FileInputStream(uploadFile);
	        byte[] buffer = new byte[4096];
	        int bytesRead = -1;
	        
	        while ((bytesRead = inputStream.read(buffer)) != -1) {
	            outputStream.write(buffer, 0, bytesRead);
	        }
	        outputStream.flush();
	        inputStream.close();
	                 
	        writer.append(LINE_FEED);
	        writer.flush();    
	    }
	 
	    /**
	     * Adds a header field to the request.
	     * @param name - name of the header field
	     * @param value - value of the header field
	     */
	    public void addHeaderField(String name, String value) {
	        writer.append(name + ": " + value).append(LINE_FEED);
	        writer.flush();
	    }
	     
	    /**
	     * Completes the request and receives response from the server.
	     * @return a list of Strings as response in case the server returned
	     * status OK, otherwise an exception is thrown.
	     * @throws IOException
	     */
	    public InputStream getInputStream() throws IOException {
	        
	        if(type.equals("POST")) {
	        	if(isMultipart){
		        	writer.append(LINE_FEED).flush();
		        	writer.append("--" + boundary + "--").append(LINE_FEED);
	        	}
	        	
	        	writer.close();
	        	outputStream.close();
	        }
	        	
	        // checks server's status code first
	        int status = connection.getResponseCode();
	        if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_SEE_OTHER) {
	        	inputStream = connection.getInputStream();	           
	        } else {
	        	inputStream = connection.getErrorStream();
	        	throw new TapPlusException(connection.getResponseMessage());
	        }
	        	 
	        return inputStream;
	    }
	    
	    
	    /**
	     * Get a buffered reader around the TAP response
	     * @return
	     * @throws IOException
	     */
	    public BufferedReader finish() throws IOException {
	    	reader = new BufferedReader(new InputStreamReader(getInputStream()));
	    	return reader;
	    }
	    
	    /**
	     * Obtain a List of Strings for the response. To be
	     * used for small responses
	     * @return
	     * @throws IOException
	     */
	    public List<String> getResponse() throws IOException {
	    	
	    	List<String> response = new ArrayList<String>();
	    	finish();
	        String line = null;
            while ((line = reader.readLine()) != null) {
                response.add(line);
            }
	        
            disconnect();
            return response;
	    }
	    
	    /**
	     * Get connection response code
	     * @return
	     * @throws IOException 
	     */
	    public int getResponseCode() throws IOException {
	    	return connection.getResponseCode();
	    }
	    
	    /**
	     * Get connection response message from connection
	     * @return
	     * @throws IOException 
	     */
	    public String getResponseMessage() throws IOException {
	    	return connection.getResponseMessage();
	    }
	    
	    /**
	     * Close reader and connection
	     * @throws IOException
	     */
	    public void disconnect() throws IOException {
	    	reader.close();
	    	connection.disconnect();
		}
	    
	    
	    public void followRedirects(boolean follow){
	    	connection.setInstanceFollowRedirects(follow);
	    }

}
