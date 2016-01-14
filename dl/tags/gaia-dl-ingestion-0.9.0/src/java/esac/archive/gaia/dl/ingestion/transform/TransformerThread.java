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
