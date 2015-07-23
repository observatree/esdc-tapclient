package esac.archive.gacs.sl.services.admin.handlers;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import esac.archive.gacs.sl.services.admin.ManagerHandler;
import esac.archive.gacs.sl.services.admin.ManagerUtils;
import esavo.uws.UwsException;
import esavo.uws.UwsManager;
import esavo.uws.event.UwsEventsManager;
import esavo.uws.output.UwsOutputResponseHandler;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.storage.UwsStorage;

/**
 * <pre><tt>
 * admin?ACTION=events&COMMAND=list&OWNER=userid
 * admin?ACTION=events&COMMAND=set&OWNER=userid&EVENT=401
 * admin?ACTION=events&COMMAND=removeAll&OWNER=userid
 * </tt></pre>
 * 
 * @author jsegovia
 *
 */
public class EventsHandler implements ManagerHandler {

	public static final String ACTION = "events";
	
	public static final String PARAM_COMMAND = "COMMAND";
	public static final String PARAM_TIMESTAMP = "TIMESTAMP";
	public static final String PARAM_EVENT = "EVENT";
	public static final String PARAM_OWNER = "OWNER";
	
	public enum Command {
		set,
		remove,
		removeAll,
		list
	};

	@Override
	public boolean canHandle(String action) {
		return ACTION.equalsIgnoreCase(action);
	}

	@Override
	public void handle(Map<String, String> parameters, HttpServletResponse response, UwsStorage uwsStorage) throws IOException  {
		Command command = getCommand(parameters.get(PARAM_COMMAND));
		if(command == null){
			ManagerUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Error", "Unknown events command '"+parameters.get(PARAM_COMMAND)+"'.");
		}
		UwsJobOwner owner = createOwner(parameters.get(PARAM_OWNER));
		if(owner == null){
			ManagerUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Error", "Onwer parameter not found.");
		}

		UwsEventsManager eventsManager = UwsManager.getInstance().getFactory().getEventsManager();
		//String timeStamp = parameters.get(PARAM_TIMESTAMP);
		int event = getEventId(parameters.get(PARAM_EVENT));
		String result = null;
		switch(command){
		case set:
			result = setEvent(eventsManager, owner, event);
			break;
		case remove:
			//result = removeEvent(eventsManager, owner, event);
			result = "Not implemented yet.";
			break;
		case removeAll:
			result = removeAllEvents(eventsManager, owner);
			break;
		default:
			//list
			result = listEvents(eventsManager, owner);
		}
		ManagerUtils.writeMsg(response, UwsOutputResponseHandler.OK, "result", result);
		response.flushBuffer();
		return;
	}
	
	private UwsJobOwner createOwner(String ownerid){
		if(ownerid == null){
			return null;
		}
		UwsJobOwner owner = new UwsJobOwner(ownerid, UwsJobOwner.ROLE_USER);
		return owner;
	}
	
	@Override
	public String getActionIdentifier() {
		return ACTION;
	}
	
	private Command getCommand(String command){
		if(command == null){
			return null;
		}
		try{
			return Command.valueOf(command);
		}catch(Exception e){
			return null;
		}
	}
	
	private int getEventId(String event){
		if(event == null){
			return -1;
		}
		try{
			return Integer.parseInt(event);
		}catch(NumberFormatException nfe){
			return -1;
		}
	}
	
	private String setEvent(UwsEventsManager eventsManager, UwsJobOwner owner, int eventId) throws IOException {
		try{
			eventsManager.setEventTime(owner, eventId);
			return "Event '"+eventId+"' set for user '"+owner.getId()+"'";
		}catch(UwsException e){
			throw new IOException("Cannot set event '"+eventId+"' for user '"+owner.getId()+"' due to: " + e.getMessage(), e);
		}
	}
	
	private String removeAllEvents(UwsEventsManager eventsManager, UwsJobOwner owner){
		eventsManager.removeEventItem(owner);
		return "Removed all events for user '"+owner.getId()+"'";
	}
	
	private String listEvents(UwsEventsManager eventsManager, UwsJobOwner owner){
		Map<Integer, Long> events = eventsManager.getTimesForEvents(owner);
		if(events == null){
			return "No events set for user '"+owner.getId()+"'";
		}else{
			StringBuilder sb = new StringBuilder();
			boolean firstTime = true;
			for(Entry<Integer, Long> e: events.entrySet()){
				if(firstTime){
					firstTime = false;
				}else{
					sb.append(", ");
				}
				sb.append("Event: ").append(e.getKey()).append(": ").append(e.getValue());
			}
			return sb.toString();
		}
	}


}