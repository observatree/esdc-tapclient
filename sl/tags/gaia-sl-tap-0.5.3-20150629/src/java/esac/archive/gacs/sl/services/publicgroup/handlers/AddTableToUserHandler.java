package esac.archive.gacs.sl.services.publicgroup.handlers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import esac.archive.gacs.sl.services.publicgroup.PublicGroupHandler;
import esac.archive.gacs.sl.services.publicgroup.PublicGroupItem;
import esac.archive.gacs.sl.services.publicgroup.PublicGroupUtils;
import esac.archive.gacs.sl.tap.actions.JDBCPooledFunctions;
import esac.archive.gacs.sl.tap.actions.TapServiceConnection;
import esavo.tap.TAPService;
import esavo.uws.event.UwsEventType;
import esavo.uws.output.UwsOutputResponseHandler;
import esavo.uws.owner.UwsJobOwner;

/**
 * Adds access to a public group table.<br/>
 * A table cannot be added until the table is shared to public group (using UWS sharing framework).<br/>
 * User is extracted from the security context.<br/>
 * <p>Example
 * <pre><tt>
 * server/PublicGroup?ACTION=add&TABLES=schema1.table1,schema2.table2
 * </tt></pre>
 * 
 * @author juan.carlos.segovia@sciops.esa.int
 *
 */
public class AddTableToUserHandler implements PublicGroupHandler {
	
	public static final String ACTION = "add";
	
	@Override
	public String getAction(){
		return ACTION;
	}

	@Override
	public boolean canHandle(String action) {
		return ACTION.equalsIgnoreCase(action);
	}

	@Override
	public void handle(UwsJobOwner user, HttpServletResponse response, TAPService service, Map<String,String> parameters) throws IOException {
		String tablesParam = parameters.get(PublicGroupHandler.PARAM_TABLES);
		List<String> tables = PublicGroupUtils.getTables(tablesParam);
		
		//Will raise an exception
		PublicGroupUtils.checkValidTables(tables);
		
		List<PublicGroupItem> items = PublicGroupUtils.getTableItemsForUser(tables, user.getId());
		
		try {
			JDBCPooledFunctions dbConn = (JDBCPooledFunctions)service.getFactory().createDBConnection(JDBC_PUBLIC_GROUP_CONNECTION);
			String publicGroupId = service.getFactory().getConfiguration().getProperty(TapServiceConnection.PUBLIC_GROUP_ID_PROPERTY);
			List<String> publicGroupSharedItems = dbConn.getPublicGroupSharedItems(items, publicGroupId);
			List<String> nonPublicGroupTables = PublicGroupUtils.getNonPublicTables(items, publicGroupSharedItems);
			if(nonPublicGroupTables != null && nonPublicGroupTables.size() > 0){
				PublicGroupUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, 
						"Cannot obtain create access for user '"+user.getId()+"'", 
						"The following tables are not public:" + nonPublicGroupTables);
				return;
			}
			dbConn.addAccessToPublicGroupTables(items);
			service.getFactory().getEventsManager().setEventTime(user, UwsEventType.PUBLIC_GROUP_VIEW_TABLE);
			PublicGroupUtils.writeMsg(response, UwsOutputResponseHandler.OK, UwsOutputResponseHandler.CONTENT_TYPE_TEXT_PLAIN, "OK");
		} catch (Exception e) {
			PublicGroupUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, 
					"Cannot obtain accessible public group tables for user '"+user.getId()+"'", e.getMessage());
		}

	}

}
