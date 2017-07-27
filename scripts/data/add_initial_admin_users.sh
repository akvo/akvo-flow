#!/bin/bash

#   Copyright (C) 2010-2017 Stichting Akvo (Akvo Foundation)
#
#   This file is part of Akvo Flow.
#
#   Akvo Flow is free software: you can redistribute it and modify it under the terms of
#   the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
#   either version 3 of the License or any later version.
#
#   Akvo Flow is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
#   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
#   See the GNU Affero General Public License included below for more details.
#
#   The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.

THIS_SCRIPT="$0"
SCRIPT_NAME=$(basename $THIS_SCRIPT)
INSTANCE_ID="$1"

function display_usage_and_exit
{
    printf "Usage: $SCRIPT_NAME <instance_name>\n"
    exit 1
}

if [[ -z "$INSTANCE_ID" ]]; then
    display_usage_and_exit
fi

REPOS_HOME="$(cd $(dirname "$THIS_SCRIPT")/../../.. && pwd)"
CONFIG_REPO_PATH="$REPOS_HOME/akvo-flow-server-config"

# ensure config repo path exists
if [[ ! -r "$CONFIG_REPO_PATH" ]]; then
    printf "## Invalid config repo path: $CONFIG_REPO_PATH\n"
    printf ">> Config repo path should be fully qualified and readable\n"
    display_usage_and_exit
fi

SCRIPT_HOME="$(cd $(dirname "$THIS_SCRIPT") && pwd)"

cd "$SCRIPT_HOME"

SERVICE_ACCOUNT_ADDRESS="sa-$INSTANCE_ID@$INSTANCE_ID.iam.gserviceaccount.com"
INSTANCE_CONFIG_HOME="$CONFIG_REPO_PATH/$INSTANCE_ID"
SERVICE_ACCOUNT_KEY_FILE_PATH="$INSTANCE_CONFIG_HOME/$INSTANCE_ID.p12"

function display_generate_key_file_message_and_exit
{
    printf ">> Please generate a GAE service account private key file and store this in: $INSTANCE_CONFIG_HOME\n"
    exit 1
}

# ensure service account key file exists
if [[ ! -f "$SERVICE_ACCOUNT_KEY_FILE_PATH" ]]; then
    printf "## Service account key file not found at: $SERVICE_ACCOUNT_KEY_FILE_PATH\n"
    display_generate_key_file_message_and_exit
fi

# ensure service account key file is not empty
if [[ -z "$SERVICE_ACCOUNT_KEY_FILE_PATH" ]]; then
    printf "## Service account key file is empty: $SERVICE_ACCOUNT_KEY_FILE_PATH\n"
    display_generate_key_file_message_and_exit
fi

# ensure service account key file is readable
if [[ ! -r "$SERVICE_ACCOUNT_KEY_FILE_PATH" ]]; then
    printf "## Service account key file is not readable: $SERVICE_ACCOUNT_KEY_FILE_PATH\n"
    printf ">> Please ensure the key file can be read by user [$USER]\n"
    exit 1
fi

CSV_USER_ACCOUNT_FILE_PATH="$CONFIG_REPO_PATH/0_instanceCreation/config/super_admins.csv"

printf ">> Adding initial admin users for:  $INSTANCE_ID\n"
printf ">> Using service account key file:  $SERVICE_ACCOUNT_KEY_FILE_PATH\n"
printf ">> User account data file:          $CSV_USER_ACCOUNT_FILE_PATH\n\n"

java -cp bin:"lib/*" org.akvo.gae.remoteapi.RemoteAPI AddUsers \
    "$INSTANCE_ID" \
    "$SERVICE_ACCOUNT_ADDRESS" \
    "$SERVICE_ACCOUNT_KEY_FILE_PATH" \
    "$CSV_USER_ACCOUNT_FILE_PATH"
