package esac.archive.gacs.sl.services.status;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class AllTests {
	
	public static Test suite() {
		TestSuite suite = new TestSuite("All GACS SL status services tests");
		
		//submodules
		suite.addTest(suite(StatusManagerTest.class));
		suite.addTest(suite(TaskStatusTest.class));
		suite.addTest(suite(IdManagerTest.class));
		suite.addTest(suite(ProgressInputStreamTest.class));
		suite.addTest(suite(UserStatusDataTest.class));
		suite.addTest(suite(UserInfoTest.class));

		return suite;
	}

	/**
	 * Allows you to run all tests as an application.
	 */
	public static void main(String[] arguments) {
		TestRunner.run(suite());
	}

	private static Test suite(Class<?> c) {
		return new JUnit4TestAdapter(c);
	}


}
