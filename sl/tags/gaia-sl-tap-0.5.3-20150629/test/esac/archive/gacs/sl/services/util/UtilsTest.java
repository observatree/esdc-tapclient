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
package esac.archive.gacs.sl.services.util;

import java.io.IOException;
import java.security.InvalidParameterException;

import junit.framework.Assert;

import org.junit.Test;

import esac.archive.gacs.sl.tap.actions.TapServlet;
import esac.archive.gacs.sl.tap.actions.TapTableInfo;
import esac.archive.gacs.sl.test.http.DummyHttpRequest;
import esac.archive.gacs.sl.test.http.DummyServletConfig;
import esac.archive.gacs.sl.test.http.DummyServletContext;
import esavo.tap.TAPSchemaInfo;

public class UtilsTest {
	
	@Test
	public void testIsRaDec(){
		Assert.assertFalse("No ra/dec", Utils.isRaOrDec(0));
		Assert.assertTrue("ra", Utils.isRaOrDec(Utils.TAP_COLUMN_TABLE_FLAG_RA));
		Assert.assertTrue("dec", Utils.isRaOrDec(Utils.TAP_COLUMN_TABLE_FLAG_DEC));
	}
	
	@Test
	public void testConvertTapTableFlag(){
		Assert.assertEquals("null flag", 0, Utils.convertTapTableFlag(null));
		Assert.assertEquals("0 flag", 0, Utils.convertTapTableFlag("0"));
		checkTapTableFlag(Utils.TAP_COLUMN_TABLE_FLAG_ID_RA, Utils.TAP_COLUMN_TABLE_FLAG_RA, "ra");
		checkTapTableFlag(Utils.TAP_COLUMN_TABLE_FLAG_ID_DEC, Utils.TAP_COLUMN_TABLE_FLAG_DEC, "dec");
		checkTapTableFlag(Utils.TAP_COLUMN_TABLE_FLAG_ID_MAG, Utils.TAP_COLUMN_TABLE_FLAG_MAG, "mag");
		checkTapTableFlag(Utils.TAP_COLUMN_TABLE_FLAG_ID_FLUX, Utils.TAP_COLUMN_TABLE_FLAG_FLUX, "flux");
		
		int flags = Utils.convertTapTableFlag(Utils.TAP_COLUMN_TABLE_FLAG_ID_RA + ", " + Utils.TAP_COLUMN_TABLE_FLAG_ID_DEC);
		Assert.assertTrue("ra found", (flags & Utils.TAP_COLUMN_TABLE_FLAG_RA) > 0);
		Assert.assertTrue("dec found", (flags & Utils.TAP_COLUMN_TABLE_FLAG_DEC) > 0);
		Assert.assertFalse("mag should not be found", (flags & Utils.TAP_COLUMN_TABLE_FLAG_MAG) > 0);
		Assert.assertFalse("flux should not be found", (flags & Utils.TAP_COLUMN_TABLE_FLAG_FLUX) > 0);
		
		flags = Utils.convertTapTableFlag("100");
		Assert.assertEquals("Number direct conversion", 100, flags);
	}
	
	private void checkTapTableFlag(String flag, int expected, String msg){
		Assert.assertEquals(msg + " 1", expected, Utils.convertTapTableFlag(flag));
		Assert.assertEquals(msg + " 2", expected, Utils.convertTapTableFlag(" \n"+flag+" \n"));
		Assert.assertEquals(msg + " 3", expected, Utils.convertTapTableFlag(flag.toUpperCase()));
		Assert.assertEquals(msg + " 4", expected, Utils.convertTapTableFlag(flag.toLowerCase()));
		String tmp = "" + Character.toUpperCase(flag.charAt(0)) + flag.substring(1);
		Assert.assertEquals(msg + " 5", expected, Utils.convertTapTableFlag(tmp));
	}
	
	@Test
	public void testGetFlagIds(){
		Assert.assertEquals("No flags", "", Utils.getFlagIds(0));
		Assert.assertEquals("Flag ra", Utils.TAP_COLUMN_TABLE_FLAG_ID_RA, Utils.getFlagIds(Utils.TAP_COLUMN_TABLE_FLAG_RA));
		Assert.assertEquals("Flag dec", Utils.TAP_COLUMN_TABLE_FLAG_ID_DEC, Utils.getFlagIds(Utils.TAP_COLUMN_TABLE_FLAG_DEC));
		Assert.assertEquals("Flag mag", Utils.TAP_COLUMN_TABLE_FLAG_ID_MAG, Utils.getFlagIds(Utils.TAP_COLUMN_TABLE_FLAG_MAG));
		Assert.assertEquals("Flag flux", Utils.TAP_COLUMN_TABLE_FLAG_ID_FLUX, Utils.getFlagIds(Utils.TAP_COLUMN_TABLE_FLAG_FLUX));
		int flags = Utils.TAP_COLUMN_TABLE_FLAG_RA | Utils.TAP_COLUMN_TABLE_FLAG_DEC | Utils.TAP_COLUMN_TABLE_FLAG_MAG | Utils.TAP_COLUMN_TABLE_FLAG_FLUX;
		String allFlags = Utils.TAP_COLUMN_TABLE_FLAG_ID_RA + "," + Utils.TAP_COLUMN_TABLE_FLAG_ID_DEC + "," +
				Utils.TAP_COLUMN_TABLE_FLAG_ID_FLUX + "," + Utils.TAP_COLUMN_TABLE_FLAG_ID_MAG;
		Assert.assertEquals("All flags", allFlags, Utils.getFlagIds(flags));
	}
	
	@Test
	public void testAreRaDecAlreadyIndexed(){
		String schemaName = "schema";
		String tableName = "table";
		String raColumn = "col1";
		String decColumn = "col2";
		
		TapTableInfo tti = new TapTableInfo(schemaName, tableName);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_FLAGS, Integer.class);
		tti.putColumn(raColumn, Utils.TAP_COLUMNS_TABLE_FLAGS, "a");
		tti.putColumn(decColumn, Utils.TAP_COLUMNS_TABLE_FLAGS, "b");
		try{
			Utils.areAlreadyIndexedRaDec(tti, raColumn, decColumn);
			Assert.fail("Exception expected I (string instead of integer)");
		}catch(ClassCastException e){
		}
		
		tti = new TapTableInfo(schemaName, tableName);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_FLAGS, Integer.class);
		tti.putColumn(raColumn, Utils.TAP_COLUMNS_TABLE_FLAGS, Utils.TAP_COLUMN_TABLE_FLAG_RA);
		tti.putColumn(decColumn, Utils.TAP_COLUMNS_TABLE_FLAGS, "b");
		try{
			Utils.areAlreadyIndexedRaDec(tti, raColumn, decColumn);
			Assert.fail("Exception expected II (string instead of integer)");
		}catch(ClassCastException e){
		}
		
		tti = new TapTableInfo(schemaName, tableName);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_FLAGS, Integer.class);
		tti.putColumn(raColumn, Utils.TAP_COLUMNS_TABLE_FLAGS, Utils.TAP_COLUMN_TABLE_FLAG_RA);
		tti.putColumn(decColumn, Utils.TAP_COLUMNS_TABLE_FLAGS, Utils.TAP_COLUMN_TABLE_FLAG_DEC);
		Assert.assertTrue("Index expected", Utils.areAlreadyIndexedRaDec(tti, raColumn, decColumn));
		
		tti = new TapTableInfo(schemaName, tableName);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_FLAGS, Integer.class);
		tti.putColumn(raColumn, Utils.TAP_COLUMNS_TABLE_FLAGS, Utils.TAP_COLUMN_TABLE_FLAG_MAG);
		tti.putColumn(decColumn, Utils.TAP_COLUMNS_TABLE_FLAGS, Utils.TAP_COLUMN_TABLE_FLAG_DEC);
		Assert.assertFalse("Only dec must be found => not indexed", Utils.areAlreadyIndexedRaDec(tti, raColumn, decColumn));

		tti = new TapTableInfo(schemaName, tableName);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_FLAGS, Integer.class);
		tti.putColumn(raColumn, Utils.TAP_COLUMNS_TABLE_FLAGS, Utils.TAP_COLUMN_TABLE_FLAG_RA);
		tti.putColumn(decColumn, Utils.TAP_COLUMNS_TABLE_FLAGS, Utils.TAP_COLUMN_TABLE_FLAG_MAG);
		Assert.assertFalse("Only ra must be found => not indexed", Utils.areAlreadyIndexedRaDec(tti, raColumn, decColumn));

		tti = new TapTableInfo(schemaName, tableName);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_FLAGS, Integer.class);
		tti.putColumn(raColumn, Utils.TAP_COLUMNS_TABLE_FLAGS, Utils.TAP_COLUMN_TABLE_FLAG_FLUX);
		tti.putColumn(decColumn, Utils.TAP_COLUMNS_TABLE_FLAGS, Utils.TAP_COLUMN_TABLE_FLAG_MAG);
		Assert.assertFalse("No ra nor dec must be found => not indexed", Utils.areAlreadyIndexedRaDec(tti, raColumn, decColumn));
	}
	
	@Test
	public void testRaDecRequireUpdate(){
		String ucd;
		String uType;
		int flags;
		int indexed;
		String schemaName = "schema";
		String tableName = "table";
		String tableColumnName = "col";
		TapTableInfo tti = null;
		
		ucd = "ucd";
		uType = "utype";
		flags = 0;
		indexed = 0;
		tti = new TapTableInfo(schemaName, tableName);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_UCD, String.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_UTYPE, String.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_FLAGS, Integer.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_INDEXED, Integer.class);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_UCD, ucd);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_UTYPE, uType);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_FLAGS, "c"); //wrong class
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_INDEXED, indexed);
		Assert.assertFalse("All the same", Utils.requireUpdate(tti, tableColumnName, ucd, uType, flags, indexed));
		
		//everything is the same => no change
		tti = new TapTableInfo(schemaName, tableName);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_UCD, String.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_UTYPE, String.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_FLAGS, Integer.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_INDEXED, Integer.class);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_UCD, ucd);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_UTYPE, uType);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_FLAGS, flags);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_INDEXED, indexed);
		Assert.assertFalse("All the same", Utils.requireUpdate(tti, tableColumnName, ucd, uType, flags, indexed));
		
		//Different flags
		tti = new TapTableInfo(schemaName, tableName);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_UCD, String.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_UTYPE, String.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_FLAGS, Integer.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_INDEXED, Integer.class);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_UCD, ucd);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_UTYPE, uType);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_FLAGS, flags+1);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_INDEXED, indexed);
		Assert.assertTrue("Different flags", Utils.requireUpdate(tti, tableColumnName, ucd, uType, flags, indexed));
		
		//Different ucd
		tti = new TapTableInfo(schemaName, tableName);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_UCD, String.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_UTYPE, String.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_FLAGS, Integer.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_INDEXED, Integer.class);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_UCD, ucd+"x");
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_UTYPE, uType);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_FLAGS, flags);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_INDEXED, indexed);
		Assert.assertTrue("Different ucd", Utils.requireUpdate(tti, tableColumnName, ucd, uType, flags, indexed));
		
		//Different utype
		tti = new TapTableInfo(schemaName, tableName);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_UCD, String.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_UTYPE, String.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_FLAGS, Integer.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_INDEXED, Integer.class);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_UCD, ucd);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_UTYPE, uType+"z");
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_FLAGS, flags);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_INDEXED, indexed);
		Assert.assertTrue("Different utype", Utils.requireUpdate(tti, tableColumnName, ucd, uType, flags, indexed));
		
		//Nulls
		tti = new TapTableInfo(schemaName, tableName);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_UCD, String.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_UTYPE, String.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_FLAGS, Integer.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_INDEXED, Integer.class);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_UCD, ucd);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_UTYPE, uType);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_FLAGS, flags);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_INDEXED, indexed);
		Assert.assertTrue("Different ucd (null)", Utils.requireUpdate(tti, tableColumnName, null, uType, flags, indexed));
		Assert.assertTrue("Different utype (null)", Utils.requireUpdate(tti, tableColumnName, ucd, null, flags, indexed));
		
		//Null ucd (same)
		tti = new TapTableInfo(schemaName, tableName);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_UCD, String.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_UTYPE, String.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_FLAGS, Integer.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_INDEXED, Integer.class);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_UCD, null);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_UTYPE, uType);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_FLAGS, flags);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_INDEXED, indexed);
		Assert.assertFalse("Same ucd (null)", Utils.requireUpdate(tti, tableColumnName, null, uType, flags, indexed));

		//Null utype (same)
		tti = new TapTableInfo(schemaName, tableName);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_UCD, String.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_UTYPE, String.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_FLAGS, Integer.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_INDEXED, Integer.class);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_UCD, ucd);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_UTYPE, null);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_FLAGS, flags);
		tti.putColumn(tableColumnName, Utils.TAP_COLUMNS_TABLE_INDEXED, indexed);
		Assert.assertFalse("Same utype (null)", Utils.requireUpdate(tti, tableColumnName, ucd, null, flags, indexed));
	}
	
	@Test
	public void testCheckIndexType(){
		String schemaName = "schema";
		String tableName = "table";
		String tableColumnName1 = "col1";
		String tableColumnName2 = "col2";
		String tableColumnName3 = "col3";
		TapTableInfo tti = null;
		
		tti = new TapTableInfo(schemaName, tableName);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_INDEXED, Integer.class);
		tti.addColumnDataType(Utils.TAP_COLUMNS_TABLE_FLAGS, String.class);
		tti.putColumn(tableColumnName1, Utils.TAP_COLUMNS_TABLE_INDEXED, "c"); //wrong class
		tti.putColumn(tableColumnName2, Utils.TAP_COLUMNS_TABLE_INDEXED, 1); //True
		tti.putColumn(tableColumnName3, Utils.TAP_COLUMNS_TABLE_INDEXED, 0); //False

		tti.putColumn(tableColumnName1, Utils.TAP_COLUMNS_TABLE_FLAGS, "c"); //wrong class
		tti.putColumn(tableColumnName2, Utils.TAP_COLUMNS_TABLE_FLAGS, 1); //wrong class
		tti.putColumn(tableColumnName3, Utils.TAP_COLUMNS_TABLE_FLAGS, "2"); //wrong class

		Assert.assertFalse("Indexed false, null value", Utils.isTrueFromTapTableIndexed(null));
		Assert.assertFalse("Integer 0", Utils.isTrueFromTapTableIndexed(new Integer(0)));
		Assert.assertFalse("String 0", Utils.isTrueFromTapTableIndexed("0"));
		Assert.assertFalse("Boolean false", Utils.isTrueFromTapTableIndexed(false));
		Assert.assertFalse("String false", Utils.isTrueFromTapTableIndexed("false"));
		Assert.assertFalse("String False", Utils.isTrueFromTapTableIndexed("False"));
		Assert.assertTrue("Integer 1", Utils.isTrueFromTapTableIndexed(new Integer(1)));
		Assert.assertTrue("String 1", Utils.isTrueFromTapTableIndexed("1"));
		Assert.assertTrue("Boolean true", Utils.isTrueFromTapTableIndexed(true));
		Assert.assertTrue("String true", Utils.isTrueFromTapTableIndexed("true"));
		Assert.assertTrue("String True", Utils.isTrueFromTapTableIndexed("True"));
		Assert.assertFalse("Wrong class", Utils.isTrueFromTapTableIndexed(tti, tableColumnName1));
		Assert.assertTrue("Integer 1", Utils.isTrueFromTapTableIndexed(tti, tableColumnName2));
		Assert.assertFalse("Integer 0", Utils.isTrueFromTapTableIndexed(tti, tableColumnName3));
		
		Assert.assertEquals("Wrong class", 0, Utils.getFlagsFromTapTable(tti, tableColumnName1));
		Assert.assertEquals("Integer 1", 1, Utils.getFlagsFromTapTable(tti, tableColumnName2));
		Assert.assertEquals("String 2", 2, Utils.getFlagsFromTapTable(tti, tableColumnName3));
		Assert.assertEquals("Not found", 0, Utils.getFlagsFromTapTable(tti, tableColumnName1+"xxx"));

		tti.putColumn(tableColumnName1, Utils.TAP_COLUMNS_TABLE_INDEXED, 0); //Not indexed
		tti.putColumn(tableColumnName2, Utils.TAP_COLUMNS_TABLE_INDEXED, 1); //Indexed Ra
		tti.putColumn(tableColumnName3, Utils.TAP_COLUMNS_TABLE_INDEXED, 1); //Indexed no Ra/Dec

		tti.putColumn(tableColumnName1, Utils.TAP_COLUMNS_TABLE_FLAGS, 0); //No flags
		tti.putColumn(tableColumnName2, Utils.TAP_COLUMNS_TABLE_FLAGS, 1); //flags Ra
		tti.putColumn(tableColumnName3, Utils.TAP_COLUMNS_TABLE_FLAGS, 0); //No ra/dec flags
		
		Assert.assertFalse("col1: No normal index", Utils.isNormalIndexed(tti, tableColumnName1));
		Assert.assertFalse("col1: No ra/dec index", Utils.isRaDecIndexed(tti, tableColumnName1));
		Assert.assertFalse("col2: No Normal index", Utils.isNormalIndexed(tti, tableColumnName2));
		Assert.assertTrue("col2: ra/dec index", Utils.isRaDecIndexed(tti, tableColumnName2));
		Assert.assertTrue("col3: Normal index", Utils.isNormalIndexed(tti, tableColumnName3));
		Assert.assertFalse("col3: No ra/dec index", Utils.isRaDecIndexed(tti, tableColumnName3));
	}

	@Test
	public void getTableNameOnly(){
		String s = "schema.table";
		Assert.assertEquals("table", Utils.getTableNameOnly(s));
		s = "table";
		Assert.assertEquals("table", Utils.getTableNameOnly(s));
		Assert.assertNull(Utils.getTableNameOnly(null));
	}
	
	@Test
	public void testParameters() throws IOException{
		DummyHttpRequest request = new DummyHttpRequest();
		String paramNotFoundKey = "notFound";
		String paramInvalidNumber = "InvalidNumber";
		String paramDoubleKey = "doublekey";
		double doubleValue = 2.3;
		String paramIntKey = "intKey";
		int intValue = 5;
		String paramLongKey = "longKey";
		long longValue = 27L;
		String paramBoolKey = "boolKey";
		boolean boolValue  = true;
		request.setParameter(paramInvalidNumber, "a");
		request.setParameter(paramDoubleKey, ""+doubleValue);
		request.setParameter(paramIntKey, ""+intValue);
		request.setParameter(paramLongKey, ""+longValue);
		request.setParameter(paramBoolKey, ""+boolValue);
		
		//Double
		try{
			Utils.getDoubleParameter(paramNotFoundKey, request);
			Assert.fail("Double: Exception expected: parameter not found");
		}catch(IOException e){
		}
		try{
			Utils.getDoubleParameter(paramInvalidNumber, request);
			Assert.fail("Double: Exception expected: invalid value");
		}catch(IOException e){
		}
		Assert.assertEquals("Valid double", doubleValue, Utils.getDoubleParameter(paramDoubleKey, request));
		Assert.assertEquals("Default double", doubleValue, Utils.getDoubleParameter(paramNotFoundKey, request, ""+doubleValue));
		
		//Int
		try{
			Utils.getIntegerParameter(paramNotFoundKey, request);
			Assert.fail("Integer: Exception expected: parameter not found");
		}catch(IOException e){
		}
		try{
			Utils.getIntegerParameter(paramInvalidNumber, request);
			Assert.fail("Integer: Exception expected: invalid value");
		}catch(IOException e){
		}
		Assert.assertEquals("Valid int", intValue, Utils.getIntegerParameter(paramIntKey, request));
		Assert.assertEquals("Default int", intValue, Utils.getIntegerParameter(paramNotFoundKey, request, ""+intValue));

		//Long
		try{
			Utils.getLongParameter(paramNotFoundKey, request);
			Assert.fail("Long: Exception expected: parameter not found");
		}catch(IOException e){
		}
		try{
			Utils.getLongParameter(paramInvalidNumber, request);
			Assert.fail("Long: Exception expected: invalid value");
		}catch(IOException e){
		}
		Assert.assertEquals("Valid long", longValue, Utils.getLongParameter(paramLongKey, request));
		Assert.assertEquals("Default long", longValue, Utils.getLongParameter(paramNotFoundKey, request, ""+longValue));

		//Boolean
		try{
			Utils.getBooleanParameter(paramNotFoundKey, request);
			Assert.fail("Boolean: Exception expected: parameter not found");
		}catch(IOException e){
		}
		Assert.assertFalse("No boolean", Utils.getBooleanParameter(paramInvalidNumber, request));
		Assert.assertEquals("Valid bool", boolValue, Utils.getBooleanParameter(paramBoolKey, request));
		Assert.assertEquals("Default bool", boolValue, Utils.getBooleanParameter(paramNotFoundKey, request, boolValue));
		
		try{
			Utils.getParameter(paramNotFoundKey, request);
			Assert.fail("Exception expected: parameter not found");
		}catch(IOException e){
		}
		Assert.assertEquals("Parameter found", ""+intValue, Utils.getParameter(paramIntKey, request));
	}
	
//	@Test
//	public void testCheckAuthentication(){
//		try{
//			Utils.checkAuthentication(null);
//			Assert.fail("Exception expected: null owner");
//		}catch(InvalidParameterException e){
//		}
//		DefaultJobOwner owner = new DefaultJobOwner(null);
//		try{
//			Utils.checkAuthentication(owner);
//			Assert.fail("Exception expected: null id");
//		}catch(InvalidParameterException e){
//		}
//		String id = "id";
//		String authUsername = "auth";
//		String pseudo = "p";
//		owner = new DefaultJobOwner(id, authUsername, pseudo);
//		Utils.checkAuthentication(owner);
//	}
	
//	@Test
//	public void testGetTapSchemaInfo(){
//		DummyServletContext servletContext = new DummyServletContext();
//		DummyServletConfig servletConfig = new DummyServletConfig(servletContext);
//		
//		TAPSchemaInfo tapSchemaInfo = Utils.getTapSchemaInfo(servletConfig);
//		Assert.assertEquals("Default schemas table name", "schemas", tapSchemaInfo.getTapSchemasTableName());
//		Assert.assertEquals("Default tables table name", "tables", tapSchemaInfo.getTapTablesTableName());
//		Assert.assertEquals("Default columns table name", "columns", tapSchemaInfo.getTapColumnsTableName());
//		Assert.assertEquals("Default keys table name", "keys", tapSchemaInfo.getTapKeysTableName());
//		
//		servletContext.setInitParameter(TapServlet.TAP_SCHEMAS_USE_VIEWS, "true");
//		
//		tapSchemaInfo = Utils.getTapSchemaInfo(servletConfig);
//		Assert.assertEquals("Normal (with views) schemas table name", "all_schemas", tapSchemaInfo.getTapSchemasTableName());
//		Assert.assertEquals("Normal (with views) tables table name", "all_tables", tapSchemaInfo.getTapTablesTableName());
//		Assert.assertEquals("Normal (with views) columns table name", "all_columns", tapSchemaInfo.getTapColumnsTableName());
//		Assert.assertEquals("Normal (with views) keys table name", "all_keys", tapSchemaInfo.getTapKeysTableName());
//	}
}
