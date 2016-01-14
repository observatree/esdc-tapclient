package esac.archive.gaia.dl.ingestion.objectconverter;

import esac.archive.gaia.dl.ingestion.filters.IFilter;
import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.IZeroPoints;
import gaia.cu1.tools.dm.GaiaRoot;
import gaia.cu1.tools.exception.GaiaException;


public interface IObjectConverter<I, O> {

	public O transform(I cs, IFilter<GaiaRoot> f) throws GaiaException ;
	public O transform(I cs, IFilter<GaiaRoot> f, IZeroPoints zPoints) throws GaiaException ;
}
