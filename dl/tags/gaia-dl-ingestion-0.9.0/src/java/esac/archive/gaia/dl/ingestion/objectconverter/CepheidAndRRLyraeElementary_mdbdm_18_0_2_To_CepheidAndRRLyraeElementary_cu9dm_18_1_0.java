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
//package esac.archive.gaia.dl.ingestion.objectconverter;
//
//import esac.archive.gaia.dl.ingestion.filters.IFilter;
//import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.IZeroPoints;
//import gaia.cu1.tools.dal.ObjectFactory;
//import gaia.cu1.tools.dm.GaiaRoot;
//import gaia.cu1.tools.exception.GaiaException;
//import gaia.cu9.archivearchitecture.core.abstracttables.dm.CepheidAndRRLyraeElementary;
//import gaia.cu9.archivearchitecture.core.dm.Cepheid;
//import gaia.cu9.archivearchitecture.core.dm.RRLyrae;
//
//public class CepheidAndRRLyraeElementary_mdbdm_18_0_2_To_CepheidAndRRLyraeElementary_cu9dm_18_1_0 implements
//IObjectConverter<gaia.cu1.mdb.cu7.sos.dm.CepheidAndRRLyraeElementary, CepheidAndRRLyraeElementary> {
//
//	ObjectFactory<CepheidAndRRLyraeElementary> cu9Fact;
//
//	@Override
//	public CepheidAndRRLyraeElementary transform(gaia.cu1.mdb.cu7.sos.dm.CepheidAndRRLyraeElementary cs, IFilter<GaiaRoot> f) throws GaiaException {
//		CepheidAndRRLyraeElementary cr = null;
//		if(cu9Fact==null) {
//			cu9Fact = new ObjectFactory<CepheidAndRRLyraeElementary>(CepheidAndRRLyraeElementary.class);
//		}
//		cr = cu9Fact.convert(cs);
//
//		//Transform
//		cr.setEpochG(cs.getEpochs()[0]);
//		cr.setEpochBp(cs.getEpochs()[1]);
//		cr.setEpochRp(cs.getEpochs()[2]);
//		cr.setEpochGError(cs.getEpochsError()[0]);
//		cr.setEpochBpError(cs.getEpochsError()[1]);
//		cr.setEpochRpError(cs.getEpochsError()[2]);
//		return cr;
//	
//	}
//
//	@Override
//	public CepheidAndRRLyraeElementary transform(
//			gaia.cu1.mdb.cu7.sos.dm.CepheidAndRRLyraeElementary cs,
//			IFilter<GaiaRoot> f, IZeroPoints zPoints) throws GaiaException {
//		return transform(cs, f);
//	}
//
//}
