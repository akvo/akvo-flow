Feature: filling in survey with valid data
	In order to fill in a survey successfully
	As a user
	I want to be able to fill in all the different fields in a survey.
	# uses Calabash testservey 1
	# fills in valid data
		
	Scenario: fill in all questions
		Given user "Test User 1" with email "name-1.lastname@akvo.org" exists
		When I press "Manage Users"
   	And I press "Test User 1"
		And I press "Calabash testsurvey 1 v. 1.0"
		Then I see "Group 1"
		
		#free text field	
		Then I enter "free text question text" into input field number 1
		
		#option field
	#	Then I press image button number 2	
		
		#number field
		Then I enter "42" into input field number 2

		# geo field
		Then I enter "43.343434" into input field number 3
		Then I enter "5.556655" into input field number 4
		Then I enter "3" into input field number 5
		
		# date field
		Then I press "Select Date"
		Then I press "Set"
		