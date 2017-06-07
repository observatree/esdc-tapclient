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
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import esac.archive.gaia.dl.ingestion.filters.IFilter;
//import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.ExtPhotZeroPointsTable;
//import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.IZeroPoints;
//import gaia.cu1.mdb.cu1.integrated.dm.CompleteSource;
//import gaia.cu1.tools.dal.ObjectFactory;
//import gaia.cu1.tools.dm.GaiaRoot;
//import gaia.cu1.tools.exception.GaiaException;
//import gaia.cu1.tools.numeric.GMath;
//import gaia.cu1.tools.time.GaiaTime;
//import gaia.cu9.archivearchitecture.core.dm.CatalogueSource;
//import gaia.cu9.archivearchitecture.core.dmimpl.CatalogueSourceImpl;
//
//
//public class CompleteSource_mdbdm_18_0_2_To_CatalogueSource_cu9dm_18_1_0 extends ExtPhotZeroPointsTable implements IObjectConverter<CompleteSource,CatalogueSource>{
//	ObjectFactory<CatalogueSource> cu9Fact;
//	private static final Logger logger = Logger.getLogger(CompleteSource_mdbdm_18_0_2_To_CatalogueSource_cu9dm_18_1_0.class.getName());
//
//	@Override
//	public CatalogueSource transform(CompleteSource cs, IFilter<GaiaRoot> f)
//			throws GaiaException {
//		return null;
//	}
//
//	@Override
//	public CatalogueSource transform(CompleteSource cs, IFilter<GaiaRoot> f, IZeroPoints zPoints) throws GaiaException {
//		CatalogueSource catalogueSource = null;
//		try{
//			boolean toBeIncluded = f == null || f.Filter(cs);
//			if (toBeIncluded) {
//				catalogueSource = new CatalogueSourceImpl();
////				if(cu9Fact==null) {
////					cu9Fact = new ObjectFactory<CatalogueSource>(CatalogueSource.class);
////				}
////				catalogueSource = cu9Fact.convert(cs);
//
//				//Transform
//				//######################## CU3 ####################################
//	
//				catalogueSource.setSourceId(cs.getSourceId());
//				catalogueSource.setSolutionId(cs.getSolutionId());
//				//catalogueSource.setRandomIndex(null);
//				if (cs.getRefEpoch() != null) {
//					catalogueSource.setRefEpoch(((GaiaTime)cs.getRefEpoch()).getJulianYearNumber());
//				}
//				catalogueSource.setRa(GMath.radToDeg(cs.getAlpha()));
//				catalogueSource.setRaError(cs.getAlphaStarError());
//				catalogueSource.setDec(GMath.radToDeg(cs.getDelta()));
//				catalogueSource.setDecError(cs.getDeltaError());
//				catalogueSource.setParallax(cs.getVarpi());
//				catalogueSource.setParallaxError(cs.getVarpiError());
//				catalogueSource.setPmra(cs.getMuAlphaStar());
//				catalogueSource.setPmraError(cs.getMuAlphaStarError());
//				catalogueSource.setPmdec(cs.getMuDelta());
//				catalogueSource.setPmdecError(cs.getMuDeltaError());
//				catalogueSource.setPmradial(cs.getMuR());
//				catalogueSource.setPmradialError(cs.getMuRerror());
//				
//				///TODO: begin - wait for conversions (JH)
//				catalogueSource.setRaDecCorr(null);
//				catalogueSource.setRaParallaxCorr(null);
//				catalogueSource.setRaPmraCorr(null);
//				catalogueSource.setRaPmdecCorr(null);
//				catalogueSource.setRaPmradialCorr(null);
//				
//				catalogueSource.setDecParallaxCorr(null);
//				catalogueSource.setDecPmraCorr(null);
//				catalogueSource.setDecPmdecCorr(null);
//				catalogueSource.setDecPmradialCorr(null);
//				
//				catalogueSource.setParallaxPmraCorr(null);
//				catalogueSource.setParallaxPmdecCorr(null);
//				catalogueSource.setParallaxPmradialCorr(null);
//				
//				catalogueSource.setPmraPmdecCorr(null);
//				catalogueSource.setPmraPmradialCorr(null);
//				
//				catalogueSource.setPmdecPmradialCorr(null);
//				///TODO: end - wait for conversions (JH)
//	
//				if (cs.getNObs() != null) {
//					catalogueSource.setAstrometricNObsAl(cs.getNObs()[0]);
//					catalogueSource.setAstrometricNObsAc(cs.getNObs()[1]);
//				}
//				
//				if (cs.getNOutliers() != null) {
//					catalogueSource.setAstrometricNOutliersAl(cs.getNOutliers()[0]);
//					catalogueSource.setAstrometricNOutliersAc(cs.getNOutliers()[1]);
//				}
//				catalogueSource.setAstrometricGoF(cs.getF2());
//				
//				if (cs.getChi2() != null) {
//					catalogueSource.setAstrometricChi2Al(cs.getChi2()[0]);
//					catalogueSource.setAstrometricChi2Ac(cs.getChi2()[1]);
//				}
//				catalogueSource.setAstrometricDeltaQ(cs.getDeltaQ());
//				
//				catalogueSource.setAstrometricExcessNoise(GMath.radToDeg(cs.getExcessNoise()));
//				catalogueSource.setAstrometricExcessNoiseSig(cs.getExcessNoiseSig());
//				catalogueSource.setAstrometricParamsSolved(cs.getParamsSolved());
//				catalogueSource.setAstrometricRankDefect(cs.getRankDefect());
//				catalogueSource.setAstrometricPrimaryFlag(cs.getPrimaryFlag());
//				catalogueSource.setAstrometricRelegationFactor(cs.getRelegationFactor());
//				
//				if (cs.getAstrometricWeight() != null) {
//					catalogueSource.setAstrometricWeightAl(cs.getAstrometricWeight()[0]);
//					catalogueSource.setAstrometricWeightAc(cs.getAstrometricWeight()[1]);
//				}
//				
//				if (cs.getPriorUsed() != null) {
//					catalogueSource.setAstrometricPriorsUsed(cs.getPriorUsed().ordinal());
//				}
//				
//				catalogueSource.setMatchedObservations(cs.getMatchedObservations());
//				
//				if (cs.getScanDirectionStrength() != null) {
//					catalogueSource.setScanDirectionStrengthK1(cs.getScanDirectionStrength()[0]);
//					catalogueSource.setScanDirectionStrengthK2(cs.getScanDirectionStrength()[1]);
//					catalogueSource.setScanDirectionStrengthK3(cs.getScanDirectionStrength()[2]);
//					catalogueSource.setScanDirectionStrengthK4(cs.getScanDirectionStrength()[3]);					
//				}
//				
//				if (cs.getScanDirectionMean() != null) {
//					catalogueSource.setScanDirectionMeanK1(cs.getScanDirectionMean()[0]);
//					catalogueSource.setScanDirectionMeanK2(cs.getScanDirectionMean()[1]);
//					catalogueSource.setScanDirectionMeanK3(cs.getScanDirectionMean()[2]);
//					catalogueSource.setScanDirectionMeanK4(cs.getScanDirectionMean()[3]);
//				}
//				
//				catalogueSource.setRadialVelocity(cs.getRadialVelocity());
//				catalogueSource.setRadialVelocityError(cs.getRadialVelocityError());
//				catalogueSource.setRadialVelocityConstancyProbability(cs.getRvConstancyProbability());
//				
//				//######################## CU5 ####################################
//	
//				if (zPoints != null && !zPoints.isRead()){
//					try {
//						zPoints.setZeroPoints();
//					} catch (Exception e) {
//						logger.log(Level.SEVERE, "ZeroPoints table not properly defined in configuration file");
//						zPoints = null;
//					}
//				} 
//
//				if (cs.getGMean() != null) {
//					catalogueSource.setPhotGNObs(cs.getGMean().getNObs());
//					catalogueSource.setPhotGMeanFlux(cs.getGMean().getFluxMean());
//					catalogueSource.setPhotGMeanFluxError(cs.getGMean().getFluxErr());
//					if (zPoints != null) {
//						catalogueSource.setPhotGMeanMag(-2.5* Math.log10(catalogueSource.getPhotGMeanFlux()) + zPoints.getZeroPoint(0));
//					}
//				}
//				
//				if (cs.getBpMean() != null) {
//					catalogueSource.setPhotBpNObs(cs.getBpMean().getNObs());
//					catalogueSource.setPhotBpMeanFlux(cs.getBpMean().getFluxMean());
//					catalogueSource.setPhotBpMeanFluxError(cs.getBpMean().getFluxErr());	
//					if (zPoints != null) {
//						catalogueSource.setPhotBpMeanMag(-2.5* Math.log10(catalogueSource.getPhotBpMeanFlux()) + zPoints.getZeroPoint(2));
//					}
//				}
//				
//				if (cs.getRpMean() != null) {
//					catalogueSource.setPhotRpNObs(cs.getRpMean().getNObs());
//					catalogueSource.setPhotRpMeanFlux(cs.getRpMean().getFluxMean());
//					catalogueSource.setPhotRpMeanFluxError(cs.getRpMean().getFluxErr());
//					if (zPoints != null) {
//						catalogueSource.setPhotRpMeanMag(-2.5* Math.log10(catalogueSource.getPhotRpMeanFlux()) + zPoints.getZeroPoint(4));
//					}
//				}
//								
//				//######################## CU7 ####################################
//				
//				if (cs.getPhotometricVariabilityFlag() != null) {
////					byte pvf = -1;
////					VariabilityFlag vf = cs.getPhotometricVariabilityFlag();
////					switch (vf) {
////						case VARIABLE: pvf = 1;
////						case CONSTANT: pvf = 0;
////						default: 	   pvf = -1;
////						break;
////					}
//					catalogueSource.setPhotVariableFlag(cs.getPhotometricVariabilityFlag().toString());
//				}
//				
////				if (cs.getPhotometricPeriodicity() != null) {
////					byte pvnff = -1;
////					double[] pvff = new double[]{};
////					Periodicity p = cs.getPhotometricPeriodicity();
////					if (p == null) {
////						pvnff = 0;
////					}
////					else {
////						pvnff = (byte) p.getNumFundamentalFrequencies();
////						pvff = p.getFundamentalFrequencies();
////					}
////					catalogueSource.setPhotVariableNumFundamFreq(pvnff);
////					catalogueSource.setPhotVariableFundamFreqs(pvff);
////
////				}
////				
////				if (cs.getVariabilityClassification() != null &&
////						cs.getVariabilityClassification().getBestClassificationEstimate() != null &&
////						cs.getVariabilityClassification().getBestClassificationEstimate().entrySet() != null &&
////						cs.getVariabilityClassification().getBestClassificationEstimate().entrySet().iterator() != null &&
////						cs.getVariabilityClassification().getBestClassificationEstimate().entrySet().iterator().next() != null &&
////						cs.getVariabilityClassification().getBestClassificationEstimate().entrySet().iterator().next().getKey() != null
////						) {
////					catalogueSource.setPhotVariableClassification(cs.getVariabilityClassification().getBestClassificationEstimate().entrySet().iterator().next().getKey());
////				}
//			}
//		} catch(Exception ex) {
//			catalogueSource = null;
//			logger.log(Level.SEVERE, "Error while mapping CompleteSource: " + ex.getMessage());
//
//		}
//		return catalogueSource;
//
//
//	}
//
//
////	public static float[] convertDoublesToFloats(double[] input)
////	{
////	    if (input == null)
////	    {
////	        return null; // Or throw an exception - your choice
////	    }
////	    float[] output = new float[input.length];
////	    for (int i = 0; i < input.length; i++)
////	    {
////	        output[i] = (float)input[i];
////	    }
////	    return output;
////	}
////
////
////	private double fluxToGMagErr(double fluxMean, double fluxErr) {
////		return (2.5 / Math.log(10)) * fluxErr / fluxMean;
////	}
////
////	private double fluxToGMag(double zeroPoint, double fluxMean) {
////		return zeroPoint - 2.5 * Math.log10(fluxMean);
////	}
////	
////	/**
////	 * Radians to degrees
////	 * @param rad
////	 * @return
////	 */
////	private double radToDeg(double rad){
////		return Math.toDegrees(rad);
////	}
////	
////	/**
////	 * Radian to miliarcseconds
////	 * @param rad
////	 * @return
////	 */
////	private double radToMas(double rad){
////		return 3600*1000*Math.toDegrees(rad);
////	}
//
//}
