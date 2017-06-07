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
package esac.archive.gaia.dl.ingestion.filters;

import gaia.cu1.mdb.cu1.integrated.dm.CompleteSource;

public class CompleteSourceGDR1RehersalFilters implements IFilter<CompleteSource> {

	@Override
	public boolean Filter(CompleteSource cs) {
		boolean toBeIncluded = 
				cs != null && 
				(Double.isNaN(cs.getVarpiError()) || cs.getVarpiError() <= 5) && 
				(Double.isNaN(cs.getAlphaStarError()) || cs.getAlphaStarError() <= 350) &&
				(Double.isNaN(cs.getDeltaError()) || cs.getDeltaError() <= 350);
		
		if (toBeIncluded) {
			toBeIncluded = filterBasicSource(cs.getSourceId());
		}
		else {
			toBeIncluded = false;
		}
		return toBeIncluded;
	}

	private boolean filterBasicSource(long sourceId) {
		boolean toBeIncluded = true;
		//TODO complete the BasicSource filter
		return toBeIncluded;
	}

}
