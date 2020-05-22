#!/usr/bin/env bash

set -u

function log {
   echo "$(date +"%T") - INFO - $*"
}

GITHUB_PROJECT=akvo-flow
NOTIFICATION=${4:-slack}

function read_version () {
    CLUSTER=$1
    log "Reading ${CLUSTER} version"
    VERSION=$(gcloud app versions list --project="${CLUSTER}" --hide-no-traffic --service=default | grep "default" | tr -s " " | cut -f 2  -d\ )
}

if [[ -z "$(gcloud config list --format='value(core.account)')" ]]; then
  gcloud auth login
fi

read_version "akvoflow-dev2"
TEST_LIVE_VERSION=$VERSION

read_version "akvoflow-dev1"
PROD_DARK_VERSION=$VERSION

read_version "akvoflow-23" # WHH instance
PROD_LIVE_VERSION=$VERSION

log "Deployed test version is $TEST_LIVE_VERSION"
log "Deployed prod dark version is $PROD_DARK_VERSION"
log "Diff between test and dark prod is https://github.com/akvo/${GITHUB_PROJECT}/compare/$PROD_DARK_VERSION..$TEST_LIVE_VERSION"

if [[ "$PROD_DARK_VERSION" = "$PROD_LIVE_VERSION" ]]; then
  log "Deployed prod live version is same as prod dark version"
else
  log "Deployed prod live version is $PROD_LIVE_VERSION"
  log "Diff between test and live prod is https://github.com/akvo/${GITHUB_PROJECT}/compare/$PROD_LIVE_VERSION..$TEST_LIVE_VERSION"
fi

## Lets assume this git history:
# v1 (prod-live)
# v2
# v3 (prod-dark)
# v4
# v5 (test)
# When promoting a build, we want to report the new changes (v4, v5) that will show in dark.
# But lets say that we have the previous history and we do a flip in production. We have:
# v1 (prod-dark)
# v2
# v3 (prod-live)
# v4
# v5 (test)
# So in this case we really want to report the changes between test and prod-live (still v4, v5)
# In general terms, we want to see the diff between test and the last promotion.
# Instead of looking at git tags, we just check which of the prod envs is older
if git merge-base --is-ancestor "$PROD_LIVE_VERSION" "$PROD_DARK_VERSION"; then
  NEWEST_VERSION_IN_PROD=$PROD_DARK_VERSION
else
  NEWEST_VERSION_IN_PROD=$PROD_LIVE_VERSION
fi
log "Commits to be deployed:"
echo ""
echo "$NEWEST_VERSION_IN_PROD"
git log --oneline "$NEWEST_VERSION_IN_PROD".."$TEST_LIVE_VERSION" | grep -v "Merge pull request" | grep -v "Merge branch"

TAG_NAME="promote-$(TZ=UTC date +"%Y%m%d-%H%M%S")"

echo ""
read -r -e -p "Does this deployment contain a hotfix, rollback or fix-forward for a previous deployment? [Y/n] " FIX
if [ "${FIX}" != "n" ] || [ "${FIX}" != "N" ]; then
   PROMOTION_REASON="FIX_RELEASE"
else
   PROMOTION_REASON="REGULAR_RELEASE"
fi

./ci/"generate-${NOTIFICATION}-notification.sh" "${NEWEST_VERSION_IN_PROD}" "${TEST_LIVE_VERSION}" "Promoting ${GITHUB_PROJECT} to dark production (aka https://uat1.akvoflow.org)" "good" "wrap_slack" "$GITHUB_PROJECT"

log "To deploy, run: "
echo "----------------------------------------------"
echo "git tag -a $TAG_NAME $TEST_LIVE_VERSION -m \"$PROMOTION_REASON\""
echo "git push origin $TAG_NAME"
echo "./notify.team.sh"
echo "----------------------------------------------"