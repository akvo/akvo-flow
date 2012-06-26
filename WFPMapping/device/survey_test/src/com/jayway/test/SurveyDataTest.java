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
	
	

	@Override
	public void tearDown() throws Exception {
		//Robotium will finish all the activities that have been opened
		solo.finishOpenedActivities();
	}
}
