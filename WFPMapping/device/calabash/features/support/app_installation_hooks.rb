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

require 'calabash-android/management/app_installation'

AfterConfiguration do |config|
	FeatureNameMemory.feature_name = nil
end

Before do |scenario|
  feature_name = scenario.feature.name
  if FeatureNameMemory.feature_name != feature_name
    log "Is first scenario - reinstalling apps"
    uninstall_apps
    install_app(ENV["TEST_APP_PATH"])
   install_app(ENV["APP_PATH"])
    FeatureNameMemory.feature_name = feature_name
	end
end

at_exit do
#	uninstall_apps
end

FeatureNameMemory = Class.new
class << FeatureNameMemory
  @feature_name = nil
  attr_accessor :feature_name
end
