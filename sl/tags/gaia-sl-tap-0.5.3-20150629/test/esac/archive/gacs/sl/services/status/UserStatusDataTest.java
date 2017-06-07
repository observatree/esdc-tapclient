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

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import esac.archive.gacs.sl.services.status.types.StatusDataFactory;
import esavo.uws.owner.UwsJobOwner;

public class UserStatusDataTest {
	
	@Test
	public void test1(){
		long taskid = 1;
		UwsJobOwner owner = new UwsJobOwner("anonymous", UwsJobOwner.ROLE_USER);
		UserInfo userInfo = new UserInfo(owner);
		UserStatusData usd = new UserStatusData(taskid, userInfo);
		
		Assert.assertEquals("task id", taskid, usd.getTaskId());
		Assert.assertEquals("user info", userInfo, usd.getUserInfo());
		
		usd.updateStatus(StatusDataFactory.createStatusData(TaskType.UPLOAD, "x"));
		usd.updateStatus(StatusDataFactory.createStatusData(TaskType.INGESTION, "y"));
		
		
		long t1 = usd.getLastUpdate();
		while(System.currentTimeMillis() == t1);
		
		Map<TaskType, StatusData> dataMap = usd.getAllStatus();
		Assert.assertTrue("Two status must be available", 2 == dataMap.size());
		
		Assert.assertNull("No parse status available", usd.consumeStatus(TaskType.PARSE));
		Assert.assertNotNull("Upload status not removed", usd.getStatus(TaskType.UPLOAD));
		Assert.assertNotNull("Ingestion status not removed", usd.getStatus(TaskType.INGESTION));
		Assert.assertNotNull("Upload status must be available", usd.consumeStatus(TaskType.UPLOAD));
		Assert.assertNull("Upload status removed (call to consume)", usd.getStatus(TaskType.UPLOAD));

		dataMap = usd.getAllStatus();
		Assert.assertTrue("One status must be available", 1 == dataMap.size());
		Assert.assertNotNull("Ingestion status not removed", usd.getStatus(TaskType.INGESTION));
		Assert.assertNull("No parse status available", usd.getStatus(TaskType.PARSE));
		Assert.assertNull("No upload status available", usd.getStatus(TaskType.UPLOAD));

		usd.consumeAllStatus();
		Assert.assertFalse("Times must be updated", t1 == usd.getLastUpdate());
		
		dataMap = usd.getAllStatus();
		Assert.assertTrue("No status must be available", 0 == dataMap.size());
		
		//test-coverage
		usd.toString();
	}

}
