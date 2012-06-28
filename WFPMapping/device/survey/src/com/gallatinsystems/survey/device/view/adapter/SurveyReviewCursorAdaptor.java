/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

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
		String status = "Sent: ";
		long millis = cursor.getLong(cursor
				.getColumnIndex(SurveyDbAdapter.DELIVERED_DATE_COL));
		// if millis is 0, that's because we haven't yet sent it
		if (millis == 0) {
			status = "Submitted: ";
			millis = cursor.getLong(cursor
					.getColumnIndex(SurveyDbAdapter.SUBMITTED_DATE_COL));
		}
		// if millis is still null, then the survey hasn't been submitted yet
		if (millis == 0) {
			status = "Saved: ";
			millis = cursor.getLong(cursor
					.getColumnIndex(SurveyDbAdapter.SAVED_DATE_COL));
		}

		// Format the date string
		Date date = new Date(millis);
		dateView.setText(status
				+ DateFormat.getLongDateFormat(context).format(date) + " "
				+ DateFormat.getTimeFormat(context).format(date));
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
