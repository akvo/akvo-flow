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

/*
 * FieldSurvey Robotium test cases
 * 
 * Then you can run these test cases either on the emulator or on device. You right click
 * the test project and select Run As --> Run As Android JUnit Test
 * 
 * @author Mark Westra
 * 
 */

package com.jayway.test;

import com.gallatinsystems.survey.device.activity.SurveyHomeActivity;
import com.jayway.android.robotium.solo.Solo;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.widget.TextView;
import android.widget.Button;
import java.util.ArrayList;

import java.util.TreeMap;



public class SurveyDataTest extends ActivityInstrumentationTestCase2<SurveyHomeActivity>{

	private Solo solo;
	private Solo solo1;
	
	public void launchActivity(String packageName, String className) { 
	    Intent intent = new Intent(); 
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
	    intent.setClassName(packageName, className); 
	    getInstrumentation().getContext().startActivity(intent); 
	} 
	
	public SurveyDataTest() {
		super("com.gallatinsystems.survey.device", SurveyHomeActivity.class);
	}

	public void setUp() throws Exception {
		Activity activity=getActivity();		
		solo = new Solo(getInstrumentation(), activity);		
	}
	
	
	private void createUser(String string, String string2) {
		solo.clickOnMenuItem("Manage Users");
		solo.sendKey(solo.MENU);
		solo.clickOnText("Add User") ;
		solo.enterText(0, string);
		solo.enterText(1, string2);
		solo.clickOnText("Save") ;
		solo.goBack();
	}
	
	private void createAndSelectUser(String string, String string2){
		createUser(string,string2);
		solo.clickOnMenuItem("Manage Users");
		solo.clickOnText(string);
	}
	
	private void selectUser(String string){
		solo.clickOnMenuItem("Manage Users");
		solo.clickOnText(string);
	}
	
	private void deleteTestUsers() {
		solo.clickOnMenuItem("Manage Users");
		while (solo.searchText("^Test User.*")) {
			solo.clickLongOnText("^Test User.*");
			solo.clickOnText("Delete") ;
			
		}
		solo.goBack();
		
	}
	
	private void deleteAllData() {
		solo.clickOnMenuItem("Settings");
		solo.clickOnText("Delete All Data");
		solo.enterText(0,"12345");
		solo.clickOnButton("OK");
		solo.sleep(500);
		solo.clickOnButton("OK");
		solo.sleep(2000);
		solo.goBack();
	}
	
	
	@Smoke
	public void testDo100surveys() throws Exception {
	//deleteAllData();
	
		solo.clickOnMenuItem("Settings");
		solo.clickOnText("Preferences");
		solo.clickOnImageButton(8);
		solo.enterText(0,"12345");
		solo.clickOnButton("OK");
		solo.clearEditText(0);
		solo.enterText(0,"FLOW 13 tester");
		solo.clickOnButton("OK");	
		
		
	// get surveys in
	solo.goBack();
	solo.goBack();
			
	
	
	//start app
	launchActivity("com.gallatinsystems.survey.device","com.gallatinsystems.survey.device.activity.SurveyHomeActivity");
	solo=new Solo(getInstrumentation());
	solo.sleep(1000);
	//start app again
	solo.goBack();
	solo.goBack();
	launchActivity("com.gallatinsystems.survey.device","com.gallatinsystems.survey.device.activity.SurveyHomeActivity");
	solo=new Solo(getInstrumentation());
			
	solo.sleep(1000);
	//start app again
	solo.goBack();
	solo.goBack();
	launchActivity("com.gallatinsystems.survey.device","com.gallatinsystems.survey.device.activity.SurveyHomeActivity");
	solo=new Solo(getInstrumentation());
	
	// FLOW test survey 1 and 2 should be there now
	
	createAndSelectUser("FLOW samsung ace","flow.tester@akvo.org");
	solo.clickOnText("FLOW test survey 1.*");
	
	for (int i=0;i<2;i++){	
		// free text field
		solo.clearEditText(0);
		solo.enterText(0, "Run 13 - Survey instance "+i);
		
		// number field
		solo.clearEditText(1);
		solo.enterText(1,Integer.toString(i));
		
		solo.clickOnText("Check Geo Location");
		
		solo.clickOnButton("Select Date");
		solo.setDatePicker(0, 2012+i, 2, 16);
		solo.clickOnButton("Set");
		
		solo.clickOnText("Next");
		solo.clickOnButton("Submit");
		solo.clickOnText("OK");
		
		solo.sleep(5000);
		}
	}
	
	
	

	@Override
	public void tearDown() throws Exception {
		//Robotium will finish all the activities that have been opened
		solo.finishOpenedActivities();
	}
}
