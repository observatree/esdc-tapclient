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
package esac.archive.gacs.common.constants;

/**
 * Utility class to specify HTTP constants (servlet param names, status codes, etc...)
 * contents.
 * 
 * @author Nicolas Fajersztejn
 * @author Raul Gutierrez-Sanchez Copyright (c) 2015- European Space Agency
 */
public class HttpConstants {

	/**
	 * Default constructor, it should not be called.
	 */
	protected HttpConstants() {
		// prevents calls from subclass
		throw new UnsupportedOperationException();
	}

	
	public static final String METADATA_GET_ACTION="servlet/metadata-action?"; 
	public static final String DATA_GET_ACTION="servlet/data-action?"; 
	public static final String TARGET_NAME_RESOLVER="servlet/target-resolver?"; 
	public static final String LOGIN_ACTION="login?";
	public static final String LOGIN_STATUS_ACTION="LoginStatus";
	public static final String LOGOUT_ACTION="logout";
	public static final String FILE_UPLOAD_ACTION="servlet/fileupload?";
	public static final String SHOPPING_CART_ACTION="servlet/shopping-cart-action";
	public static final String RSS_GET_ACTION="servlet/rss-action?";
	public static final String FILE_UPLOAD_TARGET_NAME_LIST_SERVLET="servlet/target-list-file-upload?";
	public static final String FILE_UPLOAD_EQUATORIAL_COORDINATE_LIST_SERVLET="servlet/eq-coord-list-file-upload?";
	public static final String SESSION_ATTRIBUTE_USERNAME="USERNAME";


	
	// Target Resolver servlet params
	public static final String SERVLET_PARAM_TARGET_NAME = "TARGET_NAME";
	public static final String SERVLET_PARAM_RESOLVER_TYPE = "RESOLVER_TYPE";


	
	///////////////////////////////////////////////////////////////////////////
	///      TARGET HTTP status codes
	///////////////////////////////////////////////////////////////////////////
	
	// Target Resolver Service Not Available	
	public static final int STATUS_CODE_TARGET_RESOLVER_SERVICE_NOT_AVAILABLE = 998;

	// Target Not Found Status code
	public static final int STATUS_CODE_TARGET_NOT_FOUND = 999;
	
	
	
	
	///////////////////////////////////////////////////////////////////////////
	///      CREDENTIALS HTTP status codes
	///////////////////////////////////////////////////////////////////////////
	
	public static final int OK = 200;

	// Login CREDENTIALS Not valid
	public static final int LOGIN_CREDENTIALS_NOT_VALID = 401;

	// Login REQUIRED
	//public static final int LOGIN_REQUIRED = 815;
	public static final int LOGIN_REQUIRED = 401;

	// User details update
	public static final int USER_DETAILS_UPDATED_SUCCESSFULLY = 819;
	public static final int ERROR_UPDATING_USER_DETAILS = 820;

	// User registration
	public static final int USER_REGISTRATION_OK = 821;
	public static final int USER_REGISTRATION_USER_ALREADY_EXISTS_ERROR = 822;
	public static final int USER_REGISTRATION_UNKNOWN_ERROR = 823;
	
	// Forgotten password
	public static final int USER_FORGOTTEN_PASSWORD_PROCESSED_OK = 830;
	public static final int USER_FORGOTTEN_PASSWORD_USERNAME_NOT_FOUND = 831;
	public static final int USER_FORGOTTEN_PASSWORD_UNKNOWN_ERROR = 832;

	// User password update
	public static final int USER_UPDATE_PASSWORD_PROCESSED_OK = 840;
	public static final int USER_UPDATE_PASSWORD_CURRENT_PASSWORD_NOT_CORRECT = 841;
	public static final int USER_UPDATE_PASSWORD_UNKWNON_ERROR = 842;

	
	
	///////////////////////////////////////////////////////////////////////////
	///      Servlet parameters
	///////////////////////////////////////////////////////////////////////////
	
	// Generic params
	public static final String SERVLET_PARAM_OBSERVATION_ID = "OBSERVATION_ID";
	public static final String SERVLET_PARAM_REVOLUTION = "REV";
	
	// Login servlet params	
	public static final String SERVLET_PARAM_USERNAME = "username";
	public static final String SERVLET_PARAM_PASSWORD = "password";
	public static final String SERVLET_PARAM_USER_ACTION = "SUBMIT";
	public static final String SERVLET_PARAM_USERNAME_FORGOTTEN_PASSWORD = "USERNAME_FORGOTTEN_PASSWORD";
	
	public static final String SERVLET_PARAM_SUBMIT_VALUE_CHECK = "CHECK";	
	public static final String SERVLET_PARAM_SUBMIT_VALUE_LOGIN = "LOGIN";
	public static final String SERVLET_PARAM_SUBMIT_VALUE_LOGOUT = "LOGOUT";
	
	public static final String PARAM_VALUE_USER_ACTION_LOGIN = "LOGIN";
	public static final String PARAM_VALUE_USER_ACTION_LOGOUT = "LOGOUT";
	public static final String PARAM_VALUE_USER_ACTION_CHECK_USER_IS_LOGGED_IN = "CHECK";
	public static final String PARAM_VALUE_USER_ACTION_UPDATE_USER_DETAILS = "UPDATE_USER_DETAILS";
	public static final String PARAM_VALUE_USER_ACTION_UPDATE_USER_PASSWORD = "UPDATE_PASSWORD";
	public static final String PARAM_VALUE_USER_ACTION_REGISTER_NEW_USER = "REGISTER";
	public static final String PARAM_VALUE_USER_ACTION_FORGOTTEN_PASSWORD = "FORGOTTEN_PASSWORD";
	

	// Target List File Upload constants
	public static final String TARGET_NAME_VALID_PATTERN = "[a-zA-Z0-9][-+\\* .\\w]*[\\w]";
	public static final String TARGET_RESOLVED_FIELD_SEPARATOR = "\t";
	public static final String TARGET_NOT_FOUND_STRING = "TARGET_NOT_FOUND";
	public static final String TARGET_WRONG_FORMAT_STRING = "TARGET_WRONG_FORMAT";

	// Coodinates List File Upload constants
	public static final String COORDINATES_WRONG_FORMAT_STRING = "COORDINATES_WRONG_FORMAT";
	public static final String RA_AND_DEC_FIELDS_SEPARATOR = ",";
	
	// Text to write in results file when value is null
	public static final String DEFAULT_DISPLAY_STRING_FOR_NULL_VALUE = "-";

	// Text to write in results file when value is proprietary
	public static final String DEFAULT_DISPLAY_STRING_FOR_PROPRIETARY_VALUE = "Proprietary";

	// Text to write in results file when image/icon is proprietary
	public static final String PROPRIETARY_IMAGE_STRING = "PROPRIETARY_IMAGE";
	
}
