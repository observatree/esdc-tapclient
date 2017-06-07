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
package esac.archive.gaia.dl.ingestion.transform;

import esac.archive.gaia.dl.ingestion.filters.IFilter;
import esac.archive.gaia.dl.ingestion.objectconverter.IObjectConverter;
import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.IZeroPoints;
import gaia.cu1.tools.dm.GaiaRoot;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TransformerThread implements Callable<Integer> {

	   private static final Logger logger = Logger.getLogger(TransformerThread.class.getName());
	    
	    File file = null;
	    TransformerSource transformer = null;
	    IObjectConverter<GaiaRoot, ?> conversor = null;
	    String outDir = null;
        IFilter filter = null;
        IZeroPoints zPoints = null;


    //for gbin
    public TransformerThread (File file, TransformerSource transformer, IObjectConverter<GaiaRoot, ?> conversor, IFilter filter, String outDir, IZeroPoints
			zeroPoints) {
        
        // Extraction strategy definition
        this.file = file;
        this.transformer = transformer;
        this.conversor = conversor;
        this.outDir = outDir;
        this.filter = filter;
        this.zPoints = zeroPoints;
    }
    
    @Override
    public Integer call() throws Exception {

        logger.log(Level.INFO, "Thread starting for file "+file.getAbsolutePath() );
        int processedSourcesNumber = 0;
            try {
            	if (transformer instanceof GbinTransformCatalogueSource) {
            		logger.log(Level.INFO, "Process starts for "+file.getAbsolutePath());
            		processedSourcesNumber = transformer.process(file, conversor, outDir, filter, zPoints);
            		logger.log(Level.INFO, "Process finished for "+file.getAbsolutePath());
            	}

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Exception processing "+file.getAbsolutePath()+" file "+ e.getMessage(), e);
                e.printStackTrace();
            }
            finally{
                logger.log(Level.INFO, "Thread finished for "+file.getAbsolutePath());
            }
         
        return processedSourcesNumber;
    }

}
