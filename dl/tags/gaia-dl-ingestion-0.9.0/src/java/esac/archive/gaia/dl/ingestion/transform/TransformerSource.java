package esac.archive.gaia.dl.ingestion.transform;

import esac.archive.gaia.dl.ingestion.filters.IFilter;
import esac.archive.gaia.dl.ingestion.objectconverter.IObjectConverter;
import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.IZeroPoints;
import gaia.cu1.tools.dm.GaiaRoot;
import gaia.cu1.tools.exception.GaiaException;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;


public class TransformerSource {

    //for gbin
	public Integer process(
			File gbinFile,
			IObjectConverter<GaiaRoot, ?> conversor,
			String outDir, IFilter<GaiaRoot> filter, IZeroPoints zPoints) 
					throws IllegalArgumentException, SecurityException, 
	IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException, PropertyVetoException, GaiaException, ClassNotFoundException, IOException
	{
		return 0;
	}

}
