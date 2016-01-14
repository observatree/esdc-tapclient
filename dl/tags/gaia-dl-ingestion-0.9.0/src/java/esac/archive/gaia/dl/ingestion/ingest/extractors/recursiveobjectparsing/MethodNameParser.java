package esac.archive.gaia.dl.ingestion.ingest.extractors.recursiveobjectparsing;

import gaia.cu1.tools.dm.GaiaRoot;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.CaseFormat;


/**
 * Parses a Gaia DM class that is to be ingested. It returns
 * a plain alphabetical list of those methods which return data to be ingested 
 * as a table, both from inherited superclasses and Gaia subclasses obtained in getters.
 * It also filters out inherited methods which are not containing relevant data.
 * 
 * @author jgonzale
 *
 */
public class MethodNameParser {
	
	private static final Logger logger = Logger.getLogger(MethodNameParser.class.getName());

	
	
	
	/**
	 * Public call method
	 * @param theClass
	 * @return
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static List<String> getGaiaMethodNames(Class<?> theClass) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		logger.log(Level.FINE, "getGaiaMethods "+theClass);
		return getGaiaMethodNames("", ReturnedObjectsParser.getGetterMethods(theClass));
	}
	
	
	/**
	 * Method for usage by the extractor to GBIN SW. Returns the DB names of the colums for an 
	 * specific attribute (in DB name) in a specific class.
	 * @param theClass
	 * @param attributeName
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static List<String> getGaiaMethodNames(Class theClass, String attributeName) throws NoSuchMethodException, SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		
		String getterMethodName = "get" + attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
		
		Method method = theClass.getMethod(getterMethodName, null);
		
		List<String> methods = getGaiaMethodNames("", method);
		
		List<String> dbNames = new ArrayList<String>();
		
		for(String m:methods){
			dbNames.add(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, m.replace("get", "")));
		}
		
		return dbNames;
		
	}
	
	
	
	/**
	 * First recursive call method. Returns the filtered and flattened (including inherited, referenced, etc)
	 * methods.
	 * @param gaiaObjectMethods
	 * @return
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private static List<String> getGaiaMethodNames (String prefix, List<Method> gaiaObjectMethods) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		
		if(gaiaObjectMethods.size() == 1){
			logger.log(Level.FINE, "Is 1 method");
			return getGaiaMethodNames(prefix, gaiaObjectMethods.get(0));
		}else{
			logger.log(Level.FINE, "Is Several methods");
			List<String> part1 = getGaiaMethodNames(prefix, gaiaObjectMethods.get(0));
			List<String> part2 = getGaiaMethodNames(prefix, gaiaObjectMethods.subList(1, gaiaObjectMethods.size()));
			part1.addAll(part2);
			return part1;
		}
	}
	
	/**
	 * Second recursive call method. Returns the same as the previous one, but for just one method.
	 * @param method
	 * @return
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private static List<String> getGaiaMethodNames(String prefix, Method method) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{

		if(ReturnedObjectsParser.isPrimitiveDatatype(method.getReturnType())){
			logger.log(Level.FINE, "Is Datatype");
			List<String> mList = new ArrayList<String>();
			mList.add(prefix+method.getName());
			return mList;
		} else if(GaiaRoot.class.isAssignableFrom(method.getReturnType())){
			logger.log(Level.FINE, "Is GaiaRoot");
			return getGaiaMethodNames(method.getName(),new ArrayList<Method>(ReturnedObjectsParser.getGetterMethods(method.getReturnType())));
		} else if(method.getReturnType().equals(java.util.Map.class)){
			logger.log(Level.FINE, "Is HashMap or Annotation (ignored)");
			List<String> valueList = new ArrayList<String>();
			return valueList;	
		} else {
			logger.log(Level.FINE, "Is Else");
			throw new IllegalArgumentException("Data Type "+ method.getReturnType().getName()+
					" for method "+ method.getName()+" in class "+method.getDeclaringClass().getName()+" not supported");

		}
		
	}
	
	
	
}
	
	
	
	
	
	
	
	

