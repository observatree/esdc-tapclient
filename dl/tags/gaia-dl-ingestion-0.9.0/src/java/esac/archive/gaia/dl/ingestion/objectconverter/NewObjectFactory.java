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
package esac.archive.gaia.dl.ingestion.objectconverter;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.mchange.v1.lang.ClassUtils;

import gaia.cu1.tools.dal.ObjectFactory;
import gaia.cu1.tools.dm.GaiaRoot;
import gaia.cu1.tools.dmimpl.GaiaRootImpl;
import gaia.cu1.tools.exception.ErrorMessageFormat;
import gaia.cu1.tools.exception.ErrorMessageKeys;
import gaia.cu1.tools.exception.GaiaConfigurationException;
import gaia.cu1.tools.exception.GaiaException;

public class NewObjectFactory<V> extends ObjectFactory<V> {

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(NewObjectFactory.class);

	public NewObjectFactory(Class<?> definitionClass) throws GaiaException {
		super(definitionClass);
	}

	/**
	 * Converts a given Object into one implementing the reference class of this Factory <V>
	 * It uses reflection to find out the macting reference class setter methods with a
	 * correspondant getter method on the given object. After creating a new Object it will
	 * invoke the mapped setter objects with the values returned by the call to the passed
	 * Object getter.
	 *
	 * This method is useful when we want to extend the functionality of the DM class, one
	 * can extend the DM class and after reading the data use this method to convert them
	 * into the extended class used by the algorithms, similarly when data is being written
	 * to the Store you can apply the reverse conversion back.
	 *
	 * @param toConvert the Object to be converted
	 * @return a filled in implementarion of the reference class of this factory
	 * @throws GaiaConfigurationException if error while instantiating the reference class implementation
	 */
	@Override
	@SuppressWarnings("unchecked")
	public V convert(final Object toConvert) throws GaiaConfigurationException {
		final String className = this.classRefImpl.getCanonicalName();

		try {
			String key = className + "/" +
					toConvert.getClass().getCanonicalName();

			// Check if the conversion methods are already in the map
			ArrayList<Method> methodList = convertMap.get(key);

			if (methodList == null) {
				methodList = generateConvertMethods(toConvert.getClass());
				convertMap.put(key, methodList);
			}

			// create empty Object and invoke its setters
			final V obj = (V) this.classRefImpl.newInstance();

			Method setMethod;
			Method getMethod;

			for (int i = 0; i < (methodList.size() - 1); i += 2) {
				setMethod = methodList.get(i);
				getMethod = methodList.get(i + 1);

				Object returned = getMethod.invoke(toConvert, (Object[])null);
				
				//switch from NaN to null
				if (returned instanceof Double)
					if (Double.isNaN(((Double)returned).doubleValue()))
						returned = null; 

				if (returned instanceof Float )
					if (Float.isNaN(((Float)returned).floatValue()))
						returned = null;

				setMethod.invoke(obj, returned);
			}

			return obj;
		} catch (final Exception e) {
			NewObjectFactory.logger.error(
					ErrorMessageFormat.format(
							ErrorMessageKeys.Config.EXCEPTION_IN_METHOD_CALL,
							className),
							e);
			throw new GaiaConfigurationException(
					ErrorMessageFormat.format(
							ErrorMessageKeys.Config.EXCEPTION_IN_METHOD_CALL,
							className),
							e);
		}
	}
	
	/**
	 * Generate the set/get pair of methods needed to convert one Object into an equivalent one
	 * @param aClass the class of the Object we want to transform
	 * @return an ArrayList containing the set/get pairs
	 */
	private ArrayList<Method> generateConvertMethods(final Class<?> aClass) {
		return generateConvertMethods(classRef, aClass);
	}

	/**
	 * Generate the set/get pair of methods needed to convert one Object into an equivalent one
	 * @param referenceClass the class of the reference Object result of the transformation
	 * @param aClass the class of the Object we want to transform
	 * @return an ArrayList containing the set/get pairs
	 */
	private static ArrayList<Method> generateConvertMethods(
			final Class<?> referenceClass, final Class<?> aClass) {
		ArrayList<Method> common = new ArrayList<Method>();

		// Get a list of methods in both classes
		ArrayList<Method> mList = getMethods(aClass);
		ArrayList<Method> mListRef = getMethods(referenceClass);

		for (final Method m : mListRef) { // for each method in the reference class

			if (m.getName().startsWith("set")) { // if is a setter

				Method getMethod = existsGet(m, mList);

				if (getMethod != null) { // if it exist an equivalent getter on 
					// the given Object to be converted then
					// add the pair.  
					common.add(m);
					common.add(getMethod);
				}
			}
		}

		return common;
	}
	
	/**
	 * Get a list of methods available to a class, this is the methods on it or on its
	 * parents.
	 * @param aClass the class whose methods we want
	 * @return an ArrayList with all the methods available to the class
	 */
	private static ArrayList<Method> getMethods(Class<?> aClass) {
		ArrayList<Method> mList = new ArrayList<Method>();

		add(mList, aClass.getMethods());

		while ((aClass = aClass.getSuperclass()) != null) {
			add(mList, aClass.getMethods());
		}

		return mList;
	}
	
	/**
	 * Add a set of methods to a list
	 * @param mList the ArrayList where we want to add the methods
	 * @param methods an array of methods
	 */
	private static void add(final ArrayList<Method> mList,
			final Method[] methods) {
		for (final Method m : methods) {
			Class<?> aClass = m.getDeclaringClass();
			if (aClass != GaiaRoot.class && aClass != GaiaRootImpl.class) {
				mList.add(m);
			}
		}
	}
	
	/**
	 * Tells if there exists a correspondant get method returnning the argument used
	 * by the given set method.
	 * @param setMethod the set method whose relevant get we are looking for
	 * @param mlist the list of methods where the get may be present
	 * @return the getter method returning the given set method argument
	 */
	private static Method existsGet(final Method setMethod,
			final ArrayList<Method> mlist) {
		Method getMethod = null;

		String methodRoot = setMethod.getName();
		methodRoot = methodRoot.substring(1, methodRoot.length());

		for (final Method mi : mlist) {
			if (mi.getName().startsWith("get") &&
					mi.getName().endsWith(methodRoot)) {
				if ((setMethod.getParameterTypes().length == 1) &&
						sameType(setMethod.getParameterTypes()[0], mi.getReturnType())
						) {
					getMethod = mi;

					break;
				}
			}
		}

		return getMethod;
	}

	private static boolean sameType(Class<?> c1, Class<?> c2) {
		String a = c1.toString().toUpperCase();
		String e = c2.toString().toUpperCase();
		String[] splitC1 = c1.toString().toUpperCase().split("\\.");
		String[] splitC2 = c2.toString().toUpperCase().split("\\.");
		
		a = splitC1.length == 0 ? c1.toString().toUpperCase() : splitC1[splitC1.length-1];
		e = splitC2.length == 0 ? c2.toString().toUpperCase() : splitC2[splitC2.length-1];
		boolean same = a.equals(e);
		return same;
	}


}
