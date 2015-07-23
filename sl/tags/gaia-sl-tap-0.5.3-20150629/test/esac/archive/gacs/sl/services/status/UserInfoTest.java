package esac.archive.gacs.sl.services.status;

import junit.framework.Assert;

import org.junit.Test;

import esavo.uws.owner.UwsJobOwner;

public class UserInfoTest {
	
	@Test
	public void test1(){
		UwsJobOwner owner = new UwsJobOwner("anonymous", UwsJobOwner.ROLE_USER);
		UserInfo userInfo = new UserInfo(null);
		
		Assert.assertNull("No owner set", userInfo.getOwner());
		
		userInfo.setOwner(owner);
		Assert.assertEquals("Owner", owner, userInfo.getOwner());
		
		String ipAddress = "address";
		userInfo.setip(ipAddress);
		Assert.assertEquals("ip", ipAddress, userInfo.getip());
		
		String key = "x";
		String value = "value";
		Assert.assertNull("no key", userInfo.get(key));
		userInfo.add(key, value);
		Assert.assertEquals("key", value, userInfo.get(key));
		
		//test-coverage
		userInfo.getStartTime();
		
	}

}
