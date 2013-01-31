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

package com.gallatinsystems.survey.device.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * Displays a menu for going on to the different kinds of survey status:
 *  Saved ()
 *  Submitted (how the upload has progressed)
 *  
 *  Also displays a count for each choice.
 * 
 * 
 * @author Stellan Lagerström
 * 
 */
public class SurveyStatusHomeActivity extends Activity {

	private static final String TAG = "SurveyStatusHomeActivity";
	private TextView SavedCount;
	private TextView SubmittedCount;
	private SurveyDbAdapter databaseAdapter;

	
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.surveystatushome);
	        SavedCount = (TextView) findViewById(R.id.savedSurveysCount);
	        SubmittedCount = (TextView) findViewById(R.id.submittedSurveysCount);
			databaseAdapter = new SurveyDbAdapter(this);
	    }
/*
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.activity_login, menu);
	        return true;
	    }
*/
	    
	    @Override
	    public void onResume() {
	        super.onResume();  // Always call the superclass method first

			databaseAdapter.open();
	        //Update the survey counts
	        SavedCount.setText(Integer.toString(databaseAdapter.countSurveyRespondents(ConstantUtil.SAVED_STATUS)));
	        SubmittedCount.setText(Integer.toString(databaseAdapter.countSurveyRespondents(ConstantUtil.SUBMITTED_STATUS)));
	    }
	    
		protected void onDestroy() {
			if (databaseAdapter != null) {
				databaseAdapter.close();
			}
			super.onDestroy();
		}

	    
	    //Review Saved Surveys button pushed
	    public void ReviewSavedPushed(View view){
		    Intent intent = new Intent(this, SavedSurveyReviewActivity.class);
		    startActivity(intent);
	    }

	    //Review Submitted Surveys button pushed
	    public void ReviewSubmittedPushed(View view){
		    Intent intent = new Intent(this, SubmittedSurveyReviewActivity.class);
		    startActivity(intent);
	    }

	    //Transitionally, Review classic Surveys button pushed
	    public void ReviewClassicPushed(View view){
		    Intent intent = new Intent(this, SurveyReviewActivity.class);
		    startActivity(intent);
	    }


	}

	
