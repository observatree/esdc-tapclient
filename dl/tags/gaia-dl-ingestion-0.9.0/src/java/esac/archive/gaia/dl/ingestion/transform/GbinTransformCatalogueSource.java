package esac.archive.gaia.dl.ingestion.transform;

import esac.archive.gaia.dl.ingestion.filters.IFilter;
import esac.archive.gaia.dl.ingestion.objectconverter.IObjectConverter;
import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.IZeroPoints;
import gaia.cu1.tools.dal.ClassMap;
import gaia.cu1.tools.dal.ClassMapImpl;
import gaia.cu1.tools.dal.gbin.DefaultGbinFactory;
import gaia.cu1.tools.dal.gbin.GbinFactory;
import gaia.cu1.tools.dal.gbin.GbinReader;
import gaia.cu1.tools.dal.gbin.GbinReaderV1;
import gaia.cu1.tools.dal.gbin.GbinReaderV2;
import gaia.cu1.tools.dal.gbin.GbinReaderV3;
import gaia.cu1.tools.dal.gbin.GbinReaderWriterFactory;
import gaia.cu1.tools.dm.GaiaRoot;
import gaia.cu1.tools.exception.GaiaException;
import gaia.cu1.tools.util.props.PropertyLoader;

import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GbinTransformCatalogueSource extends TransformerSource{
	private static final Logger logger = Logger.getLogger(GbinTransformCatalogueSource.class.getName());

	private static int numberFolder = 0;
	private int numberEntries = 0;
	private boolean gaiaPropertiesInitialized = false;

    //for gbin
	@Override
	public Integer process(
			File gbinFile,
			IObjectConverter<GaiaRoot, ?> conversor,
			String outDir, IFilter<GaiaRoot> filter, IZeroPoints zPoints) 
					throws IllegalArgumentException, SecurityException, 
	IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException, PropertyVetoException, GaiaException, ClassNotFoundException, IOException
	{
		
		if (!gaiaPropertiesInitialized) {
			initializeGaiaToolsProperties();
			gaiaPropertiesInitialized = true;
		}

		logger.log(Level.INFO, "Inside process for "+gbinFile.getAbsolutePath());

		ArrayList<GaiaRoot> gr = new ArrayList<GaiaRoot>();
		logger.log(Level.INFO, "Before creation of reader for "+gbinFile.getAbsolutePath());

		try {
			final GbinReader<GaiaRoot> reader = GbinFactory.getGbinReader(gbinFile);
			logger.log(Level.INFO, "Reader created for "+gbinFile.getAbsolutePath());
	
			while (reader.hasNext()) {
				GaiaRoot source = reader.next();
				
				if(conversor!=null){
					source = (GaiaRoot) conversor.transform(source, filter, zPoints);
					if (source == null) continue;
					numberEntries++;
					gr.add(source);
				}
			}
	
			synchronized (this) {
				File f = new File(outDir + File.separator + numberFolder);
				if (f.exists()) {
					int tam = f.listFiles().length;
					while (tam > 10000)
					{
						numberFolder++;
						f = new File(outDir + File.separator + numberFolder);
						if (f.exists()) {
							tam = f.listFiles().length;
						}
						else {
							new File(outDir + File.separator + numberFolder).mkdirs();
							tam = 0;
						}
					}
				}
				else {
					new File(outDir + File.separator + numberFolder).mkdirs();
				}
			}
			
			GbinFactory.getGbinWriter().writeToFile(new File(outDir + File.separator + numberFolder + File.separator + gbinFile.getName().replace("Complete", "Catalogue")), gr, false);
									
		} catch (GaiaException ex) {
			logger.log(Level.SEVERE, "GaiaException transforming "+gbinFile.getAbsolutePath() + " " + ex.getMessage());
			File f = new File(outDir + File.separator + "errors");
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.append("GaiaException transforming "+gbinFile.getAbsolutePath() + ": " + ex.getMessage() + "/n");
			bw.close();
			fw.close();


		} catch(Exception ex) {
			logger.log(Level.SEVERE, "General exception transforming "+gbinFile.getAbsolutePath() + ": " + ex.getMessage());
			File f = new File(outDir + File.separator + "errors");
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.append("General exception transforming "+gbinFile.getAbsolutePath() + ": " + ex.getMessage() + "/n");
			bw.close();
			fw.close();

		}
		gr.clear();
		
		return numberEntries;
	}
	
	private static void initializeGaiaToolsProperties(){
		
//		PropertyLoader.setProperty(GbinReaderWriterFactory.class.getName(), DefaultGbinFactory.class.getName());
		PropertyLoader.setProperty("gaia.cu1.tools.dal.gbin.GbinReaderV4.inflateInputBufferSize", 
				(new Integer (8 * 1024)).toString());
		PropertyLoader.setProperty("gaia.cu1.tools.dal.gbin.GbinReaderV4.inflateBlockSize",
				(new Integer (8 * 1024)).toString());
//		PropertyLoader.setProperty("gaia.cu1.tools.dal.gbin.GbinReaderV2.useInputStreamFlag", "TRUE");
//		PropertyLoader.setProperty("gaia.cu1.tools.dal.gbin.GbinReaderV4.useThreadContextClassLoader", "FALSE");
//		PropertyLoader.setProperty("gaia.cu1.tools.util.DefaultGaiaFactory.useClassCache", "FALSE");
//		PropertyLoader.setProperty("gaia.cu1.tools.dal.gbin.GbinWriterV4.resetThreshold", "52428800");
//		PropertyLoader.setProperty("gaia.cu1.tools.dal.gbin.GbinWriterV4.outputBufferSize", "8192");
//		PropertyLoader.setProperty("gaia.cu1.tools.dal.gbin.GbinWriterV4.writeTableAttributes", "FALSE");
//		PropertyLoader.setProperty("gaia.cu1.tools.dal.Store.loadDmProps", "TRUE");
		PropertyLoader.setProperty(ClassMap.class.getName(), ClassMapImpl.class.getName());
//		PropertyLoader.setProperty("gaia.cu1.tools.dal.jdbc.properties.PropertyConfiguration.replaceFlag", "FALSE");
//		PropertyLoader.setProperty("gaia.cu9.archivearchitecture.core.dm.CatalogueSource", "gaia.cu9.archivearchitecture.core.dmimpl.CatalogueSourceImpl");
		//PropertyLoader.setProperty(GbinReader.class.getName() + "." + GbinFactory.GbinVersions.V4.toString(),
		//		GbinReaderV4.class.getName());

		PropertyLoader.setProperty(GbinReader.class.getName() + "." + GbinFactory.GbinVersions.V3.toString(),
				GbinReaderV3.class.getName());

		PropertyLoader.setProperty(GbinReader.class.getName() + "." + GbinFactory.GbinVersions.V2.toString(),
				GbinReaderV2.class.getName());

		PropertyLoader.setProperty(GbinReader.class.getName() + "." + GbinFactory.GbinVersions.V1.toString(),
				GbinReaderV1.class.getName());
	
	}
}
