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
package esac.archive.gacs.sl.services.status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import esac.archive.gacs.sl.services.status.types.StatusDataFactory;
import esac.archive.gacs.sl.services.status.types.StatusUpload;
import esac.archive.gacs.sl.tap.TapUtils;
import esavo.uws.owner.UwsJobOwner;

public class StatusManagerTest {
	
	private static final TaskType[] TYPES_TO_TEST = {};
	
	@Test
	public void test1(){
		//Disable check mechanism thread
		StatusManager.enableCheckThread(false);
		//DefaultJobOwner owner = TapUtils.createJobOwner("test");
		UwsJobOwner owner = new UwsJobOwner("anonymous", UwsJobOwner.ROLE_USER);
		UserInfo userInfo = new UserInfo(owner);
		
		StatusManager sm = StatusManager.getInstance();
		
		try{
			sm.getStatus(-1);
			Assert.fail("Exception expected: identifier not found");
		}catch(IllegalArgumentException e){
			
		}
		
		checkAllTypes(userInfo, sm);
		
		//test-coverage
		sm.dump();
		sm.toString();
	}
	
	@Test
	public void testTimeOut(){
		//Disable check mechanism thread
		StatusManager.enableCheckThread(false);
		
		StatusManager sm = StatusManager.getInstance();
		sm.removeAll();
		
		sm.removeInactiveEntries(0, 0);

		//DefaultJobOwner owner = TapUtils.createJobOwner("test");
		UwsJobOwner owner = new UwsJobOwner("anonymous", UwsJobOwner.ROLE_USER);
		UserInfo userInfo = new UserInfo(owner);
		long id = sm.createUserIdentifier(userInfo);
		
		StatusData sd = StatusDataFactory.createStatusData(TaskType.UPLOAD, "50");
		sm.updateStatus(id, sd);
		
		UserStatusData usd = sm.getUserStatusData(id);
		long lastUpdate = usd.getLastUpdate();
		
		long time = lastUpdate;
		long timeOut = 0;
		
		//No timeout => must contain one client
		time = lastUpdate;
		timeOut = 0;
		sm.removeInactiveEntries(time, timeOut);
		Assert.assertTrue("No tiemout, one client expected.", sm.getNumClients() == 1);
		
		//timeout => the client must be removed
		time += 100;
		sm.removeInactiveEntries(time, timeOut);
		Assert.assertTrue("Timeout, one client expected.", sm.getNumClients() == 0);
	}
	
	private void checkAllTypes(UserInfo userInfo, StatusManager sm){
		long id;
		String dump;
		for(TaskType type: TaskType.values()){
			id = sm.createUserIdentifier(userInfo);
			StatusData sd = StatusDataFactory.createStatusData(type, "x");
			sm.updateStatus(id, sd);
			checkByType(id, sd.getType(), sm);
			Map<TaskType, StatusData> data = sm.getStatus(id);
			Assert.assertTrue("Number of status must be 1", 1 == data.size());
			Assert.assertNotNull("Expected " + sd.getType().name(), data.get(sd.getType()));
			dump = sm.dump();
			findIdAndType(dump, id);
			sm.remove(id);
			checkNotFound(id, sm, sd.getType());	
		}
	}
	
	private void findIdAndType(String dump, long id){
		//"Task id: " + this.taskid + ", User info: " + this.userInfo + ", last update: " + this.lastUpdate;
		BufferedReader br = new BufferedReader(new StringReader(dump));
		String line;
		try {
			while((line = br.readLine()) != null){
				if(line.startsWith("Task id: " + id)){
					return;
				}
			}
			br.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Assert.fail("Task identifier '"+id+"' not found in:\n" + dump);
	}
	
	private void checkByType(long id, TaskType validType, StatusManager sm){
		StatusData sd;
		for(TaskType t: TaskType.values()){
			sd = sm.getStatus(id, t);
			if (t == validType){
				Assert.assertNotNull("Valid type '"+t.name()+"' expected", sd);
			}else{
				Assert.assertNull("Type '"+t.name()+"' unexpected", sd);
			}
		}
	}
	
	private void checkNotFound(long id, StatusManager sm, TaskType type){
		try{
			sm.getStatus(id, type);
			Assert.fail("Exception expected: id removed");
		}catch(IllegalArgumentException e){
		}
		try{
			sm.getStatus(id);
			Assert.fail("Exception expected: id removed");
		}catch(IllegalArgumentException e){
		}
	}

}
