//package esac.archive.gaia.dl.ingestion.objectconverter;
//
//import esac.archive.gaia.dl.ingestion.filters.IFilter;
//import gaia.cu1.mdb.cu1.integrated.dm.CompleteSource;
//import gaia.cu1.tools.dal.ObjectFactory;
//import gaia.cu1.tools.exception.GaiaException;
//import gaia.cu9.archivearchitecture.core.abstracttables.dm.CepheidAndRRLyraeElementary;
//import gaia.cu9.archivearchitecture.core.dm.Cepheid;
//import gaia.cu9.archivearchitecture.core.dm.RRLyrae;
//import gaia.cu9.archivearchitecture.core.dmimpl.RRLyraeImpl;
//
//public class RRLyrae_mdbdm_18_0_2_To_RRLyrae_cu9dm_18_1_0 extends 
//	CepheidAndRRLyraeElementary_mdbdm_18_0_2_To_CepheidAndRRLyraeElementary_cu9dm_18_1_0 {
//
//	public RRLyrae transform(gaia.cu1.mdb.cu7.sos.dm.RRLyrae cs, IFilter f) throws GaiaException {
//		RRLyrae	rrl = (RRLyrae) transform((gaia.cu1.mdb.cu7.sos.dm.CepheidAndRRLyraeElementary) cs, f);
//		
//		//Transform
////		rrl.setRrlClassification(cs.getRrlClassification());
////		rrl.setRrlBestClassification(cs.getRrlClassification());
////		rrl.setRrlDoubleFlag(cs.getRrlDoubleFlag());
//		
//		return rrl;
//	}
//
//}
