package esac.archive.gaia.dl.ingestion.dump.gaiadump;

import gaia.cu1.tools.dal.table.GaiaTable;
import gaia.cu1.tools.dm.GaiaRoot;
import gaia.cu1.tools.dmimpl.GaiaRootPersistanceManager;
import gaia.cu1.tools.exception.GaiaConfigurationException;
import gaia.cu1.tools.exception.GaiaException;

public class GacsPopulator {


	public void populate(GacsTranslator translator, GaiaTable tab, GaiaRoot dmObject) throws GaiaException {
		for(String f:translator.getFieldMap().keySet()) {
			GaiaRoot gr;
			try {
				gr = (GaiaRoot) translator.getFieldMap().get(f).newInstance();
			} catch (Exception e) {
				throw new GaiaConfigurationException(e);
			} 

			GaiaRootPersistanceManager.setField(dmObject, f, gr);

			GaiaRootPersistanceManager.defaultPopulate(gr, tab); 
		}

		for(String field:translator.getArrayMap().keySet()) {
			Class<?> baseType = translator.getArrayMap().get(field);
			String value = tab.getString(translator.getGaiaGacsMap().get(field));

			if(!value.equals("[0.0]") && !value.equals("[0]")) {

				value = value.replaceAll("\\[", "");
				value = value.replaceAll("\\]", "");

				String[] arrVal = value.split(" ");

				if(baseType == double.class) {
					double[] arr = new double[arrVal.length];
					for(int i = 0; i < arrVal.length;i++) {
						arr[i] = Double.parseDouble(arrVal[i]);
					}

					GaiaRootPersistanceManager.setField(dmObject, field, arr);
				} else if(baseType == float.class) {
					float[] arr = new float[arrVal.length];
					for(int i = 0; i < arrVal.length;i++) {
						arr[i] = Float.parseFloat(arrVal[i]);
					}

					GaiaRootPersistanceManager.setField(dmObject, field, arr);
				} else if(baseType == int.class) {
					int[] arr = new int[arrVal.length];
					for(int i = 0; i < arrVal.length;i++) {
						arr[i] = Integer.parseInt(arrVal[i]);
					}

					GaiaRootPersistanceManager.setField(dmObject, field, arr);
				} else if(baseType == long.class) {
					long[] arr = new long[arrVal.length];
					for(int i = 0; i < arrVal.length;i++) {
						arr[i] = Long.parseLong(arrVal[i]);
					}

					GaiaRootPersistanceManager.setField(dmObject, field, arr);
				} else if(baseType == String.class) {
					String[] arr = new String[arrVal.length];
					for(int i = 0; i < arrVal.length;i++) {
						arr[i] = arrVal[i];
					}

					GaiaRootPersistanceManager.setField(dmObject, field, arr);
				} else if(baseType == boolean.class) {
					boolean[] arr = new boolean[arrVal.length];
					for(int i = 0; i < arrVal.length;i++) {
						arr[i] = Boolean.parseBoolean(arrVal[i]);
					}

					GaiaRootPersistanceManager.setField(dmObject, field, arr);
				}
			}
		}

	}

}