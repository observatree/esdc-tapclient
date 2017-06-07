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
package esac.archive.gaia.dl.ingestion.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.filefilter.IOFileFilter;

import com.google.common.base.CaseFormat;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;
import uk.ac.starlink.table.StarTableWriter;
import uk.ac.starlink.table.StreamStarTableWriter;
import uk.ac.starlink.table.TableBuilder;
import esac.archive.gaia.dl.ingestion.filters.IFilter;
import esac.archive.gaia.dl.ingestion.objectconverter.IObjectConverter;
import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.IZeroPoints;
import esac.archive.gaia.dl.ingestion.tapupload.Uploader;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLDocumentationExport;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLIndex;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLIndexParameter;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLParameter;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLTable;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLUcd;
import gaia.cu1.tools.dal.ClassMap;
import gaia.cu1.tools.dal.ClassMapImpl;
import gaia.cu1.tools.dal.gbin.DefaultGbinFactory;
import gaia.cu1.tools.dal.gbin.GbinFactory;
import gaia.cu1.tools.dal.gbin.GbinReader;
import gaia.cu1.tools.dal.gbin.GbinReaderV1;
import gaia.cu1.tools.dal.gbin.GbinReaderV2;
import gaia.cu1.tools.dal.gbin.GbinReaderV3;
import gaia.cu1.tools.dal.gbin.GbinReaderWriterFactory;
import gaia.cu1.tools.dm.GaiaRoot;
import gaia.cu1.tools.util.props.PropertyLoader;

public class AuxiliaryFunctions {
	
	private static final Logger logger = Logger.getLogger(AuxiliaryFunctions.class.getName());

	public static StarTableWriter createStiltsTableWriter(
		String stiltsTableWriter) {
		StarTableWriter tableWriter = null;
		// identify stilts class (if provided)
		try {
			logger.log(Level.INFO, "Init " + stiltsTableWriter);
			@SuppressWarnings("unchecked")
			Class<? extends StreamStarTableWriter> c = (Class<? extends StreamStarTableWriter>) Class
					.forName(stiltsTableWriter);
			logger.log(Level.INFO, "Created class");
			Constructor<?> cc = c.getConstructor();
			logger.log(Level.INFO, "Created constructor");
			tableWriter = (StreamStarTableWriter) cc.newInstance();
			logger.log(Level.INFO, "Created instance");
	
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			logger.log(Level.SEVERE, "Error creating Stilts Writer "
					+ stiltsTableWriter + ": " + e.getMessage());
			System.exit(1);
		}
	
		return tableWriter;
	}
	
	public static boolean checkIngestParameters() {
	return true;
	}
	public static boolean checkDumpParameters() {
	return true;
	}
	public static boolean checkTransformParameters() {
	return true;
	}
	
	@SuppressWarnings("unchecked")
	public static IObjectConverter<GaiaRoot, ?> createConversor(String conversorClass){
	IObjectConverter<GaiaRoot, ?> conversor = null;
	// identify conversion class (if provided)
	    try {
	    	conversor = (IObjectConverter<GaiaRoot, ?>) Class.forName(conversorClass).getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			logger.log(Level.SEVERE, "Error creating conversion class for gbin "+conversorClass);
			System.exit(1);
		}
	return conversor;
	}
	
	public static Class<?> createExtractedClass(String extractedClass){
	Class<?> eClass = null;
	// identify extracted class (if provided)
	
		try {
			eClass = Class.forName(extractedClass);
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, "Error creating extracted class for gbin "+extractedClass);
			System.exit(1);
		}
	
	return eClass;
	}
	
	public static TableBuilder createStiltsBuilder(String stilts){
	
	TableBuilder stiltsBuilderObject = null;
	// identify stilts class (if provided)
	
	    try {
	    	logger.log(Level.INFO, "Init " + stilts);
	    	@SuppressWarnings("unchecked")
			Class<? extends TableBuilder> c = (Class<? extends TableBuilder>) Class.forName(stilts);
	    	logger.log(Level.INFO, "Created class");
	    	Constructor<?> cc = c.getConstructor();
	    	logger.log(Level.INFO, "Created constructor");
	    	stiltsBuilderObject = (TableBuilder) cc.newInstance();
	    	logger.log(Level.INFO, "Created instance");
	
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			logger.log(Level.SEVERE, "Error creating Stilts builder "+stilts+ ": " + e.getMessage());
			System.exit(1);
		}
	
	return stiltsBuilderObject;
	}
	
	public static boolean executeDBScripts(String scriptFilePath, Connection conn) throws IOException,SQLException {
	boolean isScriptExecuted = false;
	
	if (conn == null) return isScriptExecuted;
	
	Statement stmt = null;
	BufferedReader in = null;
	StringBuffer sb = null;
	
	try {
		stmt = conn.createStatement();
		in = new BufferedReader(new FileReader(scriptFilePath));
		String str;
		sb = new StringBuffer();
		while ((str = in.readLine()) != null) {
			sb.append(str + "\n ");
		}
		in.close();
		stmt.executeUpdate(sb.toString());
		conn.commit();
		isScriptExecuted = true;
	} 
	catch (Exception e) {
		logger.log(Level.SEVERE, "Exception creating script " + scriptFilePath + ": " + e.getMessage());
		System.exit(1);
	} 
	finally {
		if (stmt != null)
			stmt.close();
		if (in != null)
			in.close();
	}
	return isScriptExecuted;
	} 
	
	public static void loggerInitialization() {
	//Logger initialization
	Handler fh;
	try {
		fh = new FileHandler("logs/ingestion-"+((new java.util.Date()).getTime()+".log"));
		Logger.getLogger("").addHandler(fh);
	} catch (SecurityException e) {
		logger.log(Level.SEVERE, "Security Exception creating log file", e);
	} catch (IOException e) {
		logger.log(Level.SEVERE, "I/O Exception creating log file", e);
	}
	
	}
	
	public static void loggerSetUp(String logging) {
	if(logging.toUpperCase().trim().equals("ALL")){
		logger.setLevel(Level.ALL);
	}else if(logging.toUpperCase().trim().equals("DEBUG")){
		logger.setLevel(Level.FINE);
	}else{
		logger.setLevel(Level.INFO);
	}
	}
	
	public static IOFileFilter getFileFilter(final String filePattern, final String inputFileExtension){
	return new IOFileFilter() {
		@Override
		public boolean accept(File dir, String name) {
			if (name.endsWith("."+inputFileExtension) && name.startsWith(filePattern)) {
				return true;
			} else {
				return false;
			}
		}
		@Override
		public boolean accept(File dir) {
			if (dir.getName().endsWith("."+inputFileExtension) && dir.getName().startsWith(filePattern)) {
				return true;
			} else {
				return false;
			}
		}
	};
	}
	public static void initializeGaiaToolsProperties(){
		
		PropertyLoader.setProperty(GbinReaderWriterFactory.class.getName(), DefaultGbinFactory.class.getName());
		PropertyLoader.setProperty("gaia.cu1.tools.dal.gbin.GbinReaderV4.inflateInputBufferSize", 
				(new Integer (8 * 1024)).toString());
		PropertyLoader.setProperty("gaia.cu1.tools.dal.gbin.GbinReaderV4.inflateBlockSize",
				(new Integer (8 * 1024)).toString());
		PropertyLoader.setProperty(ClassMap.class.getName(), ClassMapImpl.class.getName());
	
		PropertyLoader.setProperty(GbinReader.class.getName() + "." + GbinFactory.GbinVersions.V3.toString(),
				GbinReaderV3.class.getName());
	
		PropertyLoader.setProperty(GbinReader.class.getName() + "." + GbinFactory.GbinVersions.V2.toString(),
				GbinReaderV2.class.getName());
	
		PropertyLoader.setProperty(GbinReader.class.getName() + "." + GbinFactory.GbinVersions.V1.toString(),
				GbinReaderV1.class.getName());
		
	}
	
	public static IFilter createFilterClass(String filterClass) {
	
		IFilter filter = null;
	    try {
	    	logger.log(Level.INFO, "Init " + filterClass);
			filter = (IFilter) Class.forName(filterClass).getConstructor().newInstance();
	    	logger.log(Level.INFO, "Created instance");
	
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			logger.log(Level.SEVERE, "Error creating Stilts builder "+filterClass+ ": " + e.getMessage());
			System.exit(1);
		}
	
		return filter;
	}
	
	public static IZeroPoints createZeroPointsClass(String zeroPoitnsClass) {
		IZeroPoints zero = null;
	    try {
	    	logger.log(Level.INFO, "Init " + zeroPoitnsClass);
	    	zero = (IZeroPoints) Class.forName(zeroPoitnsClass).getConstructor().newInstance();
	    	logger.log(Level.INFO, "Created instance");
	
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			logger.log(Level.SEVERE, "Error creating ZeroPoint Class "+zeroPoitnsClass+ ": " + e.getMessage());
			System.exit(1);
		}
	
		return zero;
	}
	
	public static float[] convertDoublesToFloats(double[] input)
	{
	    if (input == null)
	    {
	        return null; // Or throw an exception - your choice
	    }
	    float[] output = new float[input.length];
	    for (int i = 0; i < input.length; i++)
	    {
	        output[i] = (float)input[i];
	    }
	    return output;
	}
	
	
	public static double fluxToGMagErr(double fluxMean, double fluxErr) {
		return (2.5 / Math.log(10)) * fluxErr / fluxMean;
	}
	
	public static double fluxToGMag(double zeroPoint, double fluxMean) {
		return zeroPoint - 2.5 * Math.log10(fluxMean);
	}
	
	/**
	 * Radians to degrees
	 * @param rad
	 * @return
	 */
	public static double radToDeg(double rad){
		return Math.toDegrees(rad);
	}
	
	/**
	 * Radian to miliarcseconds
	 * @param rad
	 * @return
	 */
	public static double radToMas(double rad){
		return 3600*1000*Math.toDegrees(rad);
	}
	
	public static XMLDocumentationExport readXMLInfo(String xmlFile) throws Exception{
	    XMLDocumentationExport export = null;
	
		try {
		    JAXBContext jc = JAXBContext.newInstance(XMLDocumentationExport.class);
		    
	        Unmarshaller unmarshaller = jc.createUnmarshaller();
	
	        //File f = getFileFromJar(xmlFile);
	        File f = getFileFromInputStream(xmlFile);
	        if (f != null) {
	        	export = (XMLDocumentationExport) unmarshaller.unmarshal(f);
	        }
		}
		catch(Exception ex) {
			throw new Exception ("Error parsing XML file: " + ex.getMessage());
		}
		return export;
	}
	
	public static File getFileFromJar(String xmlFile) throws Exception {
	    File file = null;
	    URL inputURL = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
	
	    String inputFile = xmlFile;
	    if (inputFile.startsWith("jar:")){
	      try {
	        inputURL = new URL(inputFile);
	        JarURLConnection conn = (JarURLConnection)inputURL.openConnection();
	        inputStream = conn.getInputStream();
	        
			// write the inputStream to a FileOutputStream
			file = new File("./temp.xml");
			outputStream = new FileOutputStream(file);
	
			int read = 0;
			byte[] bytes = new byte[1024];
	
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
	
	      } catch (MalformedURLException e1) {
				throw new Exception ("Error parsing XML file: Malformed URL: " + e1.getMessage());
	      } catch (IOException e1) {
				throw new Exception ("Error parsing XML file: IO Error: " + e1.getMessage());
	      }
	      if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
			if (outputStream != null) {
				try {
					// outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
				}
	
			}
	    } 
	    return file;
	}
	
	public static File getFileFromInputStream(String path) {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		File f = null;
		
		try {
			// read this file into InputStream
			inputStream = new FileInputStream(path);
			//inputStream = AuxiliaryFunctions.class.getResourceAsStream(path);
	
			// write the inputStream to a FileOutputStream
			f = new File("./temp.xml");
			outputStream = new FileOutputStream(f);
	
			int read = 0;
			byte[] bytes = new byte[1024];
	
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
	
	
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					// outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	
			}
		}
		return f;
	}
	
	public static String parseType(String type) {
		String parsedType = null;
		if (type.equals("boolean")) {
			parsedType = "BOOLEAN";
		} else if (type.equals("short")) {
			parsedType = "SMALLINT";
		} else if (type.equals("int")) {
			parsedType = "INTEGER";
		} else if (type.equals("long")) {
			parsedType = "BIGINT";
		} else if (type.equals("float")) {
			parsedType = "REAL";
		} else if (type.equals("double")) {
			parsedType = "DOUBLE PRECISION";
		} else if (type.equals("char")) {
			parsedType = "CHAR";
		} else if (type.equals("unsignedByte")) {
			parsedType = "VARBINARY";
		} else if (type.equals("string")) {
			parsedType = "VARCHAR";
		} else if (type.equals("byte")) {
			parsedType = "SMALLINT";
		}
		return parsedType;
	}
	
	public static String calculateTableName(String dataModelTableName) {
		String tableName = null;
		String[] path = dataModelTableName.split("/");
		if (path.length > 0) {
			tableName = path[path.length -1];
			tableName = transformName(tableName);
		}
		return tableName;
	}
	
	public static String transformName(String name) {
		return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
	}
	
	public static boolean isRaDecIndex(XMLTable t, XMLIndex i, String ra, String dec) {
		boolean result = false;
		if (i == null ||  i.getParameters() == null || i.getParameters().getParameters() == null) return result;
				
		boolean raFound = false;
		boolean decFound = false;
	
		for (XMLIndexParameter paramIndex : i.getParameters().getParameters()) {
			if (paramIndex.getName().equals(ra)) {
				raFound = true;
			}
			if (paramIndex.getName().equals(dec)) {
				decFound = true;
			}
		}
		
		result = raFound && decFound && i.getParameters().getParameters().size() == 2;
		return result;
	}

	public static String generateRaDecIndex(XMLTable t, String schema) {
		//alter = "create index on " + schema + "." + tableName + " using btree (q3c_ang2ipi(" + AuxiliaryFunctions.transformName(ids) + "))";
		String index = null;
		List<String> radec = getRaDec(t);
		if (radec != null && radec.size() == 2) {
			String ids = radec.get(0) + "," + radec.get(1);
			index = "create index on " + schema + "." + AuxiliaryFunctions.transformName(AuxiliaryFunctions.calculateTableName(t.getName())) + " using btree (q3c_ang2ipix(" + AuxiliaryFunctions.transformName(ids) + "))";
		}

		return index;
	}
	
	public static List<String> getRaDec(XMLTable t){
		if (t == null || t.getParameterList() == null || t.getParameterList().getParameters() == null) return null;
		
		List<String> result = new ArrayList<String>();
		
		boolean raFound = false;
		String raName = "";
		boolean decFound = false;
		String decName = "";

		for (XMLParameter param : t.getParameterList().getParameters()) {
			if (param.getUcdList() == null || param.getUcdList().getUcdElements() == null) continue;
			
			if (raFound && decFound) break;
			
			if (param.getUcdList().getUcdElements().size() == 2) {
				
				if ((param.getUcdList().getUcdElements().get(0).getName().equals("meta.main") && param.getUcdList().getUcdElements().get(1).getName().equals("pos.eq.ra")) ||
					(param.getUcdList().getUcdElements().get(0).getName().equals("pos.eq.ra") && param.getUcdList().getUcdElements().get(1).getName().equals("meta.main"))) {
						raFound = true;
						raName = param.getName();
				}
				if ((param.getUcdList().getUcdElements().get(0).getName().equals("meta.main") && param.getUcdList().getUcdElements().get(1).getName().equals("pos.eq.dec")) ||
					(param.getUcdList().getUcdElements().get(0).getName().equals("pos.eq.dec") && param.getUcdList().getUcdElements().get(1).getName().equals("meta.main"))) {
						decFound = true;
						decName = param.getName();
				}
			}
		}
		if (raFound && decFound) {
			result.add(raName);
			result.add(decName);
		}
		return result;
	}
}
