#! /bin/sh

java -cp bin:lib/appengine-api-1.0-sdk-1.8.8.jar:lib/appengine-api-labs-1.8.8.jar:lib/appengine-jsr107cache-1.8.8.jar:lib/appengine-remote-api.jar:lib/jsr107cache-1.1.jar  org.akvo.gae.remoteapi.RemoteAPI CorrectFolderSurveyPath $1 "$2" "$3"
