package com.gallatinsystems.survey.device.view.adapter;

import java.util.Date;

import android.R;
import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;

/**
 * this adaptor can format date strings using the device's locale settings prior
 * to displaying them to the screen
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyReviewCursorAdaptor extends CursorAdapter {

	public static int SURVEY_ID_KEY = com.gallatinsystems.survey.device.R.integer.surveyidkey;
	public static int RESP_ID_KEY = com.gallatinsystems.survey.device.R.integer.respidkey;
	public static int USER_ID_KEY = com.gallatinsystems.survey.device.R.integer.useridkey;

	public SurveyReviewCursorAdaptor(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView dateView = (TextView) view.findViewById(R.id.text2);
		long millis = cursor.getLong(cursor
				.getColumnIndex(SurveyDbAdapter.SAVED_DATE_COL));
		// Format the date string
		Date date = new Date(millis);
		dateView.setText(DateFormat.getLongDateFormat(context).format(date)
				+ " " + DateFormat.getTimeFormat(context).format(date));
		TextView headingView = (TextView) view.findViewById(R.id.text1);
		headingView.setText(cursor.getString(cursor
				.getColumnIndex(SurveyDbAdapter.DISP_NAME_COL)));
		view.setTag(SURVEY_ID_KEY, cursor.getLong(cursor
				.getColumnIndex(SurveyDbAdapter.SURVEY_FK_COL)));
		view.setTag(RESP_ID_KEY, cursor.getLong(cursor
				.getColumnIndex(SurveyDbAdapter.PK_ID_COL)));
		view.setTag(USER_ID_KEY, cursor.getLong(cursor
				.getColumnIndex(SurveyDbAdapter.USER_FK_COL)));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.two_line_list_item, null);
		bindView(view, context, cursor);
		return view;
	}

}
