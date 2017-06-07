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
package esac.archive.gacs.sl.services.status;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import esac.archive.gacs.sl.tap.TapUtils;
import esavo.uws.owner.UwsJobOwner;

public class ProgressInputStreamTest {
	
	@Test
	public void test1() throws IOException{
		StatusManager.enableCheckThread(false);
		String data = "12345";
		long totalSize = data.length();
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		long taskId = 1;
		
		ProgressInputStream input;

		//Null input stream
		input = new ProgressInputStream(null,0,0);
		try{
			input.read();
			Assert.fail("Exception expected: null input stream");
		}catch(IOException ioe){
		}

		StatusManager sm = StatusManager.getInstance();
		//DefaultJobOwner owner = TapUtils.createJobOwner("test");
		UwsJobOwner owner = new UwsJobOwner("anonymous", UwsJobOwner.ROLE_USER);
		UserInfo userInfo = new UserInfo(owner);
		taskId = sm.createUserIdentifier(userInfo);

		
		//updateTriggerValue = totalSize+1000 (never updates)
		input = new ProgressInputStream(bais, totalSize, taskId, totalSize+1000);
		int r;
		int index = 0;
		StatusData sd = null;
		while((r = input.read()) != -1){
			Assert.assertEquals("char at: " + index, data.charAt(index), (char)r );
			sd = sm.getStatus(taskId, TaskType.PARSE);
			//Assert.assertFalse("100% reached", sd.getData().equals("100"));
			Assert.assertNull("Without trigger value", sd);
			index++;
		}
		
		sd = sm.getStatus(taskId, TaskType.PARSE);
		Assert.assertTrue("100% reached", sd.getData().equals("100"));
		
		//updates
		bais = new ByteArrayInputStream(data.getBytes());
		input = new ProgressInputStream(bais, totalSize, taskId, 0);
		index = 0;
		int percentage = 20;
		int percentageReached = 0;
		while((r = input.read()) != -1){
			Assert.assertEquals("char at: " + index, data.charAt(index), (char)r );
			sd = sm.getStatus(taskId, TaskType.PARSE);
			//System.out.println(sd.getData());
			percentageReached += percentage;
			Assert.assertEquals("Percentage", ""+percentageReached, sd.getData());
			index++;
		}

		sd = sm.getStatus(taskId, TaskType.PARSE);
		Assert.assertTrue("100% reached", sd.getData().equals("100"));
	}

}
