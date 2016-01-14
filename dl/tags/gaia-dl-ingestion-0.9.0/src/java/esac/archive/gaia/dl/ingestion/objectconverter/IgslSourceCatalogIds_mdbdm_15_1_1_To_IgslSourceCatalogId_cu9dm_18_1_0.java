package esac.archive.gaia.dl.ingestion.objectconverter;

import java.util.logging.Logger;

import esac.archive.gaia.dl.ingestion.filters.IFilter;
import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.IZeroPoints;
import gaia.cu1.tools.dm.GaiaRoot;
import gaia.cu1.tools.exception.GaiaException;
import gaia.cu9.operations.auxiliarydata.igsl.dm.SourceCatalogIDs;


public class IgslSourceCatalogIds_mdbdm_15_1_1_To_IgslSourceCatalogId_cu9dm_18_1_0  implements IObjectConverter<gaia.cu1.mdb.cu3.auxdata.igsl.dm.SourceCatalogIDs,SourceCatalogIDs> {
	NewObjectFactory<SourceCatalogIDs> cu9Fact;
	private static final Logger logger = Logger.getLogger(IgslSourceCatalogIds_mdbdm_15_1_1_To_IgslSourceCatalogId_cu9dm_18_1_0.class.getName());

	@Override
	public SourceCatalogIDs transform(
			gaia.cu1.mdb.cu3.auxdata.igsl.dm.SourceCatalogIDs cs,
			IFilter<GaiaRoot> f) throws GaiaException {
		SourceCatalogIDs sc = null;
		
		boolean toBeIncluded = f == null || f.Filter(cs);
		if (toBeIncluded) {
			if(cu9Fact==null) {
				cu9Fact = new NewObjectFactory<SourceCatalogIDs>(SourceCatalogIDs.class);
			}
			
			sc = cu9Fact.convert(cs);

		}
		return sc;
	}

	@Override
	public SourceCatalogIDs transform(
			gaia.cu1.mdb.cu3.auxdata.igsl.dm.SourceCatalogIDs cs,
			IFilter<GaiaRoot> f, IZeroPoints zPoints) throws GaiaException {
		return transform(cs, f);
	}

}
