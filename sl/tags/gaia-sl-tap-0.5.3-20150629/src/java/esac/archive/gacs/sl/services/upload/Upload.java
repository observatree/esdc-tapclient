package esac.archive.gacs.sl.services.upload;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StarTableFactory;
import uk.ac.starlink.votable.VOTableBuilder;
import esac.archive.gacs.sl.services.status.CustomServletFileUpload;
import esac.archive.gacs.sl.services.status.ProgressInputStream;
import esac.archive.gacs.sl.services.status.StatusManager;
import esac.archive.gacs.sl.services.status.types.StatusUpload;
import esac.archive.gacs.sl.services.util.Utils;
import esac.archive.gacs.sl.tap.TapUtils;
import esac.archive.gacs.sl.tap.actions.JDBCPooledFunctions;
import esavo.tap.TAPException;
import esavo.tap.TAPFactory;
import esavo.tap.TAPService;
import esavo.tap.db.DBException;
import esavo.tap.metadata.TAPMetadata;
import esavo.tap.metadata.TAPMetadataLoader;
import esavo.tap.metadata.TAPSchema;
import esavo.tap.metadata.TAPTable;
import esavo.tap.upload.LimitedSizeInputStream;
import esavo.uws.UwsException;
import esavo.uws.UwsManager;
import esavo.uws.config.UwsConfiguration;
import esavo.uws.event.UwsEventType;
import esavo.uws.event.UwsEventsManager;
import esavo.uws.jobs.UwsJob;
import esavo.uws.jobs.UwsJobResultMeta;
import esavo.uws.output.UwsExceptionOutputFormat;
import esavo.uws.output.UwsOutputResponseHandler;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.security.UwsSecurity;
import esavo.uws.share.UwsShareItemBase;
import esavo.uws.share.UwsShareManager;
import esavo.uws.storage.QuotaException;
import esavo.uws.storage.UwsQuota;
import esavo.uws.storage.UwsQuotaSingleton;
import esavo.uws.storage.UwsStorage;
import esavo.uws.utils.UwsUtils;

import com.oreilly.servlet.multipart.ExceededSizeException;

/**
 * This class is used to upload/remove user tables and to upload query results.
 * <ol>
 * <li>If the form is multipart:
 *   <ol>
 *   <li>If no jobid is present: a user table upload is requested.</li>
 *   <li>If jobid is present: a query results upload is requested.</li>
 *   </ol>
 * </li>
 * <li>If no multipart:
 *   <ol>
 *   <li>'delete' parameter must be present: user table delete requested.</li>
 *   <li>If no 'delete' is present, an error is raised.</li>
 *   </ol>
 * </li>
 * </ol>
 * 
 * <p>Required parameters:
 * <ul>
 * <li>TABLE_NAME: required for any action</li>
 * <li>JOBID: required for query results upload only. (This is the job associated to the query, the job that contains the results to upload)</li>
 * <li>DELETE: required for table removal action only.</li>
 * <li>FORCE_REMOVAL: optional for delete operations only.</li>
 * <li>TASKID: optional for uploads actions, the progress of the task associated to the action is updated (percentage). -1 to avoid notifications</li>
 * <li>FILE: mandatory for uploads actions only.</li>
 * <li>RACOL: optional for uploads actions.</li>
 * <li>DECCOL: optional for uploads actions.</li>
 * </ul>
 * </p>
 *
 */

public class Upload {
	
	/** Part of HTTP content type header. */
	public static final String MULTIPART = "multipart/";

	public static final String PARAM_JOBID   = "JOBID";
	//public static final String PARAM_FILE   = "FILE";
	public static final String PARAM_RACOL  = "RACOL";
	public static final String PARAM_DECCOL = "DECCOL";
	public static final String PARAM_TABLENAME = "TABLE_NAME";
	public static final String PARAM_TABLEDESC = "TABLE_DESC";
	public static final String PARAM_FORCE_REMOVAL = "FORCE_REMOVAL";
	public static final String PARAM_TASKID = "TASKID";
	public static final String PARAM_DELETE = "DELETE";
	
	protected final TAPService service;

	private Map<String,String> parameters = null;
	private File	file	   = null;
	private boolean isFileUpload = false;
	

	
	public Upload(TAPService serviceConnection) {
		service = serviceConnection;
	}

	public void executeRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		parameters = new HashMap<String,String>();
		file = null;
		long taskId=-1; 
		String jobId=null; 
		
		response.setContentType("text/html");
		
		UwsSecurity security = service.getFactory().getSecurityManager();
		UwsJobOwner user;
		try {
			user = security.getUser();
		} catch (UwsException e) {
			throw new ServletException("Cannot obtain current user: " + e.getMessage(), e);
		}
		if(user == null){
			throw new ServletException("Cannot obtain current user");
		}
		
		FileItemFactory factory = new DiskFileItemFactory();

		long maxConfigurationFileSize = service.getConfiguration().getLongProperty(UwsConfiguration.CONFIG_UPLOAD_MAX_SIZE);
		long maxFileSize = 0;
		UwsQuota quota = null;
		try {
			quota = UwsQuotaSingleton.getInstance().createOrLoadQuota(user);
			maxFileSize = quota.getMinFileQuotaAvailable(maxConfigurationFileSize);
		} catch (UwsException e1) {
			throw new IOException(e1);
		}

		CustomServletFileUpload upload = new CustomServletFileUpload(factory);
		upload.setSizeMax(maxFileSize);
		
		File uploadDir = service.getFactory().getStorageManager().getUploadDir(user);
		
		if(!uploadDir.exists()){
			uploadDir.mkdirs();
		}

		UploadProgressListener uploadProgressListener = new UploadProgressListener();
		upload.setProgressListener(uploadProgressListener);

		try {
			//DENY ACCESS TO UNAUTHENTICATED/UNAUTHORIZED USERS
			Utils.checkAuthentication(user);
			
			//For uploading tables, the request must be multipart.
			//For removing tables, the request it not multipart.
			if(ServletFileUpload.isMultipartContent(request)){
				//Upload user tables or query results
				//for query results, a job id is mandatory
				List<FileItem> fields = upload.parseRequest(request);
				Iterator<FileItem> it = fields.iterator();
				if (!it.hasNext()) {
					throw new Exception("No parameters provided.");
				}
				while (it.hasNext()) {
					FileItem fileItem = it.next();
					boolean isFormField = fileItem.isFormField();
					if (isFormField) {
						String name = fileItem.getFieldName();
						String value = fileItem.getString();
						parameters.put(name, value);
						if(name.equals(PARAM_TASKID)){
							taskId = Long.parseLong(value);
							uploadProgressListener.setTaskId(taskId);
						}
						if(name.equals(PARAM_JOBID)){
							// IF jobId present: upload results from job
							jobId = value;
							if(jobId!=null && jobId.trim().length()>0 && file==null){
								file = getResultDataFileForJob(jobId, user);
								StatusUpload statusUpload = new StatusUpload("100");
								if(taskId >= 0){
									try{
										StatusManager.getInstance().updateStatus(taskId, statusUpload); 
									} catch (IllegalArgumentException iae){
										iae.printStackTrace();
									}
								}
							}
						}
						
					} else {
						uploadProgressListener.setTheContentLength(fileItem.getSize());
						if(file==null){
							isFileUpload = true;
							file = new File(uploadDir, UwsUtils.getUniqueIdentifier()+"_"+fileItem.getName());
							fileItem.write(file);
							
							try{
								quota.addFileSize(file.length());
							}catch(QuotaException qe){
								long fileSize = file.length();
								file.delete();
								quota.reduceFileSize(fileSize);
								throw qe;
							}
						}
					}
				}
			}else{
				//No upload functionality.
				//Currently: removal operations only
				
				// Extract and identify each pair (key,value):
				@SuppressWarnings("unchecked")
				Enumeration<String> e = request.getParameterNames();
				while(e.hasMoreElements()){
					String name = e.nextElement();
					String value = request.getParameter(name);
					parameters.put(name, value);
					if(name.equals(PARAM_TASKID)){
						taskId = Long.parseLong(value);
						uploadProgressListener.setTaskId(taskId);
					}
					if(name.equals(PARAM_JOBID)){
						jobId = value;
					}
				}
			}

			//Change to lowercase
			changeToLowerCaseIfNeeded(parameters, PARAM_TABLENAME);
			changeToLowerCaseIfNeeded(parameters, PARAM_RACOL);
			changeToLowerCaseIfNeeded(parameters, PARAM_DECCOL);
			changeToLowerCaseIfNeeded(parameters, PARAM_FORCE_REMOVAL);


			///////////////////////////////////////////
			/// CHECKS
			///////////////////////////////////////////
			try{
				check(maxFileSize);
			}catch(Exception e){
				if(file!=null && isFileUpload){
					long fileSize = file.length();
					file.delete();
					quota.reduceFileSize(fileSize);
				}
				throw e;
			}
			
			
			long fileSize = 0;
			if(parameters.get(PARAM_DELETE)!=null){
				///////////////////////////////////////////
				/// DELETE TABLE
				///////////////////////////////////////////
				try{
					delete(user);
				}catch(Exception e){
					throw e;
				}
			}else{
				///////////////////////////////////////////
				/// UPLOAD TABLE
				///////////////////////////////////////////
				try{
					upload(taskId, user);
				}catch(Exception e){
					if(isFileUpload){
						fileSize = file.length();
						file.delete();
						quota.reduceFileSize(fileSize);
					}
					throw e;
				}
			}
			
			response.getWriter().println(PARAM_RACOL+"  = "+parameters.get(PARAM_RACOL));
			response.getWriter().println(PARAM_DECCOL+" = "+parameters.get(PARAM_DECCOL));
			response.getWriter().println(PARAM_TABLENAME+" = "+parameters.get(PARAM_TABLENAME));
			response.getWriter().println(PARAM_TABLEDESC+" = "+parameters.get(PARAM_TABLEDESC));
			response.getWriter().println(PARAM_DELETE+" = "+parameters.get(PARAM_DELETE));
			if (file != null) {
				response.getWriter().println("File size: "+fileSize);
			}

			response.flushBuffer();
		}catch(Throwable t){
			t.printStackTrace();
			String action = parameters.containsKey(PARAM_DELETE) ? "Delete" : "Upload";
            
			String msg = "Cannot execute action: " + action + " table '"+parameters.get(PARAM_TABLENAME)+"'";
			try {
				service.getFactory().getOutputHandler().writeServerErrorResponse(
						response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, msg, action, t, UwsExceptionOutputFormat.HTML);
			} catch (UwsException e) {
				throw new ServletException(e);
			}
            
            response.getWriter().flush();
            response.flushBuffer();
		}

	}

	
	/**
	 * Checks the validity of the upload or delete request.
	 * @throws IOException 
	 */
	private void check(long maxFileSize) throws IOException{
		// Common checks
		if(parameters.get(PARAM_TABLENAME)==null) {
			throw new InvalidParameterException("Error: table name not provided.");
		}

		if(parameters.containsKey(PARAM_DELETE)) {
			checkDelete();
		} else if(parameters.containsKey(PARAM_JOBID)) {
			checkUploadJobResults(maxFileSize);
		} else {
			checkUpload(maxFileSize);
		}
	}
	
	/**
	 * Checks corresponding to a table delete.
	 */
	private void checkDelete(){
	}
	
	/**
	 * Checks corresponding to a table upload.
	 * @throws IOException
	 */
	private void checkUpload(long maxFileSize) throws IOException{
		if (file == null) {
			throw new InvalidParameterException("Upload error: no file provided.");
		}
		if (file.length()>maxFileSize) {
			throw new InvalidParameterException("Upload error: too big file, max upload size is "+((long)(maxFileSize/1024/1024))+" MB");
		}
		if(parameters.containsKey(PARAM_RACOL) || parameters.containsKey(PARAM_DECCOL)){
			//If RA or DEC parameters are present, check the table contains the specified fields
			checkVOTable(file);
		}
	}
	

	/**
	 * Checks corresponding to a job results upload.
	 * @throws IOException
	 */
	private void checkUploadJobResults(long maxFileSize) throws IOException{
		if(parameters.get(PARAM_TASKID)==null) {
			throw new InvalidParameterException("Error: task ID not provided.");
		}
		if (file == null) {
			throw new InvalidParameterException("Upload error: no file provided.");
		}
		if (file.length()>maxFileSize) {
			throw new InvalidParameterException("Upload error: too big file, max upload size is "+((long)(maxFileSize/1024/1024))+" MB");
		}
		if(parameters.containsKey(PARAM_RACOL) || parameters.containsKey(PARAM_DECCOL)){
			//If RA or DEC parameters are present, check the table contains the specified fields
			checkVOTable(file);
		}
	}

	/**
	 * Checks the validity of the uploaded VOTable.
	 * @param f
	 * @throws IOException
	 */
	private void checkVOTable(File f) throws IOException{
		String raParam = (String)parameters.get(PARAM_RACOL);
		String decParam = (String)parameters.get(PARAM_DECCOL);
		
		if((raParam == null || "".equals(raParam)) && (decParam == null || "".equals(decParam))){
			//no ra/dec params provided: do not check.
			return;
		}
		
		InputStream votable = new FileInputStream(f);

		// start parsing the VOTable:
		StarTableFactory factory = new StarTableFactory();
		StarTable table = factory.makeStarTable( file.getAbsolutePath(), "votable" );

		boolean raColPresent=false;
		boolean decColPresent=false;
		
		
		for(int col=0 ; col<table.getColumnCount(); col++){
			ColumnInfo field = table.getColumnInfo(col);
			if(field.getName().trim().equalsIgnoreCase(raParam)) {
				raColPresent=true;
			}
			if(field.getName().trim().equalsIgnoreCase(decParam)) {
				decColPresent=true;
			}
		}

		votable.close();
		
		if(!raColPresent) {
			//RA was provided, but the field is not found in the file
			throw new InvalidParameterException("UPLOAD error: "+(raParam==null?"no ra param specified.":"'"+raParam + "' not found in file."));
		}
		if(!decColPresent) {
			//DEC was provided, but the field is not found in the file
			throw new InvalidParameterException("UPLOAD error: "+(decParam==null?"no dec param specified.":"'"+decParam+"' not found in file."));
		}

	}
	
	
	
	/**
	 * 
	 * @param loaders
	 * @return
	 * @throws TAPException
	 */
	private TAPSchema upload(Long taskId, UwsJobOwner owner) throws TAPException {
		JDBCPooledFunctions dbConn = (JDBCPooledFunctions)service.getFactory().createDBConnection("UploadConnection");

		// Begin a DB transaction:
		dbConn.startTransaction();

		TAPSchema schema = new TAPSchema(TAPMetadata.getUserSchema(owner));
		InputStream votable = null;
		String tableName = null;
		String tableDesc = null;
		long maxFileSize = 0;
		UwsQuota quota = null;
		long dbQuotaToRestore = -1;
		try{
			tableName = parameters.get(PARAM_TABLENAME);
			tableDesc = parameters.get(PARAM_TABLEDESC);
			votable = new FileInputStream(file);

			long maxConfigurationFileSize = service.getConfiguration().getLongProperty(UwsConfiguration.CONFIG_UPLOAD_MAX_SIZE);
			try {
				quota = UwsQuotaSingleton.getInstance().createOrLoadQuota(owner);
				maxFileSize = quota.getMinFileQuotaAvailable(maxConfigurationFileSize);
			} catch (UwsException e1) {
				throw new IOException(e1);
			}

			if (maxFileSize > 0){
				votable = new LimitedSizeInputStream(votable, maxFileSize);
			}

			// start parsing the VOTable:
			StarTableFactory factory = new StarTableFactory();
			FileInputStream fins = new FileInputStream(file);
			ProgressInputStream pins = new ProgressInputStream(fins,file.length(),taskId);
			StarTable table = factory.makeStarTable( pins, new VOTableBuilder() );
			pins.close();

			// 1st Step: Create schema if not exists
			dbConn.createSchema(schema.getDBName());
			
			// 2nd STEP: Convert the VOTable metadata into DBTable:
			TAPTable tapTable = fetchTableMeta(tableName, tableDesc, table, parameters.get(PARAM_RACOL), parameters.get(PARAM_DECCOL));
			schema.addTable(tapTable);

			// 3th STEP: Create the corresponding table in the database:
			dbConn.createTable(tapTable);
			
			dbConn.registerInTapSchema(tapTable);

			// 4th STEP: Load rows into this table:
			dbConn.loadTableData(owner, tapTable, table, taskId);
			dbQuotaToRestore = dbConn.getTableSize(schema.getDBName(),tableName);
			votable.close();
			
			// 5th STEP: Update table size in TAP_SCHEMA
			dbConn.updateTableSizeInTapSchema(tapTable);
				
			// 6th Create PK
			dbConn.createPkInTable(tapTable);
			
			// 7th Create Indexes if ra/dec are available
			if(	parameters.get(PARAM_RACOL)!=null && parameters.get(PARAM_RACOL).length()>0  
					&& parameters.get(PARAM_DECCOL)!=null && parameters.get(PARAM_DECCOL).length()>0){
					dbConn.createRaAndDecIndexes(tapTable.getDBSchemaName(), tableName,  
							parameters.get(PARAM_RACOL), parameters.get(PARAM_DECCOL), Utils.TAP_TABLE_TYPE_RADEC);
			}
			
			//Delete original file (only if it comes from a user file upload, DO NOT DO IT if it comes from 
			//           a job result.
			if(isFileUpload){
				long fileSize = file.length();
				file.delete();
				quota.reduceFileSize(fileSize);
			}

			// Commit modifications:
			dbConn.endTransaction();
			
			UwsEventsManager eventsManager = service.getFactory().getEventsManager();
			eventsManager.setEventTime(owner, TAPFactory.TABLE_CREATED_EVENT);
			eventsManager.setEventTime(owner, UwsEventType.QUOTA_DB_UPDATED_EVENT);

			//Vacuum Analyze the new table (ouside of transaction, with autocommit)
			dbConn.vacuumAnalyze(tapTable.getDBSchemaName(), tapTable.getDBName());
			
			
		}catch(DBException dbe){
//			//Substract added DB size
			dbConn.cancelTransaction();
			restoreDbQuota(owner, dbQuotaToRestore, dbe);
			throw dbe;
		}catch(ExceededSizeException ese){
			//Substract added DB size
			dbConn.cancelTransaction();	// ROLLBACK
			
			restoreDbQuota(owner, dbQuotaToRestore, ese);

			throw new TAPException("Upload limit exceeded ! You can upload at most "+maxFileSize+" bytes.");
		}catch(IOException ioe){
			//Substract added DB size
			dbConn.cancelTransaction();
			restoreDbQuota(owner, dbQuotaToRestore, ioe);

			if(ioe instanceof QuotaException){
				throw new TAPException(ioe);
			}else{
				throw new TAPException("Error while reading the VOTable of \""+tableName+"\" !", ioe);
			}
		}catch(NullPointerException npe){
			//Substract added DB size
			dbConn.cancelTransaction();
			restoreDbQuota(owner, dbQuotaToRestore, npe);

			if (votable != null && votable instanceof LimitedSizeInputStream)
				throw new TAPException("Upload limit exceeded ! You can upload at most "+maxFileSize+" bytes.");
			else
				throw new TAPException(npe);
		} catch (UwsException e) {
			//Substract added DB size
			dbConn.cancelTransaction();
			restoreDbQuota(owner, dbQuotaToRestore, e);
			throw new TAPException(e);
		}finally{
			try{
				dbConn.close();
				if (votable != null)
					votable.close();
				if(isFileUpload){
					long fileSize = file.length();
					file.delete();
					quota.reduceFileSize(fileSize);
				}
			}catch(Exception ioe){;}
		}

		return schema;
	}
	
	private void restoreDbQuota(UwsJobOwner owner, long dbQuotaToRestore, Exception e) throws TAPException{
		if(dbQuotaToRestore < 0){
			return;
		}
		try {
			UwsQuotaSingleton.getInstance().createOrLoadQuota(owner).reduceDbSize(dbQuotaToRestore);
		} catch (UwsException e1) {
			throw new TAPException(e.getMessage() + "\nWARNING: Cannot restore user '"+owner.getId()+"' quota due to: " + e1.getMessage(), e);
		}
	}

	
	/**
	 * Delete a user table.
	 * @return
	 * @throws TAPException
	 */
	private TAPSchema delete(UwsJobOwner owner) throws TAPException {
		JDBCPooledFunctions dbConn = (JDBCPooledFunctions)service.getFactory().createDBConnection("UploadConnection");

		// Begin a DB transaction:
		dbConn.startTransaction();

		TAPSchema schema = new TAPSchema(TAPMetadata.getUserSchema(owner));
		String tableName = null;
		try{
			tableName = parameters.get(PARAM_TABLENAME);
			boolean forceRemoval = getForceRemoval();

			// Drop table from the database:
			TAPTable tapTable = new TAPTable(tableName);
			schema.addTable(tapTable);
			
			// Preserve table size before removing it.
			long tableSize = dbConn.getTableSize(schema.getDBName(), tapTable.getDBName());
			
			dbConn.dropTable(tapTable, forceRemoval);
			dbConn.unregisterFromTapSchema(tapTable);
			
			// Remove possible shares
			removePossibleShares(tapTable, owner);

			// Commit modifications:
			dbConn.endTransaction();
			
			//UwsEventsManager eventsManager = service.getFactory().getEventsManager();
			//eventsManager.setEventTime(owner, TAPFactory.TABLE_REMOVED_EVENT);
			//eventsManager.setEventTime(owner, UwsEventType.QUOTA_DB_UPDATED_EVENT);

			try{
				//Substract added DB size
				UwsQuotaSingleton.getInstance().createOrLoadQuota(owner).reduceDbSize(tableSize);
			}catch(UwsException e){
				throw new TAPException(e);
			}
			
		}catch(DBException dbe){
			dbConn.cancelTransaction();	// ROLLBACK
			throw dbe;
		}catch(NullPointerException npe){
			dbConn.cancelTransaction();	// ROLLBACK
		}catch(UwsException uws){
			dbConn.cancelTransaction(); // ROLLBACK
			throw new TAPException(uws);
		}finally{
			generateRemovalEvents(owner);
			dbConn.close();
		}

		return schema;
	}
	
	private void generateRemovalEvents(UwsJobOwner owner){
		UwsEventsManager eventsManager = service.getFactory().getEventsManager();
		try{
			eventsManager.setEventTime(owner, TAPFactory.TABLE_REMOVED_EVENT);
			eventsManager.setEventTime(owner, UwsEventType.QUOTA_DB_UPDATED_EVENT);
		}catch(UwsException e){
			//ignore
		}
	}
	
	private boolean getForceRemoval(){
		String fc = parameters.get(PARAM_FORCE_REMOVAL);
		if (fc == null) {
			return false;
		} else {
			return Boolean.parseBoolean(fc);
		}
	}
	
	/**
	 * Removes sharing info
	 * @param tapTable
	 * @throws UwsException
	 */
	private void removePossibleShares(TAPTable tapTable, UwsJobOwner owner) throws UwsException{
		UwsShareManager shareManager = UwsManager.getInstance().getFactory().getShareManager();
		List<UwsShareItemBase> sharedItems = shareManager.getUserSharedItems(owner.getId(), false);
		String title = tapTable.getSchema().getDBName() + '.' + tapTable.getDBName();
		String resourceid = null;
		for(UwsShareItemBase sib: sharedItems){
			if(sib.getResourceType() == TAPMetadataLoader.SHARED_RESOURCE_TYPE_TABLE && sib.getTitle().equals(title)){
				resourceid = sib.getResourceId();
				break;
			}
		}
		//String resourceid = tapTable.getSchema().getDBName() + '.' + tapTable.getDBName();
		if(resourceid == null){
			//throw new UwsException("Resource identifier not found for table '"+title+"'");
			//nothing to remove
			return;
		}
		shareManager.removeSharedItem(resourceid, TAPMetadataLoader.SHARED_RESOURCE_TYPE_TABLE, owner);
	}

	/**
	 * Fetches table metadata into a TAPTable object
	 * @param tableName
	 * @param tableDesc
	 * @param votable
	 * @param ra_column Name of the ICRS RA column (to overwrite its UTYPE)
	 * @param dec_column Name of the ICRS DEC column (to overwrite its UTYPE)
	 * @return
	 */
	private TAPTable fetchTableMeta(final String tableName, final String tableDesc, final StarTable votable, final String ra_column, final String dec_column){
		TAPTable tapTable = new TAPTable(tableName);
		
		if(tableDesc!=null && !tableDesc.trim().isEmpty()){
			tapTable.setDescription(tableDesc);
		}
		tapTable.setDBName(tableName);

		boolean foundRa = false;
		boolean foundDec = false;
		String fieldNameLowerCase;
		int flags;
		
		for(int col=0 ; col<votable.getColumnCount(); col++){
			ColumnInfo field = votable.getColumnInfo(col);
			fieldNameLowerCase = field.getName().toLowerCase();
			
			int arraysize = 0;
			try{
				arraysize = TapUtils.getArraySize(field);
			}catch(IllegalArgumentException iae){
				service.getFactory().getLogger().warning("Invalid array-size in the uploaded table \""+tableName+"\": "+iae.getMessage()+". It will be considered as \"*\" !");
			}
			
			flags = 0;
			
			if(fieldNameLowerCase.equalsIgnoreCase(ra_column)){
				foundRa = true;
				flags |= Utils.TAP_COLUMN_TABLE_FLAG_RA;
			}
			
			if(fieldNameLowerCase.equalsIgnoreCase(dec_column)){
				foundDec = true;
				flags |= Utils.TAP_COLUMN_TABLE_FLAG_DEC;
			}
			
			TapUtils.addColumnToTable(tapTable, field, fieldNameLowerCase, arraysize, flags);

		}
		
		if(!foundRa){
			service.getFactory().getLogger().error("RA Column "+ra_column+" not found in table");
		}
		if(!foundDec){
			service.getFactory().getLogger().error("DEC Column "+dec_column+" not found in table");
		}

		return tapTable;
	}


	private void changeToLowerCaseIfNeeded(Map<String,String> parameters, String paramId){
		if(parameters.containsKey(paramId)){
			String v = parameters.get(paramId);
			if(v == null || "".equals(v)){
				return;
			} else {
				parameters.put(paramId, v.toLowerCase());
			}
		}
	}
	
	private File getResultDataFileForJob(String jobId, UwsJobOwner currentUser) throws UwsException{
		UwsStorage storage = service.getFactory().getStorageManager();
		UwsManager uwsManager = UwsManager.getInstance();
		UwsJob job = uwsManager.tryLoadJob(jobId, currentUser);
		List<UwsJobResultMeta> results = job.getResults();
		if(results == null || results.size() != 1){
			throw new UwsException("Found no results for job '"+jobId+"'");
		}
		if(results.size() != 1){
			throw new UwsException("Found more than one results job '"+jobId+"'");
		}
		UwsJobResultMeta r = results.get(0);
		File f = storage.getJobResultDataFile(job, r.getId());
		return f;
	}
}
