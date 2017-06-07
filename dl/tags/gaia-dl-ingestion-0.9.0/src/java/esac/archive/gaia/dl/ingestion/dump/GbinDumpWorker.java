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
package esac.archive.gaia.dl.ingestion.dump;

import esac.archive.gaia.dl.ingestion.dump.gaiadump.GacsPopulator;
import esac.archive.gaia.dl.ingestion.dump.gaiadump.GacsTranslator;
import esac.archive.gaia.dl.ingestion.main.AuxiliaryFunctions;
import gaia.cu1.mdb.cu2.um.umtypes.dm.UMStellarSource;
import gaia.cu1.tools.dal.gbin.GbinFactory;
import gaia.cu1.tools.dal.jdbc.JdbcStore;
import gaia.cu1.tools.dal.table.GaiaTable;
import gaia.cu1.tools.dm.GaiaRoot;
import gaia.cu1.tools.util.GaiaFactory;
import gaia.cu1.tools.util.props.PropertyLoader;
import gaia.cu1.tools.dal.ObjectFactory;
import gaia.cu1.tools.exception.GaiaException;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

public class GbinDumpWorker extends GenericDumpWorker{
	private Class<?> extractedClass; 
	private String gaiaProperties;
	
	public GbinDumpWorker(
			String gaiaProperties,
			Class<?> extractedClass,
			String dumpOutputPath, 
			String dumpFilePattern,
			String schemaName,
			String tableName) {
		this.extractedClass = extractedClass;
		this.dumpOutputPath = dumpOutputPath;
		this.dumpFilePattern = dumpFilePattern;
		this.schemaName = schemaName;
		this.tableName = tableName;
		this.gaiaProperties = gaiaProperties;
	}

	public void dump() throws SQLException, GaiaException {
		
		//PropertyLoader.load();
		//PropertyLoader.load(gaiaProperties, true);

		JdbcStore st = GaiaFactory.getJdbcStore();
		st.getConnection().setAutoCommit(false);
		st.setSelectProperty(extractedClass, "*");
		GaiaTable tab = st.executeQueryGT("select * from "+schemaName + "." + tableName, new Object[0]);
		GacsTranslator translator = new GacsTranslator(UMStellarSource.class);
		GacsPopulator gacsPopulator = new GacsPopulator();
		ObjectFactory<UMStellarSource> cFact = new ObjectFactory<UMStellarSource>(UMStellarSource.class);

		UMStellarSource cs = null;
		
		ArrayList<GaiaRoot> gr = new ArrayList<GaiaRoot>();
		int count = 0, fileCounter = 0;
				
		while(tab.next()) {
			cs = cFact.getObject(tab);

			gacsPopulator.populate(translator, tab, cs);

			gr.add(cs);
			
			if(gr.size() % 10000 == 0) {
				System.out.println("GR "  + gr.size() );
			}
			
			if(++count%1000000 == 0) {
				String number = fileCounter + "";
				if (fileCounter < 10) {
					number = "0000" + number;
				}else if (fileCounter < 100) {
					number = "000" + number;
				}else if (fileCounter < 1000) {
					number = "00" + number;
				}else if (fileCounter < 10000) {
					number = "0" + number;
				}
				
				String out = dumpOutputPath + File.separator + "Dumped_" + number + ".gbin";
				GbinFactory.getGbinWriter().writeToFile(new File(out), gr, false);
				gr.clear();
				fileCounter++;
				System.out.println("Dumped " + count + " fileCounter " + fileCounter );
			}
			
		}
		
		String number = fileCounter + "";
		if (fileCounter < 10) {
			number = "0000" + number;
		}else if (fileCounter < 100) {
			number = "000" + number;
		}else if (fileCounter < 1000) {
			number = "00" + number;
		}else if (fileCounter < 10000) {
			number = "0" + number;
		}
		String out = dumpOutputPath + File.separator + "Dumped_" + number + ".gbin";
		GbinFactory.getGbinWriter().writeToFile(new File(out), gr, false);
		gr.clear();
		System.out.println("Dumped " + count + " fileCounter " + fileCounter );

	}
	

}
