package esac.archive.gacs.sl.services.upload;

import junit.framework.Assert;

import org.junit.Test;

import esac.archive.gacs.sl.services.status.StatusManager;
import esac.archive.gacs.sl.services.status.UserInfo;
import esac.archive.gacs.sl.tap.TapUtils;
import esavo.uws.owner.UwsJobOwner;

public class UploadProgressListenerTest {
	
	@Test
	public void test1(){
		long bytesRead = 0L;
		long contentLength = -1L;
		int items = 0;
		int percentDone = 0;

		UploadProgressListener upl = new UploadProgressListener();
		
		Assert.assertEquals("Empty num100Ks", 0L, upl.getNum100Ks());
		Assert.assertEquals("Empty bytesRead", bytesRead, upl.getTheBytesRead());
		Assert.assertEquals("Empty content-length", contentLength, upl.getTheContentLength());
		Assert.assertEquals("Empty which item", items, upl.getWhichItem());
		Assert.assertEquals("Empty percentage", 0, upl.getPercentDone());
		Assert.assertFalse("Empty content-length known", upl.isContentLengthKnown());
		Assert.assertEquals("Empty message", "0 of Unknown-Total bytes have been read.", upl.getMessage());
		
		bytesRead = 100L;
		contentLength = 1000000L;
		items = 10;
		upl.update(bytesRead, contentLength, items);
		
		Assert.assertEquals("Case 1, num100Ks", 0L, upl.getNum100Ks());
		Assert.assertEquals("Case 1, bytesRead", bytesRead, upl.getTheBytesRead());
		Assert.assertEquals("Case 1, content-length", contentLength, upl.getTheContentLength());
		Assert.assertEquals("Case 1, which item", items, upl.getWhichItem());
		Assert.assertEquals("Case 1, percentage", 0, upl.getPercentDone());
		Assert.assertTrue("Case 1, content-length known", upl.isContentLengthKnown());
		Assert.assertEquals("Case 1, message", "" + bytesRead + " of " + contentLength + " bytes have been read (" + percentDone + "% done).", upl.getMessage());
		
		bytesRead = 200000;
		upl.update(bytesRead, contentLength, items);
		
		percentDone = 20;

		Assert.assertEquals("Case 2, num100Ks", 2L, upl.getNum100Ks());
		Assert.assertEquals("Case 2, bytesRead", bytesRead, upl.getTheBytesRead());
		Assert.assertEquals("Case 2, content-length", contentLength, upl.getTheContentLength());
		Assert.assertEquals("Case 2, which item", items, upl.getWhichItem());
		Assert.assertEquals("Case 2, percentage", percentDone, upl.getPercentDone());
		Assert.assertTrue("Case 2, content-length known", upl.isContentLengthKnown());
		Assert.assertEquals("Case 2, message", "" + bytesRead + " of " + contentLength + " bytes have been read (" + percentDone + "% done).", upl.getMessage());
		
		contentLength = -1L;
		percentDone = 0;
		upl.setContentLengthKnown(false);
		upl.setNum100Ks(0);
		upl.setPercentDone(percentDone);
		upl.setTheBytesRead(0);
		upl.setTheContentLength(contentLength);
		upl.setWhichItem(0);
		upl.update(bytesRead, contentLength, items);

		Assert.assertEquals("Case 3, num100Ks", 2L, upl.getNum100Ks());
		Assert.assertEquals("Case 3, bytesRead", bytesRead, upl.getTheBytesRead());
		Assert.assertEquals("Case 3, content-length", contentLength, upl.getTheContentLength());
		Assert.assertEquals("Case 3, which item", items, upl.getWhichItem());
		Assert.assertEquals("Case 3, percentage", percentDone, upl.getPercentDone());
		Assert.assertFalse("Case 3, content-length known", upl.isContentLengthKnown());
		Assert.assertEquals("Case 3, message", "" + bytesRead + " of Unknown-Total bytes have been read.", upl.getMessage());
	}
	
	@Test
	public void test2(){
		//JobOwner owner = TapUtils.createJobOwner("test");
		UwsJobOwner owner = new UwsJobOwner("anonymous", UwsJobOwner.ROLE_USER);
		UserInfo userInfo = new UserInfo(owner);
		long taskId = StatusManager.getInstance().createUserIdentifier(userInfo);
		UploadProgressListener upl = new UploadProgressListener();
		upl.setTaskId(taskId);
		
		long bytesRead = 200000L;
		long contentLength = 1000000L;
		int items = 10;
		int percentDone = 20;
		upl.update(bytesRead, contentLength, items);
		
		Assert.assertEquals("Case 1, num100Ks", 2L, upl.getNum100Ks());
		Assert.assertEquals("Case 1, bytesRead", bytesRead, upl.getTheBytesRead());
		Assert.assertEquals("Case 1, content-length", contentLength, upl.getTheContentLength());
		Assert.assertEquals("Case 1, which item", items, upl.getWhichItem());
		Assert.assertEquals("Case 1, percentage", percentDone, upl.getPercentDone());
		Assert.assertTrue("Case 1, content-length known", upl.isContentLengthKnown());
		Assert.assertEquals("Case 1, message", "" + bytesRead + " of " + contentLength + " bytes have been read (" + percentDone + "% done).", upl.getMessage());
		
		StatusManager.getInstance().remove(taskId);
	}

}
