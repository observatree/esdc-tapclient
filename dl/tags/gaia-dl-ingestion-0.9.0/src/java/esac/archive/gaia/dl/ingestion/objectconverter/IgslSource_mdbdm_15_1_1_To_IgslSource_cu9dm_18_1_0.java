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
package esac.archive.gaia.dl.ingestion.objectconverter;

import java.util.logging.Logger;

import esac.archive.gaia.dl.ingestion.filters.IFilter;
import esac.archive.gaia.dl.ingestion.main.AuxiliaryFunctions;
import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.IZeroPoints;
import gaia.cu1.tools.dm.GaiaRoot;
import gaia.cu1.tools.exception.GaiaException;
import gaia.cu1.tools.numeric.GMath;
import gaia.cu9.operations.auxiliarydata.igsl.dm.IgslSource;


public class IgslSource_mdbdm_15_1_1_To_IgslSource_cu9dm_18_1_0 implements IObjectConverter<gaia.cu1.mdb.cu3.auxdata.igsl.dm.IgslSource,IgslSource> {
	NewObjectFactory<IgslSource> cu9Fact;
	private static final Logger logger = Logger.getLogger(IgslSource_mdbdm_15_1_1_To_IgslSource_cu9dm_18_1_0.class.getName());


	@Override
	public IgslSource transform(gaia.cu1.mdb.cu3.auxdata.igsl.dm.IgslSource cs,
			IFilter<GaiaRoot> f) throws GaiaException {
		IgslSource is = null;
		
		boolean toBeIncluded = f == null || f.Filter(cs);
		if (toBeIncluded) {
			if(cu9Fact==null) {
				cu9Fact = new NewObjectFactory<IgslSource>(IgslSource.class);
			}
			is = cu9Fact.convert(cs);
			
			//transformation

			
			is.setRa(GMath.radToDeg(setToNullIfNaN(cs.getAlpha())));
			is.setDec(GMath.radToDeg(setToNullIfNaN(cs.getDelta())));
			
			is.setRaEpoch(setToNullIfNaN(cs.getAlphaEpoch()));
			is.setDecEpoch(setToNullIfNaN(cs.getDeltaEpoch()));
			
			is.setErrorRa(setToNullIfNaN((float)GMath.radToMas(cs.getAlphaError())));
			is.setErrorDec(setToNullIfNaN((float)GMath.radToMas(cs.getDeltaError())));
			
			is.setPmRa(setToNullIfNaN(cs.getMuAlpha()));
			is.setPmDec(setToNullIfNaN(cs.getMuDelta()));
			
			is.setErrorPmra(setToNullIfNaN(cs.getMuAlphaError()));
			is.setErrorPmdec(setToNullIfNaN(cs.getMuDeltaError()));
			
		}
		return is;
	}

	private Double setToNullIfNaN(double val) {
		if (Double.isNaN(val))
			return null;
		return new Double(val);
	}	
	private Float setToNullIfNaN(float val) {
		if (Float.isNaN(val))
			return null;
		return new Float(val);
	}

	@Override
	public IgslSource transform(gaia.cu1.mdb.cu3.auxdata.igsl.dm.IgslSource cs,
			IFilter<GaiaRoot> f, IZeroPoints zPoints) throws GaiaException {
		return transform(cs, f);
	}
	

}
