package esac.archive.gacs.common.constants;


public class RetrievalConstants {

	/**
	 * Default constructor, it should not be called.
	 */
	protected RetrievalConstants() {
		// prevents calls from subclass
		throw new UnsupportedOperationException();
	}

	// //////////////////////////////////////////////////////////////////////////////
	// / Generic Retrieval constants (should be in ABSI?) ///
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * Retrieval element key (should reference directly use the one from ABSI as
	 * in the comment).
	 */
	public static final String PARAM_NAME_RETRIEVAL_TYPE = "RETRIEVAL_TYPE"; // =
																		// RetrievalElement.RETRIEVALTYPE;

	public static final String PARAM_NAME_ENTITY_TYPE = "ENTITY_TYPE";
	
	
	/**
	 * Product Retrieval type (should reference directly the one from ABSI as in
	 * the comment).
	 */
	public static final String PRODUCT_RETRIEVAL_TYPE = "PRODUCT"; // =
																	// RetrievalElement.PRODUCT_RETRIEVALTYPE;

	/**
	 * Postcard Retrieval type (should reference directly the one from ABSI as
	 * in the comment).
	 */
	public static final String POSTCARD_RETRIEVAL_TYPE = "POSTCARD"; // =
																		// RetrievalElement.POSTCARD_RETRIEVALTYPE;

	/**
	 * Shopping Basket Retrieval type
	 */
	public static final String POSTCARD_SHOPPING_BASKET_TYPE = "SHOPPING_BASKET";
	
	/**
	 * Notification email 
	 */
	// NOT IN USE ANY MORE
	//public static final String NOTIFICATION_EMAIL = "NOTIFICATION_EMAIL";
	

	// //////////////////////////////////////////////////////////////////////////////
	// / NXSA-specific Retrieval constants ///
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * Retrieval param KEYS (name of the params used as part of the URL for retrieval)
	 */

	public static final String PARAM_NAME_OBSERVATION_ID = "OBSERVATION_ID";
	public static final String PARAM_NAME_OBSERVATION_OID = "OBSERVATION_OID";
	public static final String PARAM_NAME_INSTRUMENT = "INSTRUMENT";
	public static final String PARAM_NAME_LEVEL = "LEVEL";
	
	public static final String PARAM_NAME_EXPOSURE_ID = "EXPOSURE_ID";
	public static final String PARAM_NAME_EXPOSURE_OID = "EXPOSURE_OID";

	public static final String PARAM_NAME_FILTER = "FILTER";
	public static final String PARAM_NAME_BEGIN_DATE = "BEGIN_DATE";
	public static final String PARAM_NAME_END_DATE = "END_DATE";

	public static final String PARAM_NAME_EPIC_SOURCE_CAT_OID = "EPIC_SOURCE_CAT_OID";
	public static final String PARAM_NAME_EPIC_SOURCE_CAT_PRODUCT_TYPE = "EPIC_SOURCE_CAT_PRODUCT_TYPE";

	public static final String PARAM_NAME_SLEW_OBSERVATION_OID = "SLEW_OBSERVATION_OID";
	public static final String PARAM_NAME_SLEW_OBSERVATION_ID = "SLEW_OBSERVATION_ID";
	public static final String PARAM_NAME_SLEW_EXPOSURE_ID = "SLEW_EXPOSURE_ID";
	public static final String PARAM_NAME_SLEW_SOURCE_CAT_OID = "SLEW_SOURCE_CAT_OID";
	public static final String PARAM_NAME_SLEW_SOURCE_CAT_PRODUCT_TYPE = "SLEW_SOURCE_CAT_PRODUCT_TYPE";
	
	public static final String PARAM_NAME_SHOPPING_BASKET_XML = "SHOPPING_BASKET_XML";
	public static final String XML_TAG_SHOPPING_BASKET = "shopping_basket";
	public static final String XML_TAG_SHOPPING_BASKET_ITEM = "item";
	
	public static final String PARAM_NAME_COMPRESSION = "COMPRESSION";
	
/*	public static final String PARAM_NAME_USERNAME = "USERNAME";
	public static final String PARAM_NAME_PASSWORD = "PASSWORD";*/
	
	public static final String PARAM_NAME_EXPOSURE_FLAG = "EXPOSURE_FLAG";
	public static final String PARAM_NAME_EXPOSURE_NUMBER = "EXPOSURE_NUMBER";
	public static final String PARAM_NAME_PRODUCT_TYPE = "PRODUCT_TYPE";
	public static final String PARAM_NAME_DATA_SUBSET_NUMBER = "DATA_SUBSET_NUMBER";
	public static final String PARAM_NAME_SOURCE_NUMBER = "SOURCE_NUMBER";
	public static final String PARAM_NAME_FILE_EXTENSION = "FILE_EXTENSION";
	
	// Param to identify which app is making the request (AIO/UI)
	public static final String PARAM_NAME_REQUESTOR_APP = "REQUESTOR_APP";
	
	// Obs Image Type (EPIC, RGS_FLUXED, ...)
	public static final String PARAM_NAME_OBS_IMAGE_TYPE = "OBS_IMAGE_TYPE";
	

	/**
	 * User Details XML params
	 */
	public static final String PARAM_NAME_USER_DETAILS_XML = "USER_DETAILS_XML";
	public static final String XML_TAG_VALUE_USER = "user";
	public static final String XML_TAG_VALUE_USER_USERNAME = "username";

	
	/**
	 * Instrument Configuration XML params
	 */
	public static final String PARAM_NAME_INSTR_CONFIG_XML = "INSTR_CONFIG_XML";
	public static final String XML_TAG_VALUE_INSTRUMENT_CONFIGURATION = "instrument_configuration";
	public static final String XML_TAG_VALUE_COMBINATION = "combination";
	public static final String XML_TAG_VALUE_INSTR = "instr";
	public static final String XML_TAG_VALUE_MODE = "mode";
	public static final String XML_TAG_VALUE_FILTER = "filter";
	public static final String XML_TAG_ATTRIBUTE_INSTR_NAME = "name";	
	
	
	/**
	 * Names of the params used in old AIO. They are required as they will be still 
	 * used as input.
	 */
	public static final String PARAM_NAME_OLD_AIO_OBSNO = "obsno";
	public static final String PARAM_NAME_OLD_AIO_INST_NAME = "instname";
	public static final String PARAM_NAME_OLD_AIO_EXP_FLAG = "expflag";
	public static final String PARAM_NAME_OLD_AIO_EXP_NO = "expno";
	public static final String PARAM_NAME_OLD_AIO_NAME = "name";
	public static final String PARAM_NAME_OLD_AIO_DATA_SUBSET_NO = "datasubsetno";
	public static final String PARAM_NAME_OLD_AIO_SOURCE_NO = "sourceno";
	public static final String PARAM_NAME_OLD_AIO_EXTENSION = "extension";
	public static final String PARAM_NAME_OLD_AIO_LEVEL = "level";
	public static final String PARAM_NAME_OLD_AIO_AIOUSER = "AIOUSER";
	public static final String PARAM_NAME_OLD_AIO_AIOPWD = "AIOPWD";
	
	
	/**
	 * Retrieval param VALUES (VALUES name of the params used as part of the URL
	 * for retrieval)
	 */
	public static final String PARAM_VALUE_LEVEL_ODF = "ODF";
	public static final String PARAM_VALUE_LEVEL_SDF = "SDF";
	public static final String PARAM_VALUE_LEVEL_PPS = "PPS";
	public static final String PARAM_VALUE_LEVEL_SLEW_PPS = "SLEW_PPS";
	public static final String PARAM_VALUE_LEVEL_ODF_PPS = "ODF_PPS";
	public static final String PARAM_VALUE_LEVEL_IMAGES = "IMAGES";
	public static final String PARAM_VALUE_LEVEL_SPECTRA = "SPECTRA";
	public static final String PARAM_VALUE_LEVEL_LIGHT_CURVES = "LIGHT_CURVES";
	public static final String PARAM_VALUE_LEVEL_SLEW_SOURCE_CAT_IMAGES = "SLEW_SOURCE_CAT_IMAGES";
	public static final String PARAM_VALUE_LEVEL_SLEW_SOURCE_CAT_EXP_MAPS = "SLEW_SOURCE_CAT_EXP_MAPS";
	
	public static final String PARAM_VALUE_LEVEL_EPIC_EXP_PPS = "EPIC_EXP_PPS";
	public static final String PARAM_VALUE_LEVEL_EPIC_EXP_IMAGES = "EPIC_EXP_IMAGES";
	public static final String PARAM_VALUE_LEVEL_EPIC_EXP_SPECTRA = "EPIC_EXP_SPECTRA";
	public static final String PARAM_VALUE_LEVEL_EPIC_EXP_LIGHT_CURVES = "EPIC_EXP_LIGHT_CURVES"; 

	public static final String PARAM_VALUE_LEVEL_RGS_EXP_PPS = "RGS_EXP_PPS";
	public static final String PARAM_VALUE_LEVEL_RGS_EXP_IMAGES = "RGS_EXP_IMAGES";
	public static final String PARAM_VALUE_LEVEL_RGS_EXP_SPECTRA = "RGS_EXP_SPECTRA";

	public static final String PARAM_VALUE_LEVEL_OM_EXP_PPS = "OM_EXP_PPS";
	public static final String PARAM_VALUE_LEVEL_OM_EXP_IMAGES = "OM_EXP_IMAGES";
	public static final String PARAM_VALUE_LEVEL_OM_EXP_SPECTRA = "OM_EXP_SPECTRA";
	public static final String PARAM_VALUE_LEVEL_OM_EXP_LIGHT_CURVES = "OM_EXP_LIGHT_CURVES"; 

	public static final String PARAM_VALUE_LEVEL_SLEW_EXP_PPS = "SLEW_EXP_PPS";
	public static final String PARAM_VALUE_LEVEL_SLEW_EXP_IMAGES = "SLEW_EXP_IMAGES";
	
	/**
	 * String value of the application making the request (AIO or UI)
	 * This value must be the same as in the attribute 'name' in DB table 'origin' 
	 */
	public static final String PARAM_VALUE_REQUESTOR_APP_AIO = "AIO";
	public static final String PARAM_VALUE_REQUESTOR_APP_UI = "UI";

	public static final String PARAM_VALUE_OBS_IMAGE_TYPE_EPIC = "OBS_EPIC";
	public static final String PARAM_VALUE_OBS_IMAGE_TYPE_RGS_FLUXED = "OBS_RGS_FLUXED";
	
	
	/**
	 * EPIC Source Catalogue different Retrieval Products 
	 */
	public static enum EpicSourceCatProductType {
		IMAGE, THUMBNAIL, FINDING_CHART, LIGHT_CURVE, SPECTRUM
	}

	/**
	 * Slew Source Catalogue different Retrieval Products 
	 */
	public static enum SlewSourceCatProductType {
		DSS, FINDING_CHART, IMAGE_BAND_0, IMAGE_BAND_4, IMAGE_BAND_5
	}
	
	
	/**
	 * Logical combination allowed when querying Instruement Configuration 
	 */
	public enum InstrumentCombination {
		OR, AND
	}
	
	
}

