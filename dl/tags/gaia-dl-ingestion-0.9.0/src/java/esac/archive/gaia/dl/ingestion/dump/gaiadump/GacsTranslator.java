package esac.archive.gaia.dl.ingestion.dump.gaiadump;

import esac.archive.gaia.dl.ingestion.ingest.extractors.recursiveobjectparsing.MethodNameParser;
import gaia.cu1.tools.dm.GaiaRoot;
import gaia.cu1.tools.dmimpl.GaiaRootPersistanceManager;
import gaia.cu1.tools.exception.GaiaException;
import gaia.cu1.tools.util.GaiaFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GacsTranslator {
	
	
	HashMap<String, String> gaiaGacsMap;
	HashMap<String, Class<?>> fieldMap;
	HashMap<String, Class<?>> arrayMap;
	HashMap<String, Map<String, String>> subFieldMap;

	@SuppressWarnings("unchecked")
	public GacsTranslator(Class<? extends GaiaRoot> dmInterface) throws GaiaException {
		gaiaGacsMap = new HashMap<String, String>();

		fieldMap = new HashMap<String, Class<?>>();

		arrayMap = new HashMap<String, Class<?>>();

		subFieldMap = new HashMap<String, Map<String, String>>();


		try {
			for(String field : GaiaRootPersistanceManager.getColNames(dmInterface)) {			
				System.setProperty(dmInterface.getName() + "." + field, 
						MethodNameParser.getGaiaMethodNames(dmInterface, field).get(0));

				List<String> mapList = MethodNameParser.getGaiaMethodNames(dmInterface, field);
				Class dmSubInterface = getFieldType(GaiaFactory.getClassMap().getImplementation(dmInterface), field);

				gaiaGacsMap.put(field, mapList.get(0));

				if(mapList.size() > 1) {
					if(dmSubInterface != null) {
						Class<?> dmImpl = GaiaFactory.getClassMap().getImplementation(dmSubInterface);

						fieldMap.put(field, dmImpl);
						subFieldMap.put(field, new HashMap<String, String>());

						for(String sf:GaiaRootPersistanceManager.getFieldNames(dmImpl.newInstance())) {
							String sfMap = MethodNameParser.getGaiaMethodNames(dmSubInterface, sf).get(0);
							String match = null;

							for(String s:mapList) {
								if(s.endsWith(sfMap)) {
									match = s;
									break;
								}
							}

							subFieldMap.get(field).put(sf, match);
							System.setProperty(dmSubInterface.getName() + "." + sf, 
									match);

							System.setProperty(dmInterface.getName() + "." + sf, 
									match);						

						}

					}		
				} else if(dmSubInterface.isArray()) {
					arrayMap.put(field, dmSubInterface.getComponentType());
				}
			}

			GaiaRootPersistanceManager.reset();
		} catch (Exception e) {
			throw new GaiaException(e);
		}
	}


	
	
	
	

	/**
	 * @return the gaiaGacsMap
	 */
	public HashMap<String, String> getGaiaGacsMap() {
		return gaiaGacsMap;
	}







	/**
	 * @return the fieldMap
	 */
	public HashMap<String, Class<?>> getFieldMap() {
		return fieldMap;
	}







	/**
	 * @return the arrayMap
	 */
	public HashMap<String, Class<?>> getArrayMap() {
		return arrayMap;
	}







	/**
	 * @return the subFieldMap
	 */
	public HashMap<String, Map<String, String>> getSubFieldMap() {
		return subFieldMap;
	}







	private Class getFieldType(Class class1, String fieldName) 
	{
		ArrayList<java.lang.reflect.Field> fields = new ArrayList<>();
		getAllInterfaceFields(class1, fields);

		for(java.lang.reflect.Field f:fields) {
			if(f.getName().equals(fieldName)) {
				return f.getType();
			}
		}
		return null;
	}

	
	private void getAllInterfaceFields(Class class1, ArrayList<java.lang.reflect.Field> fields) 
	{
		Class[] interfaces = class1.getInterfaces();
		Class superClass = class1.getSuperclass();
		if (interfaces.length == 0 && superClass == null) {
			for(java.lang.reflect.Field f:class1.getDeclaredFields()) {
				fields.add(f);
			}
		}
		else {
			if (superClass != null) {
				for(java.lang.reflect.Field f:superClass.getDeclaredFields()) {
					fields.add(f);
				}
			}
			if (interfaces.length != 0) {
				for(Class i:interfaces) {
					getAllInterfaceFields(i, fields);
				}
				for(java.lang.reflect.Field f:class1.getDeclaredFields()) {
					fields.add(f);
				}
			}
		}
	}

}