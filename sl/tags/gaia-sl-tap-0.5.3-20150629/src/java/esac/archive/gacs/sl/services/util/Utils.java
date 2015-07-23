package esac.archive.gacs.sl.services.util;

import java.io.IOException;
import java.security.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;

import esac.archive.gacs.sl.tap.actions.TapTableInfo;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.utils.UwsUtils;

public class Utils {
	
	//Binary flags, 0,1,2,4,8,16...
	public static final int TAP_TABLE_TYPE_NORMAL = 0;
	public static final int TAP_TABLE_TYPE_RADEC  = 1;
	public static final int TAP_TABLE_TYPE_XMATCH = 2;
	
	//Binary flags, 0,1,2,4,8,16...
	public static final int TAP_COLUMN_TABLE_FLAG_RA   = 1;
	public static final int TAP_COLUMN_TABLE_FLAG_DEC  = 2;
	public static final int TAP_COLUMN_TABLE_FLAG_FLUX = 4;
	public static final int TAP_COLUMN_TABLE_FLAG_MAG  = 8;
	public static final int TAP_COLUMN_TABLE_FLAG_PK   = 16;
	
	public static final String TAP_COLUMN_TABLE_FLAG_ID_RA = "ra";
	public static final String TAP_COLUMN_TABLE_FLAG_ID_DEC = "dec";
	public static final String TAP_COLUMN_TABLE_FLAG_ID_FLUX = "flux";
	public static final String TAP_COLUMN_TABLE_FLAG_ID_MAG = "mag";
	public static final String TAP_COLUMN_TABLE_FLAG_ID_PK = "pk";

	public static final String TAP_COLUMNS_TABLE_UCD = "ucd";
	public static final String TAP_COLUMNS_TABLE_UTYPE = "utype";
	public static final String TAP_COLUMNS_TABLE_FLAGS = "flags";
	public static final String TAP_COLUMNS_TABLE_INDEXED = "indexed";
	
	
	public static double getDoubleParameter(String parameter, HttpServletRequest request) throws IOException{
		return getDoubleParameter(parameter, request, null);
	}
	
	public static double getDoubleParameter(String parameter, HttpServletRequest request, String defaultValue) throws IOException{
		String v = getParameter(parameter, request, defaultValue);
		try{
			return Double.parseDouble(v);
		}catch(NumberFormatException nfe){
			throw new IOException("Invalid " + parameter + " value: '"+v+"'");
		}
	}

	public static int getIntegerParameter(String parameter, HttpServletRequest request) throws IOException{
		return getIntegerParameter(parameter, request, null);
	}

	public static int getIntegerParameter(String parameter, HttpServletRequest request, String defaultValue) throws IOException{
		String v = getParameter(parameter, request, defaultValue);
		try{
			return Integer.parseInt(v);
		}catch(NumberFormatException nfe){
			throw new IOException("Invalid " + parameter + " value: '"+v+"'");
		}
	}
	

	public static long getLongParameter(String parameter, HttpServletRequest request) throws IOException{
		return getLongParameter(parameter, request, null);
	}

	public static long getLongParameter(String parameter, HttpServletRequest request, String defaultValue) throws IOException{
		String v = getParameter(parameter, request, defaultValue);
		try{
			return Long.parseLong(v);
		}catch(NumberFormatException nfe){
			throw new IOException("Invalid " + parameter + " value: '"+v+"'");
		}
	}
	
	public static boolean getBooleanParameter(String parameter, HttpServletRequest request) throws IOException{
		return getBooleanParameter(parameter, request, null);
	}

	public static boolean getBooleanParameter(String parameter, HttpServletRequest request, Boolean defaultValue) throws IOException{
		String v = getParameter(parameter, request, defaultValue == null ? (String)null : defaultValue.toString());
		return Boolean.parseBoolean(v);
//		try{
//			return Boolean.parseBoolean(v);
//		}catch(NumberFormatException nfe){
//			throw new IOException("Invalid " + parameter + " value: '"+v+"'");
//		}
	}

	public static String getParameter(String parameter, HttpServletRequest request) throws IOException{
		return getParameter(parameter, request, null);
	}
	
	public static String getParameter(String parameter, HttpServletRequest request, String defaultValue) throws IOException{
		String value = request.getParameter(parameter);
		if(value == null){
			if(defaultValue == null){
				String warnMsg = "Param [" + parameter + "] must be specified in request";
				throw new IOException(warnMsg);
			}else{
				value = defaultValue;
			}
		}
		return value;
	}

	
	/**
	 * Checks corresponding to a table upload.
	 * @throws InvalidParameterException if the owner is null or user name is null
	 */
	public static void checkAuthentication(UwsJobOwner owner) throws InvalidParameterException{
		if (owner == null || owner.getAuthUsername() == null || UwsUtils.isAnonymous(owner.getId())){
			throw new InvalidParameterException("User must be logged in to perform this action.");
		}
	}
	
//	public static TAPSchemaInfo getTapSchemaInfo(ServletConfig config){
//		TAPSchemaInfo tapSchemaInfo = new TAPSchemaInfo();
//
//		String tmpValue = config.getInitParameter(TapServlet.TAP_SCHEMAS_USE_VIEWS);
//		if(tmpValue != null && Boolean.parseBoolean(tmpValue)){
//			tapSchemaInfo.setTapSchemasTableName("all_schemas");
//			tapSchemaInfo.setTapTablesTableName("all_tables");
//			tapSchemaInfo.setTapColumnsTableName("all_columns");
//			tapSchemaInfo.setTapKeysTableName("all_keys");
//		}
//
//		return tapSchemaInfo;
//	}
	
	/**
	 * Returns a table name only. I.e., in case the table contains the schema name, it is removed.
	 * @param s a table name or fully qualified name (schema+table)
	 * @return a table name only.
	 */
	public static String getTableNameOnly(String s){
		if(s == null){
			return null;
		}
		int p = s.lastIndexOf('.');
		if(p < 0){
			return s;
		}
		return s.substring(p+1);
	}
	
	/**
	 * Returns a schema name only.
	 * @param s a table name or fully qualified name (schema+table)
	 * @return a schema name only.
	 */
	public static String getSchemaNameOnly(String s){
		if(s == null){
			return null;
		}
		int p = s.lastIndexOf('.');
		if(p < 0){
			return null;
		}
		return s.substring(0,p);
	}
	
	/**
	 * Returns the user name from a full qualified table name or table name
	 * @param s
	 * @return
	 */
	public static String getUserNameFromSchema(String s){
		String schema = getSchemaNameOnly(s);
		if(schema == null){
			return null;
		}
		int p = schema.lastIndexOf('_');
		if(p < 0){
			return null;
		}
		return schema.substring(0, p);
	}
	
	public static int convertTapTableFlag(String flag){
		if(flag == null){
			return 0;
		}
		try{
			//If the string is a number, return it
			return Integer.parseInt(flag);
		}catch(NumberFormatException nfe){
		}
		int fValue = 0;
		if(flag.indexOf(',') >= 0){
			String[] flags = flag.split(",");
			for(String f: flags){
				fValue += convertTapTableFlag(f);
			}
		}else{
			String t = flag.trim();
			if(TAP_COLUMN_TABLE_FLAG_ID_RA.equalsIgnoreCase(t)){
				return TAP_COLUMN_TABLE_FLAG_RA;
			} else if(TAP_COLUMN_TABLE_FLAG_ID_DEC.equalsIgnoreCase(t)){
				return TAP_COLUMN_TABLE_FLAG_DEC;
			} else if(TAP_COLUMN_TABLE_FLAG_ID_FLUX.equalsIgnoreCase(t)){
				return TAP_COLUMN_TABLE_FLAG_FLUX;
			} else if(TAP_COLUMN_TABLE_FLAG_ID_MAG.equalsIgnoreCase(t)){
				return TAP_COLUMN_TABLE_FLAG_MAG;
			} else if(TAP_COLUMN_TABLE_FLAG_ID_PK.equalsIgnoreCase(t)){
				return TAP_COLUMN_TABLE_FLAG_PK;
			}
		}
		return fValue;
	}
	
	/**
	 * Tests whether 'flags' is 'ra' or 'dec' (binary comparison)
	 * @param flags flags to test.
	 * @return 'true' if 'flags' is 'ra' or 'dec'.
	 */
	public static boolean isRaOrDec(int flags){
		if((flags & TAP_COLUMN_TABLE_FLAG_RA) > 0){
			return true;
		}
		if((flags & TAP_COLUMN_TABLE_FLAG_DEC) > 0){
			return true;
		}
		return false;
	}

	/**
	 * Return 'true' if the RA flag is active.
	 * @param flags
	 * @return
	 */
	public static boolean isRa(int flags){
		return ((flags & TAP_COLUMN_TABLE_FLAG_RA) > 0);
	}
	
	/**
	 * Return 'true' if the DEC flag is active.
	 * @param flags
	 * @return
	 */
	public static boolean isDec(int flags){
		return ((flags & TAP_COLUMN_TABLE_FLAG_DEC) > 0);
	}
	
	/**
	 * Returns the first column name that has the specified flag set.<br/>
	 * Use this method for unique flags.
	 * @param tapTableInfo
	 * @param flag
	 * @return
	 */
	public static String findParameterByFlag(TapTableInfo tapTableInfo, int flag){
		for(String tableColumnName: tapTableInfo.getTableColumnNames()){
			int i = tapTableInfo.getInteger(tableColumnName, "flags");
			if((i & flag) > 0){
				return tableColumnName;
			}
		}
		return null;
	}

	/**
	 * Returns the flags associated to a column.<br/>
	 * Use this method for unique flags.
	 * @param tapTableInfo
	 * @param columnName
	 * @return
	 */
	public static int findFlagsFor(TapTableInfo tapTableInfo, String columnName){
		return tapTableInfo.getInteger(columnName, "flags");
	}

	/**
	 * Returns an string representation of the provided flags argument.
	 * @param flags flags
	 * @return an string representation of the provided flags argument.
	 */
	public static String getFlagIds(int flags){
		StringBuilder sb = new StringBuilder();
		boolean firstTime = true;
		if((flags & TAP_COLUMN_TABLE_FLAG_RA) > 0){
			if(firstTime){
				firstTime = false;
			}else{
				sb.append(",");
			}
			sb.append(TAP_COLUMN_TABLE_FLAG_ID_RA);
		}
		if((flags & TAP_COLUMN_TABLE_FLAG_DEC) > 0){
			if(firstTime){
				firstTime = false;
			}else{
				sb.append(",");
			}
			sb.append(TAP_COLUMN_TABLE_FLAG_ID_DEC);
		}
		if((flags & TAP_COLUMN_TABLE_FLAG_FLUX) > 0){
			if(firstTime){
				firstTime = false;
			}else{
				sb.append(",");
			}
			sb.append(TAP_COLUMN_TABLE_FLAG_ID_FLUX);
		}
		if((flags & TAP_COLUMN_TABLE_FLAG_MAG) > 0){
			if(firstTime){
				firstTime = false;
			}else{
				sb.append(",");
			}
			sb.append(TAP_COLUMN_TABLE_FLAG_ID_MAG);
		}
		return sb.toString();
	}

	/**
	 * Checks whether ra and dec are already indexed.<br/>
	 * raColumn and decColumn are the columns to be indexed. tapTableInfo contains the current database information.
	 * @param tapTableInfo current database information.
	 * @param raColumn ra column name.
	 * @param decColumn dec column name.
	 * @return 'true' if the ra and dec are already indexed.
	 */
	public static boolean areAlreadyIndexedRaDec(TapTableInfo tapTableInfo, String raColumn, String decColumn){
		boolean ra = (tapTableInfo.getInteger(raColumn, TAP_COLUMNS_TABLE_FLAGS) & Utils.TAP_COLUMN_TABLE_FLAG_RA) > 0;
		boolean dec = (tapTableInfo.getInteger(decColumn, TAP_COLUMNS_TABLE_FLAGS) & Utils.TAP_COLUMN_TABLE_FLAG_DEC) > 0;
		return (ra && dec);
	}
	
	/**
	 * Compares current database values with the new ones. If a new value is different than the old one, 'true' is returned.
	 * @param tapTableInfo current database information.
	 * @param tableColumnName tap table column name to test.
	 * @param ucd new ucd value
	 * @param uType new utype value
	 * @param flags new flags value
	 * @param indexed new index value
	 * @return 'true' if a new value is different than the old one.
	 */
	public static boolean requireUpdate(TapTableInfo tapTableInfo, String tableColumnName, String ucd, String uType, int flags, int indexed){
		String testDbData = tapTableInfo.getString(tableColumnName, TAP_COLUMNS_TABLE_UCD);
		if(!compareDataBaseDataWithValue(testDbData, ucd)){
			return true;
		}
		testDbData = tapTableInfo.getString(tableColumnName, TAP_COLUMNS_TABLE_UTYPE);
		if(!compareDataBaseDataWithValue(testDbData, uType)){
			return true;
		}
		int dbFlags = Utils.getFlagsFromTapTable(tapTableInfo, tableColumnName);
		if (flags != dbFlags){
			return true;
		}
		int dbIndexed = Utils.getIndexedFromTapTable(tapTableInfo, tableColumnName);
		return dbIndexed != indexed;
	}
	
	/**
	 * Compares a database data with a value.<br/>
	 * If database data is null, returns 'true' if value is not null nor empty string.<br/>
	 * If database data is not null, returns the result of an 'equals' java comparison.<br/>
	 * @param dataBaseData
	 * @param value
	 * @return
	 */
	public static boolean compareDataBaseDataWithValue(String dataBaseData, String value) {
		if (dataBaseData == null) {
			return (value == null || "".equals(value));
		} else {
			return dataBaseData.equals(value);
		}
	}
	
	/**
	 * Returns 'true' if the specified column indexed field contains '1' or 'true'
	 * @param tapTableInfo
	 * @param tableColumnName
	 * @return 'true' if the specified column indexed field contains '1' or 'true'
	 */
	public static boolean isTrueFromTapTableIndexed(TapTableInfo tapTableInfo, String tableColumnName){
		return isTrueFromTapTableIndexed(tapTableInfo.getColumn(tableColumnName, TAP_COLUMNS_TABLE_INDEXED));
	}
	
	public static boolean isTrueFromTapTableIndexed(Object value){
		if(value == null){
			return false;
		}
		String s = value.toString();
		if("1".equals(s)){
			return true;
		}
		return Boolean.parseBoolean(s);
	}
	
	/**
	 * Returns the integer value of the specified column flags value. If the value is null, '0' is returned.
	 * @param tapTableInfo
	 * @param tableColumnName
	 * @return
	 */
	public static int getFlagsFromTapTable(TapTableInfo tapTableInfo, String tableColumnName){
		Object o = tapTableInfo.getColumn(tableColumnName, TAP_COLUMNS_TABLE_FLAGS);
		if(o == null){
			return 0;
		}
		try{
			return ((Integer)o);
		}catch(ClassCastException e){
			try{
				return Integer.parseInt(o.toString());
			}catch(NumberFormatException nfe){
				return 0;
			}
		}
	}
	
	/**
	 * Returns the integer value of the specified column flags value. If the value is null, '0' is returned.
	 * @param tapTableInfo
	 * @param tableColumnName
	 * @return
	 */
	public static int getIndexedFromTapTable(TapTableInfo tapTableInfo, String tableColumnName){
		Object o = tapTableInfo.getColumn(tableColumnName, TAP_COLUMNS_TABLE_INDEXED);
		if(o == null){
			return 0;
		}
		try{
			return ((Integer)o);
		}catch(ClassCastException e){
			try{
				return Integer.parseInt(o.toString());
			}catch(NumberFormatException nfe){
				return 0;
			}
		}
	}
	
	/**
	 * Returns 'true' if the specified column is indexed (1) and the 'flags' field contains 'ra' or 'dec' values.
	 * @param tapTableInfo
	 * @param tableColumnName
	 * @return
	 */
	public static boolean isRaDecIndexed(TapTableInfo tapTableInfo, String tableColumnName){
		boolean indexed = isTrueFromTapTableIndexed(tapTableInfo, tableColumnName);
		if(indexed){
			int flags = getFlagsFromTapTable(tapTableInfo, tableColumnName);
			return isRaOrDec(flags);
		} else {
			return false;
		}
	}
	
	/**
	 * Returns 'true' if the specified column is indexed (1) and the 'flags' field does not contain 'ra' nor 'dec' values. 
	 * @param tapTableInfo
	 * @param tableColumnName
	 * @return
	 */
	public static boolean isNormalIndexed(TapTableInfo tapTableInfo, String tableColumnName) {
		boolean indexed = isTrueFromTapTableIndexed(tapTableInfo, tableColumnName);
		if(indexed){
			int flags = getFlagsFromTapTable(tapTableInfo, tableColumnName);
			return !isRaOrDec(flags);
		} else {
			return false;
		}
	}
	
	/**
	 * Returns an string with the stack trace.
	 * @param t
	 * @return an string with the stack trace.
	 */
	public static String dumpStackTrace (Throwable t){
		if(t == null){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for(StackTraceElement ste: t.getStackTrace()){
			sb.append(ste).append("\n");
		}
		return sb.toString();
	}
	

}
