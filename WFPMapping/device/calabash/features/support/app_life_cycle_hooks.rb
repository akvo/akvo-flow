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

require 'calabash-android/management/adb'

Before do |scenario|

  return if scenario.failed? #No need to start the server is anything before this has failed.
  cmd = "#{adb_command} shell am instrument -w -e class sh.calaba.instrumentationbackend.InstrumentationBackend #{ENV['TEST_PACKAGE_NAME']}/android.test.InstrumentationTestRunner"
  log "Starting test server using:"
  log cmd
  if is_windows?
    system(%Q(start /MIN cmd /C #{cmd}))
  else
    `#{cmd} 1>&2 &`
  end
  
  sleep 2
  begin
    connect_to_test_server
    log "Connection established"
  rescue Exception => e
    log "Exception:#{e.backtrace}"
  end
end



After do |scenario| 
  disconnect_from_test_server
end
