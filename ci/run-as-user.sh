#!/usr/bin/env bash

set -eu

NEW_UID=$(stat -c '%u' /app/src)
NEW_GID=$(stat -c '%g' /app/src)

groupmod -g "$NEW_GID" -o akvo >/dev/null 2>&1
usermod -u "$NEW_UID" -o akvo >/dev/null 2>&1

mkdir -p /home/akvo/.lein
cp -r /root/.lein/* /home/akvo/.lein/
chown akvo:akvo -R /home/akvo/.lein/

exec su-exec akvo:akvo "$@"
