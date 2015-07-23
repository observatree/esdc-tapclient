package esac.archive.gacs.sl;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class AllTests {
	
	public static Test suite() {
		TestSuite suite = new TestSuite("All GACS SL tests");
		
		//submodules
		suite.addTest(esac.archive.gacs.sl.services.AllTests.suite());
		suite.addTest(esac.archive.gacs.sl.tap.AllTests.suite());
		suite.addTest(suite(esac.archive.gacs.sl.TestVotTypes.class));

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
