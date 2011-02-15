package com.gallatinsystems.survey.device.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gallatinsystems.survey.device.domain.FileTransmission;
import com.gallatinsystems.survey.device.domain.PointOfInterest;
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
	public static final String UPDATED_DATE_COL = "updated_date";
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
	public static final String SAVED_DATE_COL = "saved_date";
	public static final String COUNTRY_COL = "country";
	public static final String PROP_NAME_COL = "property_names";
	public static final String PROP_VAL_COL = "property_values";
	public static final String INCLUDE_FLAG_COL = "include_flag";
	public static final String SCORED_VAL_COL = "scored_val";
	public static final String STRENGTH_COL = "strength";
	public static final String TRANS_START_COL = "trans_start_date";
	public static final String EXPORTED_FLAG_COL = "exported_flag";

	private static final String TAG = "SurveyDbAdapter";
	private DatabaseHelper databaseHelper;
	private SQLiteDatabase database;

	/**
	 * Database creation sql statement
	 */
	private static final String SURVEY_TABLE_CREATE = "create table survey (_id integer primary key, "
			+ "display_name text not null, version real, type text, location text, filename text, language, help_downloaded_flag text, deleted_flag text);";

	private static final String SURVEY_RESPONDENT_CREATE = "create table survey_respondent (_id integer primary key autoincrement, "
			+ "survey_id integer not null, submitted_flag text, submitted_date text,delivered_date text, user_id integer, media_sent_flag text, status text, saved_date long);";

	private static final String SURVEY_RESPONSE_CREATE = "create table survey_response (survey_response_id integer primary key autoincrement, "
			+ " survey_respondent_id integer not null, question_id text not null, answer_value text not null, answer_type text not null, include_flag text not null, scored_val text, strength text);";

	private static final String USER_TABLE_CREATE = "create table user (_id integer primary key autoincrement, display_name text not null, email text not null);";

	private static final String PLOT_TABLE_CREATE = "create table plot (_id integer primary key autoincrement, display_name text, description text, created_date text, user_id integer, status text);";

	private static final String PLOT_POINT_TABLE_CREATE = "create table plot_point (_id integer primary key autoincrement, plot_id integer not null, lat text, lon text, elevation text, created_date text);";

	private static final String PREFERENCES_TABLE_CREATE = "create table preferences (key text primary key, value text);";

	private static final String POINT_OF_INTEREST_TABLE_CREATE = "create table point_of_interest (_id integer primary key, country text, display_name text, lat real, lon real, property_names text, property_values text, type text, updated_date integer);";

	private static final String TRANSMISSION_HISTORY_TABLE_CREATE = "create table transmission_history (_id integer primary key, survey_respondent_id integer not null, status text, filename text, trans_start_date long, delivered_date long);";

	private static final String[] DEFAULT_INSERTS = new String[] {
			// "insert into survey values(999991,'Sample Survey', 1.0,'Survey','res','testsurvey','english','N','N')",
			// "insert into survey values(1039101,'Houshold Interview', 1.0,'Survey','res','hh1039101','english','N','N')",
			// "insert into survey values(1062135,'Public Institution', 1.0,'Survey','res','pi1062135','english','N','N')",
			// "insert into survey values(1086117,'CommunityWaterPoint', 1.0,'Survey','res','cwp1086117','english','N','N')",

			// "insert into survey values(943186,'Community Water Point', 1.0,'Survey','res','cw943186','english','N','N')",
			// "insert into survey values(1007024,'Household Interview', 1.0,'Survey','res','hh1007024','english','N','N')",
			// "insert into survey values(971189,'Public Institution', 1.0,'Survey','res','pi971189','english','N','N')",

			"insert into preferences values('survey.language','0')",
			"insert into preferences values('user.storelast','false')",
			"insert into preferences values('data.cellular.upload','0')",
			"insert into preferences values('plot.default.mode','manual')",
			"insert into preferences values('plot.interval','60000')",
			"insert into preferences values('user.lastuser.id','')",
			"insert into preferences values('location.sendbeacon','true')",
			"insert into preferences values('survey.precachehelp','1')",
			"insert into preferences values('upload.server','0')",
			"insert into preferences values('screen.keepon','true')",
			"insert into preferences values('precache.points.countries','2')",
			"insert into preferences values('precache.points.limit','200')",
			"insert into preferences values('survey.textsize','LARGE')" };

	private static final String DATABASE_NAME = "surveydata";
	private static final String SURVEY_TABLE = "survey";
	private static final String RESPONDENT_TABLE = "survey_respondent";
	private static final String RESPONSE_TABLE = "survey_response";
	private static final String USER_TABLE = "user";
	private static final String PLOT_TABLE = "plot";
	private static final String PLOT_POINT_TABLE = "plot_point";
	private static final String PREFERENCES_TABLE = "preferences";
	private static final String POINT_OF_INTEREST_TABLE = "point_of_interest";
	private static final String TRANSMISSION_HISTORY_TABLE = "transmission_history";

	private static final String RESPONSE_JOIN = "survey_respondent LEFT OUTER JOIN survey_response ON (survey_respondent._id = survey_response.survey_respondent_id) LEFT OUTER JOIN user ON (user._id = survey_respondent.user_id)";
	private static final String PLOT_JOIN = "plot LEFT OUTER JOIN plot_point ON (plot._id = plot_point.plot_id) LEFT OUTER JOIN user ON (user._id = plot.user_id)";
	private static final String RESPONDENT_JOIN = "survey_respondent LEFT OUTER JOIN survey ON (survey_respondent.survey_id = survey._id)";

	private static final int DATABASE_VERSION = 62;

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

		private static SQLiteDatabase database;
		private volatile static int instanceCount = 0;

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
			db.execSQL(POINT_OF_INTEREST_TABLE_CREATE);
			db.execSQL(TRANSMISSION_HISTORY_TABLE_CREATE);
			for (int i = 0; i < DEFAULT_INSERTS.length; i++) {
				db.execSQL(DEFAULT_INSERTS[i]);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion);

			if (oldVersion < 57) {
				db.execSQL("DROP TABLE IF EXISTS " + RESPONSE_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + RESPONDENT_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + SURVEY_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + PLOT_POINT_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + PLOT_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + PREFERENCES_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + POINT_OF_INTEREST_TABLE);
				db
						.execSQL("DROP TABLE IF EXISTS "
								+ TRANSMISSION_HISTORY_TABLE);
				onCreate(db);
			} else if (oldVersion == 57) {
				try {
					// just add the changes made in version 58
					db.execSQL(TRANSMISSION_HISTORY_TABLE_CREATE);
				} catch (Exception e) {
					// swallow since this fails if the update is already applied
				}
			} else if (oldVersion == 58) {
				db
						.execSQL("insert into preferences values('survey.textsize','LARGE')");
			} else if (oldVersion <= 63) {
				try {
					db
							.execSQL("alter table survey_respondent add column exported_flag text");
				} catch (Exception e) {
					// swallow
				}
			}
		}

		@Override
		public synchronized SQLiteDatabase getWritableDatabase() {
			if (database == null) {
				database = super.getWritableDatabase();
			}
			instanceCount++;
			return database;
		}

		@Override
		public synchronized void close() {
			instanceCount--;
			if (instanceCount <= 0) {				
				super.close();
				database = null;
			}
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
				RESPONDENT_TABLE + "." + PK_ID_COL, RESP_ID_COL, ANSWER_COL,
				ANSWER_TYPE_COL, QUESTION_FK_COL, DISP_NAME_COL, EMAIL_COL,
				DELIVERED_DATE_COL, SUBMITTED_DATE_COL,
				RESPONDENT_TABLE + "." + SURVEY_FK_COL, SCORED_VAL_COL,
				STRENGTH_COL }, SUBMITTED_FLAG_COL + "= 'true' AND "
				+ INCLUDE_FLAG_COL + "='true' AND" + "(" + DELIVERED_DATE_COL
				+ " is null OR " + MEDIA_SENT_COL + " <> 'true')", null, null,
				null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	/**
	 * returns a cursor that lists all unexported (sentFlag = false) survey data
	 * 
	 * @return
	 */
	public Cursor fetchUnexportedData() {
		Cursor cursor = database.query(RESPONSE_JOIN, new String[] {
				RESPONDENT_TABLE + "." + PK_ID_COL, RESP_ID_COL, ANSWER_COL,
				ANSWER_TYPE_COL, QUESTION_FK_COL, DISP_NAME_COL, EMAIL_COL,
				DELIVERED_DATE_COL, SUBMITTED_DATE_COL,
				RESPONDENT_TABLE + "." + SURVEY_FK_COL, SCORED_VAL_COL,
				STRENGTH_COL }, SUBMITTED_FLAG_COL + "= 'true' AND "
				+ INCLUDE_FLAG_COL + "='true' AND " + EXPORTED_FLAG_COL
				+ " <> 'true' AND " + "(" + DELIVERED_DATE_COL + " is null OR "
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
		vals.put(STATUS_COL, ConstantUtil.SUBMITTED_STATUS);
		database.update(RESPONDENT_TABLE, vals,
				PK_ID_COL + "= " + respondentId, null);
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
				if (database.update(RESPONDENT_TABLE, updatedValues, PK_ID_COL
						+ " = ?", new String[] { id }) < 1) {
					Log.e(TAG,
							"Could not update record for Survey_respondent_id "
									+ id);
				}
			}
		}
	}

	/**
	 * updates the respondent table by recording the sent date stamp
	 * 
	 * @param idList
	 */
	public void markDataAsExported(HashSet<String> idList) {
		if (idList != null && idList.size()>0) {
			ContentValues updatedValues = new ContentValues();
			updatedValues.put(EXPORTED_FLAG_COL, "true");
			// enhanced FOR ok here since we're dealing with an implicit
			// iterator anyway
			for (String id : idList) {
				if (database.update(RESPONDENT_TABLE, updatedValues, PK_ID_COL
						+ " = ?", new String[] { id }) < 1) {
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
			updatedValues.put(SAVED_DATE_COL, System.currentTimeMillis());
			if (database.update(RESPONDENT_TABLE, updatedValues, PK_ID_COL
					+ " = ?", new String[] { surveyRespondentId }) < 1) {
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
				SURVEY_RESPONDENT_ID_COL, INCLUDE_FLAG_COL, SCORED_VAL_COL,
				STRENGTH_COL }, SURVEY_RESPONDENT_ID_COL + "=?",
				new String[] { respondentID }, null, null, null);
	}

	/**
	 * loads a single question response
	 * 
	 * @param respondentId
	 * @param questionId
	 * @return
	 */
	public QuestionResponse findSingleResponse(Long respondentId,
			String questionId) {
		QuestionResponse resp = null;
		Cursor cursor = database.query(RESPONSE_TABLE, new String[] {
				RESP_ID_COL, QUESTION_FK_COL, ANSWER_COL, ANSWER_TYPE_COL,
				SURVEY_RESPONDENT_ID_COL, INCLUDE_FLAG_COL, SCORED_VAL_COL,
				STRENGTH_COL }, SURVEY_RESPONDENT_ID_COL + "=? and "
				+ QUESTION_FK_COL + "=?", new String[] {
				respondentId.toString(), questionId }, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				resp = new QuestionResponse();
				resp.setQuestionId(questionId);
				resp.setRespondentId(respondentId);
				resp.setType(cursor.getString(cursor
						.getColumnIndexOrThrow(ANSWER_TYPE_COL)));
				resp.setValue(cursor.getString(cursor
						.getColumnIndexOrThrow(ANSWER_COL)));
				resp.setId(cursor.getLong(cursor
						.getColumnIndexOrThrow(RESP_ID_COL)));
				resp.setIncludeFlag(cursor.getString(cursor
						.getColumnIndexOrThrow(INCLUDE_FLAG_COL)));
				resp.setScoredValue(cursor.getString(cursor
						.getColumnIndexOrThrow(SCORED_VAL_COL)));
				resp.setStrength(cursor.getString(cursor
						.getColumnIndexOrThrow(STRENGTH_COL)));
			}
			cursor.close();
		}
		return resp;
	}

	/**
	 * inserts or updates a question response after first looking to see if it
	 * already exists in the database.
	 * 
	 * @param resp
	 * @return
	 */
	public QuestionResponse createOrUpdateSurveyResponse(QuestionResponse resp) {
		QuestionResponse responseToSave = findSingleResponse(resp
				.getRespondentId(), resp.getQuestionId());
		if (responseToSave != null) {
			responseToSave.setValue(resp.getValue());
			responseToSave.setStrength(resp.getStrength());
			responseToSave.setScoredValue(resp.getScoredValue());
		} else {
			responseToSave = resp;
		}
		long id = -1;
		ContentValues initialValues = new ContentValues();
		initialValues.put(ANSWER_COL, responseToSave.getValue());
		initialValues.put(ANSWER_TYPE_COL, responseToSave.getType());
		initialValues.put(QUESTION_FK_COL, responseToSave.getQuestionId());
		initialValues.put(SURVEY_RESPONDENT_ID_COL, responseToSave
				.getRespondentId());
		initialValues.put(SCORED_VAL_COL, responseToSave.getScoredValue());
		initialValues.put(INCLUDE_FLAG_COL, resp.getIncludeFlag());
		initialValues.put(STRENGTH_COL, responseToSave.getStrength());
		if (responseToSave.getId() == null) {
			id = database.insert(RESPONSE_TABLE, null, initialValues);
		} else {
			if (database.update(RESPONSE_TABLE, initialValues, RESP_ID_COL
					+ "=?", new String[] { responseToSave.getId().toString() }) > 0) {
				id = responseToSave.getId();
			}
		}
		responseToSave.setId(id);
		return responseToSave;
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
				+ PK_ID_COL + ")" }, SUBMITTED_FLAG_COL + "='false' and "
				+ SURVEY_FK_COL + "=? and " + STATUS_COL + " =?", new String[] {
				surveyId, ConstantUtil.CURRENT_STATUS }, null, null, null);
		long id = -1;
		if (results != null && results.getCount() > 0) {
			results.moveToFirst();
			id = results.getLong(0);
		}
		if (results != null) {
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
		initialValues.put(EXPORTED_FLAG_COL,"false");
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
	 * lists all survey respondents by status
	 * 
	 * @param status
	 * @return
	 */
	public Cursor listSurveyRespondent(String status) {
		String[] whereParams = { status };
		Cursor cursor = database.query(RESPONDENT_JOIN, new String[] {
				RESPONDENT_TABLE + "." + PK_ID_COL, DISP_NAME_COL,
				SAVED_DATE_COL, SURVEY_FK_COL, USER_FK_COL, SUBMITTED_DATE_COL,
				DELIVERED_DATE_COL }, "status = ?", whereParams, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
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
				LANGUAGE_COL, HELP_DOWNLOADED_COL, VERSION_COL }, whereClause,
				whereParams, null, null, null);
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
					survey.setVersion(cursor.getDouble(cursor
							.getColumnIndexOrThrow(VERSION_COL)));
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
	public void deleteSurvey(String surveyId, boolean physicalDelete) {
		if (!physicalDelete) {
			ContentValues updatedValues = new ContentValues();
			updatedValues.put(DELETED_COL, ConstantUtil.IS_DELETED);
			database.update(SURVEY_TABLE, updatedValues, PK_ID_COL + " = ?",
					new String[] { surveyId });
		} else {
			database.delete(SURVEY_TABLE, PK_ID_COL + " = ? ",
					new String[] { surveyId });
		}
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
		int updated = database.update(PREFERENCES_TABLE, updatedValues, KEY_COL
				+ " = ?", new String[] { key });
		if (updated <= 0) {
			updatedValues.put(KEY_COL, key);
			database.insert(PREFERENCES_TABLE, null, updatedValues);
		}
	}

	/**
	 * deletes all the surveys from the database
	 */
	public void deleteAllSurveys() {
		database.delete(SURVEY_TABLE, null, null);
	}

	/**
	 * deletes all the points of interest
	 */
	public void deleteAllPoints() {
		database.delete(POINT_OF_INTEREST_TABLE, null, null);
	}

	/**
	 * deletes all survey responses from the database
	 */
	public void deleteAllResponses() {
		database.delete(RESPONSE_TABLE, null, null);
		database.delete(RESPONDENT_TABLE, null, null);
	}

	/**
	 * deletes all survey responses from the database for a specific respondent
	 */
	public void deleteResponses(String respondentId) {
		database.delete(RESPONSE_TABLE, SURVEY_RESPONDENT_ID_COL + "=?",
				new String[] { respondentId });
	}

	/**
	 * deletes the respondent record and any responses it contains
	 * 
	 * @param respondentId
	 */
	public void deleteRespondent(String respondentId) {
		deleteResponses(respondentId);
		database.delete(RESPONDENT_TABLE, PK_ID_COL + "=?",
				new String[] { respondentId });
	}

	/**
	 * deletes a single response
	 * 
	 * @param respondentId
	 * @param questionId
	 */
	public void deleteResponse(String respondentId, String questionId) {
		database.delete(RESPONSE_TABLE, SURVEY_RESPONDENT_ID_COL + "=? AND "
				+ QUESTION_FK_COL + "=?", new String[] { respondentId,
				questionId });
	}

	/**
	 * saves or updates a PointOfInterests
	 * 
	 * @param id
	 * @param country
	 * @param jsonString
	 */
	public void saveOrUpdatePointOfInterest(PointOfInterest point) {
		Cursor cursor = database.query(POINT_OF_INTEREST_TABLE,
				new String[] { PK_ID_COL }, PK_ID_COL + "=?",
				new String[] { point.getId().toString() }, null, null, null);
		boolean isUpdate = false;
		if (cursor != null && cursor.getCount() > 0) {
			isUpdate = true;
		}
		if (cursor != null) {
			cursor.close();
		}
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(PK_ID_COL, point.getId());
		updatedValues.put(COUNTRY_COL, point.getCountry() != null ? point
				.getCountry() : "unknown");
		updatedValues.put(DISP_NAME_COL, point.getName() != null ? point
				.getName() : "unknown");
		updatedValues.put(TYPE_COL, point.getType() != null ? point.getType()
				: "unknown");
		updatedValues.put(UPDATED_DATE_COL, System.currentTimeMillis());
		String temp = point.getPropertyNamesString();
		if (temp != null) {
			updatedValues.put(PROP_NAME_COL, temp);
		}
		temp = point.getPropertyValuesString();
		if (temp != null) {
			updatedValues.put(PROP_VAL_COL, temp);
		}
		if (point.getLatitude() != null) {
			updatedValues.put(LAT_COL, point.getLatitude());
		}
		if (point.getLongitude() != null) {
			updatedValues.put(LON_COL, point.getLongitude());
		}

		if (isUpdate) {
			database.update(POINT_OF_INTEREST_TABLE, updatedValues, PK_ID_COL
					+ "=?", new String[] { point.getId().toString() });
		} else {
			database.insert(POINT_OF_INTEREST_TABLE, null, updatedValues);
		}
	}

	/**
	 * lists all points of interest, optionally filtering by country code
	 * 
	 * @param country
	 * @return
	 */
	public ArrayList<PointOfInterest> listPointsOfInterest(String country,
			String prefix) {
		ArrayList<PointOfInterest> points = null;
		String whereClause = null;
		String[] whereValues = null;

		if (country != null) {
			whereClause = COUNTRY_COL + "=?";
			whereValues = new String[] { country };

		}
		if (prefix != null && prefix.trim().length() > 0) {
			if (country != null) {
				whereClause = whereClause + " AND " + DISP_NAME_COL + " like ?";
				whereValues = new String[2];
				whereValues[0] = country;
				whereValues[1] = prefix + "%";
			} else {
				whereClause = DISP_NAME_COL + " like ?";
				whereValues = new String[] { prefix + "%" };
			}
		}
		Cursor cursor = database.query(POINT_OF_INTEREST_TABLE, new String[] {
				PK_ID_COL, COUNTRY_COL, DISP_NAME_COL, UPDATED_DATE_COL,
				LAT_COL, LON_COL, PROP_NAME_COL, PROP_VAL_COL, TYPE_COL },
				whereClause, whereValues, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();

				cursor.moveToFirst();
				points = new ArrayList<PointOfInterest>();
				do {
					PointOfInterest point = new PointOfInterest();
					point.setName(cursor.getString(cursor
							.getColumnIndexOrThrow(DISP_NAME_COL)));
					point.setType(cursor.getString(cursor
							.getColumnIndexOrThrow(TYPE_COL)));
					point.setCountry(cursor.getString(cursor
							.getColumnIndexOrThrow(COUNTRY_COL)));
					point.setId(cursor.getLong(cursor
							.getColumnIndexOrThrow(PK_ID_COL)));
					point.setPropertyNames(cursor.getString(cursor
							.getColumnIndexOrThrow(PROP_NAME_COL)));
					point.setPropertyValues(cursor.getString(cursor
							.getColumnIndexOrThrow(PROP_VAL_COL)));
					point.setLatitude(cursor.getDouble(cursor
							.getColumnIndexOrThrow(LAT_COL)));
					point.setLongitude(cursor.getDouble(cursor
							.getColumnIndexOrThrow(LON_COL)));

					points.add(point);

				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		return points;
	}

	/**
	 * inserts a transmissionHistory row into the db
	 * 
	 * @param respId
	 * @param fileName
	 * @return
	 */
	public Long createTransmissionHistory(Long respId, String fileName,
			String status) {
		ContentValues initialValues = new ContentValues();
		Long idVal = null;
		initialValues.put(SURVEY_RESPONDENT_ID_COL, respId);
		initialValues.put(FILENAME_COL, fileName);

		if (status != null) {
			initialValues.put(STATUS_COL, status);
			if (ConstantUtil.IN_PROGRESS_STATUS.equals(status)) {
				initialValues.put(TRANS_START_COL, System.currentTimeMillis());
			}
		} else {
			initialValues.put(TRANS_START_COL, (Long) null);
			initialValues.put(STATUS_COL, ConstantUtil.QUEUED_STATUS);
		}
		idVal = database
				.insert(TRANSMISSION_HISTORY_TABLE, null, initialValues);
		return idVal;
	}

	/**
	 * updates the transmisson history record with the status passed in. If the
	 * status == Completed, the completion date is updated.
	 * 
	 * @param respondId
	 * @param fileName
	 * @param status
	 */
	public void updateTransmissionHistory(Long respondId, String fileName,
			String status) {
		ArrayList<FileTransmission> transList = listFileTransmission(respondId,
				fileName, true);
		Long idVal = null;
		if (transList != null && transList.size() > 0) {
			idVal = transList.get(0).getId();
			if (idVal != null) {
				ContentValues vals = new ContentValues();
				vals.put(STATUS_COL, status);
				if (ConstantUtil.COMPLETE_STATUS.equals(status)) {
					vals.put(DELIVERED_DATE_COL, System.currentTimeMillis()
							+ "");
				} else if (ConstantUtil.IN_PROGRESS_STATUS.equals(status)) {
					vals.put(TRANS_START_COL, System.currentTimeMillis() + "");
				}
				database.update(TRANSMISSION_HISTORY_TABLE, vals, PK_ID_COL
						+ " = ?", new String[] { idVal.toString() });
			}
		}
	}

	/**
	 * lists all the file transmissions for the values passed in.
	 * 
	 * @param respondentId
	 *            - MANDATORY id of the survey respondent
	 * @param fileName
	 *            - OPTIONAL file name
	 * @param incompleteOnly
	 *            - if true, only rows without a complete status will be
	 *            returned
	 * @return
	 */
	public ArrayList<FileTransmission> listFileTransmission(Long respondentId,
			String fileName, boolean incompleteOnly) {
		ArrayList<FileTransmission> transList = null;

		String whereClause = SURVEY_RESPONDENT_ID_COL + "=?";
		if (incompleteOnly) {
			whereClause = whereClause + " AND " + STATUS_COL + " <> '"
					+ ConstantUtil.COMPLETE_STATUS + "'";
		}
		String[] whereValues = null;

		if (fileName != null && fileName.trim().length() > 0) {
			whereClause = whereClause + " AND " + FILENAME_COL + " = ?";
			whereValues = new String[2];
			whereValues[0] = respondentId.toString();
			whereValues[1] = fileName;

		} else {
			whereValues = new String[] { respondentId.toString() };
		}

		Cursor cursor = database.query(TRANSMISSION_HISTORY_TABLE,
				new String[] { PK_ID_COL, FILENAME_COL, STATUS_COL,
						TRANS_START_COL, DELIVERED_DATE_COL,
						SURVEY_RESPONDENT_ID_COL }, whereClause, whereValues,
				null, null, TRANS_START_COL + " desc");
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();

				cursor.moveToFirst();
				transList = new ArrayList<FileTransmission>();
				do {
					FileTransmission trans = new FileTransmission();
					trans.setId(cursor.getLong(cursor
							.getColumnIndexOrThrow(PK_ID_COL)));
					trans.setRespondentId(respondentId);
					trans.setFileName(cursor.getString(cursor
							.getColumnIndexOrThrow(FILENAME_COL)));
					Long startDateMillis = cursor.getLong(cursor
							.getColumnIndexOrThrow(TRANS_START_COL));
					if (startDateMillis != null && startDateMillis > 0) {
						trans.setStartDate(new Date(startDateMillis));
					}
					Long delivDateMillis = cursor.getLong(cursor
							.getColumnIndexOrThrow(DELIVERED_DATE_COL));
					if (delivDateMillis != null && delivDateMillis > 0) {
						trans.setEndDate(new Date(delivDateMillis));
					}
					trans.setStatus(cursor.getString(cursor
							.getColumnIndexOrThrow(STATUS_COL)));
					transList.add(trans);
				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		return transList;
	}

	/**
	 * marks submitted data as unsent. If an ID is passed in, only that
	 * submission will be updated. If id is null, ALL data will be marked as
	 * unsent.
	 */
	public void markDataUnsent(Long id) {
		if (id == null) {
			executeSql("update survey_respondent set media_sent_flag = 'false', delivered_date = null;");
		} else {
			executeSql("update survey_respondent set media_sent_flag = 'false', delivered_date = null where _id = "
					+ id);
		}
	}

	/**
	 * executes a single insert/update/delete DML or any DDL statement without
	 * any bind arguments.
	 * 
	 * @param sql
	 */
	public void executeSql(String sql) {
		database.execSQL(sql);
	}
}