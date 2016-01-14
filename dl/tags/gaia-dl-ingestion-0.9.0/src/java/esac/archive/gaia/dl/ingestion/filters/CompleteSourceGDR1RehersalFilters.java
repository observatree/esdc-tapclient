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
