#!/usr/bin/env bash
#
# Applies an instance's own platform settings to the generated app.yaml.
#
# Meant to be sourced, not executed.
#
# ===========================================================================
# Why this exists
# ===========================================================================
#
# app.yaml is not checked in anywhere and is not a template. It is generated
# once per build, by `mvn appengine:stage` in ci/mvn-deploy.sh, from whichever
# single instance CI is deploying -- in practice UAT2. That generated file is
# then zipped into the version archive and shipped unchanged to every other
# instance by deploy.sh.
#
# The archive deliberately carries no appengine-web.xml (ci/mvn-deploy.sh
# removes it before zipping), and deploy_instance copies the correct one in
# per instance. That copy is not wasted: the Java runtime reads the descriptor
# at startup for <system-property> and <env-var>, so per-instance credentials,
# bucket names and aliases all work.
#
# What it does not affect is the platform's own view of the app. `gcloud app
# deploy` is pointed at app.yaml, so scaling, handlers and instance class come
# from the staged file -- that is, from UAT2 -- no matter what the instance's
# descriptor says. Nothing warns about it, because from gcloud's point of view
# nothing is wrong.
#
# That is how akvoflow-201 spent three years capped at 5 instances while its
# descriptor asked for 10: akvo-flow-server-config 64163c7, "[#748] Increase
# number of GAE instances for ecosia", March 2023. Reviewed, merged, deployed,
# and inert.
#
# The thorough fix is to stage per instance, but staging needs Maven and the
# full source tree, which is precisely what the prebuilt archive exists to
# avoid -- the fan-out image is a cloud-sdk image with no JDK. So instead we
# translate the handful of generated keys back out of the instance descriptor.
#
# ===========================================================================
# Why an unknown setting is fatal
# ===========================================================================
#
# The bug being fixed here is a setting that is silently ignored. A function
# that quietly dropped the settings it did not recognise would reproduce that
# bug exactly, just one layer further in, so anything unrecognised inside
# <automatic-scaling> stops the deploy and names itself.
#
# Across all 110 descriptors today that set is only max-instances (110),
# max-concurrent-requests (110) and instance-class (6).
#
# $1 instance id, for messages. $2 the instance's appengine-web.xml.
# $3 the staged app.yaml to rewrite in place.
apply_platform_settings() {
    local instance_id="${1}"
    local descriptor="${2}"
    local app_yaml="${3}"
    local tag key value unknown

    [[ -s "${descriptor}" ]] || { echo "${instance_id}: ${descriptor} is missing or empty" >&2; return 1; }
    [[ -s "${app_yaml}" ]] || { echo "${instance_id}: ${app_yaml} is missing or empty" >&2; return 1; }

    unknown=$(sed -n '/<automatic-scaling>/,/<\/automatic-scaling>/p' "${descriptor}" \
                  | grep -o '<[a-z][a-z-]*>' | tr -d '<>' \
                  | grep -vxE 'automatic-scaling|max-instances|max-concurrent-requests' \
                  | sort -u)
    if [[ -n "${unknown}" ]]; then
        echo "${instance_id}: unsupported <automatic-scaling> setting(s): ${unknown//$'\n'/ }" >&2
        echo "Deploying would silently ignore them. Teach apply_platform_settings in" >&2
        echo "scripts/deploy/platform-settings.sh how to translate them first." >&2
        return 1
    fi

    # Both of these are present in every descriptor and, because UAT2 sets them
    # too, in every generated app.yaml. A missing key means staging produced
    # something unexpected, so say so rather than deploy UAT2's value.
    for tag in max-instances max-concurrent-requests; do
        value=$(sed -n "s|.*<${tag}>\([^<]*\)</${tag}>.*|\1|p" "${descriptor}" | head -1)
        [[ -n "${value}" ]] || continue
        key="${tag//-/_}"
        grep -q "^  ${key}: " "${app_yaml}" \
            || { echo "${instance_id}: no '${key}' under automatic_scaling in ${app_yaml}" >&2; return 1; }
        sed -i "s|^  ${key}: .*|  ${key}: ${value}|" "${app_yaml}"
    done

    # instance_class is a top-level key, and UAT2 sets no <instance-class>, so
    # the generated file usually has no such line to substitute. Insert it after
    # runtime:, which staging always emits first. Appending would land inside the
    # handlers list and produce an invalid file.
    value=$(sed -n "s|.*<instance-class>\([^<]*\)</instance-class>.*|\1|p" "${descriptor}" | head -1)
    if [[ -n "${value}" ]]; then
        if grep -q '^instance_class: ' "${app_yaml}"; then
            sed -i "s|^instance_class: .*|instance_class: ${value}|" "${app_yaml}"
        else
            grep -q '^runtime: ' "${app_yaml}" \
                || { echo "${instance_id}: no top-level 'runtime:' in ${app_yaml}" >&2; return 1; }
            # awk rather than `sed a`, whose syntax differs between GNU and the
            # busybox sed in the alpine deploy image.
            awk -v ic="${value}" '{ print } /^runtime: /{ print "instance_class: " ic }' \
                "${app_yaml}" > "${app_yaml}.tmp" && mv "${app_yaml}.tmp" "${app_yaml}"
        fi
    fi
}
