/*******************************************************************************
 * Copyright (C) 2017 European Space Agency
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
//package esac.archive.gaia.dl.ingestion.objectconverter;
//
//import esac.archive.gaia.dl.ingestion.filters.IFilter;
//import gaia.cu1.mdb.cu1.integrated.dm.CompleteSource;
//import gaia.cu1.mdb.cu7.timeseries.dm.TimeSeries;
//import gaia.cu1.mdb.cu7.timeseriesresult.dm.StatisticalParameters;
//import gaia.cu1.tools.dal.ObjectFactory;
//import gaia.cu1.tools.exception.GaiaException;
//import gaia.cu9.archivearchitecture.core.dm.PhotVariableTimeSeriesGfov;
//
//public class TimeSeries_mdbdm_18_0_2_To_PhotVariableTimeSeriesGfov_cu9dm_18_1_0 extends 
//	StatisticalParameters_mdbdm_18_0_2_To_StatisticalParameters_cu9dm_18_1_0 {
//
//	ObjectFactory<PhotVariableTimeSeriesGfov> cu9Fact;
//
//	public PhotVariableTimeSeriesGfov transformGfov(TimeSeries cs, IFilter f)
//			throws GaiaException {
//
//		PhotVariableTimeSeriesGfov pvtsg = (PhotVariableTimeSeriesGfov)transform((StatisticalParameters) cs, f);
//		
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
//	
//}
