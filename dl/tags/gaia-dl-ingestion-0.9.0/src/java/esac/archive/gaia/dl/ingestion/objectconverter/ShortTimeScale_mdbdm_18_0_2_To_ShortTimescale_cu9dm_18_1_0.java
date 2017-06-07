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
//import gaia.cu1.mdb.cu7.enumeration.dm.VariabilityFlag;
//import gaia.cu1.tools.dm.GaiaRoot;
//import gaia.cu1.tools.exception.GaiaException;
//import gaia.cu9.archivearchitecture.core.dm.ShortTimescale;
//import gaia.cu9.archivearchitecture.core.dmimpl.ShortTimescaleImpl;
//
//public class ShortTimeScale_mdbdm_18_0_2_To_ShortTimescale_cu9dm_18_1_0 implements
//		IObjectConverter<gaia.cu1.mdb.cu7.sos.dm.ShortTimescale,ShortTimescale> {
//
//	@Override
//	public ShortTimescale transform(gaia.cu1.mdb.cu7.sos.dm.ShortTimescale cs, IFilter f)
//			throws GaiaException {
//
//		ShortTimescale sts = new ShortTimescaleImpl();
//		//Transform
//		sts.setSolutionId(cs.getSolutionId());
//		sts.setSourceId(cs.getSourceId());
//		sts.setAmplitudeEstimate(cs.getAmplitudeEstimate());
//		sts.setAmplitudeEstimate(cs.getAmplitudeEstimate());
//		sts.setMeanOfSquaredFovSlopes(cs.getMeanOfSquaredFovSlopes());
//		sts.setMeanOfSquaredFovSlopesErrors(cs.getMeanOfSquaredFovSlopesErrors());
//		sts.setNumberOfFovTransits(cs.getNumberOfFovTransits());
//		
//		boolean variability = false;
//		boolean variogram = false;
//		if (cs.getSlopeVariabilityFlag().equals(VariabilityFlag.VARIABLE)) {
//			variability = true;
//		}
//		if (cs.getVariogramVariabilityFlag().equals(VariabilityFlag.VARIABLE)) {
//			variogram = true;
//		}
//		sts.setSlopeVariabilityFlag(variability);
//		sts.setVariogramVariabilityFlag(variogram);
//		
//		if (cs.getFrequencySearchResult() == null) {
//			sts.setFrequency((double) 0);
//		} else {
//			sts.setFrequency(cs.getFrequencySearchResult().getFrequency());
//		}
//		
//		return sts;
//	}
//
//	@Override
//	public ShortTimescale transform(
//			gaia.cu1.mdb.cu7.sos.dm.ShortTimescale cs, IFilter<GaiaRoot> f,
//			IZeroPoints zPoints) throws GaiaException {
//		
//		return transform(cs, f);
//	}
//
//}
