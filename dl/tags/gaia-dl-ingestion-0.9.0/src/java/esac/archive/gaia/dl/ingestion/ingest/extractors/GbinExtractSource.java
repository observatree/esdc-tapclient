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
package esac.archive.gaia.dl.ingestion.ingest.extractors;

import java.beans.PropertyVetoException;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.CaseFormat;

import esac.archive.gaia.dl.ingestion.db.JDBCPoolSingleton;
import esac.archive.gaia.dl.ingestion.filters.IFilter;
import esac.archive.gaia.dl.ingestion.ingest.extractors.recursiveobjectparsing.MethodNameParser;
import esac.archive.gaia.dl.ingestion.ingest.extractors.recursiveobjectparsing.ReturnedObjectsParser;
import esac.archive.gaia.dl.ingestion.main.AuxiliaryFunctions;
import esac.archive.gaia.dl.ingestion.objectconverter.IObjectConverter;
import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.IZeroPoints;
import gaia.cu1.tools.dal.gbin.GbinFactory;
import gaia.cu1.tools.dal.gbin.GbinReader;
import gaia.cu1.tools.dm.GaiaRoot;
import gaia.cu1.tools.exception.GaiaException;
import gaia.cu1.tools.time.GaiaTime;
import gaia.cu1.tools.util.props.PropertyLoader;

/**
 * Extractor of Gaia DM objects obtained from a GBIN file onto 
 * INSERT statements with the provided table name into the JDBC connection
 * 
 * @author jgonzale
 *
 */
public class GbinExtractSource extends ExtractSource{
	
	
	private boolean gaiaPropertiesInitialized = false;
	private static final Logger logger = Logger.getLogger(GbinExtractSource.class.getName());
	

	
	
	/**
	 * Goes through one GbinReader parsing objects
	 * @param reader Gbin Reader
	 * @param dmObject Object in the Gaia Data Model processed through the reader
	 * @return number of sources processed
	 * @throws Exception 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 * @throws PropertyVetoException 
	 * @throws SQLException 
	 * @throws GaiaException 
	 */
	@Override
	public Integer process(File gbinFile, String schema, String tableName, Class<?> dmObject, IObjectConverter<GaiaRoot, ?> conversor, IFilter filter, IZeroPoints zPoints) 
			throws IllegalArgumentException, SecurityException, 
				IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException, PropertyVetoException, GaiaException{

		if (!gaiaPropertiesInitialized) {
			AuxiliaryFunctions.initializeGaiaToolsProperties();
			gaiaPropertiesInitialized = true;
		}
		
		logger.log(Level.INFO, "Starting process");

		// DB connection preparation
		
		Connection con = JDBCPoolSingleton.getInstance().getConnection();
		logger.log(Level.INFO, "Get connection");

		PreparedStatement insertValues = null;
		
		// INSERT statement preparation
		
		String table = schema + "." + tableName;
		
		String columns = "";
		String values = "";
		List<String> methods = MethodNameParser.getGaiaMethodNames(dmObject);
		for(String method:methods){
			columns += CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, method.replace("get", "")) + ",";
			values += "?,";
		}
		logger.log(Level.INFO, "Get Methods names");

		//remove last comma
		columns = columns.replaceAll(",$", "");
		values = values.replaceAll(",$", "");
		
		String insertStatement =
				"INSERT INTO "+table+ "("+columns+")" 
						+ "VALUES ("+values+");";

		logger.log(Level.INFO, "Insertion prepared statement "+insertStatement);

		con.setAutoCommit(false);
		insertValues = con.prepareStatement(insertStatement);

		
		// Parsing of the object set, insertion into the prepared statement and execution in batches
		int objectNumberCounter = 0;

		logger.log(Level.INFO, "File "+gbinFile.getAbsolutePath());

		logger.log(Level.INFO, "File "+gbinFile.getAbsolutePath() + " exists " +gbinFile.exists());
		final GbinReader<GaiaRoot> reader = GbinFactory.getGbinReader(gbinFile);
		
		logger.log(Level.INFO, "Readed File "+gbinFile.getAbsolutePath());

		try {
		while (reader.hasNext()) {
			GaiaRoot source = reader.next();
			
			if(conversor!=null){
				source = (GaiaRoot) conversor.transform(source, filter, zPoints);
			}
			
			if (source == null) continue;
			
			List<Object> objects = ReturnedObjectsParser.getGaiaObjectValues(source);
			
			logger.log(Level.FINE, "Found "+objects.size()+" columns");
			
			//index for placement in value list
			int methodIdx = 1;
			for(Object obj:objects){
				logger.log(Level.FINE, "idx "+methodIdx+" class "+obj);
				if(obj == null){			
					insertValues.setNull(methodIdx, Types.NULL);
					
				} else if(obj instanceof Integer){			
					insertValues.setInt(methodIdx, (Integer) obj);
				} else if(obj instanceof int[]  && !obj.getClass().isArray()){
					insertValues.setInt(methodIdx, Integer.valueOf((int)obj));
				} else if(obj instanceof int[]  && obj.getClass().isArray()){
					insertValues.setString(methodIdx, Arrays.toString((int []) obj));
					
				}else if (obj instanceof Double){
					insertValues.setDouble(methodIdx, (Double) obj);
				} else if(obj instanceof double[] && !obj.getClass().isArray() ){
					insertValues.setDouble(methodIdx, Double.valueOf((double)obj));
				} else if(obj instanceof double[] && obj.getClass().isArray() ){
					insertValues.setString(methodIdx, Arrays.toString((double []) obj));
					
				}else if (obj instanceof Float){
					insertValues.setFloat(methodIdx, (Float) obj);
				} else if(obj instanceof float[] && !obj.getClass().isArray() ){
					insertValues.setFloat(methodIdx, Float.valueOf((float)obj));
				} else if(obj instanceof float[] && obj.getClass().isArray() ){
					insertValues.setString(methodIdx, Arrays.toString((float []) obj));
				
				}else if (obj instanceof Short){
					insertValues.setShort(methodIdx, (Short) obj);
				} else if(obj instanceof short[] && !obj.getClass().isArray() ){
					insertValues.setShort(methodIdx, Short.valueOf((short)obj));
				} else if(obj instanceof short[] && obj.getClass().isArray() ){
					insertValues.setString(methodIdx, Arrays.toString((short []) obj));
				
				
				}else if (obj instanceof Boolean){		
					insertValues.setBoolean(methodIdx, (Boolean) obj);
				}else if(obj instanceof boolean[] && !obj.getClass().isArray()){
					insertValues.setBoolean(methodIdx, Boolean.valueOf((boolean)obj));
				}else if(obj instanceof boolean[] && obj.getClass().isArray()){
					insertValues.setString(methodIdx, Arrays.toString((boolean []) obj));
				
				}else if (obj instanceof Long){		
					insertValues.setLong(methodIdx, (Long) obj);
				} else if(obj instanceof long[] && !obj.getClass().isArray()){
					insertValues.setLong(methodIdx, Long.valueOf((long)obj));
				} else if(obj instanceof long[] && obj.getClass().isArray()){
					insertValues.setString(methodIdx, Arrays.toString((long []) obj));
				
				}else if (obj instanceof Byte){		
					insertValues.setByte(methodIdx, (Byte) obj);
				} else if(obj instanceof byte[] && !obj.getClass().isArray()){
					insertValues.setBytes(methodIdx, (byte[])obj);
				} else if(obj instanceof byte[] && obj.getClass().isArray()){
					insertValues.setString(methodIdx, Arrays.toString((byte []) obj));
				
					
				}else if (obj instanceof String){		
					insertValues.setString(methodIdx, (String) obj);
				} else if(obj instanceof Enum){
					insertValues.setInt(methodIdx, ((Enum)obj).ordinal());
				} else if(obj instanceof GaiaTime){
					insertValues.setTime(methodIdx, new Time(((GaiaTime) obj).getElapsedNanoSecs()));
				} else if(obj.getClass().isArray() ){
					insertValues.setString(methodIdx, Arrays.deepToString((Object[]) obj));
				}else{
					insertValues.setString(methodIdx, obj.toString());
				}
				methodIdx++;
			}
			insertValues.addBatch();
			objectNumberCounter ++;
			if(objectNumberCounter % 10000 == 0){
				insertValues.executeBatch();
				con.commit();
				logger.log(Level.INFO, "Executing batch update "+insertStatement);
			}
		}
		}
		catch(SQLException ex) {
			logger.log(Level.SEVERE, "EXCEPTION while inserting "+ ex.getMessage());
			logger.log(Level.SEVERE, "EXCEPTION while inserting "+ ex.getNextException());
			objectNumberCounter = 0;
		}
		catch(Exception ex) {
			logger.log(Level.SEVERE, "General Exception "+ ex.getMessage());
			objectNumberCounter = 0;

		}
		//insertion of the last batch (typically less than 10K)
		insertValues.executeBatch();
		logger.log(Level.FINE, "Executing batch update "+insertStatement);
				

		con.commit();
		reader.close();
		con.close();
		logger.log(Level.INFO, "Thread for "+gbinFile.getAbsolutePath()+" exiting having ingested "+objectNumberCounter+" sources");

		
		return objectNumberCounter;
	}
}
