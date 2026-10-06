#!/usr/bin/env bash

set -eu
# USAGE: ./remove_orphaned_survey_instances.sh

# The instance id is the directory holding the descriptor. It used to be read from
# <application>, which no longer exists in any descriptor: gen2 App Engine forbids it.
find "$FLOW_SERVER_CONFIG" -mindepth 2 -maxdepth 2 -name appengine-web.xml \
    | sed -E 's#.*/([^/]+)/appengine-web\.xml$#\1#' | sort > instances.txt

for i in $(cat instances.txt); do
  ./remove_orphaned_survey_instances.sh $i
done
