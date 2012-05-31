Feature: User management

  Scenario: As a user I can create a user
    When I press "Manage Users"
    Then I see "Select the current user by clicking. To create a new user, press the Menu button and select Add User. Long-click to edit a user."
    
    Then I select "Add User" from the menu
    And I enter "Test User 1" into input field number 1
    And I enter "m.t.westra@gmail.com" into input field number 2    
    And I press "Save"
    Then I see "Test User 1"
    
