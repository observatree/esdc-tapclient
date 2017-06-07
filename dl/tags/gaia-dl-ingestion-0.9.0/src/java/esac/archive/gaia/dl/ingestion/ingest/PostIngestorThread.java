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
package esac.archive.gaia.dl.ingestion.ingest;

import java.sql.Statement;
import java.util.concurrent.Callable;

import esac.archive.gaia.dl.ingestion.db.JDBCPoolSingleton;

public class PostIngestorThread implements Callable<Integer> {

	private JDBCPoolSingleton con = null;
	private String index = null;
	
	public PostIngestorThread(JDBCPoolSingleton c, String i) {
		con = c;
		index = i;
	}
	
	@Override
	public Integer call() throws Exception {
		if (con != null && index != null) {
			Statement stmt = null;
			try {
				//con.getConnection().setAutoCommit(false);
				stmt = con.getConnection().createStatement();
				stmt.execute(index);
				//con.getConnection().commit();
			}finally {
				if (stmt != null) {
					stmt.close();
				}
				if (con.getConnection() != null) {
					con.getConnection().close();
				}
			}
			return 1;
		}
		else {
			return 0;
		}
	}

}
