Feature: User management
	In order to keep track of who uses the phone
	As a user
	I want to create, edit, delete and select users
	
	Scenario: Create a user
    When I press "Manage Users"
    Then I see "Select the current user by clicking. To create a new user, press the Menu button and select Add User. Long-click to edit a user."
    Then I select "Add User" from the menu
    And I enter "Test User 1" into input field number 1
    And I enter "name-1.lastname@akvo.org" into input field number 2    
    And I press "Save"
    Then I see "Test User 1"
    And I long press "Test User 1"
    And I press "Edit User"
  	Then I see "Test User 1"
  	Then I see "name-1.lastname@akvo.org"
  

  Scenario: Edit a user
  	Given user "Test User 2" with email "name-2.lastname@akvo.org" exists
  	When I press "Manage Users"
   	And I long press "Test User 2"
  	And I press "Edit User"
  	And I clear input field number 1
  	And I clear input field number 2
  	And I enter "Test User 2a" into input field number 1
  	And I enter "name-2a.lastname@akvo.org" into input field number 2  
  	And I press "Save"
  	Then I see "Test User 2a"
  	And I long press "Test User 2a"
    And I press "Edit User"
  	Then I see "Test User 2a"
  	Then I see "name-2a.lastname@akvo.org"
  
  Scenario: Delete a user
  	Given user "Test User 3" with email "name-3.lastname@akvo.org" exists
  	When I press "Manage Users"
  	And I long press "Test User 3"
  	And I press "Delete User"
  	Then I don't see "Test User 3"
  	
  Scenario: Select a user
  	Given user "Test User 4" with email "name-3.lastname@akvo.org" exists
  	When I press "Manage Users" 	
  	And I press "Test User 4"
  	# on the top of the window it should say "Current user: Test user 4"
  	Then I see "Test User 4"
  	
  	