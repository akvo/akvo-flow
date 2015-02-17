#! /bin/sh

# Assumes the script is executed in the /path/to/scripts/data/ directory
# and the sources have been compiled in a bin directory also located
# in the /path/to/scripts/data directory

java -cp bin:"lib/*" org.akvo.gae.remoteapi.RemoteAPI CorrectFolderSurveyPath $1 "$2" "$3" $4
