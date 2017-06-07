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
package esac.archive.gacs.sl.services.nameresolution.actions.handlers.tgas;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;

import esavo.sl.services.nameresolution.TargetResolutionException;
import esavo.sl.services.nameresolution.actions.handlers.TargetResolutionActionHandler;

public class TgasTargetResolution implements TargetResolutionActionHandler {

	public static final String SIMBAD_TAP_SYNC_URL = "http://simbad.u-strasbg.fr/simbad/sim-tap/sync";
	
	public static final String PARAMETER_ACTION = "ACTION";
	public static final String ACTION_NAME = "tgas_resolution";
	public static final String PARAMETER_TARGET = "TARGET";

	@Override
	public boolean canHandle(HttpServletRequest request) {
		
		String action = request.getParameter(PARAMETER_ACTION);
		String target = request.getParameter(PARAMETER_TARGET);
		
		if(action!=null && action.trim().equals("tgas_resolution")){
			if(target!=null && !target.trim().isEmpty()){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) throws TargetResolutionException {
		
		String target = request.getParameter(PARAMETER_TARGET);
		
		Collection<TgasTarget> resolution;
		
		try {
			resolution = resolveByName(target);
			writeJson(response,resolution);
		} catch (JSONException e) {
			throw new TargetResolutionException(e);
		} catch (IOException e) {
			throw new TargetResolutionException(e);
		}
		
	}

	@Override
	public String toString(){
		return "Action handler: " + this.getClass().getName();
	}

	@Override
	public String getAction() {
		return ACTION_NAME;
	}
	
	
	
	private static String getQueryByName(String name) throws UnsupportedEncodingException{
	
		String escapedName = StringEscapeUtils.escapeSql(name);
		
		String queryPrincipalByName = "SELECT main_id, id2.id as tyc_id, RA, DEC "
				+ "FROM basic "
				+ "JOIN ident as id1 ON id1.oidref = oid "
				+ "JOIN ident as id2 ON id2.oidref = oid "
				+ "WHERE id1.id = '"+escapedName+"' "
				+ "AND   (id2.id like 'TYC %' OR id2.id like 'HIP %') ";
		
		
		return URLEncoder.encode(queryPrincipalByName, "UTF-8");
	}
	
	private static Collection<TgasTarget> resolveByName(String name) throws TargetResolutionException, JSONException{
		
		ArrayList<TgasTarget> result = new ArrayList<TgasTarget>();
		
		String query;
		try {
			query = getQueryByName(name);
		} catch (UnsupportedEncodingException e) {
			throw new TargetResolutionException(e);
		}
		
		URL url;
		try {
			url = new URL(SIMBAD_TAP_SYNC_URL+"?REQUEST=doQuery&LANG=ADQL&FORMAT=json&QUERY="+query);
		} catch (MalformedURLException e) {
			throw new TargetResolutionException(e);
		}

		InputStream inputStream;
		try {
			inputStream = url.openStream();
		} catch (IOException e) {
			throw new TargetResolutionException(e);
		}

		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(inputStream, writer);
		} catch (IOException e) {
			throw new TargetResolutionException(e);
		}
		String jsonString = writer.toString();
		
		
		JSONObject obj = new JSONObject(jsonString);
		JSONArray data = obj.getJSONArray("data");
		TgasTarget target = new TgasTarget();
		for (int i = 0; i < data.length(); i++){
			JSONArray row = data.getJSONArray(i);

			target.setPrincipalName(row.getString(0));
			String tycHipStr = row.getString(1);
			if(tycHipStr!=null && !tycHipStr.trim().isEmpty()){
				if(tycHipStr.toLowerCase().startsWith("tyc")){
					target.setTychoName(tycHipStr);
				}else if(tycHipStr.toLowerCase().startsWith("hip")){
					target.setHipName(tycHipStr);
				}
			}
			target.setTychoName(row.getString(1));
			target.setRa(row.getDouble(2));
			target.setDe(row.getDouble(3));
		}
		result.add(target);

		
		
		return result;
	}
	

	private static void writeJson(HttpServletResponse response, Collection<TgasTarget> data) throws JSONException, IOException{
		PrintWriter writer=response.getWriter();
		JSONWriter json = new JSONWriter(writer);

		json.array();
		for(TgasTarget target:data){
			json.object()
			.key("principalName").value(target.getPrincipalName())
			.key("tychoName").value(target.getTychoName())
			.key("hipName").value(target.getHipName())
			.key("ra").value(target.getRa())
			.key("de").value(target.getDe())
			.endObject();
		}
		json.endArray();
	}

}
