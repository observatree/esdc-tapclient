//package esac.archive.gaia.dl.ingestion.objectconverter;
//
//import esac.archive.gaia.dl.ingestion.filters.IFilter;
//import gaia.cu1.tools.dal.ObjectFactory;
//import gaia.cu1.tools.dm.GaiaRoot;
//import gaia.cu1.tools.exception.GaiaException;
//import gaia.cu9.archivearchitecture.core.abstracttables.dm.CepheidAndRRLyraeElementary;
//import gaia.cu9.archivearchitecture.core.dm.Cepheid;
//import gaia.cu9.archivearchitecture.core.dmimpl.CepheidImpl;
//
//public class Cepheid_mdbdm_18_0_2_To_Cepheid_cu9dm_18_1_0 extends 
//	CepheidAndRRLyraeElementary_mdbdm_18_0_2_To_CepheidAndRRLyraeElementary_cu9dm_18_1_0{
//
//	public Cepheid transform(gaia.cu1.mdb.cu7.sos.dm.Cepheid cs, IFilter<GaiaRoot> f) throws GaiaException {
//		
//		Cepheid cepheid = (Cepheid) transform((gaia.cu1.mdb.cu7.sos.dm.CepheidAndRRLyraeElementary)cs, f);
//
//		//Transform
////		cepheid.setCepheidTypeClassification(cs.getCepheidTypeClassification());
////		cepheid.setCepheidType2SubClassification(cs.getCepheidType2SubClassification());
////		cepheid.setCepheidModeClassification(cs.getCepheidModeClassification());
////		cepheid.setCepheidTypeBestClassification(cs.getCepheidTypeClassification());
////		cepheid.setCepheidType2BestSubClassification(cs.getCepheidType2SubClassification());
////		cepheid.setCepheidModeBestClassification(cs.getCepheidModeClassification());
////		
//		return cepheid;
//
//	}
//
//}
