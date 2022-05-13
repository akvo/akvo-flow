#!/usr/bin/env bash

set -eu
# USAGE: ./remove_user_all_instances.sh userEmail

REPOS_HOME="$(cd $(dirname "$0")/../../.. && pwd)"
FLOW_SERVER_CONFIG="$REPOS_HOME/akvo-flow-server-config"

find $FLOW_SERVER_CONFIG/ -name "appengine-web.xml" -exec sed -n "s/\(.*\)<application>\(.*\)<\/application>\(.*\)/\2/p" {} \; | sort > instances.txt

for i in $(cat instances.txt); do
  ./remove_user.sh $i $1
done
