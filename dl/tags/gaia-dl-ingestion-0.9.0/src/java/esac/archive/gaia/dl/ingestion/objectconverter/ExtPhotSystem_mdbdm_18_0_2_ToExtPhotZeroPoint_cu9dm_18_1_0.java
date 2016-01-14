//package esac.archive.gaia.dl.ingestion.objectconverter;
//
//import esac.archive.gaia.dl.ingestion.filters.IFilter;
//import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.IZeroPoints;
//import gaia.cu1.mdb.cu5.photpipe.cal.dm.ExtPhotSystem;
//import gaia.cu1.tools.dm.GaiaRoot;
//import gaia.cu1.tools.exception.GaiaException;
//import gaia.cu9.archivearchitecture.core.dm.ExtPhotZeroPoint;
//import gaia.cu9.archivearchitecture.core.dmimpl.ExtPhotZeroPointImpl;
//
//public class ExtPhotSystem_mdbdm_18_0_2_ToExtPhotZeroPoint_cu9dm_18_1_0 implements
//		IObjectConverter<ExtPhotSystem, ExtPhotZeroPoint> {
//
//	@Override
//	public ExtPhotZeroPoint transform(ExtPhotSystem cs, IFilter f)
//			throws GaiaException {
//		ExtPhotZeroPoint extPhotZeroPoint = new ExtPhotZeroPointImpl();
//		//Transform
//		extPhotZeroPoint.setGMagZeroPoint(cs.getGPassband().getMagZeroPoint());
//		extPhotZeroPoint.setGMagZeroPointError(cs.getGPassband().getMagZeroPointError());
//		extPhotZeroPoint.setBpMagZeroPoint(cs.getBpPassband().getMagZeroPoint());
//		extPhotZeroPoint.setBpMagZeroPointError(cs.getBpPassband().getMagZeroPointError());
//		extPhotZeroPoint.setRpMagZeroPoint(cs.getRpPassband().getMagZeroPoint());
//		extPhotZeroPoint.setRpMagZeroPointError(cs.getRpPassband().getMagZeroPointError());
//		return extPhotZeroPoint;
//	}
//
//	@Override
//	public ExtPhotZeroPoint transform(ExtPhotSystem cs,
//			IFilter<GaiaRoot> f, IZeroPoints zPoints) throws GaiaException {
//		return transform(cs, f);
//	}
//
//}
