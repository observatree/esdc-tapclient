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
