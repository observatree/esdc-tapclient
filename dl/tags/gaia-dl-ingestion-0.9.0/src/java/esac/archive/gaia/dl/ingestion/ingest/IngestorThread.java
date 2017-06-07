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
package esac.archive.gaia.dl.ingestion.ingest;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.starlink.table.TableBuilder;
import esac.archive.gaia.dl.ingestion.filters.IFilter;
import esac.archive.gaia.dl.ingestion.ingest.extractors.ExtractSource;
import esac.archive.gaia.dl.ingestion.ingest.extractors.GbinExtractSource;
import esac.archive.gaia.dl.ingestion.objectconverter.IObjectConverter;
import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.IZeroPoints;
import gaia.cu1.tools.dm.GaiaRoot;


public class IngestorThread implements Callable<Integer> {
	
    private static final Logger logger = Logger.getLogger(IngestorThread.class.getName());
    
    File file = null;
    String schema = null;
    String tableName = null;
    ExtractSource extractor = null;
    Class<?> extractedClass = null;
    IObjectConverter<GaiaRoot, ?> conversor = null;
    IFilter<GaiaRoot> filter;
    TableBuilder stiltsBuilder = null;
    boolean createTableStilts = false;
    IZeroPoints zPoints = null;
    
    //original
    public IngestorThread (File file, String schema, String tableName, ExtractSource extractor, Class<?> extractedClass, IObjectConverter<GaiaRoot, ?> conversor, TableBuilder stiltsBuilder) {
        
        // Extraction strategy definition
        this.file = file;
        this.schema = schema;
        this.tableName = tableName;
        this.extractor = extractor;
        this.extractedClass = extractedClass;
        this.conversor = conversor;
        this.stiltsBuilder = stiltsBuilder;
    }
    //for csv
    public IngestorThread (File file, String schema, String tableName, ExtractSource extractor) {
        
        // Extraction strategy definition
        this.file = file;
        this.schema = schema;
        this.tableName = tableName;
        this.extractor = extractor;
    }
    
    //for gbin
    public IngestorThread (File file, String schema, String tableName, ExtractSource extractor, Class<?> extractedClass, IObjectConverter<GaiaRoot, ?> conversor, IFilter<GaiaRoot> filter, IZeroPoints zPoints) {
        
        // Extraction strategy definition
        this.file = file;
        this.schema = schema;
        this.tableName = tableName;
        this.extractor = extractor;
        this.extractedClass = extractedClass;
        this.conversor = conversor;
        this.filter = filter;
        this.zPoints = zPoints;
    }
    
    //for stilts
    public IngestorThread (boolean createTable, File file, String schema, String tableName, ExtractSource extractor, TableBuilder stiltsBuilder) {
        
        // Extraction strategy definition
        this.file = file;
        this.schema = schema;
        this.tableName = tableName;
        this.extractor = extractor;
        this.stiltsBuilder = stiltsBuilder;
        this.createTableStilts = createTable;
    }
    
    @Override
    public Integer call() throws Exception {

        logger.log(Level.INFO, "Thread starting for file "+file.getAbsolutePath() );
        int processedSourcesNumber = 0;
            try {
            	if (extractor instanceof GbinExtractSource) {
            		processedSourcesNumber = extractor.process(file, schema, tableName, extractedClass, conversor, filter, zPoints);
            	}
            	else{
                    processedSourcesNumber = extractor.process(createTableStilts, file, schema, tableName, stiltsBuilder);
            	}

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Exception processing "+file.getAbsolutePath()+" file "+ e.getMessage(), e);
                e.printStackTrace();
            }
            finally{
                logger.log(Level.FINE, "Thread finished for "+file.getAbsolutePath());
            }
         
        return processedSourcesNumber;
    }
}
