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
package esac.archive.gaia.dl.ingestion.ingest.extractors.recursiveobjectparsing;

import gaia.cu1.tools.dm.GaiaRoot;
import gaia.cu1.tools.dmimpl.GaiaRootImpl;
import gaia.cu1.tools.time.GaiaTime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Parses a Gaia DM object that is to be ingested. It returns
 * a plain list of returned objects, in the native datatype, corresponding
 * to each column. It performs a similar filtering to {@link MethodNameParser}
 * 
 * @author jgonzale
 *
 */
public class ReturnedObjectsParser {

	private static final Logger logger = Logger.getLogger(ReturnedObjectsParser.class.getName());

	
	/**
	 * Comparator for sorting methods in one class alphabetically
	 * @author jgonzale
	 *
	 */
	private static class MethodComparator implements Comparator<Method> {
		public int compare(Method f1, Method f2) {
			return (f1.getName().compareTo(f2.getName()));
		}	
	}
	
		
	
	/**
	 * Traverses recursively a source catalogue object and 
	 * returns the list of native values in native datatype (Double, Integer, etc).
	 * @param childObject
	 * @return
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static List<Object> getGaiaObjectValues(Object childObject) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{

		if(childObject == null || isPrimitiveDatatype(childObject.getClass())){
			logger.log(Level.FINE, "Is Datatype");
			List<Object> valueList = new ArrayList<Object>();
			valueList.add(childObject);
			return valueList;
		} else if(childObject instanceof GaiaRoot){
			logger.log(Level.FINE, "Is GaiaRoot");
			return getGaiaObjectValues(childObject, getGetterMethods(childObject.getClass()));
		} else if(childObject instanceof HashMap){
			logger.log(Level.FINE, "Is HashMap (ignored)");
			List<Object> valueList = new ArrayList<Object>();
			return valueList;	
		} else {
			logger.log(Level.FINE, "Is Else");
			throw new IllegalArgumentException("Data Type "+ childObject + " not supported");

		}
		

		
	}


	/**
	 * Recursive function linked to {@link #getGaiaObjectValues(Object)}
	 * @param object
	 * @param gaiaObjectMethods
	 * @return
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private static List<Object> getGaiaObjectValues (Object object, List<Method> gaiaObjectMethods) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
	
		if(gaiaObjectMethods.size() == 1){
			logger.log(Level.FINE, "Is 1 method");
			return getGaiaObjectValues(object.getClass().getMethod(gaiaObjectMethods.get(0).getName()).invoke(object));
		}else{
			logger.log(Level.FINE, "Is Several methods");
			List<Object> part1 = getGaiaObjectValues(object.getClass().getMethod(gaiaObjectMethods.get(0).getName()).invoke(object));
			List<Object> part2 = getGaiaObjectValues(object, gaiaObjectMethods.subList(1, gaiaObjectMethods.size()));
			part1.addAll(part2);
			return part1;
		}
	}
	
	
	/**
	 * Returns getter methods for one Class
	 * @param classToConvert
	 * @return
	 */
	public static List<Method> getGetterMethods(Class<?> classToConvert){
		
		logger.log(Level.FINE, "Getting ReturnedObjects getter methods for object class "+classToConvert.getName());
		
		//Class fields sorted alphabetically
		SortedSet<Method> getMethods = new TreeSet<Method>(new MethodComparator());
		//Drop those which are not setters
		for(Method method:Arrays.asList(classToConvert.getMethods())){
			logger.log(Level.FINE, "Processing MethodName method "+method.getName()+" declaring class "+method.getDeclaringClass());
			if(method.getName().startsWith("get")&& 
					method.getDeclaringClass().getPackage().getName().contains("gaia") &&
					!method.getName().equals("getParamMinValues") && 
					!method.getName().equals("getParamMaxValues") && 
					!method.getName().equals("getParamOutOfRangeValues") && 
					!method.getName().equals("fillWithRandomValues") && 
					!method.getDeclaringClass().equals(GaiaRootImpl.class) && 
					!method.getDeclaringClass().equals(GaiaRoot.class)){
				getMethods.add(method);
			}
		}
		return new ArrayList<Method> (getMethods);
		
	}
	
	
	
	/**
	 * Returns whether a class is considered primitive
	 * @param c
	 * @return
	 */
	public static boolean isPrimitiveDatatype(Class c){
		

		return(
		c.equals(Double.class)|| 
		c.equals(Integer.class) || 
		c.equals(String.class) || 
		c.equals(Boolean.class) ||
		c.equals(Long.class) ||
		c.equals(Double.class) ||
		c.equals(Byte.class) ||
		c.equals(Float.class) ||
		c.equals(Short.class) ||
		c.equals(Character.class) ||
		c.equals(Integer[].class) || 
		c.equals(String[].class) || 
		c.equals(Boolean[].class) ||
		c.equals(Long[].class) ||
		c.equals(Byte[].class) ||
		c.equals(Double[].class) ||
		c.equals(Float[].class) ||
		c.equals(Short[].class) ||
		c.equals(Character[].class) ||
		c.equals(Integer[][].class) || 
		c.equals(String[][].class) || 
		c.equals(Boolean[][].class) ||
		c.equals(Long[][].class) ||
		c.equals(Byte[][].class) ||
		c.equals(Double[][].class) ||
		c.equals(Float[][].class) ||
		c.equals(Short[][].class) ||
		c.equals(Character[][].class) ||
		c.equals(Double.TYPE) ||
		c.equals(Integer.TYPE) ||
		c.equals(Boolean.TYPE) ||
		c.equals(Long.TYPE) ||
		c.equals(Byte.TYPE) ||
		c.equals(Float.TYPE) ||
		c.equals(Short.TYPE) ||
		c.equals(Character.TYPE) ||
		c.equals(double.class) ||
		c.equals(int.class) ||
		c.equals(boolean.class) ||
		c.equals(long.class) ||
		c.equals(byte.class) ||
		c.equals(float.class) ||
		c.equals(short.class) ||
		c.equals(char.class) ||
		c.equals(double[].class) ||
		c.equals(int[].class) ||
		c.equals(boolean[].class) ||
		c.equals(long[].class) ||
		c.equals(byte[].class) ||
		c.equals(float[].class) ||
		c.equals(short[].class) ||
		c.equals(char[].class) ||
		c.equals(double[][].class) ||
		c.equals(int[][].class) ||
		c.equals(boolean[][].class) ||
		c.equals(long[][].class) ||
		c.equals(byte[][].class) ||
		c.equals(float[][].class) ||
		c.equals(short[][].class) ||
		c.equals(char[][].class) ||
		c.equals(Enum.class) ||
		c.equals(Enum[].class) ||
		c.equals(Enum[][].class) ||
		Enum.class.isAssignableFrom(c) ||
		Enum[].class.isAssignableFrom(c) ||
		Enum[][].class.isAssignableFrom(c) ||
		c.isEnum() ||		
		c.equals(GaiaTime.class)
		
		);

	}
	
	
	
	
}
