#! /bin/sh

# Assumes the script is executed in the /path/to/scripts/data/ directory
# and the sources have been compiled in a bin directory also located
# in the /path/to/scripts/data directory

java -cp bin:lib/appengine-api-1.0-sdk-1.8.8.jar:lib/appengine-remote-api.jar org.akvo.gae.remoteapi.RemoteAPI CorrectFolderSurveyPath $1 "$2" "$3"
