#!/bin/sh

# USAGE: ./add_users.sh userslist.csv
echo "$1 "
java -cp bin:"lib/*" \
     org.akvo.gae.remoteapi.RemoteAPI \
     AddUsers \
     "localhost" \
     "$1"
