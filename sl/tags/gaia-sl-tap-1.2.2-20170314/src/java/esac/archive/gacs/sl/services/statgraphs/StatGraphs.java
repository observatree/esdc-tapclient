/*******************************************************************************
 * Copyright (C) 2017 rgutierrez
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
package esac.archive.gacs.sl.services.statgraphs;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import esavo.tap.TAPException;
import esavo.tap.TAPService;
import esavo.uws.UwsException;
import esavo.uws.output.UwsOutputResponseHandler;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.security.UwsSecurity;
import esavo.uws.utils.UwsUtils;

public class StatGraphs {
	private static final Logger LOG = Logger.getLogger(StatGraphs.class.getName());

	public TAPService service;
	
	public static final String PARAM_TYPE   = "TYPE";
	public static final String PARAM_TABLE  = "TABLE";
	public static final String PARAM_COLUMN   = "COLUMN";
	public static final String PARAM_CHECK  = "CHECK";
	public static final String PARAM_STATMETADATA  = "STATMETADATA";
	public static final String PARAM_WIDTH   = "WIDTH";
	public static final String PARAM_HEIGHT   = "HEIGHT";


	public StatGraphs(TAPService service){
		this.service = service;
	}
	
	public void executeRequest(HttpServletRequest request, HttpServletResponse response) {
		UwsOutputResponseHandler outputHandler = service.getFactory().getOutputHandler();
		
		String table     	= request.getParameter(PARAM_TABLE);
		String graphtype 	= request.getParameter(PARAM_TYPE);
		String column 		= request.getParameter(PARAM_COLUMN);

		StatResourceManager statResourceManager = StatResourceFactory.getStatResourceManager(service.getFactory().getAppId());

		try {
			UwsSecurity security = service.getFactory().getSecurityManager();
			UwsJobOwner user = security.getUser(request);
			
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
			response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
			response.setDateHeader("Expires", 0); // Proxies.
			
			OutputStream out = response.getOutputStream();
			if(request.getParameter(PARAM_CHECK)!=null){
				 
				response.setContentType("text/plain");
				
				if(statResourceManager.hasAccess(user, table, column) && statResourceManager.exists(user, table,graphtype,column)) {
					out.write("TRUE".getBytes());
				}else{
					out.write("FALSE".getBytes());
				}
				
			}else if(request.getParameter(PARAM_STATMETADATA)!=null){
				response.setContentType("text/plain");
	
				
				byte[] buffer = new byte[1024];
				int len = 0;
				
				InputStream ins = statResourceManager.getResourcesMetadata();
				
				while((len=ins.read(buffer))>0){
					out.write(buffer, 0, len);
				}
				
				ins.close();
	
				
			}else{
				response.setContentType("image/png");
				
				if(!statResourceManager.hasAccess(user, table,column) || !statResourceManager.exists(user, table,graphtype,column)){
					table="not_available";
				}
				
				int width = 0;
				int height = 0;
				if(request.getParameter(PARAM_WIDTH)!=null && request.getParameter(PARAM_HEIGHT)!=null){
					try{
						width    = Integer.parseInt(request.getParameter(PARAM_WIDTH));
						height 	= Integer.parseInt(request.getParameter(PARAM_HEIGHT));
					}catch(NumberFormatException e){
						e.printStackTrace();
					}
					
				}
				
				InputStream ins = statResourceManager.getResource(user, table,graphtype,column);
				if(width>0 && height>0){
					writeScaled(ins,out,width,height);
				}else{
					response.setHeader("Content-Disposition", "inline; filename="+statResourceManager.getResourceName(user, table,graphtype,column)+";");    
					response.setContentLength((int)statResourceManager.getResourceLength(user, table,graphtype,column));
					
					byte[] buffer = new byte[1024];
					int len = 0;
					
					
					while((len=ins.read(buffer))>0){
						out.write(buffer, 0, len);
					}
				}
					
				ins.close();
			}
			out.flush();
			out.close();
			
		} catch (UwsException e) {
			try {
				outputHandler.writeServerErrorResponse(response, "Executing handlers", e);
			} catch (UwsException e2) {
				LOG.severe("Cannot write output for request '"+request.getRequestURI()+"' : " + e.getMessage() + "\n" + UwsUtils.dumpStackTrace(e));
			}
			return;
		} catch (TAPException e) {
			try {
				outputHandler.writeServerErrorResponse(response, "Executing handlers", new UwsException(e));
			} catch (UwsException e2) {
				LOG.severe("Cannot write output for request '"+request.getRequestURI()+"' : " + e.getMessage() + "\n" + UwsUtils.dumpStackTrace(e));
			}
			return;
		} catch (IOException e) {
			LOG.severe("Cannot write output for request '"+request.getRequestURI()+"' : " + e.getMessage() + "\n" + UwsUtils.dumpStackTrace(e));
			return;
		} catch (Exception e){
			//e.printStackTrace();
			LOG.severe(e.getMessage());
		}

	}
	
	
    /**
     * Resizes an image to a absolute width and height (the image may not be
     * proportional)
     * @param inputStream input stream from the original image
     * @param outputStream stream to write output file
     * @param scaledWidth absolute width in pixels
     * @param scaledHeight absolute height in pixels
     * @throws IOException
     */
    public static void writeScaled(InputStream inputStream,
            OutputStream outputStream, int width, int height)
            throws IOException {
        // reads input image
        BufferedImage inputImage = ImageIO.read(inputStream);
 
        // creates output image
        //BufferedImage outputImage = new BufferedImage(width,
        //        height, inputImage.getType());
        BufferedImage outputImage = new BufferedImage(width,
                        height, BufferedImage.TYPE_INT_RGB);
 
        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, width, height, null);
        g2d.dispose();
 
        // writes to output file
        ImageIO.write(outputImage, "PNG", outputStream);
    }
    

}
