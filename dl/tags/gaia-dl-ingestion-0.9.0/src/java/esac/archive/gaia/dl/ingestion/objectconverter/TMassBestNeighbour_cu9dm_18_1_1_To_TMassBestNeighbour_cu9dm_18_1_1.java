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
package esac.archive.gaia.dl.ingestion.objectconverter;

import esac.archive.gaia.dl.ingestion.filters.IFilter;
import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.IZeroPoints;
import gaia.cu1.tools.dm.GaiaRoot;
import gaia.cu1.tools.exception.GaiaException;
import gaia.cu9.scienceenablingapplications.crossmatchcatalogues.tmass.dm.TMassBestNeighbour;

import java.util.logging.Logger;

public class TMassBestNeighbour_cu9dm_18_1_1_To_TMassBestNeighbour_cu9dm_18_1_1 implements IObjectConverter<TMassBestNeighbour,TMassBestNeighbour>{
	NewObjectFactory<TMassBestNeighbour> cu9Fact;
	private static final Logger logger = Logger.getLogger(TMassBestNeighbour_cu9dm_18_1_1_To_TMassBestNeighbour_cu9dm_18_1_1.class.getName());

	@Override
	public TMassBestNeighbour transform(
			TMassBestNeighbour cs,
			IFilter<GaiaRoot> f) throws GaiaException {
		TMassBestNeighbour bn = null;
		
		boolean toBeIncluded = f == null || f.Filter(cs);
		if (toBeIncluded) {
			if(cu9Fact==null) {
				cu9Fact = new NewObjectFactory<TMassBestNeighbour>(TMassBestNeighbour.class);
			}
			
			bn = cu9Fact.convert(cs);

		}
		return bn;
	}

	@Override
	public TMassBestNeighbour transform(
			TMassBestNeighbour cs,
			IFilter<GaiaRoot> f, IZeroPoints zPoints) throws GaiaException {
		return transform(cs, f);
	}

}
