package com.gallatinsystems.survey.device.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gallatinsystems.survey.device.domain.QuestionResponse;
import com.gallatinsystems.survey.device.domain.Survey;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * Database class for the survey db. It can create/upgrade the database as well
 * as select/insert/update survey responses.
 * 
 * TODO: break this up into separate DAOs
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyDbAdapter {

	public static final String QUESTION_FK_COL = "question_id";
	public static final String ANSWER_COL = "answer_value";
	public static final String ANSWER_TYPE_COL = "answer_type";
	public static final String SURVEY_RESPONDENT_ID_COL = "survey_respondent_id";
	public static final String RESP_ID_COL = "survey_response_id";
	public static final String SURVEY_FK_COL = "survey_id";
	public static final String PK_ID_COL = "_id";
	public static final String USER_FK_COL = "user_id";
	public static final String DISP_NAME_COL = "display_name";
	public static final String EMAIL_COL = "email";
	public static final String SUBMITTED_FLAG_COL = "submitted_flag";
	public static final String SUBMITTED_DATE_COL = "submitted_date";
	public static final String DELIVERED_DATE_COL = "delivered_date";
	public static final String CREATED_DATE_COL = "created_date";
	public static final String PLOT_FK_COL = "plot_id";
	public static final String LAT_COL = "lat";
	public static final String LON_COL = "lon";
	public static final String ELEVATION_COL = "elevation";
	public static final String DESC_COL = "description";
	public static final String STATUS_COL = "status";
	public static final String VERSION_COL = "version";
	public static final String TYPE_COL = "type";
	public static final String LOCATION_COL = "location";
	public static final String FILENAME_COL = "filename";
	public static final String KEY_COL = "key";
	public static final String VALUE_COL = "value";
	public static final String DELETED_COL = "deleted_flag";
	public static final String MEDIA_SENT_COL = "media_sent_flag";
	public static final String HELP_DOWNLOADED_COL = "help_downloaded_flag";
	public static final String LANGUAGE_COL = "language";

	private static final String TAG = "SurveyDbAdapter";
	private DatabaseHelper databaseHelper;
	private SQLiteDatabase database;

	/**
	 * Database creation sql statement
	 */
	private static final String SURVEY_TABLE_CREATE = "create table survey (_id integer primary key, "
			+ "display_name text not null, version real, type text, location text, filename text, language, help_downloaded_flag text, deleted_flag text);";

	private static final String SURVEY_RESPONDENT_CREATE = "create table survey_respondent (survey_respondent_id integer primary key autoincrement, "
			+ "survey_id integer not null, submitted_flag text, submitted_date text,delivered_date text, user_id integer, media_sent_flag text, status text);";

	private static final String SURVEY_RESPONSE_CREATE = "create table survey_response (survey_response_id integer primary key autoincrement, "
			+ " survey_respondent_id integer not null, question_id text not null, answer_value text not null, answer_type text not null);";

	private static final String USER_TABLE_CREATE = "create table user (_id integer primary key autoincrement, display_name text not null, email text not null);";

	private static final String PLOT_TABLE_CREATE = "create table plot (_id integer primary key autoincrement, display_name text, description text, created_date text, user_id integer, status text);";

	private static final String PLOT_POINT_TABLE_CREATE = "create table plot_point (_id integer primary key autoincrement, plot_id integer not null, lat text, lon text, elevation text, created_date text);";

	private static final String PREFERENCES_TABLE_CREATE = "create table preferences (key text primary key, value text);";

	private static final String[] DEFAULT_INSERTS = new String[] {
			"insert into survey values(999991,'Community Waterpoint Survey', 1.0,'Survey','res','testsurvey','english','N','N')",
			"insert into survey values(999992,'Houshold Survey', 1.0,'Survey','res','testsurvey','english','N','N')",
			"insert into survey values(999993,'Public Institution Survey', 1.0,'Survey','res','testsurvey','english','N','N')",
			"insert into survey values(999994,'Mapping', 1.0,'Mapping','res','mappingsurvey','english','N','N')",
			"insert into preferences values('survey.language','0')",
			"insert into preferences values('user.storelast','false')",
			"insert into preferences values('data.cellular.upload','0')",
			"insert into preferences values('plot.default.mode','manual')",
			"insert into preferences values('plot.interval','60000')",
			"insert into preferences values('user.lastuser.id','')",
			"insert into preferences values('location.sendbeacon','true')",
			"insert into preferences values('survey.precachehelp','0')",
			"insert into preferences values('upload.server','0')" };

	private static final String DATABASE_NAME = "surveydata";
	private static final String SURVEY_TABLE = "survey";
	private static final String RESPONDENT_TABLE = "survey_respondent";
	private static final String RESPONSE_TABLE = "survey_response";
	private static final String USER_TABLE = "user";
	private static final String PLOT_TABLE = "plot";
	private static final String PLOT_POINT_TABLE = "plot_point";
	private static final String PREFERENCES_TABLE = "preferences";

	private static final String RESPONSE_JOIN = "survey_respondent LEFT OUTER JOIN survey_response ON (survey_respondent.survey_respondent_id = survey_response.survey_respondent_id) LEFT OUTER JOIN user ON (user._id = survey_respondent.user_id)";
	private static final String PLOT_JOIN = "plot LEFT OUTER JOIN plot_point ON (plot._id = plot_point.plot_id) LEFT OUTER JOIN user ON (user._id = plot.user_id)";

	private static final int DATABASE_VERSION = 42;

	private final Context context;

	/**
	 * Helper class for creating the database tables and loading reference data
	 * 
	 * It is declared with package scope for VM optimizations
	 * 
	 * @author Christopher Fagiani
	 * 
	 */
	static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(USER_TABLE_CREATE);
			db.execSQL(SURVEY_TABLE_CREATE);
			db.execSQL(SURVEY_RESPONDENT_CREATE);
			db.execSQL(SURVEY_RESPONSE_CREATE);
			db.execSQL(PLOT_TABLE_CREATE);
			db.execSQL(PLOT_POINT_TABLE_CREATE);
			db.execSQL(PREFERENCES_TABLE_CREATE);
			for (int i = 0; i < DEFAULT_INSERTS.length; i++) {
				db.execSQL(DEFAULT_INSERTS[i]);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + RESPONSE_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + RESPONDENT_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + SURVEY_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + PLOT_POINT_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + PLOT_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + PREFERENCES_TABLE);
			onCreate(db);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public SurveyDbAdapter(Context ctx) {
		this.context = ctx;
	}

	/**
	 * Open or create the db
	 * 
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public SurveyDbAdapter open() throws SQLException {
		databaseHelper = new DatabaseHelper(context);
		database = databaseHelper.getWritableDatabase();
		return this;
	}

	/**
	 * close the db
	 */
	public void close() {
		databaseHelper.close();
	}

	/**
	 * Create a new survey using the title and body provided. If the survey is
	 * successfully created return the new id, otherwise return a -1 to indicate
	 * failure.
	 * 
	 * @param name
	 *            survey name
	 * 
	 * @return rowId or -1 if failed
	 */
	public long createSurvey(String name) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DISP_NAME_COL, name);
		initialValues.put(HELP_DOWNLOADED_COL, "N");
		return database.insert(SURVEY_TABLE, null, initialValues);
	}

	/**
	 * returns a cursor that lists all unsent (sentFlag = false) survey data
	 * 
	 * @return
	 */
	public Cursor fetchUnsentData() {
		Cursor cursor = database.query(RESPONSE_JOIN, new String[] {
				RESPONDENT_TABLE + "." + SURVEY_RESPONDENT_ID_COL, RESP_ID_COL,
				ANSWER_COL, ANSWER_TYPE_COL, QUESTION_FK_COL, DISP_NAME_COL,
				EMAIL_COL, DELIVERED_DATE_COL, SUBMITTED_DATE_COL,
				RESPONDENT_TABLE + "." + SURVEY_FK_COL, }, SUBMITTED_FLAG_COL
				+ "= 'true' AND (" + DELIVERED_DATE_COL + " is null OR "
				+ MEDIA_SENT_COL + " <> 'true')", null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	/**
	 * marks the data as submitted in the respondent table (submittedFlag =
	 * true) thereby making it ready for transmission
	 * 
	 * @param respondentId
	 */
	public void submitResponses(String respondentId) {
		ContentValues vals = new ContentValues();
		vals.put(SUBMITTED_FLAG_COL, "true");
		vals.put(SUBMITTED_DATE_COL, System.currentTimeMillis());
		database.update(RESPONDENT_TABLE, vals, SURVEY_RESPONDENT_ID_COL + "= "
				+ respondentId, null);
	}

	/**
	 * updates the respondent table by recording the sent date stamp
	 * 
	 * @param idList
	 */
	public void markDataAsSent(HashSet<String> idList, String mediaSentFlag) {
		if (idList != null) {
			ContentValues updatedValues = new ContentValues();
			updatedValues.put(DELIVERED_DATE_COL, System.currentTimeMillis()
					+ "");
			updatedValues.put(MEDIA_SENT_COL, mediaSentFlag);
			// enhanced FOR ok here since we're dealing with an implicit
			// iterator anyway
			for (String id : idList) {
				if (database.update(RESPONDENT_TABLE, updatedValues,
						SURVEY_RESPONDENT_ID_COL + " = ?", new String[] { id }) < 1) {
					Log.e(TAG,
							"Could not update record for Survey_respondent_id "
									+ id);
				}
			}
		}
	}

	/**
	 * updates the status of a survey response to the string passed in
	 * 
	 * @param surveyRespondentId
	 * @param status
	 */
	public void updateSurveyStatus(String surveyRespondentId, String status) {
		if (surveyRespondentId != null) {
			ContentValues updatedValues = new ContentValues();
			updatedValues.put(STATUS_COL, status);
			if (database.update(RESPONDENT_TABLE, updatedValues,
					SURVEY_RESPONDENT_ID_COL + " = ?",
					new String[] { surveyRespondentId }) < 1) {
				Log.e(TAG, "Could not update status for Survey_respondent_id "
						+ surveyRespondentId);
			}

		}

	}

	/**
	 * returns a cursor listing all users
	 * 
	 * @return
	 */
	public Cursor listUsers() {
		Cursor cursor = database.query(USER_TABLE, new String[] { PK_ID_COL,
				DISP_NAME_COL, EMAIL_COL }, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	/**
	 * retrieves a user by ID
	 * 
	 * @param id
	 * @return
	 */
	public Cursor findUser(Long id) {
		Cursor cursor = database.query(USER_TABLE, new String[] { PK_ID_COL,
				DISP_NAME_COL, EMAIL_COL }, PK_ID_COL + "=?", new String[] { id
				.toString() }, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	/**
	 * if the ID is populated, this will update a user record. Otherwise, it
	 * will be inserted
	 * 
	 * @param id
	 * @param name
	 * @param email
	 * @return
	 */
	public long createOrUpdateUser(Long id, String name, String email) {
		ContentValues initialValues = new ContentValues();
		Long idVal = id;
		initialValues.put(DISP_NAME_COL, name);
		initialValues.put(EMAIL_COL, email);

		if (idVal == null) {
			idVal = database.insert(USER_TABLE, null, initialValues);
		} else {
			if (database.update(USER_TABLE, initialValues, PK_ID_COL + "=?",
					new String[] { idVal.toString() }) > 0) {
			}
		}
		return idVal;
	}

	/**
	 * Return a Cursor over the list of all responses for a particular survey
	 * respondent
	 * 
	 * @return Cursor over all responses
	 */
	public Cursor fetchResponsesByRespondent(String respondentID) {
		return database.query(RESPONSE_TABLE, new String[] { RESP_ID_COL,
				QUESTION_FK_COL, ANSWER_COL, ANSWER_TYPE_COL,
				SURVEY_RESPONDENT_ID_COL }, SURVEY_RESPONDENT_ID_COL + "=?",
				new String[] { respondentID }, null, null, null);
	}

	/**
	 * if the response has the ID populated, it will update the database row,
	 * otherwise it will be inserted
	 * 
	 * @param response
	 * @return
	 */
	public long createOrUpdateSurveyResponse(QuestionResponse response) {
		long id = -1;
		ContentValues initialValues = new ContentValues();
		initialValues.put(ANSWER_COL, response.getValue());
		initialValues.put(ANSWER_TYPE_COL, response.getType());
		initialValues.put(QUESTION_FK_COL, response.getQuestionId());
		initialValues.put(SURVEY_RESPONDENT_ID_COL, response.getRespondentId());
		if (response.getId() == null) {
			id = database.insert(RESPONSE_TABLE, null, initialValues);
		} else {
			if (database.update(RESPONSE_TABLE, initialValues, RESP_ID_COL
					+ "=?", new String[] { response.getId().toString() }) > 0) {
				id = response.getId();
			}
		}
		return id;
	}

	/**
	 * this method will get the max survey respondent ID that has an unsubmitted
	 * survey or, if none exists, will create a new respondent
	 * 
	 * @param surveyId
	 * @return
	 */
	public long createOrLoadSurveyRespondent(String surveyId, String userId) {
		Cursor results = database.query(RESPONDENT_TABLE, new String[] { "max("
				+ SURVEY_RESPONDENT_ID_COL + ")" }, SUBMITTED_FLAG_COL
				+ "='false' and " + SURVEY_FK_COL + "=? and " + STATUS_COL
				+ " =?",
				new String[] { surveyId, ConstantUtil.CURRENT_STATUS }, null,
				null, null);
		long id = -1;
		if (results != null && results.getCount() > 0) {
			results.moveToFirst();
			id = results.getLong(0);
			results.close();
		}
		if (id <= 0) {
			id = createSurveyRespondent(surveyId, userId);
		}
		return id;
	}

	/**
	 * creates a new unsubmitted survey respondent record
	 * 
	 * @param surveyId
	 * @return
	 */
	public long createSurveyRespondent(String surveyId, String userId) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(SURVEY_FK_COL, surveyId);
		initialValues.put(SUBMITTED_FLAG_COL, "false");
		initialValues.put(USER_FK_COL, userId);
		initialValues.put(STATUS_COL, ConstantUtil.CURRENT_STATUS);
		return database.insert(RESPONDENT_TABLE, null, initialValues);
	}

	/**
	 * creates a new plot point in the database for the plot and coordinates
	 * sent in
	 * 
	 * @param plotId
	 * @param lat
	 * @param lon
	 * @return
	 */
	public long savePlotPoint(String plotId, String lat, String lon,
			double currentElevation) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(PLOT_FK_COL, plotId);
		initialValues.put(LAT_COL, lat);
		initialValues.put(LON_COL, lon);
		initialValues.put(ELEVATION_COL, currentElevation);
		initialValues.put(CREATED_DATE_COL, System.currentTimeMillis());
		return database.insert(PLOT_POINT_TABLE, null, initialValues);
	}

	/**
	 * returns a cursor listing all plots with the status passed in or all plots
	 * if status is null
	 * 
	 * @return
	 */
	public Cursor listPlots(String status) {
		Cursor cursor = database.query(PLOT_TABLE, new String[] { PK_ID_COL,
				DISP_NAME_COL, DESC_COL, CREATED_DATE_COL, STATUS_COL },
				status == null ? null : STATUS_COL + " = ?",
				status == null ? null : new String[] { status }, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	/**
	 * retrieves a plot by ID
	 * 
	 * @param id
	 * @return
	 */
	public Cursor findPlot(Long id) {
		Cursor cursor = database.query(PLOT_TABLE, new String[] { PK_ID_COL,
				DISP_NAME_COL, DESC_COL, CREATED_DATE_COL, STATUS_COL },
				PK_ID_COL + "=?", new String[] { id.toString() }, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	/**
	 * if the ID is populated, this will update a plot record. Otherwise, it
	 * will be inserted
	 * 
	 * @param id
	 * @param name
	 * @param email
	 * @return
	 */
	public long createOrUpdatePlot(Long id, String name, String desc,
			String userId) {
		ContentValues initialValues = new ContentValues();
		Long idVal = id;
		initialValues.put(DISP_NAME_COL, name);
		initialValues.put(DESC_COL, desc);
		initialValues.put(CREATED_DATE_COL, System.currentTimeMillis());
		initialValues.put(USER_FK_COL, userId);
		initialValues.put(STATUS_COL, ConstantUtil.IN_PROGRESS_STATUS);

		if (idVal == null) {
			idVal = database.insert(PLOT_TABLE, null, initialValues);
		} else {
			if (database.update(PLOT_TABLE, initialValues, PK_ID_COL + "=?",
					new String[] { idVal.toString() }) > 0) {
			}
		}
		return idVal;
	}

	/**
	 * retrieves all the points for a given plot
	 * 
	 * @param plotId
	 * @return
	 */
	public Cursor listPlotPoints(String plotId, String afterTime) {

		Cursor cursor = database
				.query(PLOT_POINT_TABLE, new String[] { PK_ID_COL, LAT_COL,
						LON_COL, CREATED_DATE_COL }, PLOT_FK_COL + " = ? and "
						+ CREATED_DATE_COL + " > ?", new String[] { plotId,
						afterTime != null ? afterTime : "0" }, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	/**
	 * updates the status of a plot in the db
	 * 
	 * @param plotId
	 * @param status
	 */
	public long updatePlotStatus(String plotId, String status) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(STATUS_COL, status);
		return database.update(PLOT_TABLE, initialValues, PK_ID_COL + " = ?",
				new String[] { plotId });
	}

	/**
	 * updates the status of all the plots identified by the ids sent in to the
	 * value of status
	 * 
	 * @param idList
	 * @param status
	 */
	public void updatePlotStatus(HashSet<String> idList, String status) {
		if (idList != null) {
			ContentValues updatedValues = new ContentValues();
			updatedValues.put(STATUS_COL, status);
			// enhanced FOR ok here since we're dealing with an implicit
			// iterator anyway
			for (String id : idList) {
				if (updatePlotStatus(id, status) < 1) {
					Log.e(TAG, "Could not update plot status for plot " + id);
				}
			}
		}
	}

	/**
	 * lists all plot points for plots that are in the COMPLETED state
	 * 
	 * @return
	 */
	public Cursor listCompletePlotPoints() {
		Cursor cursor = database
				.query(PLOT_JOIN, new String[] {
						PLOT_TABLE + "." + PK_ID_COL + " as plot_id",
						PLOT_TABLE + "." + DISP_NAME_COL,
						PLOT_POINT_TABLE + "." + PK_ID_COL, LAT_COL, LON_COL,
						ELEVATION_COL,
						PLOT_POINT_TABLE + "." + CREATED_DATE_COL }, STATUS_COL
						+ "= ?", new String[] { ConstantUtil.COMPLETE_STATUS },
						null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	/**
	 * deletes the plot_point row denoted by the ID passed in
	 * 
	 * @param id
	 */
	public void deletePlotPoint(String id) {
		database.delete(PLOT_POINT_TABLE, PK_ID_COL + " = ?",
				new String[] { id });
	}

	/**
	 * returns a list of survey objects that are out of date (missing from the
	 * db or with a lower version number). If a survey is present but marked as
	 * deleted, it will not be listed as out of date (and thus won't be updated)
	 * 
	 * @param surveys
	 * @return
	 */
	public ArrayList<Survey> checkSurveyVersions(ArrayList<Survey> surveys) {
		ArrayList<Survey> outOfDateSurveys = new ArrayList<Survey>();
		for (int i = 0; i < surveys.size(); i++) {
			Cursor cursor = database.query(SURVEY_TABLE,
					new String[] { PK_ID_COL },
					PK_ID_COL + " = ? and (" + VERSION_COL + " >= ? or "
							+ DELETED_COL + " = ?)", new String[] {
							surveys.get(i).getId(),
							surveys.get(i).getVersion() + "",
							ConstantUtil.IS_DELETED }, null, null, null);

			if (cursor == null || cursor.getCount() <= 0) {
				outOfDateSurveys.add(surveys.get(i));
			}
			if (cursor != null) {
				cursor.close();
			}
		}
		return outOfDateSurveys;
	}

	/**
	 * updates the survey table by recording the help download flag
	 * 
	 * @param idList
	 */
	public void markSurveyHelpDownloaded(String surveyId, boolean isDownloaded) {
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(HELP_DOWNLOADED_COL, isDownloaded ? "Y" : "N");

		if (database.update(SURVEY_TABLE, updatedValues, PK_ID_COL + " = ?",
				new String[] { surveyId }) < 1) {
			Log.e(TAG, "Could not update record for Survey " + surveyId);
		}
	}

	/**
	 * updates a survey in the db and resets the deleted flag to "N"
	 * 
	 * @param survey
	 * @return
	 */
	public void saveSurvey(Survey survey) {
		Cursor cursor = database.query(SURVEY_TABLE,
				new String[] { PK_ID_COL }, PK_ID_COL + " = ?",
				new String[] { survey.getId(), }, null, null, null);
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(PK_ID_COL, survey.getId());
		updatedValues.put(VERSION_COL, survey.getVersion());
		updatedValues.put(TYPE_COL, survey.getType());
		updatedValues.put(LOCATION_COL, survey.getLocation());
		updatedValues.put(FILENAME_COL, survey.getFileName());
		updatedValues.put(DISP_NAME_COL, survey.getName());
		updatedValues.put(LANGUAGE_COL, survey.getLanguage() != null ? survey
				.getLanguage().toLowerCase() : survey.getLanguage());
		updatedValues.put(HELP_DOWNLOADED_COL, survey.isHelpDownloaded() ? "Y"
				: "N");
		updatedValues.put(DELETED_COL, ConstantUtil.NOT_DELETED);

		if (cursor != null && cursor.getCount() > 0) {
			// if we found an item, it's an update, otherwise, it's an insert
			database.update(SURVEY_TABLE, updatedValues, PK_ID_COL + " = ?",
					new String[] { survey.getId() });
		} else {
			database.insert(SURVEY_TABLE, null, updatedValues);
		}

		if (cursor != null) {
			cursor.close();
		}
	}

	/**
	 * Gets a single survey from the db using its primary key
	 */
	public Survey findSurvey(String surveyId) {
		Survey survey = null;
		Cursor cursor = database.query(SURVEY_TABLE, new String[] { PK_ID_COL,
				DISP_NAME_COL, LOCATION_COL, FILENAME_COL, TYPE_COL,
				LANGUAGE_COL, HELP_DOWNLOADED_COL }, PK_ID_COL + " = ?",
				new String[] { surveyId }, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				survey = new Survey();
				survey.setId(surveyId);
				survey.setName(cursor.getString(cursor
						.getColumnIndexOrThrow(DISP_NAME_COL)));
				survey.setLocation(cursor.getString(cursor
						.getColumnIndexOrThrow(LOCATION_COL)));
				survey.setFileName(cursor.getString(cursor
						.getColumnIndexOrThrow(FILENAME_COL)));
				survey.setType(cursor.getString(cursor
						.getColumnIndexOrThrow(TYPE_COL)));
				survey.setHelpDownloaded(cursor.getString(cursor
						.getColumnIndexOrThrow(HELP_DOWNLOADED_COL)));
				survey.setLanguage(cursor.getString(cursor
						.getColumnIndexOrThrow(LANGUAGE_COL)));
			}
			cursor.close();
		}

		return survey;
	}

	/**
	 * Lists all non-deleted surveys from the database
	 */
	public ArrayList<Survey> listSurveys(String language) {
		ArrayList<Survey> surveys = new ArrayList<Survey>();
		String whereClause = DELETED_COL + " <> ?";
		String[] whereParams = null;
		if (language != null) {
			whereClause += " and " + LANGUAGE_COL + " = ?";
			whereParams = new String[] { ConstantUtil.IS_DELETED,
					language.toLowerCase().trim() };
		} else {
			whereParams = new String[] { ConstantUtil.IS_DELETED };
		}
		Cursor cursor = database.query(SURVEY_TABLE, new String[] { PK_ID_COL,
				DISP_NAME_COL, LOCATION_COL, FILENAME_COL, TYPE_COL,
				LANGUAGE_COL, HELP_DOWNLOADED_COL }, whereClause, whereParams,
				null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					Survey survey = new Survey();
					survey.setId(cursor.getString(cursor
							.getColumnIndexOrThrow(PK_ID_COL)));
					survey.setName(cursor.getString(cursor
							.getColumnIndexOrThrow(DISP_NAME_COL)));
					survey.setLocation(cursor.getString(cursor
							.getColumnIndexOrThrow(LOCATION_COL)));
					survey.setFileName(cursor.getString(cursor
							.getColumnIndexOrThrow(FILENAME_COL)));
					survey.setType(cursor.getString(cursor
							.getColumnIndexOrThrow(TYPE_COL)));
					survey.setHelpDownloaded(cursor.getString(cursor
							.getColumnIndexOrThrow(HELP_DOWNLOADED_COL)));
					survey.setLanguage(cursor.getString(cursor
							.getColumnIndexOrThrow(LANGUAGE_COL)));
					surveys.add(survey);
				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		return surveys;
	}

	/**
	 * marks a survey record identified by the ID passed in as deleted.
	 * 
	 * @param surveyId
	 */
	public void deleteSurvey(String surveyId) {
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(DELETED_COL, ConstantUtil.IS_DELETED);
		database.update(SURVEY_TABLE, updatedValues, PK_ID_COL + " = ?",
				new String[] { surveyId });
	}

	/**
	 * returns the value of a single setting identified by the key passed in
	 */
	public String findPreference(String key) {
		String value = null;
		Cursor cursor = database.query(PREFERENCES_TABLE, new String[] {
				KEY_COL, VALUE_COL }, KEY_COL + " = ?", new String[] { key },
				null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				value = cursor.getString(cursor
						.getColumnIndexOrThrow(VALUE_COL));
			}
			cursor.close();
		}
		return value;
	}

	/**
	 * Lists all settings from the database
	 */
	public HashMap<String, String> listPreferences() {
		HashMap<String, String> settings = new HashMap<String, String>();
		Cursor cursor = database.query(PREFERENCES_TABLE, new String[] {
				KEY_COL, VALUE_COL }, null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					settings
							.put(cursor.getString(cursor
									.getColumnIndexOrThrow(KEY_COL)), cursor
									.getString(cursor
											.getColumnIndexOrThrow(VALUE_COL)));
				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		return settings;
	}

	/**
	 * persists setting to the db
	 * 
	 * @param surveyId
	 */
	public void savePreference(String key, String value) {
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(VALUE_COL, value);
		database.update(PREFERENCES_TABLE, updatedValues, KEY_COL + " = ?",
				new String[] { key });
	}

	/**
	 * deletes all the surveys from the database surveys;
	 */
	public void deleteAllSurveys() {
		database.delete(SURVEY_TABLE, null, null);
	}
}
