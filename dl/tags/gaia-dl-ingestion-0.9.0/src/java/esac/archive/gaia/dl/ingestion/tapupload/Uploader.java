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
package esac.archive.gaia.dl.ingestion.tapupload;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.CaseFormat;

import esac.archive.gaia.dl.ingestion.db.JDBCPoolSingleton;
import esac.archive.gaia.dl.ingestion.main.AuxiliaryFunctions;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLDocumentationExport;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLParameter;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLPrimaryKey;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLTable;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLUcd;

public class Uploader {
	private static final Logger logger = Logger.getLogger(Uploader.class.getName());

	public boolean upload(String schema, String ra, String dec, boolean public_schema, boolean public_table, String xmlFile, String dataModelTableToUpload, String tap_schema, String tap_alltables, String tap_allschemas, String tap_allcolumns, String classToIngest) throws Exception{
		boolean uploaded = false;
		XMLDocumentationExport export = null;
		try 
		{
			export = AuxiliaryFunctions.readXMLInfo(xmlFile);
			for (XMLTable t : export.getTables()) {
				if (t.getName().equals(dataModelTableToUpload)) {
					System.out.println(t.getName());
					dataModelTableToUpload = AuxiliaryFunctions.calculateTableName(dataModelTableToUpload);
					if (dataModelTableToUpload == null) {
						throw new Exception ("Not valid table name: " + t.getName());
					}
					uploaded = tapUpload(schema, dataModelTableToUpload, ra, dec, t.getPk(), public_schema, public_table, t, tap_schema, tap_alltables, tap_allschemas, tap_allcolumns, classToIngest);
					break;
				}
			}
		}
		catch (Exception ex) {
			throw new Exception ("Error uploading table to tap: " + ex.getMessage());
		}
		logger.log(Level.INFO, "UPLOAD SUCCESS");
		return uploaded;
	}

	private boolean tapUpload(String schema, String tableName, String ra, String dec, XMLPrimaryKey pk, boolean public_schema, boolean public_table, XMLTable t, String tap_schema, String tap_alltables, String tap_allSchemas,
			String tap_allcolumns, String classToIngest) throws Exception{
		
		int result = 0;
		ResultSet rs = null;
		Connection con = null;
		Statement stmt = null;
		try //row in all_tables (and in all_schemas if it is needed) will be inserted now
		{
			con = JDBCPoolSingleton.getInstance().getConnection();
			con.setAutoCommit(false);

			String checkStatement = "select * from " + tap_schema + "." + tap_allSchemas + " where schema_name='" + schema + "'";
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			logger.log(Level.INFO, "CHECK QUERY: " + checkStatement);

			rs = stmt.executeQuery(checkStatement);
			
			if (!rs.last() || rs.getRow() == 0) {//row in all_schemas will be created
				String insert_schema = "insert into " + tap_schema + "." + tap_allSchemas + " (schema_name,public) values ('" + schema + "', " + public_schema + ")" ;
				logger.log(Level.INFO, "INSERT in all_schemas: " + insert_schema);

				result = stmt.executeUpdate(insert_schema);
				if (result != 1) {
					logger.log(Level.SEVERE, "INSERT in all_schemas ERROR");
					throw new Exception ("Error trying to insert schema " + schema + " in " + tap_allSchemas);
				}
				else {
					logger.log(Level.INFO, "INSERT in all_schemas DONE");
				}
			}
			
			String table = tap_schema + "." + tap_alltables;
			String columns = "table_name,schema_name,table_type,description,utype,flags,size,public";
			String values = "";
			
			values += "'" + tableName + "'" + ",";
			values += "'" + schema + "'" + ",";
			values += "'table'" + ",";
			String description = null;
			if (!t.getDescription().equals("")) {
				description = t.getDescription();
			}
			values += "'" + description + "'" + ",";		
			values += "'" + classToIngest + "'" + ",";
			values += "'" + 1 + "'" + ",";
			values += "NULL" + ",";
			values += public_table == true ? true: false;	
	
			String insertStatement =
					"INSERT INTO "+table+ " ("+columns+")" 
							+ "VALUES ("+values+");";
			
			logger.log(Level.INFO, "INSERT QUERY: " + insertStatement);

			result = stmt.executeUpdate(insertStatement);
		}
		catch(Exception ex) {
			throw new Exception ("Error trying to insert row in all_tables (or in all_schemas) table: " + ex.getMessage());
		}
		finally{
			if (rs != null) {
				rs.close();
			}
			if (con != null) {
				con.commit();
			}
		}
		if (result == 1) {//rows in all_columns will be inserted now
			result = 0;
			XMLParameter p = null;
			try
			{
				//CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, method.replace("get", "")) + ",";
				con = JDBCPoolSingleton.getInstance().getConnection();
				con.setAutoCommit(false);

				stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				String table = tap_schema + "." + tap_allcolumns;
				String columns = "column_name,description,ucd,utype,datatype,unit,table_name,schema_name,size,principal,std,indexed,flags,public";
				String values = "";
				
				for (XMLParameter parameter : t.getParameterList().getParameters()) {
					p = parameter;
					values = "";
					values += "'" + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, parameter.getName()) + "'" + ",";
					values += "'" + parameter.getDetailedDescription() + "'" + ",";
					values += "'" + concatenateUcdList(parameter.getUcdList().getUcdElements()) + "'" + ",";
					values += "'" + calculateUtype(parameter, ra, dec) + "'" + ",";
					values += "'" + AuxiliaryFunctions.parseType(parameter.getType()) + "'" + ",";		
					values += "'" + parameter.getUnit() + "'" + ",";
					values += "'" + tableName + "'" + ",";
					values += "'" + schema + "'" + ",";
					values += "NULL" + ",";//size
					values += "'" + 0 + "'" + ",";//principal
					values += "'" + 0 + "'" + ",";//std
					values += "'" + 1 + "'" + ",";//indexed
					values += "'" + calculateFlags(parameter,ra, dec, pk) + "'"+ ",";//flags
					values += public_table == true ? true : false;	
			
					String insertStatement =
							"INSERT INTO "+table+ " ("+columns+")" 
									+ "VALUES ("+values+");";
					
					logger.log(Level.INFO, "INSERT QUERY: " + insertStatement);
	
					result += stmt.executeUpdate(insertStatement);
				}
			}
			catch(Exception ex) {
				throw new Exception ("Error trying to insert row in all_columns table (" + p.getName() + "): " + ex.getMessage());
			}
			finally {
				if (con != null) {
					con.commit();
				}
			}
		}
		
		return result == t.getParameterList().getParameters().size();
	}

	private String calculateUtype(XMLParameter parameter, String ra, String dec) {
		String utype = "";
		if (parameter.getUtype() == null || parameter.getUtype().equals("")) {
			if (parameter.getName().equals(ra)) {
				utype = "Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C1";
			}
			else if (parameter.getName().equals(dec)) {
				utype = "Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C2";
			}
		}

		return utype;
	}

	private int calculateFlags(XMLParameter parameter, String ra,
			String dec, XMLPrimaryKey pk) {
		int flag = 0;
		String name = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, parameter.getName());
		if (name.equals(ra)) {
			flag = 1;
		} else if (name.equals(dec)) {
			flag = 2;
		} else if (isPk(name, pk)) {
			flag = 16;
		}
		return flag;
	}

	private boolean isPk(String name, XMLPrimaryKey pk) {
		boolean isPk = false;
		if (pk == null ||
				pk.getParameters() == null ||
				pk.getParameters().getParameters() == null) {
		}
		else {
			for (int i=0;i < pk.getParameters().getParameters().size(); i++) {
				String name_pk = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, pk.getParameters().getParameters().get(i).getName());
				if (name.equals(name_pk)) {
					isPk = true;
					break;
				}
			}
		}
		return isPk;
	}

	private String concatenateUcdList(List<XMLUcd> ucdElements) {
		String ucdList = "";
		if (ucdElements != null) {
			for (XMLUcd ucd : ucdElements) {
				ucdList += ucd.getName() + ";";
			}
			ucdList = ucdList.replaceAll(";$", "");
		}

		return ucdList;
	}

}
