package esac.archive.gacs.sl.services.admin;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class AllTests {
	
	public static Test suite() {
		TestSuite suite = new TestSuite("All GACS SL admin services tests");
		
		//submodules
		suite.addTest(suite(TemplatesTest.class));
		suite.addTest(suite(ManagerUtilsTest.class));
		suite.addTest(suite(ManagerTest.class));
		
		suite.addTest(esac.archive.gacs.sl.services.admin.handlers.AllTests.suite());

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
