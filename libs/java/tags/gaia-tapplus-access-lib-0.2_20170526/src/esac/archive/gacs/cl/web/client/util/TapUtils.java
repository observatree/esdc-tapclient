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
package esac.archive.gacs.cl.web.client.util;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import esac.archive.gacs.cl.tapclient.tapSchema.DbColumn;
import esac.archive.gacs.cl.tapclient.tapSchema.DbSchema;
import esac.archive.gacs.cl.tapclient.tapSchema.DbTable;

public class TapUtils {
		
		/**
		 * Parses a XML output of a TAP service to obtain the public table schemas.
		 * @param xml data to parse
		 * @return a List with the requested public schemas.
		 * @throws IOException 
		 * @throws SAXException 
		 * @throws ParserConfigurationException 
		 */
		public static List<DbSchema> getTableSchemas(InputStream tapSchemaInputStream) throws ParserConfigurationException, SAXException, IOException{
			return getTableSchemas(tapSchemaInputStream, null);
		}


		/**
		 * Parses a XML output of a TAP service to obtain the requested table schemas plus the public ones.
		 * @param xml data to parse
		 * @param otherSchemasToAdd Map with the schema name and alias to show to be searched for.
		 * @return a List with the requested schemas plus the public ones.
		 * @throws ParserConfigurationException 
		 * @throws IOException 
		 * @throws SAXException 
		 */
		public static List<DbSchema> getTableSchemas(InputStream tapSchemaInputStream, Map<String,String> otherSchemasToAdd) throws ParserConfigurationException, SAXException, IOException{
			
			
			ArrayList<DbSchema> tableSchemas = new ArrayList<DbSchema>();
			
			 DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();  
			 DocumentBuilder 		documentBuilder = documentFactory.newDocumentBuilder();  
				  
			 // define root elements  
			 Document dom = documentBuilder.parse(tapSchemaInputStream);

			 // Process schemas: first: only the public schema
			 parseSchema(tableSchemas, dom, "public", "GAIA tables");
			
			if(otherSchemasToAdd != null){
				//Add other schemas
				for(Entry<String, String> otherSchema: otherSchemasToAdd.entrySet()){
					parseSchema(tableSchemas, dom, otherSchema.getKey(), otherSchema.getValue());
				}
			}

			generateIds(tableSchemas);
			
			return tableSchemas;
		}
		
		
		private static void parseSchema(List<DbSchema> tableSchemas, Document dom, String schemaName, String nameToShow){
			
			for(int s=0; s<dom.getElementsByTagName("schema").getLength(); s++){
				
				DbSchema sch = parseSchemaNode(dom.getElementsByTagName("schema").item(s));
				if(!sch.getName().equalsIgnoreCase(schemaName)) {
					continue;
				}
				sch.setName(nameToShow);
				tableSchemas.add(sch);
			}
		}


		/**
		 * Parses a schema from XML output of tap/tables service.
		 * @param node
		 * @return
		 */
		private static DbSchema parseSchemaNode(Node node){
			DbSchema schema = new DbSchema("");
			NodeList children = node.getChildNodes();

			for(int i=0; i<children.getLength(); i++){
				if(children.item(i).getNodeName().equalsIgnoreCase("name")) schema.setName(children.item(i).getFirstChild().getNodeValue());
				if(children.item(i).getNodeName().equalsIgnoreCase("table")){
					DbTable table = parseTableNode(children.item(i));
					schema.addTable(table);
				}
			}

			return schema;
		}

		/**
		 * Parses a table from a XML table node of a tap/tables service output.
		 * @param node
		 * @return
		 */
		private static DbTable parseTableNode(Node node){
			DbTable table = new DbTable();
			NodeList children = node.getChildNodes();

			for(int i=0; i<children.getLength(); i++){
				if(children.item(i).getNodeName().equalsIgnoreCase("name")) table.setName(children.item(i).getFirstChild().getNodeValue());
				if(children.item(i).getNodeName().equalsIgnoreCase("column")){
					DbColumn column = parseColumnNode(children.item(i));
					table.addColumn(column);
				}
			}

			return table;
		}

		/**
		 * Parses a column from a XML column node of a tap/tables service output.
		 * @param node
		 * @return
		 */

		private static DbColumn parseColumnNode(Node node){
			DbColumn column = new DbColumn();
			NodeList children = node.getChildNodes();

			for(int i=0; i<children.getLength(); i++){
				if(children.item(i).getFirstChild()== null) continue;
				if(children.item(i).getNodeName().equalsIgnoreCase("name")) column.setName(children.item(i).getFirstChild().getNodeValue());
				if(children.item(i).getNodeName().equalsIgnoreCase("description")) column.setDesc(children.item(i).getFirstChild().getNodeValue());
				if(children.item(i).getNodeName().equalsIgnoreCase("unit")) column.setUnit(children.item(i).getFirstChild().getNodeValue());
				if(children.item(i).getNodeName().equalsIgnoreCase("utype")) column.setUtype(children.item(i).getFirstChild().getNodeValue());
				if(children.item(i).getNodeName().equalsIgnoreCase("ucd")) column.setUcd(children.item(i).getFirstChild().getNodeValue());
				if(children.item(i).getNodeName().equalsIgnoreCase("dataType")) column.setDataType(children.item(i).getFirstChild().getNodeValue());
				if(children.item(i).getNodeName().equalsIgnoreCase("flag")){
					if(isIndexed(children.item(i).getFirstChild().getNodeValue())){
						column.setIndexed(true);
					}
				}
			}

			return column;
		}

		/**
		 * Searches for 'indexed' literal inside nodeValue argument.
		 * @param nodeValue node value
		 * @return 'true' if nodeValue contains 'indexed'
		 */
		private static boolean isIndexed(String nodeValue){
			if(nodeValue == null){
				return false;
			}
			return nodeValue.contains("indexed");
		}

		/**
		 * Generate unique identifiers for tables and columns to be represented in the view.
		 * @param tableSchemas
		 */
		private static void generateIds(ArrayList<DbSchema> tableSchemas){

			int id=0;
			for(DbSchema sch: tableSchemas){
				for(DbTable tbl: sch.getTables()){
					tbl.setId(id++);
					for(DbColumn col: tbl.getColumns()){
						col.setId(id++);
					}
				}
			}

		}

	}


