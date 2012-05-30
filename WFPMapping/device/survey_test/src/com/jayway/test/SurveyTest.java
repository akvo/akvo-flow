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
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.widget.TextView;
import android.widget.Button;
import java.util.ArrayList;


public class SurveyTest extends ActivityInstrumentationTestCase2<SurveyHomeActivity>{

	private Solo solo;
	
	public SurveyTest() {
		super("com.gallatinsystems.survey.device", SurveyHomeActivity.class);

	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		
	}


	@Smoke
	public void testAndDeleteUser() throws Exception {
		
		// go to manage users 
		solo.clickOnMenuItem("Manage Users");
		
		// click on menu button
		solo.sendKey(solo.MENU);
		
		solo.clickOnText("Add User") ;
	
		solo.enterText(0, "Test User 1");
		solo.enterText(1, "test@akvo.org");
		solo.clickOnText("Save") ;
		solo.goBack();
		solo.clickOnMenuItem("Manage Users");
		
		//check if user has been added correctly
		boolean actual = solo.searchText("^Test User 1$");
		assertEquals("User not added correctly", true, actual); 
		
		//delete user
		solo.clickLongOnText("^Test User 1$");
		solo.clickOnText("Delete User");
		
		//check if user has been deleted correctly
		boolean actual2 = solo.searchText("^Test User 1$");
		assertEquals("User not deleted correctly", false, actual2); 	
		
		
		// Make sure user 'Test User' exists
		
		boolean test_user_exists = solo.searchText("^Test User$");
		
		if (!test_user_exists){
			solo.sendKey(solo.MENU);
			solo.clickOnText("Add User") ;
			solo.enterText(0, "Test User");
			solo.enterText(1, "test@akvo.org");
			solo.clickOnText("Save") ;
			solo.goBack();
		}
	}

	@Smoke 
	public void testSelectSurvey() throws Exception {
		
		solo.clickOnMenuItem("Test Survey.*");
		// no user yet, should show error message
		boolean actual = solo.searchText("Please click the Manage Users icon and choose a user before continuing.");
		assertEquals("no-user-selected error message not shown", true, actual); 
		
		solo.clickOnMenuItem("OK");
		
		// select Test User
		solo.clickOnMenuItem("Manage Users");
		solo.clickOnText("^Test User$");
		
		//should go back to main screen now
		actual = solo.searchText("Test User");
		assertEquals("User not selected", true, actual); 
		
		// select to FieldSurvey Test 1
		solo.clickOnMenuItem("Test Survey.*");
		actual = solo.searchText("^Test Survey");
		assertEquals("Survey not started", true, actual); 	
		
		solo.goBack();	
	}


	@Smoke
	public void fillInSurvey() throws Exception {
		solo.clickOnMenuItem("FieldSurvey Test.*");
		
	}

	@Override
	public void tearDown() throws Exception {
		//Robotium will finish all the activities that have been opened
		solo.finishOpenedActivities();
	}
}
