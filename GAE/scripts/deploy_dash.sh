#!/bin/bash

#   Copyright (C) 2010-2016 Stichting Akvo (Akvo Foundation)
#
#   This file is part of Akvo FLOW.
#
#   Akvo FLOW is free software: you can redistribute it and modify it under the terms of
#   the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
#   either version 3 of the License or any later version.
#
#   Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
#   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
#   See the GNU Affero General Public License included below for more details.
#
#   The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.


THIS_SCRIPT="$0"
SCRIPT_NAME="$(basename $THIS_SCRIPT)"
PASSWORD_FILE_PATH="$1"
INSTANCE_NAME="$2"

function display_usage_and_exit
{
    printf "Usage: $SCRIPT_NAME /full/path/to/gae_password_file <instance_name> \n"
    exit 1
}

if [[ -z "$INSTANCE_NAME" ]]; then
    display_usage_and_exit
fi

if [[ -z "$PASSWORD_FILE_PATH" ]]; then
    display_usage_and_exit
fi


REPOS_HOME="$(cd $(dirname "$THIS_SCRIPT")/../../.. && pwd)"
PROJECT_HOME="$REPOS_HOME/akvo-flow"
CODE_DIR="$PROJECT_HOME/GAE"
DASHBOARD_DIR="$PROJECT_HOME/Dashboard"

cd "$CODE_DIR"

if [[ ! -r "$PASSWORD_FILE_PATH" ]]; then
    printf "## GAE password file path should be fully qualified\n"
    display_usage_and_exit
fi


function prebuild_cleanup
{
    printf "\n>> Clearing generated class files...\n"
    rm -r "$CODE_DIR/war/WEB-INF/classes"
    printf "\n>> Clearing SDK libs...\n"
    cd "$CODE_DIR/war/WEB-INF/lib"
    rm appengine*.jar datanucleus*.jar geronimo*.jar jdo2*.jar jsr107cache*.jar
    cd "$CODE_DIR"
    printf "\n"
}

function display_build_env_details
{
    echo ">> Java compiler:"
    java -version
    printf "\n>> Ant runner:\n"
    ant -version
    printf "\n"
}

function run_build_task
{
    TASK_NAME=$1
    TASK_PARAMETERS="$2"

    cd $CODE_DIR
    ant $TASK_NAME "$TASK_PARAMETERS"

    # exit if build errors occur
    if [ $? -ne 0 ]; then
        printf "## Exiting due to failed build for [$INSTANCE_NAME] as above\n"
        exit 1
    fi
}

CONFIG_DIR="$REPOS_HOME/akvo-flow-server-config"

printf ">> Starting build for [$INSTANCE_NAME]\n"
prebuild_cleanup
display_build_env_details
run_build_task copyconfig -Dconfig=$CONFIG_DIR/$INSTANCE_NAME
run_build_task update -Dpwd=$PASSWORD_FILE_PATH
printf "\n>>>> [$INSTANCE_NAME] deployed and ready\n\n"
