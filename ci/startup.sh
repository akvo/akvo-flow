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

set -eu

groupadd --gid "$HOST_GID" "$HOST_USER"

useradd "$HOST_USER" --home "/home/$HOST_USER" --gid "$HOST_GID" --uid "$HOST_UID" --shell /bin/bash
echo "$HOST_USER:pw" | chpasswd

cp -r /root/.lein "/home/$HOST_USER/"

chown -R "$HOST_USER":"$HOST_USER" "/home/$HOST_USER"

su "$HOST_USER" -c "$@"
