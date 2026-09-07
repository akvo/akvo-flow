#!/usr/bin/env bash
#
# Checks for apply_platform_settings. Run it directly:
#
#     ./scripts/deploy/platform-settings-test.sh
#
# The function rewrites the file that decides production scaling, and it runs
# unattended across every instance, so the cases it has to get wrong-loudly
# rather than wrong-quietly are worth pinning down.

set -uo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"
source ./platform-settings.sh

work=$(mktemp -d)
trap 'rm -rf "${work}"' EXIT

failures=0

check() {
    local what="${1}" expected="${2}" actual="${3}"
    if [[ "${expected}" == "${actual}" ]]; then
        echo "  ok    ${what}"
    else
        echo "  FAIL  ${what}: expected '${expected}', got '${actual}'"
        failures=$((failures + 1))
    fi
}

# A descriptor with the given <automatic-scaling> body and optional instance class.
descriptor() {
    local path="${1}" scaling="${2}" instance_class="${3:-}"
    {
        echo '<?xml version="1.0" encoding="utf-8"?>'
        echo '<appengine-web-app xmlns="http://appengine.google.com/ns/1.0">'
        echo '    <runtime>java21</runtime>'
        echo '    <app-engine-apis>true</app-engine-apis>'
        [[ -n "${instance_class}" ]] && echo "    <instance-class>${instance_class}</instance-class>"
        echo '    <automatic-scaling>'
        echo "${scaling}"
        echo '    </automatic-scaling>'
        echo '    <system-property name="alias" value="example.appspot.com" />'
        echo '</appengine-web-app>'
    } > "${path}"
}

# app.yaml as `mvn appengine:stage` emits it from UAT2: 5 instances, 50 concurrent.
staged_app_yaml() {
    cat > "${1}" <<'YAML'
runtime: java21
automatic_scaling:
  max_concurrent_requests: 50
  max_instances: 5
inbound_services:
- warmup
app_engine_apis: True
handlers:
- url: /.*
  script: unused
  login: optional
  secure: always
YAML
}

echo "akvoflow-201: a descriptor asking for more instances than UAT2"
descriptor "${work}/201.xml" '        <max-instances>10</max-instances>
        <max-concurrent-requests>50</max-concurrent-requests>'
staged_app_yaml "${work}/201.yaml"
apply_platform_settings akvoflow-201 "${work}/201.xml" "${work}/201.yaml" >/dev/null 2>&1
check "max_instances follows the descriptor" \
      "  max_instances: 10" "$(grep '^  max_instances:' "${work}/201.yaml")"
check "max_concurrent_requests kept" \
      "  max_concurrent_requests: 50" "$(grep '^  max_concurrent_requests:' "${work}/201.yaml")"
check "handlers left alone" "1" "$(grep -c '^- url: /\.\*' "${work}/201.yaml")"

echo "an instance pinning its own instance class"
descriptor "${work}/f1.xml" '        <max-instances>5</max-instances>
        <max-concurrent-requests>50</max-concurrent-requests>' F1
staged_app_yaml "${work}/f1.yaml"
apply_platform_settings akvoflow-101 "${work}/f1.xml" "${work}/f1.yaml" >/dev/null 2>&1
check "instance_class inserted once" "1" "$(grep -c '^instance_class: F1' "${work}/f1.yaml")"
check "inserted at the top level, right after runtime" \
      "instance_class: F1" "$(sed -n '2p' "${work}/f1.yaml")"

echo "instance class when app.yaml already carries one"
staged_app_yaml "${work}/f2.yaml"
sed -i '1a instance_class: F1' "${work}/f2.yaml"
descriptor "${work}/f2.xml" '        <max-instances>5</max-instances>
        <max-concurrent-requests>50</max-concurrent-requests>' F4
apply_platform_settings akvoflow-x "${work}/f2.xml" "${work}/f2.yaml" >/dev/null 2>&1
check "substituted, not duplicated" "1" "$(grep -c '^instance_class:' "${work}/f2.yaml")"
check "and it is the descriptor's value" "instance_class: F4" "$(grep '^instance_class:' "${work}/f2.yaml")"

echo "a scaling setting the function does not understand"
descriptor "${work}/unknown.xml" '        <max-instances>5</max-instances>
        <max-concurrent-requests>50</max-concurrent-requests>
        <min-idle-instances>2</min-idle-instances>'
staged_app_yaml "${work}/unknown.yaml"
err=$(apply_platform_settings akvoflow-y "${work}/unknown.xml" "${work}/unknown.yaml" 2>&1)
check "refuses to deploy" "1" "$?"
check "names the setting" "yes" "$(grep -q 'min-idle-instances' <<<"${err}" && echo yes || echo no)"

echo "a staged app.yaml missing the key we need to rewrite"
descriptor "${work}/nokey.xml" '        <max-instances>10</max-instances>
        <max-concurrent-requests>50</max-concurrent-requests>'
staged_app_yaml "${work}/nokey.yaml"
sed -i '/max_instances/d' "${work}/nokey.yaml"
apply_platform_settings akvoflow-z "${work}/nokey.xml" "${work}/nokey.yaml" >/dev/null 2>&1
check "refuses rather than shipping UAT2's value" "1" "$?"

echo "sourced into a strict shell"
# deploy.sh runs `set -euo pipefail`, and only escapes this because parallel
# invokes deploy_instance in a fresh shell that does not inherit -e. Anything
# calling the function directly from that script would not be so lucky, so pin
# the success path down under the strictest settings a caller might use.
descriptor "${work}/strict.xml" '        <max-instances>7</max-instances>
        <max-concurrent-requests>50</max-concurrent-requests>'
staged_app_yaml "${work}/strict.yaml"
(
    set -eo pipefail
    source ./platform-settings.sh
    apply_platform_settings akvoflow-strict "${work}/strict.xml" "${work}/strict.yaml"
) >/dev/null 2>&1
check "does not abort a set -e caller" "0" "$?"
check "and still rewrote the file" \
      "  max_instances: 7" "$(grep '^  max_instances:' "${work}/strict.yaml")"

echo
if [[ "${failures}" -eq 0 ]]; then
    echo "all checks passed"
else
    echo "${failures} check(s) failed"
    exit 1
fi
