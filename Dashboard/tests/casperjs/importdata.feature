Feature: Import a data spreadsheet that overwrites existing data
	In order to perform 

export data from flow
change data .xls and .xlsx
import data to flow (with changes made to excel file)


GAE looks for the survey instance_id in excel spreadsheet
GAE matches the question_id in excel spreadsheet

	Given I am logged into the Dashboard as Caetie
	And I click the DATA tab
	And I click the "DATA Cleaning" form
	And I select "caetie test" from the "Akvo group" survey group (both selections are in dropdown menus)
	And I click "import clean data" button (server then initiates a file upload prompt for system)
	And I select the updated .xlsx
	(Test for the presence of the updated text box showing the file path of the selected file. In firefox it shows up to the left of the browse button and in chrome it shows up to the right.)
	And I should see evidence that the file I have chosen shows up in text box
	And I click "import clean data"
	And I should see the progress bar (updating)
	And I should see the name of the uploaded file just beneath progress bar
 	And then I should see the "Upload Complete"  popup window when the upload is complete
	And I click Messages (navigate to "Messages" page)
	I should see a table with DATE, SURVEY ID, SURVEY (group/user), TYPE, and MESSAGE columns
	
Scenario: Data has been updated and we test for presence of changes in "Data" section
	When I click on "DATA" dashboard link
	I should "land" on "INSPECT DATA" active table
	And I click on the "edit" link in the last column of the row
	And I should see new data

Next Test: Add User Tab Process
Next Test: 
	


