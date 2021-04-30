#!/usr/bin/env bash

set -eu
# USAGE: ./remove_orphaned_survey_instances.sh

#find $FLOW_SERVER_CONFIG/ -name "appengine-web.xml" -exec sed -n "s/\(.*\)<application>\(.*\)<\/application>\(.*\)/\2/p" {} \; | sort > instances.txt

for i in $(cat instances.txt); do
  ./remove_orphaned_survey_instances.sh $i
done