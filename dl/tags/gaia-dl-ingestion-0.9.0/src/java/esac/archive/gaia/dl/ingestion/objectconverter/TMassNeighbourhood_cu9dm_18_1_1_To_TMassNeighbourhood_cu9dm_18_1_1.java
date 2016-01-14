package esac.archive.gaia.dl.ingestion.objectconverter;

import esac.archive.gaia.dl.ingestion.filters.IFilter;
import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.IZeroPoints;
import gaia.cu1.tools.dm.GaiaRoot;
import gaia.cu1.tools.exception.GaiaException;
import gaia.cu9.scienceenablingapplications.crossmatchcatalogues.tmass.dm.TMassNeighbourhood;

import java.util.logging.Logger;

public class TMassNeighbourhood_cu9dm_18_1_1_To_TMassNeighbourhood_cu9dm_18_1_1 implements IObjectConverter<TMassNeighbourhood,TMassNeighbourhood>{
	NewObjectFactory<TMassNeighbourhood> cu9Fact;
	private static final Logger logger = Logger.getLogger(TMassNeighbourhood_cu9dm_18_1_1_To_TMassNeighbourhood_cu9dm_18_1_1.class.getName());

	@Override
	public TMassNeighbourhood transform(
			TMassNeighbourhood cs,
			IFilter<GaiaRoot> f) throws GaiaException {
		TMassNeighbourhood n = null;
		
		boolean toBeIncluded = f == null || f.Filter(cs);
		if (toBeIncluded) {
			if(cu9Fact==null) {
				cu9Fact = new NewObjectFactory<TMassNeighbourhood>(TMassNeighbourhood.class);
			}
			
			n = cu9Fact.convert(cs);

		}
		return n;
	}

	@Override
	public TMassNeighbourhood transform(
			TMassNeighbourhood cs,
			IFilter<GaiaRoot> f, IZeroPoints zPoints) throws GaiaException {
		return transform(cs, f);
	}

}
