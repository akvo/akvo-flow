# Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
#
# This file is part of Akvo FLOW.
#
# Akvo FLOW is free software: you can redistribute it and modify it under the terms of
# the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
# either version 3 of the License or any later version.
#
# Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
# without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU Affero General Public License included below for more details.
#
# The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>

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
