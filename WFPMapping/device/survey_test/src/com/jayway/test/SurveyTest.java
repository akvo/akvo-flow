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



public class SurveyTest extends ActivityInstrumentationTestCase2<SurveyHomeActivity>{

	private Solo solo;
	private Solo solo1;
	
	public void launchActivity(String packageName, String className) { 
	    Intent intent = new Intent(); 
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
	    intent.setClassName(packageName, className); 
	    getInstrumentation().getContext().startActivity(intent); 
	} 
	
	public SurveyTest() {
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
	
	// test 3.1
	@Smoke
	public void testACT1AppOpen() throws Exception {
		assertEquals("App not opening correctly", true, solo.searchText("^Manage Users$")); 
		assertEquals("App not opening correctly", true, solo.searchText("^Review Saved Surveys$"));
		assertEquals("App not opening correctly", true, solo.searchText("^Settings$")); 
	}
	
	// test 4.1
	@Smoke
	public void testACT2CreateUser() throws Exception {
		////////////////  SCENARIO - CREATE A USER /////////////////
		// go to manage users 
		deleteTestUsers();
		solo.clickOnMenuItem("Manage Users");
		
		//check if user has been added correctly
		assertEquals("User selection text not present", true, solo.searchText("^Select the current user by clicking. To create a new user, press the Menu button and select Add User. Long-click to edit a user.$")); 
		
		// click on menu button and select Add User
		solo.sendKey(solo.MENU);
		solo.clickOnText("Add User") ;
		
		solo.enterText(0, "Test User 1");
		solo.enterText(1, "name-1.lastname@akvo.org");
		solo.clickOnText("Save") ;
		
		// test if user is there
		assertEquals("Test User 1 not present", true, solo.searchText("Test User 1")); 
	
		//test if user details are ok
		solo.clickLongOnText("^Test User 1$");
		solo.clickOnText("Edit User");
		
		assertEquals("Name Test User 1 not there", true, solo.searchText("Test User 1")); 
		assertEquals("email Test User 1 not there", true, solo.searchText("name-1.lastname@akvo.org")); 
	}
	
	// test 4.3
	@Smoke
	public void testACT3EditUser() throws Exception {
		//////////////// SCENARIO -  EDIT A USER /////////////////////
		deleteTestUsers();
		createUser("Test User 2","name-2.lastname@akvo.org");
		
		solo.clickOnMenuItem("Manage Users");
		solo.clickLongOnText("^Test User 2$");
		solo.clickOnText("Edit User");
		
		solo.clearEditText(0);
		solo.enterText(0, "Test User 2a");
		solo.clearEditText(1);
		solo.enterText(1, "name-2a.lastname@akvo.org");
		solo.clickOnText("Save") ;
	
		assertEquals("Name change did not work", true, solo.searchText("Test User 2a")); 
		
		solo.clickLongOnText("^Test User 2a$");
		solo.clickOnText("Edit User");
		
		assertEquals("Name not correct", true, solo.searchText("Test User 2a")); 
		assertEquals("email not correct", true, solo.searchText("name-2a.lastname@akvo.org")); 
	}
	
	// test 4.2
	@Smoke
	public void testACT4DeleteUser() throws Exception {
		////////////////SCENARIO -  Delete A USER /////////////////////	
		deleteTestUsers();
		createUser("Test User 3","name-3.lastname@akvo.org");
		solo.clickOnMenuItem("Manage Users");
		solo.clickLongOnText("^Test User 3$");
		solo.clickOnText("Delete");
		assertEquals("user not deleted", false, solo.searchText("Test User 3")); 	
	}
	
	// test 4.4
	@Smoke
	public void testACT5SelectUser() throws Exception {
		////////////////SCENARIO -  Delete A USER /////////////////////	
		deleteTestUsers();
		createUser("Test User 4","name-4.lastname@akvo.org");
		solo.clickOnMenuItem("Manage Users");
		solo.clickOnText("^Test User 4$");
		assertEquals("user not selected", true, solo.searchText("Test User 4")); 	
	}
	
	// test 5.1
	@Smoke
	public void testACT6DownloadSurveys() throws Exception {
		deleteAllData();
		assertEquals("Survey should not be here", false, solo.searchText("FLOW test survey")); 	
		
		//leave app
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
		
		// check if expected surveys are there now
		assertEquals("Survey 1 should be here", true, solo.searchText("FLOW test survey 1")); 	
		assertEquals("Survey 2 should be here", true, solo.searchText("FLOW test survey 2")); 
	}
	
	//test 5.3
	public void testACT7DownloadExtraSurvey() throws Exception {
		deleteAllData();
		assertEquals("Survey should not be here", false, solo.searchText("FLOW test survey")); 	
		
		solo.clickOnMenuItem("Settings");
		solo.clickOnText("Download Survey");
		
		solo.enterText(0,"12345");
		solo.clickOnButton("OK");
		solo.sleep(500);
		// FLOW test survey 4 is 686193
		solo.enterText(0,"686193");
		solo.clickOnButton("Ok");
		
		solo.sleep(1000);
		
		solo.goBack();
		solo.goBack();
		launchActivity("com.gallatinsystems.survey.device","com.gallatinsystems.survey.device.activity.SurveyHomeActivity");
		solo=new Solo(getInstrumentation());
		
		solo.sleep(1000);
		solo.goBack();
		solo.goBack();
		launchActivity("com.gallatinsystems.survey.device","com.gallatinsystems.survey.device.activity.SurveyHomeActivity");
		solo=new Solo(getInstrumentation());
		
		
		assertEquals("Survey FLOW test survey 4 should be here", true, solo.searchText("FLOW test survey 4")); 	
		
	}
	// test 14.8
	public void testACT8DeleteAllData() throws Exception {
		//create user
		createUser("Test User 2","name-2.lastname@akvo.org");
		
		//download survey
		solo.clickOnMenuItem("Settings");
		solo.clickOnText("Download Survey");
		
		solo.enterText(0,"12345");
		solo.clickOnButton("OK");
		
		// FLOW test servey 1 has id 671060
		solo.enterText(0,"671060");
		solo.clickOnButton("Ok");
		solo.goBack();
		solo.sleep(500);
		
		// delete all data
		deleteAllData();
		assertEquals("Survey should not be here", false, solo.searchText("FLOW test survey")); 	
		solo.clickOnText("Manage Users");
		assertEquals("User should not be here", false, solo.searchText("Test User")); 	
		
	}
	
	// test 6.1
	@Smoke
	public void testACT90OpenSurvey() throws Exception {
		deleteTestUsers();
		
		solo.clickOnText("FLOW test survey 1");
		assertEquals("Survey should not be selectable without user", true, solo.searchText("Please click the Manage Users icon and choose a user before continuing.")); 	
		
		solo.clickOnButton("OK");
		createAndSelectUser("Test User 1","name-1.lastname@akvo.org");
		
		// now I should be able to select the survey
		solo.clickOnText("FLOW test survey 1");
		assertEquals("Survey not selected", true, solo.searchText("Group 1.*")); 	
	}	
	
	// test 5.2
	@Smoke
	public void testACT91ReloadSurveys() throws Exception {
		deleteAllData();
		assertEquals("Survey should not be here", false, solo.searchText("FLOW test survey")); 	
		
		solo.clickOnMenuItem("Settings");
		solo.clickOnText("Reload all surveys");
		solo.enterText(0,"12345");
		solo.clickOnButton("OK");
		solo.clickOnButton("OK");
		solo.sleep(3000);
		solo.goBack();
		
		assertEquals("Survey 1 should be here", true, solo.searchText("FLOW test survey 1")); 	
		assertEquals("Survey 2 should be here", true, solo.searchText("FLOW test survey 2")); 	
		
	}
	
	// test 9.1
	@Smoke
	public void testACT92Dependencies() throws Exception {
		deleteTestUsers();
		createAndSelectUser("Test User 1","name-1.lastname@akvo.org");
		
		// make sure we can select FLOW test survey 2
		solo.clickOnText("FLOW test survey 2.*");
		solo.sleep(1000);
		assertEquals("Survey not selected", true, solo.searchText("Group 1.*")); 	
	
		// click on first option of button 1 - nothing should be visible now
		solo.clickOnRadioButton(0);
		solo.sleep(1000);
		// make sure questions are not visible
		assertEquals("question 2a should not be here", false, solo.searchText("question 2a",true)); 
		assertEquals("question 3a should not be here", false, solo.searchText("question 3a",true)); 
		
		// make sure group 2 is still empty
		solo.clickOnText("Group 2");
		assertEquals("question 1b should not be here", false, solo.searchText("question 1b",true)); 
		assertEquals("question 2b should not be here", false, solo.searchText("question 2b",true)); 
		solo.clickOnText("Group 1");
		
		// option field
		solo.clickOnRadioButton(2);
		assertEquals("button 3 of question 2 not selected", true, solo.searchText("question 2a",true)); 
		
		solo.clickOnRadioButton(4);
		assertEquals("button 2 of question 3 not selected", true, solo.searchText("question 3a",true)); 
		
		solo.clickOnRadioButton(8);
		
		// check if group 2 now shows right questions
		solo.clickOnText("Group 2");
		assertEquals("question 1b should be here", true, solo.searchText("question 1b",true)); 
		assertEquals("question 2b should be here", true, solo.searchText("question 2b",true)); 
		
		solo.clickOnRadioButton(2);
		solo.clickOnRadioButton(5);
			
	}
	
	// test 13.2
	@Smoke
	public void testACT93KeepLastSelectedUserLoggedIn() throws Exception {
		deleteTestUsers();
		createAndSelectUser("Test User 1","name-1.lastname@akvo.org");
		assertEquals("Test user 1 not selected", true, solo.searchText("Test User 1",true)); 
		
		solo.clickOnMenuItem("Settings");
		solo.clickOnText("Preferences");
		if (solo.isCheckBoxChecked(0)){
			solo.clickOnCheckBox(0);
			solo.goBack();
			solo.goBack();
		}
		
		
		//leave app
		solo.goBack();
		solo.goBack();
		
		//start app
		launchActivity("com.gallatinsystems.survey.device","com.gallatinsystems.survey.device.activity.SurveyHomeActivity");
		solo=new Solo(getInstrumentation());
		
		// User should be gone now.
		assertEquals("Test user 1 still selected", false, solo.searchText("Test User 1",true)); 
		
		solo.clickOnMenuItem("Settings");
		solo.clickOnText("Preferences");
		solo.clickOnCheckBox(0);
		solo.goBack();
		solo.goBack();
		
		// select user again
		solo.clickOnMenuItem("Manage Users");
		solo.clickOnText("Test User 1");
	
		//leave app
		solo.goBack();
		solo.goBack();
				
		//start app
		launchActivity("com.gallatinsystems.survey.device","com.gallatinsystems.survey.device.activity.SurveyHomeActivity");
		solo=new Solo(getInstrumentation());
				
		// User should be selected now.
		assertEquals("Test user 1 not selected", true, solo.searchText("Test User 1",true)); 
		
		
	}
	// test 13.5
	public void testACT94ChangeSurveyLanguage() throws Exception {
		solo.scrollDown();
		solo.clickOnText("Settings.*");
		//solo.clickOnMenuItem("Settings");
		solo.clickOnText("Preferences");
		solo.clickOnImageButton(2);
		
		solo.sleep(500);
		// if Spanish is selected, uncheck
		if (solo.isTextChecked("Spanish")) {
			solo.clickOnText("Spanish");
			solo.clickOnButton("OK");
			solo.goBack();
			solo.goBack();
			solo.clickOnMenuItem("Settings");
			solo.clickOnText("Preferences");
			solo.clickOnImageButton(2);
		}

		solo.clickOnText("Spanish");
		solo.clickOnButton("OK");
		solo.goBack();
		solo.goBack();
		solo.clickOnMenuItem("Settings");
		solo.clickOnText("Preferences");
		solo.clickOnImageButton(2);
		solo.sleep(500);
		// check if spanish is still selected
		assertEquals("Language spanish not selected.", true, solo.isTextChecked("Spanish")); 
	}
	
	// test 13.12
	public void testACT95SetDeviceIdentifier() throws Exception {
		solo.clickOnMenuItem("Settings");
		solo.clickOnText("Preferences");
		solo.clickOnImageButton(8);
	
		solo.enterText(0,"12345");
		solo.clickOnButton("OK");
		solo.clearEditText(0);
		solo.clickOnButton("OK");
		solo.sleep(500);
		
		assertEquals("device identifier not cleared", false, solo.searchText("FLOW test ID")); 	
		solo.goBack();
		solo.goBack();
		
		// fill in device id
		solo.clickOnMenuItem("Settings");
		solo.clickOnText("Preferences");
		solo.clickOnImageButton(8);
		solo.enterText(0,"12345");
		solo.clickOnButton("OK");
		solo.clearEditText(0);
		solo.enterText(0,"FLOW_test_ID");
		solo.clickOnButton("OK");
		
		
		assertEquals("device identifier not set", true, solo.searchText("FLOW_test_ID")); 	
	}
	
	// test 7.1, 7.2, 7.3, 7.8
	@Smoke
	public void testACT96FillQuestions() throws Exception {
		////////////////SCENARIO -  Delete A USER /////////////////////	
		deleteTestUsers();
		createAndSelectUser("Test User 1","name-1.lastname@akvo.org");
		
		solo.clickOnText("FLOW test survey 1.*");
		assertEquals("Survey not selected", true, solo.searchText("Group 1.*")); 	
		
		// free text field
		solo.clearEditText(0);
		solo.enterText(0, "content textfield 1");
		
		// option field
		solo.clickOnRadioButton(1);
		
		// number field
		solo.clearEditText(1);
		solo.enterText(1,"42");
	
		// date field
		solo.clickOnButton("Select Date");
		solo.setDatePicker(0, 2012, 2, 16);
		solo.clickOnButton("Set");
		boolean dates=solo.searchText("Mar 16, 2012") | solo.searchText("16 Mar 2012");
		assertEquals("date not set", true, dates); 
		
		solo.goBack();
		//solo.sendKey(solo.MENU);
		//solo.clickOnText("Save and Start New") ;
		
	}

	@Override
	public void tearDown() throws Exception {
		//Robotium will finish all the activities that have been opened
		solo.finishOpenedActivities();
	}
}
