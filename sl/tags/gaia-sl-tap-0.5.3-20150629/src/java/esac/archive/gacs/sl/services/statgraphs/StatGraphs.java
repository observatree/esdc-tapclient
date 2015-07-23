package esac.archive.gacs.sl.services.statgraphs;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StatGraphs {

	public String appId;
	
	public static final String PARAM_TYPE   = "TYPE";
	public static final String PARAM_TABLE  = "TABLE";
	public static final String PARAM_COLUMN   = "COLUMN";
	public static final String PARAM_CHECK  = "CHECK";
	public static final String PARAM_STATMETADATA  = "STATMETADATA";
	public static final String PARAM_WIDTH   = "WIDTH";
	public static final String PARAM_HEIGHT   = "HEIGHT";


	public StatGraphs(String appId){
		this.appId = appId;
	}
	
	public void executeRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String table     	= request.getParameter(PARAM_TABLE);
		String graphtype 	= request.getParameter(PARAM_TYPE);
		String column 		= request.getParameter(PARAM_COLUMN);

		StatResourceManager statResourceManager = StatResourceFactory.getStatResourceManager(appId);


		OutputStream out = response.getOutputStream();
		if(request.getParameter(PARAM_CHECK)!=null){
			 
			response.setContentType("text/plain");

			
			if(statResourceManager.exists(table,graphtype,column)) {
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
			
			if(!statResourceManager.exists(table,graphtype,column)){
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
			
			InputStream ins = statResourceManager.getResource(table,graphtype,column);
			if(width>0 && height>0){
				writeScaled(ins,out,width,height);
			}else{
				response.setHeader("Content-Disposition", "inline; filename="+statResourceManager.getResourceName(table,graphtype,column)+";");    
				response.setContentLength((int)statResourceManager.getResourceLength(table,graphtype,column));
				
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
