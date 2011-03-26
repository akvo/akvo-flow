package com.gallatinsystems.survey.device.util;

import java.util.HashMap;

import com.gallatinsystems.survey.device.activity.NearbyItemActivity;
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
	public static final String SD_CARD_ROOT = "/sdcard/";
	public static final String INTERNAL_ROOT = "/data/data/";
	public static final String DATA_DIR = "fieldsurvey/data/";
	public static final String FILE_SURVEY_LOCATION_TYPE = "file";
	public static final String ARCHIVE_SUFFIX = ".zip";
	public static final String XML_SUFFIX = ".xml";
	public static final String BOOTSTRAP_DIR = "fieldsurvey/bootstrap";
	public static final String BOOTSTRAP_DB_FILE = "dbinstructions.sql";
	public static final String PROCESSED_OK_SUFFIX = ".processed";
	public static final String PROCESSED_ERROR_SUFFIX = ".error";
	public static final String BOOTSTRAP_ROLLBACK_FILE = "rollback.sql";
	public static final String STACKTRACE_DIR = "fieldsurvey/stacktrace/";
	public static final String STACKTRACE_FILENAME = "err-";
	public static final String STACKTRACE_SUFFIX = ".stacktrace";

	/**
	 * survey file locations
	 */
	public static final String RESOURCE_LOCATION = "res";
	public static final String FILE_LOCATION = "sdcard";
	public static final String SURVEY_DEFAULT_LANG = "english";

	/**
	 * status related constants
	 */
	public static final String COMPLETE_STATUS = "Complete";
	public static final String SENT_STATUS = "Sent";
	public static final String RUNNING_STATUS = "Running";
	public static final String IN_PROGRESS_STATUS = "In Progress";
	public static final String QUEUED_STATUS = "Queued";
	public static final String FAILED_STATUS = "Failed";

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
	public static final String STRENGTH_QUESTION_TYPE = "strength";
	public static final String HEADING_QUESTION_TYPE = "heading";

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
	 * scoring types
	 */
	public static final String NUMERIC_SCORING = "numeric";
	public static final String TEXT_MATCH_SCORING = "textmatch";

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
	 * notification types
	 */
	public static final String PROGRESS = "PROGRESS";
	public static final String FILE_COMPLETE = "FILE_COMPLETE";
	public static final String ERROR = "ERROR";

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
	public static final String POINTS_KEY = "points";
	public static final String QUESTION_ID_KEY = "questionId";
	public static final String QUESTION_TYPE_KEY = "questionType";

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
	public static final String PRECACHE_SETTING_KEY = "survey.precachehelp";
	public static final String SERVER_SETTING_KEY = "upload.server";
	public static final String SCREEN_ON_KEY = "screen.keepon";
	public static final String PRECACHE_POINT_COUNTRY_KEY = "precache.points.countries";
	public static final String PRECACHE_POINT_LIMIT_KEY = "precache.points.limit";
	public static final String DEVICE_IDENT_KEY = "device.identifier";
	public static final String SURVEY_TEXT_SIZE_KEY = "survey.textsize";

	/**
	 * settings values
	 */
	public static final String LARGE_TXT = "LARGE";
	public static final String NORMAL_TXT = "NORMAL";
	/**
	 * index values into string arrays
	 */
	public static final int UPLOAD_DATA_ALLWAYS_IDX = 0;
	public static final int UPLOAD_DATA_ONLY_IDX = 1;
	public static final int UPLOAD_NEVER_IDX = 2;

	public static final int PRECACHE_ALWAYS_IDX = 0;
	public static final int PRECACHE_WIFI_ONLY_IDX = 1;
	public static final int PRECACHE_NEVER_IDX = 2;

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
	 * sub-activity options
	 */
	public static final String STANDALONE_MODE = "standalone";
	public static final String SURVEY_RESULT_MODE = "surveyresult";

	/**
	 * "code" to prevent unauthorized use of administrative settings/preferences
	 */
	public static final String ADMIN_AUTH_CODE = "12345";

	/**
	 * property file keys
	 */
	public static final String S3_ID = "s3Id";
	public static final String DATA_S3_POLICY = "dataS3Policy";
	public static final String DATA_S3_SIG = "dataS3Sig";
	public static final String IMAGE_S3_POLICY = "imageS3Policy";
	public static final String IMAGE_S3_SIG = "imageS3Sig";
	public static final String DATA_UPLOAD_URL = "dataUploadUrl";
	public static final String SERVER_BASE = "serverBase";
	public static final String SURVEY_S3_URL = "surveyS3Url";
	public static final String USE_INTERNAL_STORAGE = "useInternalStorage";
	public static final String INCLUDE_OPTIONAL_ICONS = "includeOptionalIcons";
	public static final String PROMPT_ON_OPT_CHANGE ="promptOnOptionChange";

	/**
	 * resource related constants
	 */
	public static final String RESOURCE_PACKAGE = "com.gallatinsystems.survey.device";
	public static final String RAW_RESOURCE = "raw";

	/**
	 * recognized help activities
	 */
	@SuppressWarnings("unchecked")
	public static final HashMap<String, Class> HELP_ACTIVITIES = new HashMap<String, Class>() {
		private static final long serialVersionUID = -6196886832065440000L;
		{
			put("waterflowcalculator", WaterflowCalculatorActivity.class);
			put("nearbypoint", NearbyItemActivity.class);
		}
	};

	/**
	 * prevent instantiation
	 */
	private ConstantUtil() {
	}

}
