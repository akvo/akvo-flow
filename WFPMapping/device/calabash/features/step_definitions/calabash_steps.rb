require 'calabash-android/calabash_steps'

When /^user "([^\"]*)" with email "([^\"]*)" exists$/ do |user_name,email|
			step %{I press "Manage Users"}
    	step %{I select "Add User" from the menu}
    	step %{I enter "#{user_name}" into input field number 1}
    	step %{I enter "#{email}" into input field number 2}    
    	step %{I press "Save"}
    	step %{I go back}
    	
end

When /^user "([^\"]*)" with email "([^\"]*)" is selected$/ do |user_name,email|
			step %{I press "Manage Users"}
    	step %{I select "Add User" from the menu}
    	step %{I enter "#{user_name}" into input field number 1}
    	step %{I enter "#{email}" into input field number 2}    
    	step %{I press "Save"}
    	step %{I press "#{user_name}"}
end

