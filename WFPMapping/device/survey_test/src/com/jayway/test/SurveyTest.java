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

	private void createUser(String string, String string2) {
		solo.clickOnMenuItem("Manage Users");
		solo.sendKey(solo.MENU);
		solo.clickOnText("Add User") ;
		solo.enterText(0, string);
		solo.enterText(1, string2);
		solo.clickOnText("Save") ;
		solo.goBack();
	}
	
	private void deleteTestUsers() {
		solo.clickOnMenuItem("Manage Users");
		while (solo.searchText("^Test User.*")) {
			solo.clickLongOnText("^Test User.*");
			solo.clickOnText("Delete") ;
			
		}
		solo.goBack();
		
	
	}

	@Smoke
	public void testCreateUser() throws Exception {
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
	
	@Smoke
	public void testEditUser() throws Exception {
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
	

	@Smoke
	public void testfillInSurvey() throws Exception {
		solo.clickOnMenuItem("Calabash testsurvey 1.*");
		
	}

	@Override
	public void tearDown() throws Exception {
		//Robotium will finish all the activities that have been opened
		solo.finishOpenedActivities();
	}
}
