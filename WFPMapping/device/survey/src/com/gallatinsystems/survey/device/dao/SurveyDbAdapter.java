package com.gallatinsystems.survey.device.dao;

import java.util.HashSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gallatinsystems.survey.device.domain.QuestionResponse;

/**
 * Database class for the survey db. It can create/upgrade the database as well
 * as select/insert/update survey reponses.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyDbAdapter {
	public static final String NAME_COL = "name";
	public static final String QUESTION_COL = "question_id";
	public static final String ANSWER_COL = "answer_value";
	public static final String ANSWER_TYPE_COL = "answer_type";
	public static final String SURVEY_RESPONDENT_ID_COL = "survey_respondent_id";
	public static final String RESP_ID_COL = "survey_response_id";
	public static final String SURVEY_ID_COL = "survey_id";
	public static final String USER_ID_COL = "_id";
	public static final String USER_FK_COL = "user_id";
	public static final String DISP_NAME_COL = "display_name";
	public static final String EMAIL_COL = "email";
	public static final String SUBMITTED_FLAG_COL = "submitted_flag";
	public static final String DELIVERED_DATE_COL = "delivered_date";

	private static final String TAG = "SurveyDbAdapter";
	private DatabaseHelper databaseHelper;
	private SQLiteDatabase database;

	/**
	 * Database creation sql statement
	 */
	private static final String SURVEY_TABLE_CREATE = "create table survey (survey_id integer primary key autoincrement, "
			+ "title text not null);";

	private static final String SURVEY_RESPONDENT_CREATE = "create table survey_respondent (survey_respondent_id integer primary key autoincrement, "
			+ "survey_id integer not null, submitted_flag text, submitted_date text,delivered_date text, user_id integer);";

	private static final String SURVEY_RESPONSE_CREATE = "create table survey_response (survey_response_id integer primary key autoincrement, "
			+ " survey_respondent_id integer not null, question_id text not null, answer_value text not null, answer_type text not null);";

	private static final String USER_TABLE_CREATE = "create table user (_id integer primary key autoincrement, display_name text not null, email text not null);";

	private static final String DATABASE_NAME = "surveydata";
	private static final String SURVEY_TABLE = "survey";
	private static final String RESPONDENT_TABLE = "survey_respondent";
	private static final String RESPONSE_TABLE = "survey_response";
	private static final String USER_TABLE = "user";

	private static final String RESPONSE_JOIN = "survey_respondent LEFT OUTER JOIN survey_response ON (survey_respondent.survey_respondent_id = survey_response.survey_respondent_id) LEFT OUTER JOIN user ON (user._id = survey_respondent.user_id)";

	private static final int DATABASE_VERSION = 7;

	private final Context context;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(USER_TABLE_CREATE);
			db.execSQL(SURVEY_TABLE_CREATE);
			db.execSQL(SURVEY_RESPONDENT_CREATE);
			db.execSQL(SURVEY_RESPONSE_CREATE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + RESPONSE_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + RESPONDENT_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + SURVEY_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
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
		initialValues.put(NAME_COL, name);
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
				ANSWER_COL, ANSWER_TYPE_COL, QUESTION_COL, DISP_NAME_COL,
				EMAIL_COL, DELIVERED_DATE_COL }, SUBMITTED_FLAG_COL
				+ "= 'true' AND " + DELIVERED_DATE_COL + " is null", null,
				null, null, null);
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
		database.update(RESPONDENT_TABLE, vals, SURVEY_RESPONDENT_ID_COL + "= "
				+ respondentId, null);
	}

	/**
	 * updates the respondent table by recording the sent date stamp
	 * 
	 * @param idList
	 */
	public void markDataAsSent(HashSet<String> idList) {
		if (idList != null) {
			ContentValues updatedValues = new ContentValues();
			updatedValues.put(DELIVERED_DATE_COL, System.nanoTime() + "");
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
	 * returns a cursor listing all users
	 * 
	 * @return
	 */
	public Cursor fetchUsers() {
		Cursor cursor = database.query(USER_TABLE, new String[] { USER_ID_COL,
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
	public Cursor fetchUser(Long id) {
		Cursor cursor = database.query(USER_TABLE, new String[] { USER_ID_COL,
				DISP_NAME_COL, EMAIL_COL }, USER_ID_COL + "=?",
				new String[] { id.toString() }, null, null, null);
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
			if (database.update(USER_TABLE, initialValues, USER_ID_COL + "=?",
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
				QUESTION_COL, ANSWER_COL, ANSWER_TYPE_COL,
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
		initialValues.put(QUESTION_COL, response.getQuestionId());
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
				+ "='false' and " + SURVEY_ID_COL + "=?",
				new String[] { surveyId }, null, null, null);
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
		initialValues.put(SURVEY_ID_COL, surveyId);
		initialValues.put(SUBMITTED_FLAG_COL, "false");
		initialValues.put(USER_FK_COL, userId);
		return database.insert(RESPONDENT_TABLE, null, initialValues);
	}

}
