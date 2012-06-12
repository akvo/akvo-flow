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

import java.util.TreeMap;



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

	@Smoke
	public void t1estCreateUser() throws Exception {
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
	public void t1estEditUser() throws Exception {
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
	public void t1estDeleteUser() throws Exception {
		////////////////SCENARIO -  Delete A USER /////////////////////	
		deleteTestUsers();
		createUser("Test User 3","name-3.lastname@akvo.org");
		solo.clickOnMenuItem("Manage Users");
		solo.clickLongOnText("^Test User 3$");
		solo.clickOnText("Delete");
		assertEquals("user not deleted", false, solo.searchText("Test User 3")); 	
	}
	
	@Smoke
	public void t1estSelectUser() throws Exception {
		////////////////SCENARIO -  Delete A USER /////////////////////	
		deleteTestUsers();
		createUser("Test User 4","name-4.lastname@akvo.org");
		solo.clickOnMenuItem("Manage Users");
		solo.clickOnText("^Test User 4$");
		assertEquals("user not selected", true, solo.searchText("Test User 4")); 	
	}
	
	@Smoke
	public void testFillAllQuestions() throws Exception {
		////////////////SCENARIO -  Delete A USER /////////////////////	
		//deleteTestUsers();
		//createAndSelectUser("Test User 1","name-1.lastname@akvo.org");
		selectUser("Test User 1");
		solo.clickOnText("Robotium test survey 1.*");
		assertEquals("Survey not selected", true, solo.searchText("Group 1.*")); 	
		
		// free text field
		solo.clearEditText(0);
		solo.enterText(0, "content textfield 1");
		
		// option field
		solo.clickOnRadioButton(1);
		
		// number field
		solo.clearEditText(1);
		solo.enterText(1,"42.42");

		// geo field
		solo.clickOnText("Check Geo Location");
		
		// date field
		solo.clickOnText("Select Date");
		solo.clickOnText("Set");
		
		// barcode field
		solo.clearEditText(2);
		solo.enterText(2,"Barcode code");

		solo.clickOnText("Next");
		
		solo.sendKey(solo.MENU);
		solo.clickOnText("Save and Start New") ;
		
		
	}

	@Override
	public void tearDown() throws Exception {
		//Robotium will finish all the activities that have been opened
		solo.finishOpenedActivities();
	}
}
