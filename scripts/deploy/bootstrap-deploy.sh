#!/usr/bin/env sh

set -eu

apk add --no-cache \
    bash=4.4.19-r1 \
    git=2.15.0-r1 \
    jq=1.5-r5 \
    unzip=6.0-r2 \
    openjdk8=8.151.12-r0 \
    parallel=20171122-r0 \
    shadow=4.5-r0 \
    su-exec=0.2-r0

gcloud components install app-engine-java --quiet
rm -rf /google-cloud-sdk/.install/.backup
rm -rf /google-cloud-sdk/.install/.download

adduser -D -h /home/akvo -s /bin/bash akvo akvo

NEW_UID=$(stat -c '%u' /akvo-flow)
NEW_GID=$(stat -c '%g' /akvo-flow)

groupmod -g "$NEW_GID" -o akvo >/dev/null 2>&1
usermod -u "$NEW_UID" -o akvo >/dev/null 2>&1

mkdir /akvo-flow-server-config
chown akvo:akvo /akvo-flow-server-config

# Disable annoying citation warning
mkdir -p /home/akvo/.parallel
touch /home/akvo/.parallel/will-cite
chown akvo:akvo /home/akvo/.parallel/will-cite

exec su-exec akvo:akvo ./scripts/deploy/deploy.sh "$@"
