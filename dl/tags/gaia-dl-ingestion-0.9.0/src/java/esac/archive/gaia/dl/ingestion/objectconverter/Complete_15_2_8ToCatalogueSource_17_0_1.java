/*******************************************************************************
 * Copyright (C) 2017 rgutierrez
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
//import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.IZeroPoints;
//import gaia.cu1.mdb.cu1.integrated.dm.CompleteSource;
//import gaia.cu1.tools.dal.ObjectFactory;
//import gaia.cu1.tools.dm.GaiaRoot;
//import gaia.cu1.tools.exception.GaiaException;
//import gaia.cu9.archivearchitecture.core.dm.CatalogueSource;
//
//public class Complete_15_2_8ToCatalogueSource_17_0_1 implements IObjectConverter<CompleteSource,CatalogueSource> {
//
//	ObjectFactory<CatalogueSource> cu9Fact;
//
//	public CatalogueSource transform(CompleteSource cs, IFilter f) throws GaiaException {
//		CatalogueSource catalogueSource = null;
//		
//		boolean toBeIncluded = f == null || f.Filter(cs);
//		if (toBeIncluded) {
//			if(cu9Fact==null) {
//				cu9Fact = new ObjectFactory<CatalogueSource>(CatalogueSource.class);
//			}
//			catalogueSource = cu9Fact.convert(cs);
//	
//			//Transform
////			catalogueSource.setAlpha(GMath.radToDeg(catalogueSource.getAlpha()));
////			catalogueSource.setDelta(GMath.radToDeg(catalogueSource.getDelta()));
////			catalogueSource.setAlphaError(radToMas(cs.getAlphaError() / Math.cos(cs.getDelta())));
////			catalogueSource.setDeltaError(radToMas(cs.getDeltaError()));
////			catalogueSource.setAstrometricDecomposedN(convertDoublesToFloats(cs.getLinDecompNormals()));
//
//		}
//		return catalogueSource;
//	}
//
//	@Override
//	public CatalogueSource transform(CompleteSource cs, IFilter<GaiaRoot> f,
//			IZeroPoints zPoints) throws GaiaException {
//		return null;
//	}
//	
//	public static float[] convertDoublesToFloats(double[] input)
//	{
//	    if (input == null)
//	    {
//	        return null; // Or throw an exception - your choice
//	    }
//	    float[] output = new float[input.length];
//	    for (int i = 0; i < input.length; i++)
//	    {
//	        output[i] = (float)input[i];
//	    }
//	    return output;
//	}
//
//
//	private double fluxToGMagErr(double fluxMean, double fluxErr) {
//		return (2.5 / Math.log(10)) * fluxErr / fluxMean;
//	}
//
//	private double fluxToGMag(double zeroPoint, double fluxMean) {
//		return zeroPoint - 2.5 * Math.log10(fluxMean);
//	}
//	
//	/**
//	 * Radians to degrees
//	 * @param rad
//	 * @return
//	 */
//	private double radToDeg(double rad){
//		return Math.toDegrees(rad);
//	}
//	
//	/**
//	 * Radian to miliarcseconds
//	 * @param rad
//	 * @return
//	 */
//	private double radToMas(double rad){
//		return 3600*1000*Math.toDegrees(rad);
//	}
//
//}
