//package esac.archive.gaia.dl.ingestion.objectconverter;
//
//import esac.archive.gaia.dl.ingestion.filters.IFilter;
//import gaia.cu1.mdb.cu1.integrated.dm.CompleteSource;
//import gaia.cu1.mdb.cu7.timeseries.dm.TimeSeries;
//import gaia.cu1.mdb.cu7.timeseriesresult.dm.StatisticalParameters;
//import gaia.cu1.tools.dal.ObjectFactory;
//import gaia.cu1.tools.exception.GaiaException;
//import gaia.cu9.archivearchitecture.core.dm.PhotVariableTimeSeriesGccd;
//
//public class TimeSeries_mdbdm_18_0_2_To_PhotVariableTimeSeriesGccd_cu9dm_18_1_0 extends 
//	StatisticalParameters_mdbdm_18_0_2_To_StatisticalParameters_cu9dm_18_1_0 {
//
//	public PhotVariableTimeSeriesGccd transformGccd(TimeSeries cs, IFilter f)
//		throws GaiaException {
//
//		PhotVariableTimeSeriesGccd pvtsg = (PhotVariableTimeSeriesGccd)transform((StatisticalParameters) cs, f);
//		//Transform
////		pvtsg.setNumObservations();
////		pvtsg.setObservationTimes();
////		pvtsg.setTransitIds();
////		pvtsg.setFlags();
////		pvtsg.setGFluxes();
////		pvtsg.setGFluxErrors();
////		pvtsg.setGMagnitudes();
//	
//		return pvtsg;
//	}
//}
