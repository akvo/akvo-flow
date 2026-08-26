#!/usr/bin/env bash
#
# Preconditions shared by the two things that deploy Flow: ci/mvn-deploy.sh in CI and
# dev-deploy.sh from a developer's machine. They are kept together because they have to
# agree -- a rule that holds in CI but not locally would let a developer deploy something
# CI would have refused.
#
# Meant to be sourced, not executed.

# Everything the caller shells out to. These used to fail at the point of use, which is a
# poor place to find out: zip sits on the last line of ci/mvn-deploy.sh, so a missing zip
# meant a full deploy had already happened and been promoted before anything complained.
require_commands() {
    local missing=()
    local c
    for c in "$@"; do
	command -v "${c}" >/dev/null 2>&1 || missing+=("${c}")
    done
    if [[ ${#missing[@]} -gt 0 ]]; then
	echo "Missing required command(s): ${missing[*]}" >&2
	echo "They have to be provided by the deploy image, ci/Dockerfile.gae-deploy" >&2
	exit 1
    fi
}

# This build carries App Engine SDK 2.x and the DataNucleus fix for JVM 9 and later, so it
# only runs on a second-generation runtime -- and java8 does not accept deployments at all
# any more. An instance whose descriptor has not been migrated yet is therefore a mistake
# worth naming, rather than several minutes of staging followed by an opaque rejection.
#
# $1 is the descriptor, $2 a label for the instance it belongs to.
assert_gen2_descriptor() {
    local descriptor="${1}"
    local instance="${2}"

    [[ -s "${descriptor}" ]] || { echo "${descriptor} is missing or empty" >&2; exit 1; }
    grep -q "<appengine-web-app" "${descriptor}" \
	|| { echo "${descriptor} is not an appengine-web.xml descriptor" >&2; exit 1; }

    if grep -q "<runtime>java8</runtime>" "${descriptor}"; then
	echo "${instance} still declares <runtime>java8</runtime>." >&2
	echo "This build only supports the second generation runtime; migrate that" >&2
	echo "instance's appengine-web.xml before deploying to it." >&2
	exit 1
    fi

    grep -q "<app-engine-apis>true</app-engine-apis>" "${descriptor}" \
	|| { echo "${instance} is missing <app-engine-apis>true</app-engine-apis>; Flow would deploy and then fail at its first datastore call" >&2; exit 1; }
}
