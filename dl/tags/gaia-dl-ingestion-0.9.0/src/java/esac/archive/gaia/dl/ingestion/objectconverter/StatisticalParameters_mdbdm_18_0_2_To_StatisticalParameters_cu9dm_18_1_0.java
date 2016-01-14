//package esac.archive.gaia.dl.ingestion.objectconverter;
//
//import esac.archive.gaia.dl.ingestion.filters.IFilter;
//import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.IZeroPoints;
//import gaia.cu1.mdb.cu7.timeseries.dm.TimeSeries;
//import gaia.cu1.tools.dal.ObjectFactory;
//import gaia.cu1.tools.dm.GaiaRoot;
//import gaia.cu1.tools.exception.GaiaException;
//import gaia.cu9.archivearchitecture.core.abstracttables.dm.StatisticalParameters;
//import gaia.cu9.archivearchitecture.core.dm.PhotVariableTimeSeriesGccd;
//import gaia.cu9.archivearchitecture.core.dm.PhotVariableTimeSeriesGfov;
//
//public class StatisticalParameters_mdbdm_18_0_2_To_StatisticalParameters_cu9dm_18_1_0  implements
//IObjectConverter<gaia.cu1.mdb.cu7.timeseriesresult.dm.StatisticalParameters, StatisticalParameters> {
//
//	ObjectFactory<StatisticalParameters> cu9Fact;
//
//	@Override
//	public StatisticalParameters transform(gaia.cu1.mdb.cu7.timeseriesresult.dm.StatisticalParameters cs, IFilter<GaiaRoot> f) throws GaiaException {
//		StatisticalParameters sp = null;
//		if(cu9Fact==null) {
//			cu9Fact = new ObjectFactory<StatisticalParameters>(StatisticalParameters.class);
//		}
//		sp = cu9Fact.convert(cs);
//
//		//Transform
//		sp.setNumObservationsProcessed(cs.getNumPointsObsTime());
//		return sp;
//	
//	}
//
//	@Override
//	public StatisticalParameters transform(
//			gaia.cu1.mdb.cu7.timeseriesresult.dm.StatisticalParameters cs,
//			IFilter<GaiaRoot> f, IZeroPoints zPoints) throws GaiaException {
//		
//		return transform(cs, f);
//	}
//
//}