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
//package esac.archive.gaia.dl.ingestion.objectconverter;
//
//import esac.archive.gaia.dl.ingestion.filters.IFilter;
//import gaia.cu1.mdb.cu1.integrated.dm.CompleteSource;
//import gaia.cu1.tools.dal.ObjectFactory;
//import gaia.cu1.tools.exception.GaiaException;
//import gaia.cu9.archivearchitecture.core.abstracttables.dm.CepheidAndRRLyraeElementary;
//import gaia.cu9.archivearchitecture.core.dm.Cepheid;
//import gaia.cu9.archivearchitecture.core.dm.RRLyrae;
//import gaia.cu9.archivearchitecture.core.dmimpl.RRLyraeImpl;
//
//public class RRLyrae_mdbdm_18_0_2_To_RRLyrae_cu9dm_18_1_0 extends 
//	CepheidAndRRLyraeElementary_mdbdm_18_0_2_To_CepheidAndRRLyraeElementary_cu9dm_18_1_0 {
//
//	public RRLyrae transform(gaia.cu1.mdb.cu7.sos.dm.RRLyrae cs, IFilter f) throws GaiaException {
//		RRLyrae	rrl = (RRLyrae) transform((gaia.cu1.mdb.cu7.sos.dm.CepheidAndRRLyraeElementary) cs, f);
//		
//		//Transform
////		rrl.setRrlClassification(cs.getRrlClassification());
////		rrl.setRrlBestClassification(cs.getRrlClassification());
////		rrl.setRrlDoubleFlag(cs.getRrlDoubleFlag());
//		
//		return rrl;
//	}
//
//}
