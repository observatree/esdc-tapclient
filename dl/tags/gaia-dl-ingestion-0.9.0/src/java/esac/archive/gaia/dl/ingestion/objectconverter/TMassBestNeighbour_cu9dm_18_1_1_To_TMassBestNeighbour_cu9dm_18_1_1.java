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
