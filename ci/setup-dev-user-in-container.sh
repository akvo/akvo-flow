#!/usr/bin/env sh

#
# Copyright 2015 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License")
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -ue

HOST_UID=$(stat -c "%u" docker-compose.yml)
HOST_GID=$(stat -c "%g" docker-compose.yml)

HOST_USER=akvo

GROUP_EXISTS=$(id -g ${HOST_GID} || echo "")

if [ -z ${GROUP_EXISTS} ]; then
    echo "Creating group $HOST_UID in container"
    groupadd --gid "$HOST_GID" "$HOST_USER"
else
    echo "Group $HOST_UID already exists"
fi

USER_EXISTS=$(id -u ${HOST_UID} || echo "")

if [ -z ${USER_EXISTS} ]; then
    echo "Creating user 'akvo' with uid $HOST_UID in container"

    useradd "$HOST_USER" --home "/home/$HOST_USER" --gid "$HOST_GID" --uid "$HOST_UID" --shell /bin/bash
    echo "$HOST_USER:pw" | chpasswd

    cp -r /root/.lein "/home/$HOST_USER/"

    chown -R "$HOST_USER":"$HOST_USER" "/home/$HOST_USER"
else
 echo "Not creating any user in container"
fi


if [ ${HOST_UID} -eq "0" ]; then
    echo "Guessing you are in a Mac, running as root"
    $@
else
    echo "Guessing you are in Linux, running as akvo"
    su "$HOST_USER" -c "$@"
fi
