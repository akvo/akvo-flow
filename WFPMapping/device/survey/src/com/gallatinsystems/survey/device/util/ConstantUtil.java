package com.gallatinsystems.survey.device.util;

import java.util.HashMap;

import com.gallatinsystems.survey.device.activity.WaterflowCalculatorActivity;

/**
 * Class to hold all public constants used in the application
 * 
 * @author Christopher Fagiani
 * 
 */
public class ConstantUtil {

	/**
	 * filesystem constants
	 */
	public static final String DATA_DIR = "/sdcard/fieldsurvey/data/";

	public static final String FILE_SURVEY_LOCATION_TYPE = "file";

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
	public static final String SCAN_QUESTION_TYPE = "scan";
	public static final String TRACK_QUESTION_TYPE = "track";

	/**
	 * help types
	 */
	public static final String VIDEO_HELP_TYPE = "video";
	public static final String IMAGE_HELP_TYPE = "image";
	public static final String ACTIVITY_HELP_TYPE = "activity";
	public static final String TIP_HELP_TYPE = "tip";

	/**
	 * rendering options
	 */
	public static final String SPINNER_RENDER_MODE = "spinner";
	public static final String RADIO_RENDER_MODE = "radio";

	/**
	 * response types
	 */
	public static final String VALUE_RESPONSE_TYPE = "VALUE";
	public static final String IMAGE_RESPONSE_TYPE = "IMAGE";
	public static final String VIDEO_RESPONSE_TYPE = "VIDEO";
	public static final String GEO_RESPONSE_TYPE = "GEO";
	public static final String TRACK_RESPONSE_TYPE = "TRACK";
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
	public static final String NEARBY_OP = "NEARBY";
	public static final String REVIEW_OP = "REVIEW";
	public static final String WATERFLOW_CALC_OP = "WFCALC";

	/**
	 * data sync options
	 */
	public static final String EXPORT = "EXPORT";
	public static final String SEND = "SEND";
	public static final String OP_TYPE_KEY = "TYPE";
	public static final String FORCE_KEY = "FORCE";

	/**
	 * keys for saved state and bundle extras
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
	public static final String IMAGE_URL_LIST_KEY = "imageurls";
	public static final String IMAGE_CAPTION_LIST_KEY = "imagecaps";
	public static final String AP_KEY = "accesspoint";
	public static final String READONLY_KEY = "readonly";
	public static final String CALC_RESULT_KEY = "calcresult";
	public static final String MODE_KEY = "mode";

	/**
	 * settings keys
	 */
	public static final String SURVEY_LANG_SETTING_KEY = "survey.language";
	public static final String USER_SAVE_SETTING_KEY = "user.storelast";
	public static final String CELL_UPLOAD_SETTING_KEY = "data.cellular.upload";
	public static final String PLOT_MODE_SETTING_KEY = "plot.default.mode";
	public static final String PLOT_INTERVAL_SETTING_KEY = "plot.interval";
	public static final String LAST_USER_SETTING_KEY = "user.lastuser.id";
	public static final String LOCATION_BEACON_SETTING_KEY = "location.sendbeacon";
	public static final String PRECACHE_HELP_SETTING_KEY = "survey.precachehelp";
	public static final String SERVER_SETTING_KEY = "upload.server";

	/**
	 * index values into string arrays
	 */
	public static final int UPLOAD_DATA_ALLWAYS_IDX = 0;
	public static final int UPLOAD_DATA_ONLY_IDX = 1;
	public static final int UPLOAD_NEVER_IDX = 2;

	public static final int PRECACHE_HELP_ALLWAYS_IDX = 0;
	public static final int PRECACHE_HELP_WIFI_ONLY_IDX = 1;
	public static final int PRECACHE_HELP_NEVER_IDX = 2;

	/**
	 * intents
	 */
	public static final String DATA_AVAILABLE_INTENT = "com.gallatinsystems.survey.device.DATA_SUBMITTED";
	public static final String PRECACHE_INTENT = "com.gallatinsystems.survey.device.PRECACHE";
	public static final String GPS_STATUS_INTENT = "com.eclipsim.gpsstatus.VIEW";
	public static final String BARCODE_SCAN_INTENT = "com.google.zxing.client.android.SCAN";

	/**
	 * zxing barcode extra keys
	 */
	public static final String BARCODE_CONTENT = "SCAN_RESULT";
	public static final String BARCODE_FORMAT = "SCAN_RESULT_FORMAT";

	/**
	 * survey respondent statuses
	 */
	public static final String SAVED_STATUS = "SAVED";
	public static final String DELETED_STATUS = "DELETED";
	public static final String SUBMITTED_STATUS = "SUBMITTED";
	public static final String CURRENT_STATUS = "CURRENT";

	/**
	 * language codes
	 */
	public static final String ENGLISH_CODE = "en";

	/**
	 * html colors
	 */
	public static final String WHITE_COLOR = "white";
	public static final String BLACK_COLOR = "black";

	/**
	 * sub-activty options
	 */
	public static final String STANDALONE_MODE = "standalone";
	public static final String SURVEY_RESULT_MODE = "surveyresult";

	/**
	 * recognized help activities
	 */
	@SuppressWarnings("unchecked")
	public static final HashMap<String, Class> HELP_ACTIVITIES = new HashMap<String, Class>() {	
		private static final long serialVersionUID = -6196886832065440000L;
		{
			put("waterflowcalculator", WaterflowCalculatorActivity.class);
		}
	};

	/**
	 * prevent instantiation
	 */
	private ConstantUtil() {
	}

}
