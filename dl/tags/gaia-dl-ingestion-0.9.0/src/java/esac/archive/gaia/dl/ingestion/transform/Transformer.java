package esac.archive.gaia.dl.ingestion.transform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import esac.archive.gaia.dl.ingestion.filters.IFilter;
import esac.archive.gaia.dl.ingestion.objectconverter.IObjectConverter;
import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.IZeroPoints;
import gaia.cu1.tools.dm.GaiaRoot;

public class Transformer {
	
	private static final Logger logger = Logger.getLogger(Transformer.class.getName());
	private static ExecutorService taskExecutor = null;

	//for gbin
	public static void transform(
			IOFileFilter filter,
			IObjectConverter<GaiaRoot, ?> conversorClass, 
			TransformerSource transformer,
			IFilter fil,
			String sourcePath,
			String outDir,
			Integer threads,
			IZeroPoints zPoints){	

		try{

			//Create tasks for each file
			List<Callable<Integer>> tasks = new ArrayList<Callable<Integer>>();
			for (File file : /*new File(sourcePath).list(filter)*/ FileUtils.listFiles(new File(sourcePath), filter, TrueFileFilter.INSTANCE)) {
				logger.log(Level.INFO, "Found "+ file.getAbsolutePath());
				tasks.add(new TransformerThread(file, transformer, conversorClass, fil, outDir, zPoints));
			}
			logger.log(Level.INFO, "Identified "+tasks.size()+" files for a pool of "+threads+" threads");
			//Run tasks in a fixed thread pool
			taskExecutor = Executors.newFixedThreadPool(threads);
			// invokeAll() returns when all tasks are complete
			List<Future<Integer>> futures = taskExecutor.invokeAll(tasks);

			int flag = 0;
			for (Future<Integer> f : futures) {
				Integer res = f.get();
				logger.log(Level.INFO, "Thread finished and synchronized transforming "+res+" sources");
				if (!f.isDone()){
					flag = 1;
					logger.log(Level.INFO, "Thread finished with no sources transforming");
				}
			}
			
			if (flag == 0){
				logger.log(Level.INFO, "TRANSFORMATION SUCCEEDED");
			} else {
				logger.log(Level.SEVERE, "TRANSFORMATION FAILED");
			}

		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Exception in the execution pool", e);
		} catch (ExecutionException e) {
			logger.log(Level.SEVERE, "Exception in the execution pool", e);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in the execution pool", e);
		}finally{
			if(taskExecutor!=null){
				taskExecutor.shutdown();
			}
		}
	}

}
