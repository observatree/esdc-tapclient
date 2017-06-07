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
package esac.archive.gacs.sl.services;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class AllTests {
	
	public static Test suite() {
		TestSuite suite = new TestSuite("All GACS SL services tests");
		
		//submodules
		suite.addTest(esac.archive.gacs.sl.services.status.AllTests.suite());
		suite.addTest(esac.archive.gacs.sl.services.transform.AllTests.suite());
		suite.addTest(esac.archive.gacs.sl.services.upload.AllTests.suite());
		suite.addTest(esac.archive.gacs.sl.services.login.AllTests.suite());
		suite.addTest(esac.archive.gacs.sl.services.nameresolution.AllTests.suite());
		suite.addTest(esac.archive.gacs.sl.services.tabletool.AllTests.suite());
		suite.addTest(esac.archive.gacs.sl.services.util.AllTests.suite());
		suite.addTest(esac.archive.gacs.sl.services.actions.AllTests.suite());
		suite.addTest(esac.archive.gacs.sl.services.admin.AllTests.suite());

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
