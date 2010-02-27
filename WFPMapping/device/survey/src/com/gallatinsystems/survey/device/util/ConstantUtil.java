package com.gallatinsystems.survey.device.util;

/**
 * Class to hold all public constants used in the application
 * 
 * @author Christopher Fagiani
 * 
 */
public class ConstantUtil {

	/**
	 * status related constants
	 */
	public static final String COMPLETE_STATUS = "Complete";
	public static final String SENT_STATUS = "Sent";
	public static final String RUNNING_STATUS = "Running";
	public static final String IN_PROGRESS_STATUS = "In Progress";

	/**
	 * deletion indicators
	 */
	public static final String IS_DELETED = "Y";
	public static final String NOT_DELETED = "N";

	/**
	 * question types
	 */
	public static final String FREE_QUESTION_TYPE = "free";
	public static final String OPTION_QUESTION_TYPE = "option";
	public static final String GEO_QUESTION_TYPE = "geo";
	public static final String VIDEO_QUESTION_TYPE = "video";
	public static final String PHOTO_QUESTION_TYPE = "photo";

	/**
	 * rendering options
	 */
	public static final String SPINNER_RENDER_MODE = "spinner";

	/**
	 * response types
	 */
	public static final String VALUE_RESPONSE_TYPE = "VALUE";
	public static final String IMAGE_RESPONSE_TYPE = "IMAGE";
	public static final String VIDEO_RESPONSE_TYPE = "VIDEO";
	public static final String GEO_RESPONSE_TYPE = "GEO";
	public static final String OTHER_RESPONSE_TYPE = "OTHER";

	/**
	 * validation types
	 */
	public static final String NUMERIC_VALIDATION_TYPE = "numeric";
	public static final String NAME_VALIDATION_TYPE = "name";
	
	/**
	 * survey types
	 * 
	 */
	public static final String SURVEY_TYPE = "survey";
	
	/**
	 * media question support
	 */
	public static final String MEDIA_FILE_KEY = "filename";
	

	/**
	 * operation types
	 */
	public static final String USER_OP = "USER";
	public static final String SURVEY_OP = "SURVEY";
	public static final String CONF_OP = "CONF";
	public static final String PLOT_OP = "PLOT";
	
	/**
	 * data sync options
	 */
	public static final String EXPORT = "EXPORT";
	public static final String SEND = "SEND";
	public static final String OP_TYPE_KEY = "TYPE";
	public static final String FORCE_KEY = "FORCE";
	
	/**
	 * keys for saves state and bundle extras
	 */
	public static final String PLOT_ID_KEY = "plotid";
	public static final String STATUS_KEY = "status";	
	public static final String SURVEY_RESOURCE_ID_KEY = "RESID";
	public static final String USER_ID_KEY = "UID";
	public static final String SURVEY_ID_KEY = "SID";
	public static final String ID_KEY = "_id";
	public static final String DISPLAY_NAME_KEY = "display_name";
	public static final String EMAIL_KEY = "email";
	public static final String INTERVAL_KEY = "interval";	
	public static final String RESPONDENT_ID_KEY = "survey_respondent_id";
	
	/**
	 * intents
	 */
	public static final String DATA_AVAILABLE_INTENT = "com.gallatinsystems.survey.device.DATA_SUBMITTED";
	
	/**
	 * prevent instantiation
	 */
	private ConstantUtil() {
	}

}
