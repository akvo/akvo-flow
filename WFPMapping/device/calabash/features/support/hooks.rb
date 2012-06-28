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

Before do |scenario|
  StepCounter.step_index = 0
  StepCounter.step_line = scenario.raw_steps[StepCounter.step_index].line
end

AfterStep do |scenario|
  #Handle multiline steps
  StepCounter.step_index = StepCounter.step_index + 1
  StepCounter.step_line = scenario.raw_steps[StepCounter.step_index].line unless scenario.raw_steps[StepCounter.step_index].nil?
end

StepCounter = Class.new
class << StepCounter
  @step_index = 0
  @step_line = 0
  attr_accessor :step_index, :step_line
end
